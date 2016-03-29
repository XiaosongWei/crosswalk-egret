package org.egret.egretruntimelauncher.webview;

import org.egret.egretruntimelauncher.config.GameConfiguration;
import org.egret.egretruntimelauncher.utils.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
//import android.webkit.JavascriptInterface;
import org.xwalk.core.JavascriptInterface;
/**
 * 渠道接入Runtime.请勿修改此类.
 * @author imathliu
 *
 */
public class GameEngineJavaScriptDelegate {

    private static String TAG = "GameEngine";
    private IGameEngine gameEngine;
    private Handler mainThreadHandler;
    private Context context;
    private GameConfiguration gameConf;

    private enum GAME_OPTION {
        gameId, gameUrl, spId, nest, coop, channelTag
    }

    public GameEngineJavaScriptDelegate(Context context, IGameEngine gameEngine) {
        mainThreadHandler = new Handler(Looper.getMainLooper());
        this.context = context;
        this.gameEngine = gameEngine;
        gameConf = GameConfiguration.getInstance(context);
    }

    /**
     * gameEngine初始化
     * 
     * @param engineName
     */
    @JavascriptInterface
    public void init(String engineName) {
        LogUtil.d(TAG, "init:" + engineName);
        Log.e(TAG, "JavaScriptDelegate Init");
    }

    /**
     * 设置横竖屏
     * 
     * @param orientation
     */
    @JavascriptInterface
    public void setScreenOrientation(String orientation) {
        LogUtil.d(TAG, "setScreenOrientation:" + orientation);
        gameConf.setScreenOrientation(orientation);
    }

    /**
     * gameEngine参数设置
     * 
     * @param key
     * @param value
     */
    @JavascriptInterface
    public void setOption(String key, String value) {
        LogUtil.d(TAG, "setOption:" + key + "->" + value);

        GAME_OPTION option = GAME_OPTION.valueOf(key);

        switch (option) {
            case gameId :
                gameConf.setGameId(value);
                break;
            case gameUrl :
                gameConf.setGameUrl(value);
                break;
            case spId :
                gameConf.setSpId(value);
                break;
            case nest :
                gameConf.setNestMode(value);
                break;
            case coop :
                gameConf.setCoopMode(value);
                break;
            case channelTag :
                gameConf.setChannelTag(value);
                break;
            default :
                break;
        }
    }

    @JavascriptInterface
    public void start(String engineName) {

        LogUtil.d(TAG, "start:" + engineName);

        mainThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                gameEngine.start();
            }
        });
    }
}
