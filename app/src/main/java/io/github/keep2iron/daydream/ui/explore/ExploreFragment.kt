package io.github.keep2iron.daydream.ui.explore

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
import io.github.keep2iron.fast4android.core.util.dp2px
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
import java.io.IOException


class ExploreModel(owner: LifecycleOwner) : LifeCycleViewModel(owner), LoadListener {

    private val movieService: MovieService by findService()

    val recommendMovies = AsyncDiffObservableList<Movie>(object : DiffUtil.ItemCallback<Movie>() {
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

    private fun getBitmapByUrl(url: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
        } catch (e: IOException) {
            Logger.e(e, "")
            null
        }
    }

    override fun onLoad(controller: LoadController, pagerValue: Any, isPullToRefresh: Boolean) {
        if (controller.isLoadDefault(pagerValue)) {
            Observable.zip(
                //1.query recommend movies
                movieService.queryRecommendMovies(
                    pagerValue as Int,
                    Constants.PAGE_SIZE
                ).map { it.value }.toObservable(),
                //2.query recommend ranks
                movieService.queryRecommendRanks().map { it.value }.toObservable(),
                //merge results
                BiFunction<List<Movie>, List<Rank>, Pair<List<Movie>, List<Rank>>> { t1, t2 ->
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

                    return@BiFunction Pair(t1, t2)
                }
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(AndroidSubscriber<Pair<List<Movie>, List<Rank>>> {
                    onSuccess = {
                        recommendMovies.update(it.first)
                        ranksList.update(it.second)
                        controller.loadComplete()
                    }
                    onError = {
                        controller.loadFailedComplete()
                    }
                })
        }
    }

}

class ExploreFragment : AbstractFragment<ViewDataBinding>() {

    val recyclerView: RecyclerView by FindViewById(R.id.recyclerView)
    val refreshLayout: SwipeRefreshLayout by FindViewById(R.id.refreshLayout)
    val pageStateLayout: PomeloPageStateLayout by FindViewById(R.id.pageStateLayout)

    val model: ExploreModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, LifecycleViewModelFactory(this)).get(ExploreModel::class.java)
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        val viewPool = RecyclerView.RecycledViewPool()
        ListBinder(recyclerView, SwipeRefreshAble(refreshLayout), false)
            .addSubAdapter(HotMovieAdapter(model.recommendMovies, viewPool))
            .addSubAdapter(ExploreRankAdapter(model.ranksList, viewPool))
            .setLoadListener(model)
            .setViewPool(viewPool)
            .bind()
    }

    override fun resId(): Int = R.layout.explore_fragment

}