package org.egret.egretruntimelauncher.config;
/**
 * 功能描述:Runtime下载配置类
 * 是否修改:是
 * 修改点1:RuntimeConfiguration()
 * @author imathliu
 *
 */
public class RuntimeConfiguration {

    /**
     * 发布模式
     */
    private static final int RUNTIME_RELEASE_VERSION = 0;
    /**
     * 调试模式
     */
    private static final int RUNTIME_DEBUG_VERSION = 1;
    /**
     * 通过变量dev指定当前Runtime接入模式(发布模式或调试模式)
     */
    private int dev;
    /**
     * 渠道ID.Egret平台获取
     */
    private String spId;
    /**
     * 应用KEY.Egret平台获取
     */
    private String appKey;

    private static RuntimeConfiguration instance;

    /**
     * 功能描述:游戏引擎下载器配置类
     * 修改内容:1.dev 2.spId 3.appKey
     */
    private RuntimeConfiguration() {
        dev = RUNTIME_RELEASE_VERSION;
        // 以下参数仅供测试使用,发布上线请替换正式参数
        spId = "9166";
        appKey = "Z2LnKzxk22jNw7UNknpDU";
    }

    public static RuntimeConfiguration getInstance() {
        if (instance == null) {
            instance = new RuntimeConfiguration();
        }
        return instance;
    }

    public int getDev() {
        return dev;
    }

    public String getSpId() {
        return spId;
    }

    public String getAppKey() {
        return appKey;
    }
}
