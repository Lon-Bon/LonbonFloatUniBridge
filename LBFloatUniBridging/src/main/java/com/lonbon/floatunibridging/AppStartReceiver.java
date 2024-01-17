package com.lonbon.floatunibridging;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.lb.extend.security.broadcast.AreaDivision;
import com.lb.extend.security.broadcast.IBroadcastService;
import com.lb.extend.security.card.SwingCardService;
import com.lb.extend.security.education.EducationService;
import com.lb.extend.security.fingerprint.FingerprintService;
import com.lb.extend.security.intercom.IntercomService;
import com.lb.extend.security.setting.SystemSettingService;
import com.lb.extend.security.temperature.TemperatureMeasurementService;
import com.lb.extend.service.ILonbonService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
 * io.dcloud.PandoraEntryActivity ： Uniapp 固定启动 activity
 * 双重保险，设置 io.dcloud.PandoraEntryActivity检测
 */
public class AppStartReceiver extends BroadcastReceiver {

    private String TAG = "AppStartReceiver";

    public static boolean isConnect = false;
    public static boolean hasStartExecutor = false;

    public static ArrayList<AreaDivision> areaDivisionArrayList = new ArrayList<>();
    public static ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    /**
     * 获取服务类
     */
    public static ILonbonService iLonbonService;
    public static IntercomService intercomService ;
    public static SwingCardService swingCardService ;
    public static SystemSettingService systemSettingService ;
    public static TemperatureMeasurementService temperatureMeasurementService ;
    public static FingerprintService fingerprintService ;
    public static EducationService educationService;
    public static IBroadcastService broadcastService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AppStartReceiver", "onReceive: "+intent.toString() + Thread.currentThread().getId());
        String packName = intent.getComponent().flattenToShortString().split("/")[0];
        Log.d("AppStartReceiver", "onReceive: "+packName);
        startActivity(context,packName);

    }

    /**
     * 调起应用
     * @param context
     * @param packageName
     */
    private void startActivity(Context context,String packageName){
        if (isRunningApp(context,packageName)){
            Log.d(TAG, "startActivity: app is running "+packageName);
            return;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent it = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(it);
        }catch (Exception e){
            Log.d(TAG, "startActivity: "+e.toString());
        }

    }
    private boolean isRunningApp(Context context,String packageName){
        return (isRunningAppProcesses(context,packageName) || isForeground(context,"io.dcloud.PandoraEntryActivity"));
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
        if (list != null && !list.isEmpty()) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }




}
