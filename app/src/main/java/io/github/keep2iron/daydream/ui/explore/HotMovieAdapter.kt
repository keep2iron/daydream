package io.github.keep2iron.daydream.ui.explore

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.daydream.R
import io.github.keep2iron.daydream.data.entity.Movie
import io.github.keep2iron.fast4android.core.Toaster
import io.github.keep2iron.fast4android.core.alpha.FastAlphaFrameLayout
import io.github.keep2iron.fast4android.core.util.dp2px
import io.github.keep2iron.peach.DrawableCreator
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.divider.LinearLayoutColorDivider
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubListAdapter
import io.github.keep2iron.pomelo.pager.adapter.RecyclerViewHolder

class HotMovieChildAdapter(movies: AsyncDiffObservableList<Movie>) :
    AbstractSubListAdapter<Movie>(movies, ExploreType.HOT_MOVIE_CHILD, 10) {

    init {
        setOnItemClickListener { position, _, itemView ->
            Toaster.s("$position ${recyclerView.getChildAdapterPosition(itemView)}")
        }
    }

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int =
        R.layout.explore_item_hot_movie_child

    override fun render(holder: RecyclerViewHolder, item: Movie, position: Int) {
        ImageLoaderManager.getInstance()
            .showImageView(holder.findViewById(R.id.middleImageView), item.movieCover)
        (holder.itemView as FastAlphaFrameLayout).setRadius(
            dp2px(4)
        )
    }

}

class HotMovieAdapter(
    private val movies: AsyncDiffObservableList<Movie>,
    private val viewPool: RecyclerView.RecycledViewPool
) : AbstractSubAdapter(ExploreType.HOT_MOVIE, 1) {

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int =
        R.layout.explore_item_hot_movie

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)
        val recyclerView = viewHolder.itemView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setRecycledViewPool(viewPool)
        recyclerView.layoutManager =
            LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = HotMovieChildAdapter(movies)
        recyclerView.addItemDecoration(
            LinearLayoutColorDivider(
                parent.context,
                -1,
                dp2px(12),
                LinearLayoutManager.HORIZONTAL
            )
        )

        return viewHolder
    }

    override fun render(holder: RecyclerViewHolder, position: Int) {
    }
}