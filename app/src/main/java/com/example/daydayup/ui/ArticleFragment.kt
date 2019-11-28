package com.example.daydayup.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.example.daydayup.R
import com.example.daydayup.respository.model.ArticleBean
import com.example.daydayup.respository.model.Banner
import com.example.daydayup.respository.model.Status
import com.example.daydayup.ui.adapter.ArticleAdapter
import com.example.daydayup.ui.base.OnItemClickListener
import com.example.daydayup.utils.GlideUtil
import com.example.daydayup.utils.webview.WebViewActivity
import com.example.daydayup.viewmodel.ArticleViewModel
import com.lxm.module_library.base.BaseFragment
import com.lxm.module_library.utils.RefreshHelper
import com.lxm.module_library.utils.ToastUtil
import com.lxm.module_library.xrecycleview.XRecyclerView
import com.zhouwei.mzbanner.holder.MZViewHolder
import kotlinx.android.synthetic.main.article_banner.*
import kotlinx.android.synthetic.main.article_fragment.*


class ArticleFragment : BaseFragment<ArticleViewModel>() {

    private lateinit var headerView: View
    private var bannerList: List<Banner>? = null
    private val mAdapter: ArticleAdapter by lazy {
        ArticleAdapter()
    }

    override fun getLayoutID(): Int {
        return R.layout.article_fragment
    }

    companion object {
        fun getInstance(): ArticleFragment {
            return ArticleFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showContentView()
        initView()
    }

     override fun initView() {
        swipeLayout.setOnRefreshListener {
            viewModel.mPage = 0
            loadData()
        }
        swipeLayout.isRefreshing = true
        RefreshHelper.init(recyclerView, false)
        headerView = layoutInflater.inflate(R.layout.article_banner, null)
        recyclerView.addHeaderView(headerView)
        recyclerView.adapter = mAdapter
        recyclerView.setLoadingListener(object : XRecyclerView.LoadingListener {

            override fun onLoadMore() {
                viewModel.mPage = viewModel.mPage + 1;
                getHomeList()
            }

            override fun onRefresh() {
            }
        })
        viewModel.loadStatus.observe(this, Observer {

            when (it?.status) {
                Status.ERROR -> showError()
                Status.SUCCESS -> showContentView()
            }
        })
        viewModel.pagedList.observe(this, Observer {
            swipeLayout.isRefreshing = false
            if (it == null) {
                return@Observer
            }
            if (viewModel.mPage == 0) {
                mAdapter.setData(it.data?.datas!!)
                return@Observer
            }
            mAdapter.addDataAll(it.data?.datas!!)
            if (it?.data?.datas?.size!! < it?.data?.size!!) {
                recyclerView.noMoreLoading()
            } else {
                recyclerView.refreshComplete()
            }
        })

        mAdapter.setOnItemClickListener(object : OnItemClickListener<ArticleBean> {
            override fun onClick(t: ArticleBean, position: Int) {
                activity?.let { WebViewActivity.loadUrl(it, t.link, t.title) }
            }
        })
    }

    private fun getBanners() {
        viewModel.getBanners().observe(this@ArticleFragment, Observer {
            bannerList = it?.data
            banner.setBannerPageClickListener { view, i ->
                activity?.let { it1 -> WebViewActivity.loadUrl(it1, it?.data?.get(i)?.url, null) }
            }
            banner.setPages(
                it?.data as List<Nothing>?
            ) {
                BannerViewHolder()
            }
            banner.start()

        })
    }

    class BannerViewHolder : MZViewHolder<Banner> {
        private var mImageView: ImageView? = null

        override fun createView(context: Context): View {
            val view = LayoutInflater.from(context).inflate(R.layout.item_banner, null)
            mImageView = view.findViewById(R.id.banner_image) as ImageView
            return view
        }

        override fun onBind(context: Context, position: Int, data: Banner?) {
            data?.let {
                GlideUtil.displayCircleCorner(mImageView!!, it.imagePath)
            }
        }
    }


    override fun onRetry() {
        loadData()
    }

    override fun loadData() {
        getHomeList()
        getBanners()
    }

    private fun getHomeList() {
        this.viewModel.getHomeList()
    }
}
