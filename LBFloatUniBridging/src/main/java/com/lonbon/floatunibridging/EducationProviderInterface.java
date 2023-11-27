package com.lonbon.floatunibridging;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

public interface EducationProviderInterface {

    /**
     * 初始化电教模块
     */
    void initEducation();

    /**
     * 进入点播直播页面
     */
    void enterLiveShow();

    /**
     * 退出点播直播页面
     */
    void exitLiveShow();

    /**
     * 获取电教任务列表（同步方法）
     */
    void syncGetEducationTaskList(UniJSCallback uniJSCallback);

    /**
     * 进入电教任务播放页面
     */
    void enterEducationTask();

    /**
     * 退出电教任务播放页面
     */
    void exitEducationTask();

    /**
     * 电教任务状态回调
     */
    void setEducationStateListener(UniJSCallback uniJSCallback);

    /**
     * 控制HDMI
     * Params:
     * outputConfigure - 1:HDMI一直有信号输出，2：HDMI仅在设备接收到信息发布或点播直播任务时有信号输出
     */
    void hdmiOpen(int outputConfigure);

    /**
     * 双屏异常异声开关
     * Params:
     * enable - 0:仅从电视输出，1:声音和终端声音同步输出
     */
    void audioSyncOutput(int enable);
}
