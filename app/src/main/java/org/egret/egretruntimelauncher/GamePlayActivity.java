package org.egret.egretruntimelauncher;

import java.util.HashMap;

import org.egret.egretruntimelauncher.config.GameConfiguration;
import org.egret.egretruntimelauncher.config.RuntimeConfiguration;
import org.egret.egretruntimelauncher.nest.NestAppImpl;
import org.egret.egretruntimelauncher.nest.NestLoginImpl;
import org.egret.egretruntimelauncher.nest.NestPayImpl;
import org.egret.egretruntimelauncher.nest.NestShareImpl;
import org.egret.egretruntimelauncher.nest.NestSocialImpl;
import org.egret.egretruntimelauncher.utils.EgretReflectUtils;
import org.egret.egretruntimelauncher.utils.LogUtil;
import org.egret.java.egretruntimelauncher.EgretRuntimeLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 渠道接入Runtime.请勿修改此类.
 * 
 * Egret Runtime 下载初始化启动流程:
 * 1.设置GamePlayActivity屏幕属性(屏幕方向［横屏OR竖屏］、屏幕大小[全屏OR窗口])
 * 2.读取RuntimeConfiguration.class Runtime下载参数
 * 3.下载-->校验——>解压-->动态加载Runtime
 * 4.读取GameConfiguration.class Game启动参数 设置进Runtime
 * 5.初始化Runtime
 * 6.注册SDK接口
 * 7.启动游戏
 * 
 * Egret Runtime 生命周期管理:
 * 在GamePlayActivity onCreate() 中 进行Egret Runtime 下载初始化启动流程
 * 在GamePlayActivity onResume() 中 调用EgretReflectUtils.onResume(gameEngine)方法 恢复gameEngine
 * 在GamePlayActivity onPause() 中 调用EgretReflectUtils.onPause(gameEngine)方法 暂停gameEngine
 * 在GamePlayAcitivity finish()前 调用EgretReflectUtils.onStop(gameEngine)方法 关闭gameEngine
 * 在GamePlayActivity onDestroy() 中调用System.gc()方法 进行垃圾回收
 * @author imathliu
 */
public class GamePlayActivity extends Activity {

    private static final String TAG = "GamePlayActivity";

    /**
     * Egret Runtime 的实例
     */
    private Class<?> gameEngineClass;
    private Object gameEngine;

    /**
     * Egret Runtime 的视图，内部主要包括一个 GLSurfaceView 和一个可以定制的 Loading 界面
     */
    private View frameLayout = null;

    /**
     * Egret Runtime 自身的加载器，负责维护 Runtime 自身的更新机制
     */
    private EgretRuntimeLauncher launcher;

    private Context mContext;

    private GameConfiguration gameConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        mContext = this;
        gameConf = GameConfiguration.getInstance(mContext);

        setGameScreen();
        createRuntimeLauncher();
    }

    /**
     * 设置游戏场景
     */
    private void setGameScreen() {
        // 设置游戏全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String screenOrientation = gameConf.getScreenOrientation();
        // 设置屏幕方向
        if (screenOrientation.equals("landscape")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 创建游戏引擎下载器
     */
    private void createRuntimeLauncher() {

        final RuntimeLoadingView runtimeLoadingView = new RuntimeLoadingView(
                this);
        this.setContentView(runtimeLoadingView);

        // 获取下载器配置参数
        RuntimeConfiguration rtConf = RuntimeConfiguration.getInstance();
        int dev = rtConf.getDev();
        String spId = rtConf.getSpId();
        String appKey = rtConf.getAppKey();
        String egretRoot = gameConf.getRuntimeRoot();

        // 创建下载器对象
        launcher = new EgretRuntimeLauncher(this, egretRoot, spId, appKey, dev);

        // 运行下载器
        launcher.run(new EgretRuntimeLauncher.EgretRuntimeDownloadListener() {

            /**
             * Egret Runtime 初始化完毕（下载成功）
             */
            public void onSuccess(Class<?> aGameEngineClass) {
                startGame(aGameEngineClass);
            }

            public void onProgress(String fileName, int currentByte,
                    int totalByte) {

                runtimeLoadingView.updateProgress(fileName, currentByte,
                        totalByte);
            }

            public void onError(String message) {

                LogUtil.e(TAG, message);
            }

            @Override
            public void onProgressTotal(int currentByte, int totalByte) {

                runtimeLoadingView.updateProgressSum(currentByte, totalByte);
            }
        });
    }
    /**
     * 启动游戏
     * @param aGameEngineClass
     */
    private void startGame(Class<?> aGameEngineClass) {
        if (aGameEngineClass == null) {
            return;
        }
        gameEngineClass = aGameEngineClass;

        try {
            gameEngine = gameEngineClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        onCreateGameEngine();
    }

    /**
     * 创建游戏引擎
     */
    public void onCreateGameEngine() {
        callSetGameOptions();
        callSetLoadingView();
        callInitRuntime();
        callSetRuntimeView();
        callSetSdk();
    }

    /**
     * 注册SDK相关接口 包含用户登录、应用内支付、发送桌面、分享和社交
     */
    private void callSetSdk() {
        EgretReflectUtils.registerPlugin(gameEngine, "user", new NestLoginImpl(
                this, gameEngine));
        EgretReflectUtils.registerPlugin(gameEngine, "iap", new NestPayImpl());
        EgretReflectUtils.registerPlugin(gameEngine, "app", new NestAppImpl());
        EgretReflectUtils.registerPlugin(gameEngine, "share",
                new NestShareImpl());
        EgretReflectUtils.registerPlugin(gameEngine, "social",
                new NestSocialImpl());
    }

    /**
     * 设置游戏引擎视图
     */
    private void callSetRuntimeView() {
        frameLayout = EgretReflectUtils.getRuntimeView(gameEngine);
        setContentView(frameLayout);
    }

    /**
     * 初始化游戏引擎
     */
    private void callInitRuntime() {
        EgretReflectUtils.initRuntime(gameEngine, this);
    }

    /**
     * 设置游戏加载进度条
     */
    private void callSetLoadingView() {
        EgretReflectUtils.setLoadingView(gameEngine, new GameLoadingView(this));
    }

    /**
     * 设置游戏引擎启动参数
     */
    private void callSetGameOptions() {
        HashMap<String, Object> options = gameConf.getOptions();
        EgretReflectUtils.setOptions(gameEngine, options);
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pKeyEvent) {
        LogUtil.i(TAG, "onKeyDown");
        switch (pKeyCode) {
        // 关闭 Egret Runtime
            case KeyEvent.KEYCODE_BACK : {
                // 关闭 Egret 下载器
                if (launcher != null) {
                    launcher.stop();
                    launcher = null;
                }
                if (gameEngine == null) {

                } else {
                    EgretReflectUtils.onStop(gameEngine);
                    //TODO add C++ callback 
                    gameEngine = null;
                }
                finish();
                return true;
            }
            default :
                return super.onKeyDown(pKeyCode, pKeyEvent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EgretReflectUtils.onPause(gameEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EgretReflectUtils.onResume(gameEngine);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.gc();
        LogUtil.i(TAG, "onDestroy");
    }
}
