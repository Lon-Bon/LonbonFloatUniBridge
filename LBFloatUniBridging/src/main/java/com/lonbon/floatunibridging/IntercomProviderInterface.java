package com.lonbon.floatunibridging;

import android.util.Log;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

/**
 * *****************************************************************************
 * <p>
 * Copyright (C),2007-2016, LonBon Technologies Co. Ltd. All Rights Reserved.
 * <p>
 * *****************************************************************************
 *
 * @ProjectName: LBFloatUniDemo
 * @Package: com.lonbon.floatunibridging
 * @ClassName: IntercomProviderInterface
 * @Author： neo
 * @Create: 2022/4/8
 * @Describe:
 */
public interface IntercomProviderInterface {

    /**
     * 设置对讲页面显示位置（单位px）
     * @param left  对讲页面离屏幕左间距
     * @param top 对讲页面离屏幕上间距
     * @param width 对讲页面宽
     * @param height 对讲页面高
     */
    public void setTalkViewPosition(int left,int top , int width ,int height);

    /**
     * 门灯控制
     * @param color 门灯颜色 1 红闪，2 红亮，3 蓝闪，4 蓝亮，5 绿闪，6 绿亮，7 青闪，8 青亮， 9 红蓝闪,
     *              10 红绿闪，11 蓝绿闪，12 紫闪，13 紫亮，14 黄闪，15 黄亮， 16 白亮， 17 白闪，
     *              18 黑亮，19 黑闪
     * @param open 门灯开关 1 打开 0关闭
     */
    void extDoorLampCtrl(int color , int open);

    /**
     * 门磁开关回调
     * @param uniJsCallback
     */
    void onDoorContactValue(UniJSCallback uniJsCallback);

    /**
     * 设备终端管理接口
     * @param areaId
     * @param masterNum
     * @param slaveNum
     * @param devRegType
     * @param uniJsCallback
     */
    void asyncGetDeviceListInfo(int areaId , int masterNum ,int slaveNum ,int devRegType, UniJSCallback uniJsCallback);

    /**
     * 设备对讲状态回调接口
     * @param uniJsCallback
     */
    void updateDeviceTalkState(UniJSCallback uniJsCallback);

    /**
     * 界面主动点击呼出时
     * @param areaId
     * @param masterNum
     * @param slaveNum
     * @param devRegType
     */
    void deviceClick(int areaId , int masterNum ,int slaveNum ,int devRegType);

    /**
     * 呼叫对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    public void nativeCall(int areaId , int masterNum ,int slaveNum ,int devRegType);

    /**
     * 接听对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    public void nativeAnswer(int areaId , int masterNum ,int slaveNum ,int devRegType);

    /**
     * 挂断对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    public void nativeHangup(int areaId , int masterNum ,int slaveNum ,int devRegType);

}
