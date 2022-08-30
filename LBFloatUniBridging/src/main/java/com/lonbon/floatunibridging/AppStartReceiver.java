package com.lonbon.floatunibridging;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

/**
 * *****************************************************************************
 * <p>
 * Copyright (C),2007-2016, LonBon Technologies Co. Ltd. All Rights Reserved.
 * <p>
 * *****************************************************************************
 *
 * @ProjectName: LBFloatUniDemo
 * @Package: com.lonbon.floatunibridging
 * @ClassName: AppStartReceiver
 * @Author： neo
 * @Create: 2022/8/30
 * @Describe:
 */
public class AppStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AppStartReceiver", "onReceive: "+intent.toString());
        //本地测试包名
        startActivity(context,"uni.UNI8EA39EE");
        //客户应用包名
        startActivity(context,"uni.UNIE9B1944");
    }

    /**
     * 调起应用
     * @param context
     * @param packageName
     */
    private void startActivity(Context context,String packageName){
        if (isRunningAppProcesses(context,packageName)){
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(it);
    }
    
    public boolean isRunningAppProcesses(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


}
