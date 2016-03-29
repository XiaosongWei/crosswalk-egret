package org.egret.java.egretruntimelauncher;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.egret.egretruntimelauncher.utils.ExecutorLab;
import org.egret.egretruntimelauncher.utils.FileUtil;
import org.egret.egretruntimelauncher.utils.LogUtil;
import org.egret.egretruntimelauncher.utils.Md5Util;
import org.egret.egretruntimelauncher.utils.NetClass;
import org.egret.egretruntimelauncher.utils.ZipClass;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

/**
 * 渠道接入Runtime.请勿修改此类.
 * @author imathliu
 *
 */
public class EgretRuntimeLauncher {

    public interface EgretRuntimeDownloadListener {
        /**
         * 下载多个文件 进度条更新监听
         * 
         * @param currentByte
         * @param totalByte
         */
        void onProgressTotal(int currentByte, int totalByte);

        void onProgress(String fileName, int currentByte, int totalByte);

        void onError(String message);

        void onSuccess(Class<?> gameEngineClass);
    }

    public enum UrlType {
        REMOTE_TEST, PRODUCTION
    }

    private static final String EGRET_RUNTIME_VERSION_URL_TEST =
            "http://runtime.egret-labs.org/test-rw3435d/runtime.php";
    
    private static final String EGRET_RUNTIME_VERSION_URL =
            "http://runtime.egret-labs.org/runtime.php";
    
    private static final String EGRET_RUNTIME_CACHE_ROOT = "update";
    public static final String EGRET_RUNTIME_SD_ROOT = "egret/runtime";
    public static final String EGRET_ROOT = "egret";
    public static final String EGRET_JSON = "egret.json";

    private static final String TAG = "EgretRuntimeLauncher";

    /**
     * 多文件下载 进度条更新 所需数据 存储
     */
    private ConcurrentHashMap<String, Integer> mapFileSize =
            new ConcurrentHashMap<String, Integer>();
    private int fileSizeSum = 0;
    private int downLoadSum = 0;

    /**
     * 在一个完整的 app 生命周期内可以下载多次，但是只能加载一次
     */

    private ArrayList<EgretRuntimeLibrary> downloaderList;
    private EgretRuntimeDownloadListener downloadListener;
    private String runtimeVersionUrl;
    private String urlData;
    private File libraryRoot;
    private File sdRoot;
    private File cacheRoot;
    protected int updatedNumber;

    private Handler mainHandler;

    /**
     * 是否强制开启 Runtime下载
     */
    public static int DEBUG_RUNTIME_DOWNLOAD = 0;

    public EgretRuntimeLauncher(Context context, String libraryRoot) {
        downloaderList = new ArrayList<EgretRuntimeLibrary>();
        mainHandler = new Handler(context.getMainLooper());
        this.runtimeVersionUrl = EGRET_RUNTIME_VERSION_URL;
        this.libraryRoot = libraryRoot != null ? new File(libraryRoot) : null;
        this.cacheRoot = new File(libraryRoot, EGRET_RUNTIME_CACHE_ROOT);
        this.sdRoot = getSdRoot();
        cacheRoot.mkdirs();
    }

    public EgretRuntimeLauncher(Context context, String libraryRoot,
            String appId, String appKey, int devVersion) {
        this(context, libraryRoot);
        urlData = "?appId=" + appId + "&appKey=" + appKey;
        if (devVersion > 0) {
            urlData += "&dev=" + devVersion;
        }
        this.runtimeVersionUrl += urlData;
    }

    public void setUrlType(UrlType type) {
        switch (type) {
            case REMOTE_TEST :
                runtimeVersionUrl = EGRET_RUNTIME_VERSION_URL_TEST;
                break;
            case PRODUCTION :
                runtimeVersionUrl = EGRET_RUNTIME_VERSION_URL;
                break;
            default :
                runtimeVersionUrl = EGRET_RUNTIME_VERSION_URL;
                break;
        }
        if (urlData != null) {
            runtimeVersionUrl += urlData;
        }
    }

    private File getSdRoot() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File runtimeRootInExternalStorage = new File(
                    Environment.getExternalStorageDirectory(),
                    EGRET_RUNTIME_SD_ROOT);
            if (runtimeRootInExternalStorage.exists()
                    || runtimeRootInExternalStorage.mkdirs()) {
                return runtimeRootInExternalStorage;
            }
        }
        return null;
    }

    public void run(EgretRuntimeDownloadListener listener) {
        if (runtimeVersionUrl == null || libraryRoot == null
                || listener == null) {
            String message = "library root, url or listener may be null";
            LogUtil.e("EgretRuntimeLauncher", message);
            listener.onError(message);
            ExecutorLab.releaseInstance();
            return;
        }

        LogUtil.d(TAG, "run");
        downloadListener = listener;
        fetchRemoteVersion();
    }

    private void fetchRemoteVersion() {
        ExecutorLab.getInstance().addTask(new Thread(new Runnable() {

            @Override
            public void run() {
                NetClass net = new NetClass();
                net.getRequest(runtimeVersionUrl, new NetClass.OnNetListener() {

                    @Override
                    public void onSuccess(String content) {
                        if (content == null) {
                            handleError("response content is null");
                            return;
                        }
                        parseRuntimeVersion(content);
                    }

                    @Override
                    public void onProgress(int progress, int length) {
                    }

                    @Override
                    public void onError(String message) {
                        handleError(message);
                    }
                });
            }
        }));
    }

    private void parseRuntimeVersion(String content) {
        EgretRuntimeVersion.get().setLibraryLabBy(content);
        File runtime = new File(libraryRoot, EGRET_JSON);
        FileUtil.writeFile(runtime, content);
        updateLibrary();
    }

    /**
     * 多文件下载 更新已下载总数据量
     */
    private synchronized void updateDownLoadSum() {
        downLoadSum = 0;

        for (Entry<String, Integer> entry : mapFileSize.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            downLoadSum += value;
        }
    }

    /**
     * 获取请求文件大小
     * 
     * @param fileUrl
     * @return fileLengs
     */
    private int getFileSize(final String fileUrl) {

        HttpURLConnection conn = null;

        int fileLens = 0;

        URL url;
        try {

            url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();
            fileLens = conn.getContentLength();
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLens;
    }

    private void updateLibrary() {
        updatedNumber = 0;
        ArrayList<Library> libraryList = getNeedUpdateLibraryList();
        if (libraryList.size() == 0) {
            updated();
        }
        LogUtil.d(TAG,
                "rt libraryList size: " + String.valueOf(libraryList.size()));

        int fileSizeTemp = 0;

        for (final Library library : libraryList) {

            String fileUrl;
            fileUrl = EgretRuntimeVersion.get().getUrlBy(library.getZipName());

            int zipSize = getFileSize(fileUrl);

            fileSizeTemp += zipSize;
        }

        fileSizeSum = fileSizeTemp;

        for (final Library library : libraryList) {
            EgretRuntimeLibrary downloader = new EgretRuntimeLibrary(library,
                    libraryRoot, cacheRoot, sdRoot);
            downloader.download(new EgretRuntimeLibrary.OnDownloadListener() {

                @Override
                public void onSuccess() {
                    updatedNumber += 1;
                    updated();
                }

                @Override
                public void onProgress(int progress, int length) {
                    mapFileSize.put(library.getZipName(), progress);
                    updateDownLoadSum();
                    downloadListener.onProgressTotal(downLoadSum, fileSizeSum);
                }

                @Override
                public void onError(String message) {
                    handleError("Fail to download file: "
                            + library.getZipName() + " detail: " + message);
                    ExecutorLab.releaseInstance();
                }
            });
            downloaderList.add(downloader);
            LogUtil.d(TAG, "addTask: " + library.getZipName());
            ExecutorLab.getInstance().addTask(downloader);
        }
    }

    private ArrayList<Library> getNeedUpdateLibraryList() {
        ArrayList<Library> result = new ArrayList<Library>();
        ArrayList<Library> libraryList = EgretRuntimeVersion.get()
                .getLibraryList();
        for (Library library : libraryList) {
            if (checkLocal(library) || checkCache(library) || checkSd(library)) {
                continue;
            }
            result.add(library);
        }
        return result;
    }

    private boolean checkLocal(Library library) {
        return isLatest(new File(libraryRoot, library.getLibraryName()),
                library.getLibraryCheckSum());
    }

    private boolean isLatest(File file, String checkSum) {
        if (EgretRuntimeLauncher.DEBUG_RUNTIME_DOWNLOAD > 0) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        if (Md5Util.checkMd5(file, checkSum)) {
            return true;
        }
        if (!file.delete()) {
            handleError("Fail to delete file: " + file.getAbsolutePath());
            ExecutorLab.releaseInstance();
        }
        return false;
    }

    private boolean checkZipInRoot(Library library, File root) {
        return isLatest(new File(root, library.getZipName()),
                library.getZipCheckSum());
    }

    private boolean checkCache(Library library) {
        if (!checkZipInRoot(library, cacheRoot)) {
            return false;
        }
        File cacheZip = new File(cacheRoot, library.getZipName());
        ZipClass zip = new ZipClass();
        if (!zip.unzip(cacheZip, libraryRoot)) {
            LogUtil.e(TAG, "fail to unzip " + cacheZip.getAbsolutePath());
            return false;
        }
        if (!cacheZip.delete()) {
            LogUtil.e(TAG, "fail to delete " + cacheZip.getAbsolutePath());
            return false;
        }
        return true;
    }

    private boolean checkSd(Library library) {
        if (!checkZipInRoot(library, sdRoot)) {
            return false;
        }
        File cacheZip = new File(cacheRoot, library.getZipName());
        if (!FileUtil.copy(new File(sdRoot, library.getZipName()), cacheZip)) {
            return false;
        }
        return checkCache(library);
    }

    private void updated() {
        if (downloaderList.size() > 0
                && updatedNumber != downloaderList.size()) {
            return;
        }
        loadLibrary();
        // ExecutorLab.releaseInstance();
    }

    private void loadLibrary() {
        if (!EgretRuntimeLoader.get().isLoaded()) {
            ArrayList<Library> libraryList = EgretRuntimeVersion.get()
                    .getLibraryList();
            for (Library library : libraryList) {
                EgretRuntimeLoader.get().load(
                        new File(libraryRoot, library.getLibraryName())
                                .getAbsolutePath());
            }
        }
        notifyLoadHandler();
    }

    private void handleError(String message) {
        String content = FileUtil.readFile(new File(libraryRoot, EGRET_JSON));
        if (content == null) {
            downloadListener.onError(message);
            ExecutorLab.releaseInstance();
            return;
        }
        EgretRuntimeVersion.get().setLibraryLabBy(content);
        ArrayList<Library> libraries = EgretRuntimeVersion.get()
                .getLibraryList();
        if (libraries == null) {
            return;
        }
        for (Library library : libraries) {
            if (!checkLocal(library)) {
                downloadListener.onError(message);
                ExecutorLab.releaseInstance();
                return;
            }
            if (!EgretRuntimeLoader.get().isLoaded()) {
                EgretRuntimeLoader.get().load(
                        new File(libraryRoot, library.getLibraryName())
                                .getAbsolutePath());
            }
        }
        notifyLoadHandler();
    }

    private void notifyLoadHandler() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Class<?> gameEngineClass = EgretRuntimeLoader.get()
                        .getEgretGameEngineClass();
                if (gameEngineClass == null) {
                    downloadListener.onError("fails to new game engine");
                    ExecutorLab.releaseInstance();
                    return;
                }
                downloadListener.onSuccess(gameEngineClass);
            }
        };
        mainHandler.post(r);
    }

    public void stop() {
        for (int i = 0; i < downloaderList.size(); ++i) {
            downloaderList.get(i).stop();
        }
        ExecutorLab.releaseInstance();
    }
}
