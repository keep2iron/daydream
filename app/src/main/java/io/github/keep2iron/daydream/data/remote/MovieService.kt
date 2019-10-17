package io.github.keep2iron.daydream.data.remote

import io.github.keep2iron.daydream.data.entity.BaseResponse
import io.github.keep2iron.daydream.data.entity.Movie
import io.github.keep2iron.daydream.data.entity.Rank
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MovieService {

    @FormUrlEncoded
    @POST("/api/movie/recommendMovies")
    fun queryRecommendMovies(
        @Field("page") page: Int,
        @Field("size") size: Int
    ): Single<BaseResponse<List<Movie>>>

    @POST("/api/rank/recommendRanks")
    fun queryRecommendRanks(): Single<BaseResponse<List<Rank>>>

}