package io.github.keep2iron.daydream.ui.explore

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.keep2iron.daydream.R
import io.github.keep2iron.daydream.data.entity.Rank
import io.github.keep2iron.fast4android.core.alpha.FastAlphaFrameLayout
import io.github.keep2iron.fast4android.core.util.dp2px
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.collections.AsyncDiffObservableList
import io.github.keep2iron.pomelo.divider.LinearLayoutColorDivider
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubAdapter
import io.github.keep2iron.pomelo.pager.adapter.AbstractSubListAdapter
import io.github.keep2iron.pomelo.pager.adapter.RecyclerViewHolder

class ExploreRankChildAdapter(data: ObservableList<Rank>) :
    AbstractSubListAdapter<Rank>(data, ExploreType.RANKING_CHILD, 5) {
    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int {
        return R.layout.explore_item_rank_child
    }

    override fun render(holder: RecyclerViewHolder, item: Rank, position: Int) {
        (holder.itemView as FastAlphaFrameLayout).setRadius(dp2px(4))
        ImageLoaderManager.getInstance()
            .showImageView(holder.findViewById(R.id.middlewareView), item.rankCover)
        holder.setText(R.id.tvRankTitle, item.rankTitle)
        val rankListContainer = holder.findViewById<LinearLayout>(R.id.rankListContainer)
        rankListContainer.setBackgroundColor(item.rankCoverMainColor)
        item.rankList.forEachIndexed { index, rankItem ->
            val textView = TextView(holder.itemView.context)
            textView.text = "${index + 1}.${rankItem.movieName} ${rankItem.movieEvaluate}"
            textView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            textView.textSize = 12f
            textView.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.fast_config_color_white
                )
            )
            textView.setPadding(dp2px(24), dp2px(6), 0, dp2px(6))
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rankListContainer.addView(textView)
        }
    }
}

/**
 * 排行榜电影
 */
class ExploreRankAdapter(
    private val ranks: AsyncDiffObservableList<Rank>,
    private val viewPool: RecyclerView.RecycledViewPool
) : AbstractSubAdapter(ExploreType.RANKING, 1) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)
        val recyclerView = viewHolder.itemView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.setRecycledViewPool(viewPool)
        recyclerView.layoutManager =
            LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ExploreRankChildAdapter(ranks)
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

    override fun onInflateLayoutId(parent: ViewGroup, viewType: Int): Int {
        return R.layout.explore_item_rank
    }

    override fun render(holder: RecyclerViewHolder, position: Int) {
    }
}