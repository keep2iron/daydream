package io.github.keep2iron.daydream.data.entity


data class Movie(

        val id: Int = 0,

        /**
         * 电影名称
         */
        val movieName: String = "",

        /**
         * 电影路径
         */
        val moviePath: String = "",

        /**
         * 电影封面 相对路径
         */
        val movieCover: String = "",

        /**
         * 电影年份
         */
        val year: Int = 0,

        /**
         * 描述
         */
        val description: String?,

        /**
         * 封面
         */
        val movieCovers: List<MovieCover>?,

        /**
         * 电影Tag
         */
        val movieTag: List<MovieTag>?


)

/**
 * 电影封面
 */
data class MovieCover(

        /**
         * 封面Id
         */
        val id: Int,

        /**
         * 对应的Movie
         */
        val movie: Movie? = null,

        /**
         * 封面路径
         */
        val movieCoverPath: String
)

/**
 * 电影标签
 */
data class MovieTag(

        /**
         * 标签Id
         */
        val id: Int,

        /**
         * 标签名
         */
        val tagName: String
)