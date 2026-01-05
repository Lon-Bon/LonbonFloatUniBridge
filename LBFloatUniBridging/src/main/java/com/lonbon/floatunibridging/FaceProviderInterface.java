package com.lonbon.floatunibridging;

import java.util.Map;

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
 * @ClassName: FaceProviderInterface
 * @Author： chenbin
 * @Create: 2025/9/11
 * @Describe: 人脸相关接口
 */
public interface FaceProviderInterface {

    /**
     * 开始录入，跳转录入页面
     */
    void startEnroll(String code);

    /**
     * 关闭录入，关闭录入页面
     */
    void closeEnroll();

    /**
     * 开始识别，跳转识别页面
     */
    void startCompare();

    /**
     * 关闭识别，关闭识别页面
     */
    void closeCompare();

    /**
     * 通过图片录入人脸
     */
    void enrollFaceByImg(String imgPath, String code);

    /**
     * 识别一次
     */
    void compareFace();

    /**
     * 删除人脸，为空则清空人脸数据
     */
    void deleteFace(String code);

    /**
     * 配置识别框位置大小
     */
    void configureScanRectF(
            Float leftPercent,
            Float topPercent,
            Float widthPercent,
            Float heightPercent
    );

    /**
     * 开始无界面识别
     */
    void startCompareWithParams(boolean openPreview);

    /**
     * 配置无界面人预览框
     */
    void configFacePreview(int left, int top, int width, int height, boolean hideBoolean);

    /**
     * 获取已录入人脸编号列表
     */
    void getFaceCodesList();

    /**
     * 批量录入人脸图片
     */
    void enrollFaceByImgBatch(Map<String, String> enrollMap);

    /**
     * 开关人脸活体检测
     */
    void switchFaceLive(int open);

    /**
     * 开关人脸识别提示词
     */
    void switchFaceVerifyHint(int open);

    /**
     * 通过图片识别人脸
     */
    void faceVerifyByImg(String imgPath);

    //回调相关

    /**
     * 开启人脸识别页面回调，页面开启成功后可以调用compareFace进行识别
     * @param uniJSCallback
     */
    void setOnStartCompare(UniJSCallback uniJSCallback);

    /**
     * 关闭人脸识别回调
     * @param uniJSCallback
     */
    void setOnCloseCompare(UniJSCallback uniJSCallback);

    /**
     * 识别一次回调
     * @param uniJSCallback
     */
    void setOnFaceCompare(UniJSCallback uniJSCallback);

    /**
     * 人脸录入页面结果回调
     * @param uniJSCallback
     */
    void setOnFaceEnrollByCamera(UniJSCallback uniJSCallback);

    /**
     * 图片录入结果回调
     * @param uniJSCallback
     */
    void setOnFaceEnrollByImg(UniJSCallback uniJSCallback);

    /**
     * 删除人脸回调
     * @param uniJSCallback
     */
    void setOnFaceDelete(UniJSCallback uniJSCallback);

    /**
     * 获取已录入人脸列表回调
     * @param uniJSCallback
     */
    void setOnGetFaceCodesListCallBack(UniJSCallback uniJSCallback);

    /**
     * 批量录入人脸图片回调
     * @param uniJSCallback
     */
    void setOnFaceEnrollByImgBatch(UniJSCallback uniJSCallback);

    /**
     * 图片识别结果回调
     * @param uniJSCallback
     */
    void setOnFaceVerifyByImg(UniJSCallback uniJSCallback);


}
