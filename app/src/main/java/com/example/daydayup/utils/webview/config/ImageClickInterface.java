package com.example.daydayup.utils.webview.config;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;



public class ImageClickInterface {
    private Context context;

    public ImageClickInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void imageClick(String imgUrl, String hasLink) {
        Toast.makeText(context, "----点击了图片", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(context, ViewBigImageActivity.class);
//        context.startActivity(intent);
        Log.e("----点击了图片 url: ", "" + imgUrl);
    }

    @JavascriptInterface
    public void textClick(String type, String item_pk) {
        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(item_pk)) {
            Log.e("----点击了文字", "");
        }
    }
}
