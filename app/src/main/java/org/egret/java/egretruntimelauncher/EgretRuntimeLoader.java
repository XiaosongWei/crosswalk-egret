package org.egret.java.egretruntimelauncher;

import java.io.File;

import org.egret.egretruntimelauncher.utils.LogUtil;

import dalvik.system.DexClassLoader;

/**
 * 渠道接入Runtime.请勿修改此类.
 * @author imathliu
 *
 */
public class EgretRuntimeLoader {
    private static final String GAME_ENGINE_CLASS =
            "org.egret.egretframeworknative.engine.EgretGameEngineBase";
    private static final String TAG = "EgretRuntimeLoader";
    private static EgretRuntimeLoader instance = null;
    private Class<?> egretGameEngineClass;
    private boolean loaded;

    private EgretRuntimeLoader() {
        egretGameEngineClass = null;
        loaded = false;
    }

    public static EgretRuntimeLoader get() {
        if (instance == null) {
            instance = new EgretRuntimeLoader();
        }
        return instance;
    }

    public void load(String library) {
        loaded = true;
        if (library.endsWith(".jar")) {
            loadJar(library);
        }
    }

    public void loadJar(String pathName) {
        File f = new File(pathName);
        f.setExecutable(true);
        LogUtil.d(TAG,
                "loadJar: " + pathName + ": " + String.valueOf(f.exists()));

        try {
            DexClassLoader classLoader = new DexClassLoader(pathName, new File(
                    pathName).getParent(), null, getClass().getClassLoader());
            if (egretGameEngineClass == null) {
                egretGameEngineClass = classLoader.loadClass(GAME_ENGINE_CLASS);
            }
        } catch (Exception e) {
            LogUtil.e("Loader", "need dex format jar");
            e.printStackTrace();
        }
    }

    public Class<?> getEgretGameEngineClass() {
        return egretGameEngineClass;
    }

    public boolean isLoaded() {
        return loaded;
    }

}
