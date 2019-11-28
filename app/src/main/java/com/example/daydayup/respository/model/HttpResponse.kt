package com.example.daydayup.respository.model


data class HttpResponse<T>(
    var data: T? = null,
    var errorCode: Int = 0,
    var errorMsg: String? = null
)