package com.example.daydayup

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.daydayup.ui.ArticleFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

const val PHOTO_URL =
    "https://cdn.duitang.com/uploads/blog/201404/22/20140422142715_8GtUk.thumb.600_0.jpeg"

class MainActivity : AppCompatActivity() {


    private val mTitle by lazy {
        mutableListOf("主页", "知识体系", "导航", "项目", "福利")
    }
    private val mFragment: MutableList<Fragment> by lazy {
        mutableListOf<Fragment>()

    }
    private val mAdapter: MyPagerAdapter by lazy {
        MyPagerAdapter(supportFragmentManager)
    }


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        iv_photo.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }
        initFragment()
    }

    private fun initFragment() {
        mFragment.run {
            add(ArticleFragment.getInstance())
            add(ArticleFragment.getInstance())
            add(ArticleFragment.getInstance())
            add(ArticleFragment.getInstance())
            add(ArticleFragment.getInstance())
        }

        mAdapter.setFragments(mFragment)
        mAdapter.setTitles(mTitle)
        view_pager.adapter = mAdapter
        view_pager.offscreenPageLimit = 5
        tablayout.setViewPager(view_pager)
    }
}

class MyPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    var fragmentList: MutableList<Fragment> = mutableListOf()
    var titleList: MutableList<String> = mutableListOf()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    fun setFragments(fragment: MutableList<Fragment>) {
        fragmentList = fragment
    }

    fun setTitles(mTitles: MutableList<String>) {
        titleList = mTitles
    }
}
