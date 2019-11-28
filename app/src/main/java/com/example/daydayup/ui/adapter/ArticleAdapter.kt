package com.example.daydayup.ui.adapter

import android.widget.TextView
import com.example.daydayup.R
import com.example.daydayup.respository.model.ArticleBean
import com.example.daydayup.ui.base.BaseRecyclerAdapter
import com.example.daydayup.ui.base.BaseRecyclerViewHolder


class ArticleAdapter : BaseRecyclerAdapter<ArticleBean>() {
    override fun getItemLayout(): Int {
        return R.layout.itemview
    }

    override fun onBindViewHoder(holder: BaseRecyclerViewHolder, position: Int) {
        var articleBean: ArticleBean = mutableList?.get(position)
        with(holder) {
            setValue(R.id.tv_title, articleBean.title)
            setValue(R.id.tv_time, articleBean.niceDate)
            setValue(R.id.tv_author, articleBean.author)
            setValue(R.id.iv_image, articleBean.envelopePic)
        }
        var tagTextView = holder.getView(R.id.tv_tag_name) as TextView
        tagTextView.text = articleBean.chapterName


    }
}