package com.lonbon.floatunibridging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

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
        PackageManager packageManager = context.getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(it);
    }
}
