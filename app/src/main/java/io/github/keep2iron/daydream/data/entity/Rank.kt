package io.github.keep2iron.daydream.data.entity

import android.graphics.Color

data class Rank(
    /**
     * 榜单title
     */
    val rankTitle: String,
    /**
     * 榜单封面
     */
    val rankCover: String,
    /**
     * 排行榜
     */
    val rankList: List<RankItem>,

    var rankCoverMainColor: Int = Color.BLACK
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rank

        if (rankTitle != other.rankTitle) return false
        if (rankCover != other.rankCover) return false
        if (rankList != other.rankList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rankTitle.hashCode()
        result = 31 * result + rankCover.hashCode()
        result = 31 * result + rankList.hashCode()
        return result
    }
}

data class RankItem(
    /**
     * 影片名字
     */
    val movieName: String,
    /**
     * 影片评分
     */
    val movieEvaluate: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RankItem

        if (movieName != other.movieName) return false
        if (movieEvaluate != other.movieEvaluate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movieName.hashCode()
        result = 31 * result + movieEvaluate.hashCode()
        return result
    }
}