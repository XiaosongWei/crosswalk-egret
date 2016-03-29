package org.egret.java.egretruntimelauncher;

import java.io.File;

import org.egret.egretruntimelauncher.utils.FileUtil;
import org.egret.egretruntimelauncher.utils.LogUtil;
import org.egret.egretruntimelauncher.utils.Md5Util;
import org.egret.egretruntimelauncher.utils.NetClass;
import org.egret.egretruntimelauncher.utils.ZipClass;

/**
 * 渠道接入Runtime.请勿修改此类.
 * @author imathliu
 *
 */
public class EgretRuntimeLibrary implements Runnable {

    protected static final String TAG = "EgretRuntimeLibrary";
    private OnDownloadListener downloadListener;
    private String url;
    private File root;
    private File cacheRoot;
    private File sdRoot;
    private Library library;
    private volatile boolean isCancelling;

    public interface OnDownloadListener {
        void onProgress(int progress, int length);

        void onSuccess();

        void onError(String message);
    }

    public EgretRuntimeLibrary(Library library, File root, File cacheRoot,
            File sdRoot) {
        this.library = library;
        this.root = root;
        this.cacheRoot = cacheRoot;
        this.sdRoot = sdRoot;
        this.url = EgretRuntimeVersion.get().getUrlBy(library.getZipName());
    }

    public void download(OnDownloadListener listener) {
        if (library == null || root == null || cacheRoot == null
                || listener == null) {
            listener.onError("libray, root, cacheRoot, listener may be null");
            return;
        }
        downloadListener = listener;
    }

    @Override
    public void run() {
        isCancelling = false;
        doDownload();
    }

    private void doDownload() {
        File targetRoot = sdRoot != null ? sdRoot : cacheRoot;
        File target = new File(targetRoot, library.getZipName());

        NetClass net = new NetClass();

        net.writeResponseToFile(url, target, new NetClass.OnNetListener() {

            @Override
            public void onSuccess(String arg0) {
                if (isCancelling) {
                    return;
                }
                if (doMove()) {
                    doUnzip();
                }
            }

            @Override
            public void onProgress(int progress, int length) {
                downloadListener.onProgress(progress, length);
            }

            @Override
            public void onError(String message) {
                downloadListener.onError(message);
            }
        });
    }

    private boolean doMove() {
        if (isCancelling) {
            downloadListener.onError("thread is cancelling");
            return false;
        }
        if (sdRoot != null
                && !FileUtil.copy(new File(sdRoot, library.getZipName()),
                        new File(cacheRoot, library.getZipName()))) {
            downloadListener.onError("copy file error");
            return false;
        }
        return true;
    }

    private void doUnzip() {
        if (isCancelling) {
            downloadListener.onError("thread is cancelling");
        }
        final File cache = new File(cacheRoot, library.getZipName());
        final File target = new File(root, library.getLibraryName());
        if (!Md5Util.checkMd5(cache, library.getZipCheckSum())) {
            downloadListener.onError("cache file md5 error");
        }
        ZipClass zip = new ZipClass();
        zip.unzip(cache, root, new ZipClass.OnZipListener() {

            @Override
            public void onSuccess() {
                LogUtil.i(TAG,
                        "Success to unzip file: " + cache.getAbsolutePath());
                if (!cache.delete()) {
                    LogUtil.e(TAG,
                            "Fail to delete file: " + cache.getAbsolutePath());
                }
                if (!Md5Util.checkMd5(target, library.getLibraryCheckSum())) {
                    downloadListener.onError("target file md5 error");
                    return;
                }
                downloadListener.onSuccess();
            }

            @Override
            public void onProgress(int arg0, int arg1) {

            }

            @Override
            public void onFileProgress(int arg0, int arg1) {
            }

            @Override
            public void onError(String arg0) {
                downloadListener.onError("fail to unzip file: "
                        + cache.getAbsolutePath());
            }
        });
    }

    public void stop() {
        isCancelling = true;
    }
}
