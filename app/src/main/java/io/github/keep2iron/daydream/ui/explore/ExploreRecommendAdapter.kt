package io.github.keep2iron.daydream.ui.explore

import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.daydream.R
import io.github.keep2iron.daydream.data.entity.Movie
import io.github.keep2iron.daydream.data.entity.MovieCover
import io.github.keep2iron.daydream.ui.detail.MovieDetailActivity
import io.github.keep2iron.fast4android.core.Fast4Android
import io.github.keep2iron.fast4android.core.alpha.FastAlphaFrameLayout
import io.github.keep2iron.fast4android.core.util.dp2px
import io.github.keep2iron.fast4android.core.util.startActivity
import io.github.keep2iron.looplayout.FastLoopLayout
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pineapple.MiddlewareView
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.RecyclerViewHolder
import java.util.*

class ExploreRecommendLabelAdapter : AbstractSubAdapter(ExploreType.RECOMMEND_LABEL, 1) {

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int =
        R.layout.explore_item_recommend_label

    override fun render(holder: RecyclerViewHolder, position: Int) {
    }

}

class ExploreRecommendViewPagerAdapter(
) : AbstractSubAdapter(ExploreType.RECOMMEND_MOVIE_BANNER_ITEM, 10) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val middlewareView = MiddlewareView(parent.context)
        middlewareView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return RecyclerViewHolder(middlewareView)
    }

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int = 0

    override fun render(holder: RecyclerViewHolder, position: Int) {
    }

    var movieCovers: List<MovieCover> = Collections.emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val cover = movieCovers[position]
        ImageLoaderManager.getInstance()
            .showImageView(holder.itemView as MiddlewareView, cover.movieCoverPath) {
                radius = dp2px(4).toFloat()
            }
    }


    override fun getItemViewType(position: Int): Int = ExploreType.RECOMMEND_MOVIE_BANNER_ITEM

    override fun getItemCount(): Int {
        return movieCovers.size
    }
//    override fun getCount(): Int = movieCovers.size
}

class ExploreRecommendMovieAdapter(
    val data: ObservableList<Any>,
    private val viewPool: RecyclerView.RecycledViewPool
) :
    AbstractSubAdapter(ExploreType.RECOMMEND_MOVIE, 10) {

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int =
        R.layout.explore_item_recommend_movie

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)
        val looperLayout = viewHolder.findViewById<FastLoopLayout>(R.id.looperLayout)
        looperLayout.setAdapter(ExploreRecommendViewPagerAdapter(), viewPool)
        return viewHolder
    }

    override fun render(holder: RecyclerViewHolder, position: Int) {
        val recommendMovie = data[position] as Movie

        val middleImageViewLayout =
            holder.findViewById<FastAlphaFrameLayout>(R.id.middleImageViewLayout)
        middleImageViewLayout.setRadius(dp2px(4))

        val viewPagerLayout =
            holder.findViewById<FastAlphaFrameLayout>(R.id.viewPagerLayout)
        viewPagerLayout.setRadius(dp2px(4))

        val middleImageView = holder.findViewById<MiddlewareView>(R.id.middleImageView)
        ImageLoaderManager.getInstance().showImageView(middleImageView, recommendMovie.movieCover)
        holder.setText(R.id.tvMovieTitle, recommendMovie.movieName)

        val looperLayout = holder.findViewById<FastLoopLayout>(R.id.looperLayout)
        (looperLayout.getRecyclerViewAdapter() as ExploreRecommendViewPagerAdapter).movieCovers =
            recommendMovie.movieCovers ?: Collections.emptyList()

        holder.setText(R.id.tvMovieDescription, recommendMovie.description ?: "")
    }

}

class ExploreRecommendMovieCollectionAdapter(recommendMovies: AsyncDiffObservableList<Any>) :
    AbstractSubAdapter(ExploreType.RECOMMEND_MOVIE_COLLECTION, 10) {

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int {
        return R.layout.explore_item_recommend_movie_collection
    }

    override fun render(holder: RecyclerViewHolder, position: Int) {
    }
}

