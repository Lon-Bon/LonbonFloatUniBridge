package com.lonbon.floatunibridging;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
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
    private String TAG = "AppStartReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AppStartReceiver", "onReceive: "+intent.toString());
        //客户应用包名
//        startActivity(context,"uni.UNIE9B1944");
        //本地测试包名
        testStartActivity(context,"uni.UNI8EA39EE");
    }

    /**
     * 调起应用
     * @param context
     * @param packageName
     */
    private void startActivity(Context context,String packageName){
        if (isRunningAppProcesses(context,packageName)){
            Log.d(TAG, "startActivity: is running "+packageName);
            return;
        }
        Log.d(TAG, "startActivity: start process "+packageName);
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent it = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(it);
        }catch (Exception e){
            Log.d(TAG, "startActivity: "+e.toString());
        }

    }

    private void testStartActivity(Context context,String packageName){
        Log.d(TAG, "testStartActivity: "+isRunningAppProcesses(context,packageName));
        Log.d(TAG, "testStartActivity: "+isAppRunning(context,packageName));
        Log.d(TAG, "testStartActivity: "+isForeground(context,"uni.UNI8EA39EE/io.dcloud.PandoraEntry"));
    }

    public boolean isRunningAppProcesses(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            Log.d(TAG,"importance "+appProcess.importance);
            Log.d(TAG,"processName "+appProcess.processName.equals(packageName));
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    //只需要获取当前的上下文，即可判断应用是否在前台
    public boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
        if(list.size() <= 0) {
            return false;
        }
        Log.d(TAG,"classname is "+list.get(0).topActivity.getClassName());
        if( list.get(0).topActivity.getClassName().equals(packageName) ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }




}
