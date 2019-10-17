package io.github.keep2iron.daydream.data.entity

data class BaseResponse<T>(var code: Int = 200,
                           var message: String = "successful",
                           var value: T)