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

){
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Movie

                if (id != other.id) return false
                if (movieName != other.movieName) return false
                if (moviePath != other.moviePath) return false
                if (movieCover != other.movieCover) return false
                if (year != other.year) return false
                if (description != other.description) return false
                if (movieCovers != other.movieCovers) return false
                if (movieTag != other.movieTag) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id
                result = 31 * result + movieName.hashCode()
                result = 31 * result + moviePath.hashCode()
                result = 31 * result + movieCover.hashCode()
                result = 31 * result + year
                result = 31 * result + (description?.hashCode() ?: 0)
                result = 31 * result + (movieCovers?.hashCode() ?: 0)
                result = 31 * result + (movieTag?.hashCode() ?: 0)
                return result
        }
}

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
){
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as MovieCover

                if (id != other.id) return false
                if (movie != other.movie) return false
                if (movieCoverPath != other.movieCoverPath) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id
                result = 31 * result + (movie?.hashCode() ?: 0)
                result = 31 * result + movieCoverPath.hashCode()
                return result
        }
}

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
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as MovieTag

                if (id != other.id) return false
                if (tagName != other.tagName) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id
                result = 31 * result + tagName.hashCode()
                return result
        }
}