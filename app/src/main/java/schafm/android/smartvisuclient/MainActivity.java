package schafm.android.smartvisuclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String WEBAPP_URL = "http://192.168.1.21/smartvisu/";

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private View mLoadingView;
    private boolean mLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingView = findViewById(R.id.loading);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar.setIndeterminate(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mLoadingView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                    mLoaded = true;
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                Log.d(TAG, "javascript alert: " + message);
                return super.onJsAlert(view, url, message, result);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setIndeterminate(false);
                mProgressBar.setMax(100);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                showErrorDialog();
            }
        });

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NORMAL);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(false);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.loadUrl(WEBAPP_URL);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        })
                .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })
                .setTitle(R.string.app_name)
                .setMessage(R.string.error_loading).create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mLoaded) {
            if (mWebView.getUrl().toString().equals(WEBAPP_URL))
                finish();

            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}