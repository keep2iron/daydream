package io.github.keep2iron.daydream.data.entity

/**
 * 推荐电影集合item
 */
data class RecommendMovieCollection(
    /**
     * 推荐电影
     */
    val recommendMovies: List<Movie>,

    /**
     * 推荐封面
     */
    val recommendCover: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecommendMovieCollection

        if (recommendMovies != other.recommendMovies) return false
        if (recommendCover != other.recommendCover) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recommendMovies.hashCode()
        result = 31 * result + recommendCover.hashCode()
        return result
    }
}

data class RecommendItem(
    val recommendMovie: Movie? = null,
    val recommendMovieCollection: RecommendMovieCollection? = null,
    /**
     * 推荐类型
     * @see io.github.keep2iron.daydream.entity.dto.RecommendItem.RECOMMEND_MOVIE
     * @see io.github.keep2iron.daydream.entity.dto.RecommendItem.RECOMMEND_MOVIE_COLLECTION
     */
    val recommendItemType: Int
) {
    companion object {
        /**
         * 推荐电影
         */
        const val RECOMMEND_MOVIE = 1


        /**
         * 推荐电影集合
         */
        const val RECOMMEND_MOVIE_COLLECTION = 2
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecommendItem

        if (recommendMovie != other.recommendMovie) return false
        if (recommendMovieCollection != other.recommendMovieCollection) return false
        if (recommendItemType != other.recommendItemType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recommendMovie?.hashCode() ?: 0
        result = 31 * result + (recommendMovieCollection?.hashCode() ?: 0)
        result = 31 * result + recommendItemType
        return result
    }


}