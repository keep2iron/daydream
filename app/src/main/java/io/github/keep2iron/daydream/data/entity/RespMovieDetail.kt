package io.github.keep2iron.daydream.data.entity

import com.google.gson.annotations.SerializedName

data class RespMovieDetail(
    @SerializedName("movie")
    val movie: Movie
)