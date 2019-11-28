package com.example.daydayup.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.daydayup.respository.model.*
import com.example.daydayup.respository.remote.httpClient.RetrofitClient
import com.lxm.module_library.base.BaseViewModel
import com.lxm.module_library.helper.RxHelper

class ArticleViewModel : BaseViewModel() {


    var mPage = 0
    var banner = MutableLiveData<HttpResponse<List<Banner>>>()
    val pagedList = MutableLiveData<HttpResponse<ArticleResponseBody<ArticleBean>>>()
    val loadStatus by lazy {
        MutableLiveData<Resource<String>>()
    }


    fun getHomeList(): Listing<HttpResponse<ArticleResponseBody<ArticleBean>>>? {
        loadStatus.postValue(Resource.loading())
        val subscribe =
            RetrofitClient.getInstance(RetrofitClient.WAN_BASE_URL).getArticleList(mPage)
                .compose(RxHelper.rxSchedulerHelper())
                .subscribe({
                    if (it.data != null) {
                        loadStatus.postValue(Resource.success())
                        pagedList.value = it
                    } else {
                        loadStatus.postValue(Resource.error())
                    }

                }, {
                    if (mPage > 0) {
                        mPage--
                    }
                    loadStatus.postValue(Resource.error())
                })
        addDisposable(subscribe)
        return Listing(pagedList, loadStatus)
    }


    fun getBanners(): MutableLiveData<HttpResponse<List<Banner>>> {
        val subscribe = RetrofitClient.getInstance(RetrofitClient.WAN_BASE_URL).getHomeBanner()
            .compose(RxHelper.rxSchedulerHelper())
            .subscribe({
                banner.postValue(it)
            }, {
                banner.postValue(null)
            })

        addDisposable(subscribe)
        return banner
    }
}
