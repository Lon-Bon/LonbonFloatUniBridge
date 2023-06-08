package com.lonbon.floatunibridging;

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
     * 查询设备列表接口（带描述信息）
     * 主机用：用于查询设备在线列表进行UI显示
     * 仅传区号，其他参数传0，则为查询区号下的主机列表 传区号、主机号、注册类型，分机号传0，
     * 则为查询该区该主机下某类型的分机列表
     * @param areaId 区号
     * @param masterNum 主机号
     * @param slaveNum 分机号
     * @param devRegType 注册类型
     * @param uniJsCallback 返回该areaId下的在线设备列表
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

    /**
     * 开关电控锁
     *
     * @param num 电控锁序号
     * @param open 开关 0关 1开
     */
    public void  openLockCtrl(int num, int open);

    /**
     * 获取当前设备信息（包含设备编号）
     *
     * @param uniJsCallback 设备信息
     */
    public void getCurrentDeviceInfo(UniJSCallback uniJsCallback);

    /**
     * 设备对讲事件回调接口
     *
     * 回调当前设备对讲事件
     * @param uniJsCallback 返回状态变化的设备
     */
    public void talkEventCallback(UniJSCallback uniJsCallback);

    /**
     * 设备在线回调接口
     *
     * @param uniJsCallback 返回状态变为在线的设备
     */
    public void onDeviceOnLine(UniJSCallback uniJsCallback);

    /**
     * 设备离线回调接口
     *
     * @param uniJsCallback 返回态变为离线的设备
     */
    public void onDeviceOffLine(UniJSCallback uniJsCallback);

    /**
     * 监听转对讲
     *
     */
    public void listenToTalk();

    /**
     * 设置视频隐藏
     *
     * @param hide 隐藏视频 true隐藏 false显示
     */
    public void hideTalkView(Boolean hide);

    /**
     * 一键呼叫
     */
    public void oneKeyCall();

    /**
     * 设置本地预览视频框显示位置（单位px）
     * @param left  视频框离屏幕左间距
     * @param top 视频框离屏幕上间距
     * @param width 视频框宽
     * @param height 视频框高
     */
    public void setLocalVideoViewPosition(int left,int top , int width ,int height);

    /**
     * 设置本地预览视频隐藏
     * @param hide
     */
    public void hideLocalPreView(Boolean hide);

    /**
     * 设置外接咪头使能
     * @param enable
     */
    public void setExtMicEna(Boolean enable);
}
