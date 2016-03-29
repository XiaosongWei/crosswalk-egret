package org.egret.egretruntimelauncher;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 功能描述:Runtime下载进度条
 * 是否修改:是
 * 修改内容:RuntimeLoadingView(Context context)
 * @author imathliu
 *
 */
public class RuntimeLoadingView extends FrameLayout {

    private ProgressBar bar;
    private TextView tv;
    
    /**
     * 修改内容:替换自定义进度条
     * @param context
     */
    public RuntimeLoadingView(Context context) {
        super(context);

        tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        tv.setText("Runtime Loading...");
        this.addView(tv);

        bar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        this.addView(bar);
    }

    public void updateProgress(String name, int process, int total) {
        int result = (int) (process * 100 / total);
        bar.setProgress(result);
    }

    /**
     * 多文件下载 进度条更新
     * 
     * @param process
     * @param total
     */
    public void updateProgressSum(int process, int total) {
        int result = (int) (process * 100 / total);
        bar.setProgress(result);
    }
}
