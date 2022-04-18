package com.lonbon.floatunibridging;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lb.extend.common.CallbackData;
import com.lb.extend.security.card.CardData;
import com.lb.extend.security.card.SwingCardService;
import com.lb.extend.security.fingerprint.FingerprintService;
import com.lb.extend.security.fingerprint.GetFingerprintFeatureLeftNumResult;
import com.lb.extend.security.fingerprint.GetFingerprintFeatureResult;
import com.lb.extend.security.fingerprint.GetGetCompareFingerResult;
import com.lb.extend.security.intercom.DeviceInfo;
import com.lb.extend.security.intercom.DoorContact;
import com.lb.extend.security.intercom.IntercomService;
import com.lb.extend.security.setting.SystemSettingService;
import com.lb.extend.security.temperature.TemperatureData;
import com.lb.extend.security.temperature.TemperatureMeasurementService;
import com.lb.extend.service.SystemSetService;
import com.zclever.ipc.core.IpcManager;
import com.zclever.ipc.core.Result;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * *****************************************************************************
 * <p>
 * Copyright (C),2007-2016, LonBon Technologies Co. Ltd. All Rights Reserved.
 * <p>
 * *****************************************************************************
 *
 * @ProjectName: LBFloatUniDemo
 * @Package: com.lonbon.floatunibridging
 * @ClassName: FloatUniModule
 * @Author： neo
 * @Create: 2022/4/6
 * @Describe:
 */
public class FloatUniModule extends UniModule implements SettingProviderInterface,IntercomProviderInterface{

    private final String TAG = "FloatUniModule";
    private boolean isConnect = false;
    /**
     * 获取服务类
     */
    private SystemSetService systemSetService;
    private IntercomService intercomService ;
    private SwingCardService swingCardService ;
    private SystemSettingService systemSettingService ;
    private TemperatureMeasurementService temperatureMeasurementService ;
    private FingerprintService fingerprintService ;


    @UniJSMethod(uiThread = false)
    public void initIPCManager(){
        //传入上下文
        IpcManager.INSTANCE.init(mUniSDKInstance.getContext());
        //连接服务端，传入的是服务端的包名
        IpcManager.INSTANCE.open("com.lonbon.lonbon_app", new Function0<Unit>() {
            @Override
            public Unit invoke() {
                isConnect = true;
                Log.d(TAG, "initIPCManager:invoke: 服务链接成功！");
                Toast.makeText(mUniSDKInstance.getContext(), "服务链接成功！", Toast.LENGTH_LONG).show();
                initService();
                return null;
            }
        });

    }

    /**
     * 需要在服务链接成功后才能获取到注册类
     */
    private void initService(){
        systemSetService = IpcManager.INSTANCE.getService(SystemSetService.class);
        intercomService = IpcManager.INSTANCE.getService(IntercomService.class);
        swingCardService = IpcManager.INSTANCE.getService(SwingCardService.class);
        systemSettingService = IpcManager.INSTANCE.getService(SystemSettingService.class);
        temperatureMeasurementService = IpcManager.INSTANCE.getService(TemperatureMeasurementService.class);
        fingerprintService = IpcManager.INSTANCE.getService(FingerprintService.class);
    }


    //run ui thread
    @UniJSMethod(uiThread = true)
    public String printClassNameTest(){
        Toast.makeText(mUniSDKInstance.getContext(), TAG, Toast.LENGTH_SHORT).show();
        return TAG;
    }

    //run JS thread
    @UniJSMethod(uiThread = false)
    public String syncFunc(String methodName){
        Log.d(TAG, "syncFunc: "+methodName);
        Toast.makeText(mUniSDKInstance.getContext(), methodName, Toast.LENGTH_SHORT).show();
        return methodName;
    }
    /*********************************************/


    @UniJSMethod(uiThread = true)
    @Override
    public void setTalkViewPosition(int left, int top, int width, int height) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setTalkViewPosition: ");
        intercomService.setTalkViewPosition(left,top,width,height);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void extDoorLampCtrl(int color, int open) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "extDoorLampCtrl: ");
        intercomService.extDoorLampCtrl(color,open);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void onDoorContactValue(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "onDoorContactValue: ");
        intercomService.onDoorContactValue(new Result<DoorContact>() {
            @Override
            public void onData(DoorContact doorContact) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("doorNum",doorContact.getNum());
                jsonObject.put("isOpen",doorContact.getOpen());
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void asyncGetDeviceListInfo(int areaId, int masterNum, int slaveNum, int devRegType, UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "asyncGetDeviceListInfo: ");
        intercomService.asyncGetDeviceListInfo(areaId,masterNum,slaveNum,devRegType, new Result<ArrayList<DeviceInfo>>() {
            @Override
            public void onData(ArrayList<DeviceInfo> deviceInfos) {
                String gsonString = new Gson().toJson(deviceInfos);
                Log.d(TAG, "onData: "+gsonString);
                uniJsCallback.invokeAndKeepAlive(gsonString);
            }
        });
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void updateDeviceTalkState(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "updateDeviceTalkState: ");
        intercomService.updateDeviceTalkState(new Result<DeviceInfo>() {
            @Override
            public void onData(DeviceInfo deviceInfo) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("areaID",deviceInfo.getAreaID());
                jsonObject.put("masterNum",deviceInfo.getMasterNum());
                jsonObject.put("slaveNum",deviceInfo.getSlaveNum());
                jsonObject.put("childNum",deviceInfo.getChildNum());
                jsonObject.put("devRegType",deviceInfo.getDevRegType());
                jsonObject.put("ip",deviceInfo.getIp());
                jsonObject.put("description",deviceInfo.getDescription());
                jsonObject.put("talkState",deviceInfo.getTalkState());
                jsonObject.put("door1",deviceInfo.getDoorState().size() >= 1);
                jsonObject.put("door2",deviceInfo.getDoorState().size() >= 2);
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void deviceClick(int areaId, int masterNum, int slaveNum, int devRegType) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "deviceClick: ");
        intercomService.masterClickItem(masterNum,slaveNum,areaId,devRegType);
    }

    /**
     * 呼叫对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void nativeCall(int areaId , int masterNum ,int slaveNum ,int devRegType){
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "nativeCall: ");
        intercomService.call(masterNum,slaveNum,areaId,devRegType);

    }

    /**
     * 接听对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void nativeAnswer(int areaId , int masterNum ,int slaveNum ,int devRegType){
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "nativeAnswer: ");
        intercomService.answer(masterNum,slaveNum,areaId,devRegType);

    }

    /**
     * 挂断对讲设备
     * @param areaId 区号ID 最多三位
     * @param masterNum 主机号 最多三位
     * @param slaveNum 分机号 最多三位
     * @param devRegType 设备注册类型 0，主机或这分机，8门口机
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void nativeHangup(int areaId , int masterNum ,int slaveNum ,int devRegType){
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "nativeHangup: ");
        intercomService.hangup(masterNum,slaveNum,areaId,devRegType);

    }


    /*********************************************/


    /**
     * 启动刷卡
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void syncStartCard(UniJSCallback uniJSCallback){
        Log.d(TAG, "syncStartCard: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",0);
            swingCardService.start();
        }
        uniJSCallback.invoke(jsonObject);
    }

    /**
     * 关闭刷卡
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void syncStopCard(UniJSCallback uniJSCallback){
        Log.d(TAG, "syncStopCard: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",0);
            swingCardService.stop();
        }
        uniJSCallback.invoke(jsonObject);
    }

    /**
     * 刷卡回调
     * @param uniJSCallback
     */
    @UniJSMethod(uiThread = false)
    @Override
    public void setCardDataCallBack(UniJSCallback uniJSCallback){
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setCardDataCallBack: ");
        swingCardService.setCardDataCallBack(new Result<CallbackData<CardData>>() {
            @Override
            public void onData(CallbackData<CardData> cardDataCallbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(cardDataCallbackData.getCode()));
                jsonObject.put("msg",cardDataCallbackData.getMsg());
                jsonObject.put("cardNum",cardDataCallbackData.getData().getCardNum());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }
    /**********************************************************************************/

    @UniJSMethod(uiThread = true)
    @Override
    public void syncStartFinger(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStartFinger: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",0);
            fingerprintService.start();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void syncStopFinger(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStopFinger: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",0);
            fingerprintService.stop();
        }
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintCollect(int id,UniJSCallback uniJSCallback) {
        Log.d(TAG, "fingerprintCollect: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",fingerprintService.fingerprintCollect(id));
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintFeatureInput(int id, String feature,UniJSCallback uniJSCallback) {
        Log.d(TAG, "fingerprintFeatureInput: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",fingerprintService.fingerprintFeatureInput(id,feature));
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setGetFingerprintFeatureCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureCallBack: ");
        fingerprintService.setGetFingerprintFeatureCallBack(new Result<CallbackData<GetFingerprintFeatureResult>>() {
            @Override
            public void onData(CallbackData<GetFingerprintFeatureResult> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(callbackData.getCode()));
                jsonObject.put("msg",callbackData.getMsg());
                jsonObject.put("id",String.valueOf(callbackData.getData().getId()));
                jsonObject.put("feature",callbackData.getData().getFeature());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setGetFingerprintFeatureLeftNumCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureLeftNumCallBack: ");
        fingerprintService.setGetFingerprintFeatureLeftNumCallBack(new Result<GetFingerprintFeatureLeftNumResult>() {
            @Override
            public void onData(GetFingerprintFeatureLeftNumResult result) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("leftCounts",String.valueOf(result.getLeftCounts()));
                jsonObject.put("fingerprintBase64Str",result.getFingerprintBase64Str());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setGetCompareFingerprintCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        fingerprintService.setGetCompareFingerprintCallBack(new Result<GetGetCompareFingerResult>() {
            @Override
            public void onData(GetGetCompareFingerResult result) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",String.valueOf(result.getId()));
                jsonObject.put("feature",result.getFeature());
                jsonObject.put("fingerprintBase64Str",result.getFingerprintBase64Str());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }
    /**********************************************************************************/


    @UniJSMethod(uiThread = true)
    @Override
    public void syncStartTemperature(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStartTemperature: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",0);
            temperatureMeasurementService.start();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void syncStopTemperature(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStopTemperature: ");
        JSONObject jsonObject = new JSONObject();
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            jsonObject.put("code",0);
            temperatureMeasurementService.stop();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setTemperatureDataCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setTemperatureDataCallBack: ");
        temperatureMeasurementService.setTemperatureDataCallBack(new Result<CallbackData<TemperatureData>>() {
            @Override
            public void onData(CallbackData<TemperatureData> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(callbackData.getCode()));
                jsonObject.put("msg",callbackData.getMsg());
                jsonObject.put("temperature",String.valueOf(callbackData.getData().getTemperature()));
                Log.d(TAG, "setTemperatureDataCallBack: "+jsonObject.toString());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }
    /**********************************************************************************/

    @UniJSMethod(uiThread = false)
    @Override
    public void setSystemTime(long time) {
        if (!isConnect){
            showToast();
        return ;
        }
        Log.d(TAG, "setSystemTime: "+time);
        systemSettingService.setSystemTime(time);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setStreamVolumeTypeMusic(int value) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_MUSIC);
        audioManagerHelper.setVoice100(value);
        Log.d(TAG, "setValue: "+value);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getStreamVolumeTypeMusic(UniJSCallback uniJSCallback) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_MUSIC);
        int value = audioManagerHelper.get100CurrentVolume();
        Log.d(TAG, "value: "+value);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",value);
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setStreamVolumeTypeAlarm(int value) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_ALARM);
        audioManagerHelper.setVoice100(value);
        Log.d(TAG, "setValue: "+value);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getStreamVolumeTypeAlarm(UniJSCallback uniJSCallback) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_ALARM);
        int value = audioManagerHelper.get100CurrentVolume();
        Log.d(TAG, "value: "+value);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",value);
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setStreamVolumeTypeRing(int value) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_RING);
        audioManagerHelper.setVoice100(value);
        Log.d(TAG, "setValue: "+value);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getStreamVolumeTypeRing(UniJSCallback uniJSCallback) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_RING);
        int value = audioManagerHelper.get100CurrentVolume();
        Log.d(TAG, "value: "+value);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",value);
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setStreamVolumeTypeSystem(int value) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_SYSTEM);
        audioManagerHelper.setVoice100(value);
        Log.d(TAG, "setValue: "+value);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getStreamVolumeTypeSystem(UniJSCallback uniJSCallback) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_SYSTEM);
        int value = audioManagerHelper.get100CurrentVolume();
        Log.d(TAG, "value: "+value);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",value);
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setStreamVolumeTypeVoiceCall(int value) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_VOICE_CALL);
        audioManagerHelper.setVoice100(value);
        Log.d(TAG, "setValue: "+value);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getStreamVolumeTypeVoiceCall(UniJSCallback uniJSCallback) {
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setAudioType(AudioManagerHelper.TYPE_VOICE_CALL);
        int value = audioManagerHelper.get100CurrentVolume();
        Log.d(TAG, "value: "+value);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",value);
        uniJSCallback.invoke(jsonObject);
    }

    /**********************************************************************************/

    private void showToast(){
        Toast.makeText(mUniSDKInstance.getContext(), "连接服务中，请稍后！", Toast.LENGTH_LONG).show();
    }
    /**********************************************************************************/
}
