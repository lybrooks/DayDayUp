package com.example.daydayup.utils.webview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.daydayup.R
import com.example.daydayup.utils.webview.config.*
import com.lxm.module_library.statusbar.StatusBarUtil
import com.lxm.module_library.utils.BaseTools
import com.lxm.module_library.utils.CheckNetwork
import com.lxm.module_library.utils.ToastUtil

class WebViewActivity : AppCompatActivity(), IWebPageView {
    // 进度条
    private var mProgressBar: ProgressBar? = null
    private var webView: WebView? = null
    // 全屏时视频加载view
    var videoFullView: FrameLayout? = null
        private set
    private var mTitleToolBar: Toolbar? = null
    // 加载视频相关
    private var mWebChromeClient: MyWebChromeClient? = null
    // title
    private var mTitle: String? = null
    // 网页链接
    private var mUrl: String? = null
    // 可滚动的title 使用简单 没有渐变效果，文字两旁有阴影
    private var tvGunTitle: TextView? = null
    private var isTitleFix = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        intentData
        initTitle()
        initWebView()
        webView!!.loadUrl(mUrl)
        getDataFromBrowser(intent)
    }

    private val intentData: Unit
        private get() {
            if (intent != null) {
                mTitle = intent.getStringExtra("mTitle")
                mUrl = intent.getStringExtra("mUrl")
                isTitleFix = intent.getBooleanExtra("isTitleFix", false)
            }
        }

    private fun initTitle() {
        StatusBarUtil.setColor(this, resources.getColor(R.color.colorTheme), 0)
        mProgressBar = findViewById(R.id.pb_progress)
        webView = findViewById(R.id.webview_detail)
        videoFullView = findViewById(R.id.video_fullView)
        mTitleToolBar = findViewById(R.id.title_tool_bar)
        tvGunTitle = findViewById(R.id.tv_gun_title)
        initToolBar()
    }

    private fun initToolBar() {
        setSupportActionBar(mTitleToolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        mTitleToolBar!!.overflowIcon = ContextCompat.getDrawable(this, R.drawable.actionbar_more)
        tvGunTitle!!.postDelayed({ tvGunTitle!!.isSelected = true }, 1900)
        tvGunTitle!!.text = mTitle
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun setTitle(mTitle: String?) {
        if (!isTitleFix) {
            tvGunTitle!!.text = mTitle
            this.mTitle = mTitle
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->  // 返回键
                handleFinish()
            R.id.actionbar_share -> {
            }
            R.id.actionbar_cope -> {
                //                 复制链接
                BaseTools.copy(webView!!.url)
                ToastUtil.showToast("复制成功")
            }
            R.id.actionbar_open ->  // 打开链接
                BaseTools.openLink(this@WebViewActivity, webView!!.url)
            R.id.actionbar_webview_refresh ->  // 刷新页面
                if (webView != null) {
                    webView!!.reload()
                }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        mProgressBar!!.visibility = View.VISIBLE
        val ws = webView!!.settings
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.loadWithOverviewMode = false
        // 保存表单数据
        ws.saveFormData = true
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true)
        ws.builtInZoomControls = true
        ws.displayZoomControls = false
        // 启动应用缓存
        ws.setAppCacheEnabled(true)
        // 设置缓存模式
        ws.cacheMode = WebSettings.LOAD_DEFAULT
        // setDefaultZoom  api19被弃用
// 设置此属性，可任意比例缩放。
        ws.useWideViewPort = true
        // 不缩放
        webView!!.setInitialScale(100)
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.javaScriptEnabled = true
        //  页面加载好以后，再放开图片
        ws.blockNetworkImage = false
        // 使用localStorage则必须打开
        ws.domStorageEnabled = true
        // 排版适应屏幕
        ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        // WebView是否新窗口打开(加了后可能打不开网页)
//        ws.setSupportMultipleWindows(true);
// webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用) */
        ws.textZoom = 100
        mWebChromeClient = MyWebChromeClient(this)
        webView!!.webChromeClient = mWebChromeClient
        // 与js交互
        webView!!.addJavascriptInterface(ImageClickInterface(this), "injectedObject")
        webView!!.webViewClient = MyWebViewClient(this)
        webView!!.setOnLongClickListener { handleLongImage() }
    }

    override fun hindProgressBar() {
        mProgressBar!!.visibility = View.GONE
    }

    override fun showWebView() {
        webView!!.visibility = View.VISIBLE
    }

    override fun hindWebView() {
        webView!!.visibility = View.INVISIBLE
    }

    override fun fullViewAddView(view: View?) {
        val decor = window.decorView as FrameLayout
        videoFullView = FullscreenHolder(this@WebViewActivity)
        (videoFullView as FullscreenHolder).addView(view)
        decor.addView(videoFullView)
    }

    override fun showVideoFullView() {
        videoFullView!!.visibility = View.VISIBLE
    }

    override fun hindVideoFullView() {
        videoFullView!!.visibility = View.GONE
    }

    override fun startProgress(newProgress: Int) {
        mProgressBar!!.visibility = View.VISIBLE
        mProgressBar!!.progress = newProgress
        if (newProgress == 100) {
            mProgressBar!!.visibility = View.GONE
        }
    }

    override fun addImageClickListener() { //        loadImageClickJS();
//        loadTextClickJS();
    }

    private fun loadImageClickJS() { // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView!!.loadUrl(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"has_link\"));}" +
                    "}" +
                    "})()"
        )
    }

    private fun loadTextClickJS() { // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        webView!!.loadUrl(
            "javascript:(function(){" +
                    "var objs =document.getElementsByTagName(\"a\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){" +
                    "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                    "}" +
                    "})()"
        )
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    fun hideCustomView() {
        mWebChromeClient!!.onHideCustomView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 上传图片之后的回调
     */
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE) {
            mWebChromeClient!!.mUploadMessage(intent, resultCode)
        } else if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient!!.mUploadMessageForAndroid5(intent, resultCode)
        }
    }

    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     * 如果这个实例已经存在，intent就会通过onNewIntent传递到这个Activity。
     * 否则新的Activity实例被创建。
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        getDataFromBrowser(intent)
    }

    /**
     * 作为三方浏览器打开
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    private fun getDataFromBrowser(intent: Intent) {
        val data = intent.data
        if (data != null) {
            try {
                val scheme = data.scheme
                val host = data.host
                val path = data.path
                //                String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "path: " + path;
//                Log.e("data", text);
                val url = "$scheme://$host$path"
                webView!!.loadUrl(url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 长按图片事件处理
     */
    private fun handleLongImage(): Boolean {
        val hitTestResult = webView!!.hitTestResult
        // 如果是图片类型或者是带有图片链接的类型
        if (hitTestResult.type == HitTestResult.IMAGE_TYPE ||
            hitTestResult.type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE
        ) { // 弹出保存图片的对话框
            AlertDialog.Builder(this@WebViewActivity)
                .setItems(
                    arrayOf("查看大图", "保存图片到相册"),
                    DialogInterface.OnClickListener { dialog, which ->
                        val picUrl = hitTestResult.extra
                        //获取图片
                        //                            Log.e("picUrl", picUrl);
                        //                            switch (which) {
                        //                                case 0:
                        //                                    ViewBigImageActivity.start(WebViewActivity.this, picUrl, picUrl);
                        //                                    break;
                        //                                case 1:
                        //                                    if (!PermissionHandler.isHandlePermission(WebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //                                        return;
                        //                                    }
                        //                                    RxSaveImage.saveImageToGallery(WebViewActivity.this, picUrl, picUrl);
                        //                                    break;
                        //                                default:
                        //                                    break;
                        //                            }
                    })
                .show()
            return true
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //全屏播放退出全屏
            if (mWebChromeClient!!.inCustomView()) {
                hideCustomView()
                return true
                //返回网页上一页
            } else if (webView!!.canGoBack()) {
                webView!!.goBack()
                return true
                //退出网页
            } else {
                handleFinish()
            }
        }
        return false
    }

    /**
     * 直接通过三方浏览器打开时，回退到首页
     */
    fun handleFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        webView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView!!.onResume()
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView!!.resumeTimers()
        // 设置为横屏
        if (requestedOrientation !== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDestroy() {
        if (videoFullView != null) {
            videoFullView!!.clearAnimation()
            videoFullView!!.removeAllViews()
        }
        if (webView != null) {
            webView!!.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView!!.clearHistory()
            val parent = webView!!.parent as ViewGroup
            parent?.removeView(webView)
            webView!!.removeAllViews()
            webView!!.stopLoading()
            webView!!.webChromeClient = null
            webView!!.webViewClient = null
            webView!!.destroy()
            webView = null
            mProgressBar!!.clearAnimation()
            tvGunTitle!!.clearAnimation()
            tvGunTitle!!.clearFocus()
        }
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.fontScale != 1f) {
            resources
        }
    }

    /**
     * 禁止改变字体大小
     */
    override fun getResources(): Resources {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    companion object {
        /**
         * 打开网页:
         *
         * @param mContext     上下文
         * @param mUrl         要加载的网页url
         * @param mTitle       title
         * @param isTitleFixed title是否固定
         */
        /**
         * 打开网页:
         *
         * @param mContext 上下文
         * @param mUrl     要加载的网页url
         * @param mTitle   title
         */
        @JvmOverloads
        fun loadUrl(
            mContext: Context,
            mUrl: String?,
            mTitle: String?,
            isTitleFixed: Boolean = false
        ) {
            if (CheckNetwork.isNetworkConnected(mContext)) {
                val intent = Intent(mContext, WebViewActivity::class.java)
                intent.putExtra("mUrl", mUrl)
                intent.putExtra("isTitleFix", isTitleFixed)
                intent.putExtra("mTitle", mTitle ?: "")
                mContext.startActivity(intent)
            } else {
                ToastUtil.showToastLong("当前网络不可用，请检查你的网络设置")
            }
        }
    }
}
