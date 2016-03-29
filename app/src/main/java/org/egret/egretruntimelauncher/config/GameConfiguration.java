package org.egret.egretruntimelauncher.config;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
/**
 * 功能描述:Game启动配置类
 * 是否修改:是
 * 修改点1:EGRET_GAME_PATH
 * 修改点2:EGRET_RUNTIME_PATH
 * 修改点3:EGRET_RUNTIME_APP_SECRET
 * 修改点4:EGRET_RUNTIME_CHANNEL_TAG
 * @author imathliu
 *
 */
public class GameConfiguration {
    // gameEngine加载方式.2为动态加载(默认)
    private static final String EGRET_RUNTIME_LOADER_TYPE = "2";
    // 应用验证码.从Egret开放平台获取
	private static final String EGRET_RUNTIME_APP_SECRET = "";
	// 渠道标识.从Egret开放平台获取，默认传空字符串    
	private static final String EGRET_RUNTIME_CHANNEL_TAG = "";
	// 游戏根目录,请注意游戏下载位置(存储在/data/data/应用私有空间指定目录下)
	private static final String EGRET_GAME_PATH = "egret/game";
	// runtime根目录
    private static final String EGRET_RUNTIME_PATH = "egret";

    private static GameConfiguration instance;

    private HashMap<String, Object> options;

    /**
     * 游戏引擎配置类 设置游戏相关的启动参数
     * 
     * @param context
     */
    private GameConfiguration(Context context) {
        options = new HashMap<String, Object>();       
        options.put("egret.runtime.libraryLoaderType", EGRET_RUNTIME_LOADER_TYPE);       
        options.put("egret.runtime.libraryRoot", new File(
                context.getFilesDir(), EGRET_RUNTIME_PATH).getAbsolutePath());       
        options.put("egret.runtime.egretRoot", new File(
                context.getFilesDir(), EGRET_GAME_PATH).getAbsolutePath());
        /*
        // 游戏根目录,请注意游戏下载位置(存储在sdcard/指定目录下)
        options.put("egret.runtime.egretRoot",
                new File(Environment.getExternalStorageDirectory(),
                        EGRET_GAME_PATH).getAbsolutePath());
        */        
        options.put("egret.runtime.appSecret", EGRET_RUNTIME_APP_SECRET);       
        options.put("egret.runtime.channelTag", EGRET_RUNTIME_CHANNEL_TAG);
    }

    public static GameConfiguration getInstance(Context context) {
        if (instance == null) {
            instance = new GameConfiguration(context);
        }
        return instance;
    }

    public HashMap<String, Object> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, Object> options) {
        this.options = options;
    }

    public String getScreenOrientation() {
        return (String) options.get("egret.runtime.screen.orientation");
    }

    public void setScreenOrientation(String screenOrientation) {
        options.put("egret.runtime.screen.orientation", screenOrientation);
    }

    public String getGameId() {
        return (String) options.get("egret.runtime.gameId");
    }

    public void setGameId(String gameId) {
        options.put("egret.runtime.gameId", gameId);
    }

    public String getGameUrl() {
        return (String) options.get("egret.runtime.loaderUrl");
    }

    public void setGameUrl(String gameUrl) {
        options.put("egret.runtime.loaderUrl", gameUrl);
    }

    public String getRuntimeLoaderMode() {
        return (String) options.get("egret.runtime.libraryLoaderType");
    }

    public void setRuntimeLoaderMode(String runtimeLoaderMode) {
        options.put("egret.runtime.libraryLoaderType", runtimeLoaderMode);
    }

    public String getRuntimeRoot() {
        return (String) options.get("egret.runtime.libraryRoot");
    }

    public void setRuntimeRoot(String runtimeRoot) {
        options.put("egret.runtime.libraryRoot", runtimeRoot);
    }

    public String getGameRoot() {
        return (String) options.get("egret.runtime.egretRoot");
    }

    public void setGameRoot(String gameRoot) {
        options.put("egret.runtime.egretRoot", gameRoot);
    }

    public String getNestMode() {
        return (String) options.get("egret.runtime.nest");
    }

    public void setNestMode(String nestMode) {
        options.put("egret.runtime.nest", nestMode);
    }

    public String getSpId() {
        return (String) options.get("egret.runtime.spid");
    }

    public void setSpId(String spId) {
        options.put("egret.runtime.spid", spId);
    }

    public String getCoopMode() {
        return (String) options.get("egret.runtime.coop");
    }

    public void setCoopMode(String coopMode) {
        options.put("egret.runtime.coop", coopMode);
    }

    public String getAppSecret() {
        return (String) options.get("egret.runtime.appSecret");
    }

    public void setAppSecret(String appSecret) {
        options.put("egret.runtime.appSecret", appSecret);
    }

    public String getChannelTag() {
        return (String) options.get("egret.runtime.channelTag");
    }

    public void setChannelTag(String channelTag) {
        options.put("egret.runtime.channelTag", channelTag);
    }
    
    public String getRuntimeLanguage() {
        return (String) options.get("egret.runtime.language");
    }

    public void setRuntimeLanguage(String language) {
        options.put("egret.runtime.language", language);
    }
    
    public String getOpenPlatformHostUrl() {
        return (String) options.get("egret.openplatform.host");
    }

    public void setOpenPlatformHostUrl(String hostUrl) {
        options.put("egret.openplatform.host", hostUrl);
    }
}
