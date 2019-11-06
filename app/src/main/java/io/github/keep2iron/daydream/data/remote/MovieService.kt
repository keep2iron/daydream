package io.github.keep2iron.daydream.data.remote

import io.github.keep2iron.daydream.data.entity.*
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MovieService {

    @FormUrlEncoded
    @POST("/api/movie/hotMovies")
    fun queryHotMovies(
        @Field("page") page: Int,
        @Field("size") size: Int
    ): Single<BaseResponse<List<Movie>>>

    @POST("/api/rank/recommendRanks")
    fun queryHotRanks(): Single<BaseResponse<List<Rank>>>

    @FormUrlEncoded
    @POST("/api/movie/recommendMovies")
    fun queryRecommendMovies(
        @Field("page") page: Int,
        @Field("size") size: Int
    ): Single<BaseResponse<List<RecommendItem>>>

    @FormUrlEncoded
    @POST("/api/movie/detail")
    fun queryMovieDetail(
        @Field("id") id: Int
    ): Single<BaseResponse<RespMovieDetail>>

}