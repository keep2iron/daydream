package io.github.keep2iron.daydream.ui.explore

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.keep2iron.daydream.R
import io.github.keep2iron.daydream.data.entity.Movie
import io.github.keep2iron.daydream.data.entity.Rank
import io.github.keep2iron.daydream.data.remote.MovieService
import io.github.keep2iron.daydream.util.Constants
import io.github.keep2iron.fast4android.arch.*
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.pager.SwipeRefreshAble
import io.github.keep2iron.pomelo.pager.load.ListBinder
import io.github.keep2iron.pomelo.pager.load.LoadController
import io.github.keep2iron.pomelo.pager.load.LoadListener
import io.github.keep2iron.pomelo.state.PomeloPageStateLayout
import io.github.keep2iron.pomelo.utilities.findService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.net.URL
import com.orhanobut.logger.Logger
import io.github.keep2iron.daydream.data.entity.RecommendItem
import io.github.keep2iron.daydream.data.entity.RecommendMovieCollection
import io.github.keep2iron.daydream.ui.detail.MovieDetailActivity
import io.github.keep2iron.daydream.util.PageTransition
import io.github.keep2iron.pomelo.pager.adapter.MultiTypeListAdapter
import io.github.keep2iron.pomelo.pager.rx.LoadSubscriber
import java.io.IOException
import java.util.*
import io.github.keep2iron.fast4android.arch.util.findViewByDelegate
import io.github.keep2iron.pomelo.state.PageState
import io.github.keep2iron.pomelo.state.PageStateObservable
import io.reactivex.functions.Function3

class ExploreModel(owner: LifecycleOwner) : LifeCycleViewModel(owner), LoadListener {

    private val movieService: MovieService by findService()

    val pageState = PageStateObservable(PageState.LOADING)

    val hotMovies = AsyncDiffObservableList<Movie>(object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    })

    val ranksList = AsyncDiffObservableList<Rank>(object : DiffUtil.ItemCallback<Rank>() {
        override fun areItemsTheSame(oldItem: Rank, newItem: Rank): Boolean {
            return oldItem.rankTitle == newItem.rankTitle
        }

        override fun areContentsTheSame(oldItem: Rank, newItem: Rank): Boolean {
            return oldItem == newItem
        }
    })

    val recommendMovies =
        AsyncDiffObservableList<Any>(object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return if (oldItem is Movie && newItem is Movie) {
                    oldItem.id == newItem.id
                } else if (oldItem is RecommendMovieCollection && newItem is RecommendMovieCollection) {
                    oldItem.recommendCover == newItem.recommendCover
                } else {
                    false
                }
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Any,
                newItem: Any
            ): Boolean {
                return if (oldItem is Movie && newItem is Movie) {
                    oldItem == newItem
                } else if (oldItem is RecommendMovieCollection && newItem is RecommendMovieCollection) {
                    oldItem == newItem
                } else {
                    false
                }
            }
        })

    private fun getBitmapByUrl(url: String): Bitmap? {
        return try {
            val conn = URL(url).openConnection()
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            BitmapFactory.decodeStream(conn.getInputStream())
        } catch (e: IOException) {
            Logger.e(e, "")
            null
        }
    }

    override fun onLoad(controller: LoadController, pagerValue: Any, isPullToRefresh: Boolean) {
        if (controller.isLoadDefault(pagerValue)) {
            onLoadRefresh(pagerValue, controller)
        } else {
            onLoadRecommendList(pagerValue, controller)
        }
    }

    private fun onLoadRefresh(
        pagerValue: Any,
        controller: LoadController
    ) {
        Observable.zip(
            //1.query hot movies
            movieService.queryHotMovies(
                pagerValue as Int,
                Constants.PAGE_SIZE
            ).map { it.value }.toObservable(),
            //2.query recommend ranks
            movieService.queryHotRanks()
                .map { it.value }.toObservable(),
            //3.query recommend movie
            movieService.queryRecommendMovies(pagerValue as Int, Constants.PAGE_SIZE)
                .map { it.value }.toObservable(),
            //merge results
            Function3<List<Movie>, List<Rank>, List<RecommendItem>, Array<Any>> { t1, t2, t3 ->
                t2.forEach {
                    val bitmap = getBitmapByUrl(it.rankCover)
                    val defaultColor = ContextCompat.getColor(
                        getApplication(),
                        R.color.fast_config_color_gray_2
                    )
                    if (bitmap == null) {
                        it.rankCoverMainColor = defaultColor
                    } else {
                        it.rankCoverMainColor =
                            Palette.from(bitmap).generate().darkMutedSwatch?.rgb
                                ?: defaultColor
                    }
                }

                return@Function3 arrayOf(t1, t2, t3)
            }).observeOn(AndroidSchedulers.mainThread())
            .subscribe(AndroidSubscriber<Array<Any>> {
                onSuccess = {arr->
                    val movies = arr[0] as List<Movie>
                    val ranks = arr[1] as List<Rank>
                    val recommendItems = arr[2] as List<RecommendItem>

                    pageState.setPageState(PageState.ORIGIN)
                    hotMovies.update(movies)
                    ranksList.update(ranks)

                    val convertList = recommendItems.map {
                        when (it.recommendItemType) {
                            RecommendItem.RECOMMEND_MOVIE -> {
                                it.recommendMovie
                            }
                            RecommendItem.RECOMMEND_MOVIE_COLLECTION -> {
                                it.recommendMovieCollection
                            }
                            else -> {
                                it
                            }
                        }
                    }
                    if (controller.isLoadDefault(pagerValue)) {
                        recommendMovies.update(convertList)
                    } else {
                        recommendMovies.updateAppend(convertList)
                    }

                    controller.intInc()
                    controller.loadComplete()
                }
                onError = {
                    pageState.setPageState(PageState.ORIGIN)
                    controller.setLoadMoreEnable(true)
                    controller.loadFailedComplete()
                }
            })
    }

    private fun onLoadRecommendList(
        pagerValue: Any,
        controller: LoadController
    ) {
        movieService.queryRecommendMovies(pagerValue as Int, Constants.PAGE_SIZE)
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.value }
            .subscribe(LoadSubscriber<List<RecommendItem>>(controller, { it.isNullOrEmpty() }) {
                onSuccess = { list ->
                    val convertList = list.map {
                        when (it.recommendItemType) {
                            RecommendItem.RECOMMEND_MOVIE -> {
                                it.recommendMovie
                            }
                            RecommendItem.RECOMMEND_MOVIE_COLLECTION -> {
                                it.recommendMovieCollection
                            }
                            else -> {
                                it
                            }
                        }
                    }
                    if (controller.isLoadDefault(pagerValue)) {
                        recommendMovies.update(convertList)
                    } else {
                        recommendMovies.updateAppend(convertList)
                    }
                    controller.intInc()
                }
            })
    }
}

class ExploreFragment : AbstractFragment<ViewDataBinding>() {

    val recyclerView: RecyclerView by findViewByDelegate(R.id.recyclerView)
    val refreshLayout: SwipeRefreshLayout by findViewByDelegate(R.id.refreshLayout)
    val pageStateLayout: PomeloPageStateLayout by findViewByDelegate(R.id.pageStateLayout)

    val model: ExploreModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, LifecycleViewModelFactory(this)).get(ExploreModel::class.java)
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        val viewPool = RecyclerView.RecycledViewPool()
        model.pageState.setupWithPageStateLayout(pageStateLayout)
        ListBinder(recyclerView, SwipeRefreshAble(refreshLayout), true)
            .addSubAdapter(HotMovieAdapter(model.hotMovies, viewPool))
            .addSubAdapter(ExploreRankAdapter(model.ranksList, viewPool))
            .addSubAdapter(ExploreRecommendLabelAdapter())
            .addSubAdapter(MultiTypeListAdapter(model.recommendMovies).apply {
                registerAdapter<Movie>(
                    ExploreRecommendMovieAdapter(
                        model.recommendMovies,
                        viewPool
                    ).apply {
                        setOnItemClickListener { position, view, itemView ->
                            val movie = model.recommendMovies[position] as Movie
                            PageTransition.start<MovieDetailActivity>(
                                requireActivity(),
                                "movieId" to movie.id
                            )
                        }
                    }
                )
                registerAdapter<RecommendMovieCollection>(
                    ExploreRecommendMovieCollectionAdapter(
                        model.recommendMovies
                    )
                )
            })
            .setLoadListener(model)
            .setViewPool(viewPool)
            .bind()
            .load()
    }

    override fun resId(): Int = R.layout.explore_fragment

}