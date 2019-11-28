package com.example.daydayup.ui.base


interface OnItemClickListener<T> {


    fun onClick(t: T, position: Int)
}