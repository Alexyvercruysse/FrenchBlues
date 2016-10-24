package com.example.avercruysse.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    /* URL saved to be loaded after fb login */
    private static final String target_url="http://www.frenchblues.fr/";
    private static final String target_url_prefix="www.frenchblues.fr";
    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    private FrameLayout mContainer;
    private long mLastBackPressTime = 0;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // final View controlsView =
        // findViewById(R.id.fullscreen_content_controls);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        mWebview = (WebView) findViewById(R.id.webview);
        //mWebviewPop = (WebView) findViewById(R.id.webviewPop);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.setWebChromeClient(new UriChromeClient());
        mWebview.loadUrl(target_url);

        mContext=this.getApplicationContext();

    }


    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            Log.d("shouldOverrideUrlLoading", url+" host: "+host);
            if (host.equals(target_url_prefix) || host.equals(target_url) || host.equals("http://disq.us/") || host.equals("http://www.disq.us/") || host.equals("www.disq.us") || host.equals("disq.us"))
            {
                // This is my web site, so do not override; let my WebView load
                // the page
                if(mWebviewPop!=null)
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop=null;
                }
                return false;
            }

            if(host.equals("m.facebook.com") || host.equals("www.facebook.com") || host.equals("twitter.com") || host.equals("plus.google.com"))
            {
                return false;

            }
            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }
    }
    public static Intent getOpenFacebookIntent(Context context)
    {
        try
        {
            context.getPackageManager().getPackageInfo("com.facebook.katana",0);
            return new Intent(Intent.ACTION_VIEW,Uri.parse("fb://page/<id_here>"));
        }catch (Exception e)
        {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/<user_name_here>"));
        }
    }
    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebview.canGoBack()) {
                        mWebview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}