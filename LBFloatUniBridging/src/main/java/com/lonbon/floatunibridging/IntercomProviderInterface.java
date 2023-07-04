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
    void setTalkViewPosition(int left, int top, int width, int height);

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
    void nativeCall(int areaId, int masterNum, int slaveNum, int devRegType);

    /**
     * 接听对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    void nativeAnswer(int areaId, int masterNum, int slaveNum, int devRegType);

    /**
     * 挂断对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    void nativeHangup(int areaId, int masterNum, int slaveNum, int devRegType);

    /**
     * 开关电控锁
     *
     * @param num 电控锁序号
     * @param open 开关 0关 1开
     */
    void  openLockCtrl(int num, int open);

    /**
     * 获取当前设备信息（包含设备编号）
     *
     * @param uniJsCallback 设备信息
     */
    void getCurrentDeviceInfo(UniJSCallback uniJsCallback);

    /**
     * 设备对讲事件回调接口
     *
     * 回调当前设备对讲事件
     * @param uniJsCallback 返回状态变化的设备
     */
    void talkEventCallback(UniJSCallback uniJsCallback);

    /**
     * 设备在线回调接口
     *
     * @param uniJsCallback 返回状态变为在线的设备
     */
    void onDeviceOnLine(UniJSCallback uniJsCallback);

    /**
     * 设备离线回调接口
     *
     * @param uniJsCallback 返回态变为离线的设备
     */
    void onDeviceOffLine(UniJSCallback uniJsCallback);

    /**
     * 监听转对讲
     *
     */
    void listenToTalk();

    /**
     * 设置视频隐藏
     *
     * @param hide 隐藏视频 true隐藏 false显示
     */
    void hideTalkView(Boolean hide);

    /**
     * 一键呼叫
     */
    void oneKeyCall();

    /**
     * 设置本地预览视频框显示位置（单位px）
     * @param left  视频框离屏幕左间距
     * @param top 视频框离屏幕上间距
     * @param width 视频框宽
     * @param height 视频框高
     */
    void setLocalVideoViewPosition(int left, int top, int width, int height);

    /**
     * 设置本地预览视频隐藏
     * @param hide
     */
    void hideLocalPreView(Boolean hide);

    /**
     * 设置外接咪头使能
     * @param enable
     */
    void setExtMicEna(Boolean enable);

    /**
     * 开启本地摄像头
     * @param isOpen
     */
    void openLocalCamera(Boolean isOpen);

    /**
     * 拍照初始化
     */
    void initFrame();

    /**
     * 设置拍照的图像宽高参数
     * @param width
     * @param height
     */
    void setViewWidthHeight(int width,int height);

    /**
     * 开启启动发送数据
     */
    void startTakeFrame();
    /**
     * 停止采集数据
     */
    void stopTakeFrame();

    /**
     * 拍照
     */
    void takePicture();

    /**
     * 取摄像头数据
     */
    void takeFrame();

    /**
     * 获取照片数据回调
     * @param uniJsCallback
     */
    void takePictureCallBack(UniJSCallback uniJsCallback);
    /**
     * 获取视频预览数据回调
     * @param uniJsCallback
     */
    void takeFrameCallBack(UniJSCallback uniJsCallback);


    /**
     * 设置通话记录文件存储路径
     * @param path
     * @param uniJsCallback
     */
    void setRecordPath(String path,UniJSCallback uniJsCallback);

    /**
     * 获取某路径下文件列表
     * @param path
     * @param uniJsCallback
     */
    void getFileList(String path,UniJSCallback uniJsCallback);

    /**
     * 删除文件
     * @param path
     * @param uniJsCallback
     */
    void deleteFile(String path,UniJSCallback uniJsCallback);


    /**
     * 主机控制分机通话音量
     * @param volume - 范围 0-5
     */
    void setSlaveVolume(int volume);

    /**
     * 主机获取分机通话音量（同步方法）
     * @return 0：成功，其它值失败
     */
    void syncGetSlaveVolume(UniJSCallback uniJSCallback);

    /**
     * 初始化喊话广播
     */
    void initBroadcast();

    /**
     * 设备IO事件回调
     * @param uniJSCallback
     */
    void setOnIONotifyListener(UniJSCallback uniJSCallback);

    /**
     * 喊话广播相关状态回调
     * @param uniJSCallback
     */
    void setOnSpeakBroadcastListener(UniJSCallback uniJSCallback);

    /**
     * 设置广播Toast提示
     * @param uniJSCallback
     */
    void setOnToastListener(UniJSCallback uniJSCallback);

    /**
     * 添加喊话广播设备
     * @param num
     * @param uniJSCallback 回调目前设备列表
     */
    void addBroadcastObj(int num, UniJSCallback uniJSCallback);

    /**
     * 清空喊话广播设备
     */
    void clearBroadcastObj();

    /**
     * 设置喊话广播设备
     */
    void setSpeakBroadcastDevice();

    /**
     * 开始喊话广播
     * @param data
     */
    void startSpeakBroadcast(int data);

    /**
     * 停止喊话广播
     * @param data
     */
    void stopSpeakBroadcast(int data);
}