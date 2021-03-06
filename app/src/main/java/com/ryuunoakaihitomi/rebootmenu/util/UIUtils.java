package com.ryuunoakaihitomi.rebootmenu.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.ryuunoakaihitomi.rebootmenu.R;

/**
 * 本应用关于界面操作的工具集合
 * Created by ZQY on 2018/2/10.
 *
 * @author ZQY
 */

public class UIUtils {

    /**
     * 加载特定主题颜色的AlertDialog
     *
     * @param isWhite      是否白色主题
     * @param activityThis 当前activity的上下文
     * @return 已处理Builder对象
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static AlertDialog.Builder LoadDialog(boolean isWhite, Context activityThis) {
        //在API级别23中，AlertDialog的主题定义被废弃。用在API级别22中新引入的Android默认主题格式代替。
        boolean isAndroidMPlus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
        int themeCode;
        if (isWhite) {
            if (isAndroidMPlus)
                themeCode = android.R.style.Theme_DeviceDefault_Light_Dialog_Alert;
            else
                themeCode = AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        } else {
            if (isAndroidMPlus)
                themeCode = android.R.style.Theme_DeviceDefault_Dialog_Alert;
            else
                themeCode = AlertDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        return new AlertDialog.Builder(activityThis, themeCode);
    }

    /**
     * 将窗体透明展示
     *
     * @param w 欲透明化的dialog
     * @param f 透明度
     * @throws NullPointerException window.getAttributes()
     */
    public static void alphaShow(AlertDialog w, Float f) {
        Window window = w.getWindow();
        assert window != null;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = f;
        window.setAttributes(lp);
        w.show();
    }

    //显示帮助对话框
    public static void helpDialog(final Context activityThis, final AlertDialog.Builder returnTo, boolean cancelable, boolean isWhite) {
        new TextToast(activityThis, String.format(activityThis.getString(R.string.help_notice), getAppVersionName(activityThis), activityThis.getString(R.string.help_update_date)));
        AlertDialog.Builder h = LoadDialog(isWhite, activityThis);
        h.setTitle(activityThis.getString(R.string.help));
        h.setMessage(activityThis.getString(R.string.help_body));
        h.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface p1) {
                alphaShow(returnTo.create(), 0.75f);
            }
        });
        h.setNeutralButton(activityThis.getString(R.string.offical_download_link), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface p1, int p2) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ryuunoakaihitomi/rebootmenu/releases"));
                activityThis.startActivity(i);
                System.exit(0);
            }
        });
        h.setNegativeButton(activityThis.getString(R.string.donate), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface p1, int p2) {
                activityThis.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ryuunoakaihitomi.info/donate/")));
                System.exit(0);
            }
        });
        //有意保留的bug:帮助对话框的退出方式与配置相反
        if (cancelable) {
            h.setPositiveButton(activityThis.getString(R.string.exit), new AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface p1, int p2) {
                    alphaShow(returnTo.create(), 0.75f);
                }
            });
            h.setCancelable(false);
        }
        alphaShow(h.create(), 0.8f);
    }

    /**
     * 使状态栏透明
     * 来自https://github.com/laobie/StatusBarUtil
     *
     * @param activity 要渲染的活动
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            //保留
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 取应用VersionName
     * （姑且放在这里）
     *
     * @param context c
     * @return vn
     */
    private static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception ignored) {
        }
        return versionName;
    }
}
