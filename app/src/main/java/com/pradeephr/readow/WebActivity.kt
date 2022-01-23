package com.pradeephr.readow

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.pradeephr.readow.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding
    private lateinit var webView: WebView
    private lateinit var url:String
    private lateinit var imageViewCancel: ImageView
    private lateinit var imageViewRefresh: ImageView
    private lateinit var imageViewDarkMode: ImageView
    private lateinit var imageViewShare: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var dialog:Dialog

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        url= intent.getStringExtra("loadUrl").toString()
        Log.d("open",url)

        initializer()

        runCode()

        imageViewCancel.setOnClickListener { finish()}
        imageViewRefresh.setOnClickListener { runCode() }
        imageViewDarkMode.setOnClickListener {
            if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_ON);
            }else{
                //do nothing
            }
        }

        imageViewShare.setOnClickListener {
            val shareIntent=Intent(Intent.ACTION_SEND)
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT,url)
            startActivity(Intent.createChooser(shareIntent,"Share link via"))
        }


    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun runCode(){
        webView=binding.webView
        webView.webViewClient= MyWebViewClient()
        webView.webChromeClient=MyWebChromeClient()
        webView.settings.javaScriptEnabled=true
        webView.settings.setSupportZoom(true)
        //settings
        webView.settings.useWideViewPort=true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls=true
        webView.settings.displayZoomControls=false
        webView.settings.userAgentString="Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
        webView.scrollBarStyle=WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView.settings.allowContentAccess=true
        webView.settings.domStorageEnabled=true
        webView.isScrollbarFadingEnabled=false
        webView.isNestedScrollingEnabled=true
        webView.loadUrl(url)
        progressBar.isVisible=true
    }


    private inner class MyWebViewClient: WebViewClient() {

        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.isVisible=false
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)

        }



    }

    private inner class MyWebChromeClient: WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            progressBar.progress=newProgress
            super.onProgressChanged(view, newProgress)
        }
    }



    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            super.onBackPressed()
        }
    }

    private fun initializer(){
        imageViewCancel=binding.imageViewCancel
        imageViewRefresh=binding.imageViewRefresh
        imageViewDarkMode=binding.imageViewDarkMode
        imageViewShare=binding.imageViewShare
        progressBar=binding.loadingBar
        dialog= Dialog(this)
    }

}