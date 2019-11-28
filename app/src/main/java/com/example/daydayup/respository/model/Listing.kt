package com.example.daydayup.respository.model

import androidx.lifecycle.MutableLiveData

data class Listing<T>(
    val pagedList: MutableLiveData<T>,
    val loadStatus: MutableLiveData<com.example.daydayup.respository.model.Resource<String>>
)