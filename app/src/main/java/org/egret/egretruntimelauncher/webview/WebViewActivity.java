package org.egret.egretruntimelauncher.webview;

import org.egret.egretruntimelauncher.GamePlayActivity;
import org.egret.egretruntimelauncher.utils.LogUtil;
import org.xwalk.core.XWalkView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
/**
 * 功能描述:H5游戏启动入口
 * 是否修改:是
 * 修改点1:createGameEnter()
 * @author imathliu
 *
 */
public class WebViewActivity extends Activity implements IGameEngine {

    private static String TAG = "WebViewActivity";

    //private WebView webView;
    private XWalkView mXWalkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGameEnter();
        
        //设置日志显示级别。关闭日志输出。请设置LogUtil.NOTHING
        LogUtil.setLEVEL(LogUtil.VERBOSE);
    }
    
    /**
     * 功能描述:创建游戏入口
     * 修改内容:gameUrl 指向游戏入口html文件
     */
    private void createGameEnter() {
        // 设置游戏入口全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        webView = new WebView(this);
//        this.setContentView(webView);
//        // 游戏入口地址
//        String gameUrl = "file:///android_asset/GameEntry.html";
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl(gameUrl);
//        webView.addJavascriptInterface(new GameEngineJavaScriptDelegate(this,
//                this), "GameEngine");
//
//        webView.setWebViewClient(new WebViewClient() {
//
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
//
//        webView.setWebChromeClient(new WebChromeClient() {
//        });

        mXWalkView = new XWalkView(this);
        setContentView(mXWalkView);

        String gameUrl = "file:///android_asset/GameEntry.html";
        mXWalkView.addJavascriptInterface(new GameEngineJavaScriptDelegate(this, this), "GameEngine");
        mXWalkView.load(gameUrl, null);
        Log.e(TAG, "Using Crosswalk");
    }

    public void start() {

        Intent intent =
                new Intent(WebViewActivity.this, GamePlayActivity.class);
        startActivity(intent);
    }
}