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
 * @ClassName: SettingProviderInterface
 * @Author： neo
 * @Create: 2022/4/8
 * @Describe:
 */
public interface SettingProviderInterface {
    /**
     * 启动刷卡模块（同步方法），进入界面时可调用
     * @return 0：成功，其它值失败
     */
    void syncStartCard(UniJSCallback uniJSCallback);

    /**
     * 停止刷卡模块（同步方法），离开界面时可调用
     * @return 0：成功，其它值失败
     */
    void syncStopCard(UniJSCallback uniJSCallback);

    /**
     * 刷卡回调
     * @param uniJSCallback callBack - 根据返回的CallbackData的code来判断是否成功（0成功，其他值都是失败），
     *                      如果成功，则通过CallbackData的data获取CardData，CardData的cardNum是卡号；
     *                      如果失败，则通过CallbackData的code来判断失败类型，CallbackData的msg会写明失败原因
     */
    void setCardDataCallBack(UniJSCallback uniJSCallback);

    /***************************************************************************/

    /**
     * 启动指纹模块（同步方法），进入界面时可调用
     * @return 0：成功，其它值失败
     */
    void syncStartFinger(UniJSCallback uniJSCallback);

    /**
     * 停止指纹模块（同步方法），离开界面时可调用
     * @return 0：成功，其它值失败
     */
    void syncStopFinger(UniJSCallback uniJSCallback);

    /**
     * 指纹采集（同步方法）
     * @param id  指纹id
     * 通过setGetFingerprintFeatureCallBack设置的回调返回
     */
    void fingerprintCollect(String id);

    /**
     * 指纹识别同步方法
     */
    void fingerprintRecognition();

    /**
     * 指纹特征值入库（同步方法）
     * @param id 指纹id
     * @param feature 指纹特征值
     * @return 0：调用成功，其它值失败。
     * 指纹id的入库的特征值通过setGetFingerprintFeatureCallBack设置的回调返回
     */
    void fingerprintFeatureInput(String id,String feature);

    /**
     * 指纹id和特征值的回调，指纹采集和指纹特征值入库的结果通过此CallBack返回
     * 根据返回的CallbackData的code来判断是否成功（0成功，其他值都是失败），
     * 如果成功，则通过CallbackData的data获取GetFingerprintFeatureResult；
     * 如果失败，则通过CallbackData的code来判断失败类型，CallbackData的msg会写明失败原因。
     * @param uniJSCallback
     */
    void setFingerprintFeatureCallBack(UniJSCallback uniJSCallback);

    /**
     * 指纹采集过程回调，每一枚指纹采集完成都会回调，leftCounts为0是则代表全部指纹采集完成
     * 根据返回的CallbackData的code来判断是否成功（0成功，其他值都是失败），
     * 如果成功，则通过CallbackData的data获取GetFingerprintFeatureLeftNumResult；
     * 如果失败，则通过CallbackData的code来判断失败类型，CallbackData的msg会写明失败原因。（失败用不到）
     * @param uniJSCallback
     */
    void setFingerprintFeatureLeftNumCallBack(UniJSCallback uniJSCallback);

    /**
     * 指纹比对回调
     * 根据返回的CallbackData的code来判断是否成功（0成功，其他值都是失败），
     * 如果成功，则通过CallbackData的data获取GetGetCompareFingerResult；
     * 如果失败，则通过CallbackData的code来判断失败类型，CallbackData的msg会写明失败原因。
     * @param uniJSCallback
     */
    void setCompareFingerprintCallBack(UniJSCallback uniJSCallback);


    /**
     * 根据人员id清除本地指纹存储信息
     * @param id String
     */
    void clearFingerprintById(String id);

    /**
     * 根据指纹特征值清除本地指纹存储信息
     * @param feature String
     */
    void clearFingerprintByFeature(String feature);

    /**
     * 清空本地所有指纹存储信息
     */
    void clearAllFingerprint();

    /**
     * 指纹模块停止工作：模块断电
     */
    void fingerModuleStop();

    /***************************************************************************/

    /**
     * 启动测温模块（同步方法），进入界面时可调用
     * @return 0：成功，其它值失败
     */
    void syncStartTemperature(UniJSCallback uniJSCallback);

    /**
     *停止测温模块（同步方法），离开界面时可调用
     * @return 0：成功，其它值失败
     */
    void syncStopTemperature(UniJSCallback uniJSCallback);

    /**
     * 测温回调
     * @param uniJSCallback
     * @return 根据返回的CallbackData的code来判断是否成功（0成功，其他值都是失败），
     * 如果成功，则通过CallbackData的data获取TemperatureData，TemperatureData的temperature是浮点型的温度值；
     * 如果失败，则通过CallbackData的code来判断失败类型，CallbackData的msg会写明失败原因。
     */
    void setTemperatureDataCallBack(UniJSCallback uniJSCallback);
    /***************************************************************************/

    /**
     * 设置系统时间
     * @param time 毫秒数
     */
    void setSystemTime(long time);

    /**
     * 媒体音量
     * @param value 0 -100 0为静音
     */
    void setStreamVolumeTypeMusic(int value);
    void getStreamVolumeTypeMusic(UniJSCallback uniJSCallback);

    /**
     *闹钟
     * @param value 0 -100 0为静音
     */
    void setStreamVolumeTypeAlarm(int value);
    void getStreamVolumeTypeAlarm(UniJSCallback uniJSCallback);

    /**
     *铃声
     * @param value 0 -100 0为静音
     */
    void setStreamVolumeTypeRing(int value);
    void getStreamVolumeTypeRing(UniJSCallback uniJSCallback);

    /**
     *系统音量
     * @param value 0 -100 0为静音
     */
    void setStreamVolumeTypeSystem(int value);
    void getStreamVolumeTypeSystem(UniJSCallback uniJSCallback);

    /**
     *通话音量
     * @param value 0 -100 0为静音
     */
    void setStreamVolumeTypeVoiceCall(int value);
    void getStreamVolumeTypeVoiceCall(UniJSCallback uniJSCallback);

    /**
     * 闹铃 4
     * 媒体音量 3
     * 铃声 2
     * 系统音量 1
     * 通话音量 0
     * @param volumeType
     * @param isMute
     */
    void setStreamMute(int volumeType,boolean isMute);

    /***************************************************************************/
    /**
     * 重启设备
     */
    void rebootSystem();

    /***************************************************************************/


}
