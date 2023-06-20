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
     * 启用/关闭实时监听电教任务进入播放页面
     * Params:
     * isExecute - 是否执行 true:执行，false：不执行
     */
    void controlEducationListener(boolean isExecute);

    /**
     * 点此按钮20秒后退出电教播放页面
     */
    void exitEducation();

    /**
    * 控制HDMI
     * Params:
     * outputConfigure - 1:HDMI一直有信号输出，2：HDMI仅在设备接收到信息发布或点播直播任务时有信号输出
     */
    void hdmiOpen(int outputConfigure);

    /**
     * 获取电教任务状态（同步方法）
     * @return 0：成功，其它值失败
     */
    void syncGetEducationState(UniJSCallback uniJSCallback);

    /**
     * 电教任务回调
     * @param uniJSCallback 是否存在电教任务
     */
    void setEduTaskCallBack(UniJSCallback uniJSCallback);
}
