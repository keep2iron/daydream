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
)

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
}