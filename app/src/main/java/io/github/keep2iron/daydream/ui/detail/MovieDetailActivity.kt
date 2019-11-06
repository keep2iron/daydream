package io.github.keep2iron.daydream.ui.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import io.github.keep2iron.base.util.FastStatusBarHelper
import io.github.keep2iron.base.util.setPaddingTop
import io.github.keep2iron.base.util.setStatusBarDark
import io.github.keep2iron.daydream.R
import io.github.keep2iron.daydream.data.entity.Movie
import io.github.keep2iron.daydream.data.entity.RespMovieDetail
import io.github.keep2iron.daydream.data.remote.MovieService
import io.github.keep2iron.daydream.databinding.DetailMovieActivityBinding
import io.github.keep2iron.fast4android.arch.AbstractActivity
import io.github.keep2iron.fast4android.arch.LifeCycleViewModel
import io.github.keep2iron.fast4android.arch.LifecycleViewModelFactory
import io.github.keep2iron.fast4android.arch.swipe.ParallaxBack
import io.github.keep2iron.fast4android.core.layout.IFastLayout
import io.github.keep2iron.fast4android.core.util.dp2px
import io.github.keep2iron.fast4android.tabsegment.TextFastTabSegmentAdapter
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.AndroidSubscriber
import io.github.keep2iron.pomelo.state.PageState
import io.github.keep2iron.pomelo.state.PageStateObservable
import io.github.keep2iron.pomelo.state.PomeloPageStateLayout
import io.github.keep2iron.pomelo.utilities.findService
import io.reactivex.android.schedulers.AndroidSchedulers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.View
import io.github.keep2iron.peach.DrawableCreator


class MovieDetailModel(owner: LifecycleOwner) : LifeCycleViewModel(owner) {

    var movieId: Int = 0

    val pageStateObservable = PageStateObservable(PageState.LOADING)

    val movieDetail = MutableLiveData<Movie>()

    val movieService: MovieService by findService()

    fun queryDetail() {
        movieService.queryMovieDetail(movieId)
            .map {
                it.value
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(AndroidSubscriber<RespMovieDetail> {
                onSuccess = {
                    movieDetail.value = it.movie
                    pageStateObservable.setPageState(PageState.ORIGIN)
                }
                onError = {
                    pageStateObservable.setPageState(PageState.NETWORK_ERROR)
                }
            })
    }

}

@ParallaxBack
class MovieDetailActivity : AbstractActivity<DetailMovieActivityBinding>() {

    val model: MovieDetailModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, LifecycleViewModelFactory(this))
            .get(MovieDetailModel::class.java)
    }

    override fun initVariables(savedInstanceState: Bundle?) {
        FastStatusBarHelper.translucent(this)
        setStatusBarDark()

        dataBinding.movie = model.movieDetail
        dataBinding.lifecycleOwner = this

        val pageStateLayout = findViewById<PomeloPageStateLayout>(R.id.pageStateLayout)
        model.pageStateObservable.setupWithPageStateLayout(pageStateLayout)

        subscribeOnUI()

        initView()

        model.queryDetail()

    }

    private fun subscribeOnUI() {
        val movieId = intent.getIntExtra("movieId", 0)
        model.movieId = movieId
        model.movieDetail.observe(this, Observer { movie ->
            ImageLoaderManager.getInstance()
                .showImageView(dataBinding.ivMovieCover, movie.movieCover) {
                    radius = dp2px(5).toFloat()
                }

            dataBinding.topBarLayout.setup {
                title = movie.movieName
            }

            val defaultColor =
                ContextCompat.getColor(this@MovieDetailActivity, R.color.colorPrimary)
            ImageLoaderManager.getInstance()
                .getBitmap(applicationContext, movie.movieCover, { bitmap ->
                    if (bitmap != null) {
                        Palette.from(bitmap).generate {
                            if (it != null) {
                                val startColor = it.getDarkMutedColor(defaultColor)
                                val endColor = it.getDarkVibrantColor(defaultColor)
                                window.setBackgroundDrawable(
                                    GradientDrawable(
                                        GradientDrawable.Orientation.TOP_BOTTOM,
                                        intArrayOf(
                                            startColor, endColor
                                        )
                                    )
                                )
                            }
                        }
                    }
                })
        })
    }

    private fun initView() {
        dataBinding.topBarLayout.setup {
            addLeftBackImageButton().setOnClickListener {
                finish()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(dataBinding.topBarLayout) { view, inset ->
            view.setPaddingTop(inset.systemWindowInsetTop)
            inset
        }

        dataBinding.bottomSheetContainer.background = DrawableCreator()
            .rectangle()
            .complete()
            .solidColor(Color.WHITE)
            .complete()
            .topLeftRadius(dp2px(10))
            .topRightRadius(dp2px(10))
            .complete()
            .build()

        dataBinding.tabSegmentLayout.setAdapter(TextFastTabSegmentAdapter(listOf("影评", "短评")))
        dataBinding.tabSegmentLayout.setupWithViewPager(dataBinding.viewPager)
    }

    override fun resId(): Int = R.layout.detail_movie_activity
}