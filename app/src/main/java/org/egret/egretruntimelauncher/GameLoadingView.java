package org.egret.egretruntimelauncher;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 功能描述:Game下载进度条
 * 是否修改:是
 * 修改点1:GameLoadingView(Context context)
 * @author imathliu
 *
 */
public class GameLoadingView extends FrameLayout {

    private ProgressBar bar;
    private TextView tv;

    /**
     * 修改内容:替换自定义进度条
     * @param context
     */
    public GameLoadingView(Context context) {
        super(context);

        tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        tv.setText("Game Loading...");
        this.addView(tv);

        bar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        this.addView(bar);
    }

    public void onProgress(float progress) {
        bar.setProgress((int) progress);
    }

    public void onGameZipUpdateProgress(float percent) {
        bar.setProgress((int) percent);
    }

    public void onGameZipUpdateError() {

    }

    public void onGameZipUpdateSuccess() {

    }

}
