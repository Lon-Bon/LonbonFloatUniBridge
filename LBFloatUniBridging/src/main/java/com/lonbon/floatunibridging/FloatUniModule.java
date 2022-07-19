package com.lonbon.floatunibridging;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lb.extend.common.CallbackData;
import com.lb.extend.security.card.CardData;
import com.lb.extend.security.card.SwingCardService;
import com.lb.extend.security.fingerprint.FingerprintCompareResult;
import com.lb.extend.security.fingerprint.FingerprintFeatureResult;
import com.lb.extend.security.fingerprint.FingerprintLeftNumResult;
import com.lb.extend.security.fingerprint.FingerprintService;
import com.lb.extend.security.intercom.DeviceInfo;
import com.lb.extend.security.intercom.DoorContact;
import com.lb.extend.security.intercom.IntercomService;
import com.lb.extend.security.intercom.LocalDeviceInfo;
import com.lb.extend.security.intercom.TalkEvent;
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
    public void initIPCManager(UniJSCallback uniJsCallback){
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",0);
                uniJsCallback.invoke(jsonObject);
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
    @UniJSMethod(uiThread = true)
    @Override
    public void openLockCtrl(int num, int open) {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.openLockCtrl(num,open);

    }
    @UniJSMethod(uiThread = false)
    @Override
    public void getCurrentDeviceInfo(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.getCurrentDeviceInfo(new Result<LocalDeviceInfo>() {
            @Override
            public void onData(LocalDeviceInfo localDeviceInfo) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("deviceName",localDeviceInfo.getDeviceName());
                jsonObject.put("deviceModel",localDeviceInfo.getDeviceModel());
                jsonObject.put("customizedModel",localDeviceInfo.getCustomizedModel());
                jsonObject.put("hardwareVersion",localDeviceInfo.getHardwareVersion());
                jsonObject.put("NKVersion",localDeviceInfo.getNKVersion());
                jsonObject.put("modelCode",localDeviceInfo.getModelCode());
                jsonObject.put("platform",localDeviceInfo.getPlatform());
                jsonObject.put("account",localDeviceInfo.getAccount());
                jsonObject.put("password",localDeviceInfo.getPassword());
                jsonObject.put("encPassword",localDeviceInfo.getEncPassword());
                jsonObject.put("sipPort",localDeviceInfo.getSipPort());
                jsonObject.put("sn",localDeviceInfo.getSn());
                jsonObject.put("mac",localDeviceInfo.getMac());
                jsonObject.put("ip",localDeviceInfo.getIp());

                jsonObject.put("gateway",localDeviceInfo.getGateway());
                jsonObject.put("netmask",localDeviceInfo.getNetmask());
                jsonObject.put("isAllowSDRecording",localDeviceInfo.isAllowSDRecording());
                jsonObject.put("manufactoryType",localDeviceInfo.getManufactoryType());
                jsonObject.put("paymentTermCode",localDeviceInfo.getPaymentTermCode());
                jsonObject.put("produceTime",localDeviceInfo.getProduceTime());
                jsonObject.put("displayNum",localDeviceInfo.getDisplayNum());
                jsonObject.put("masterNum",localDeviceInfo.getMasterNum());
                jsonObject.put("slaveNum",localDeviceInfo.getSlaveNum());
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });

    }
    @UniJSMethod(uiThread = false)
    @Override
    public void talkEventCallback(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.talkEventCallback(new Result<TalkEvent>() {
            @Override
            public void onData(TalkEvent talkEvent) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("eventID",talkEvent.getEventID());

                jsonObject.put("areaID",talkEvent.getDeviceInfo().getAreaID());
                jsonObject.put("masterNum",talkEvent.getDeviceInfo().getMasterNum());
                jsonObject.put("slaveNum",talkEvent.getDeviceInfo().getSlaveNum());
                jsonObject.put("childNum",talkEvent.getDeviceInfo().getChildNum());
                jsonObject.put("devRegType",talkEvent.getDeviceInfo().getDevRegType());
                jsonObject.put("ip",talkEvent.getDeviceInfo().getIp());
                jsonObject.put("description",talkEvent.getDeviceInfo().getDescription());
                jsonObject.put("talkState",talkEvent.getDeviceInfo().getTalkState());
                jsonObject.put("door1",talkEvent.getDeviceInfo().getDoorState().size() >= 1);
                jsonObject.put("door2",talkEvent.getDeviceInfo().getDoorState().size() >= 2);
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });

    }
    @UniJSMethod(uiThread = false)
    @Override
    public void onDeviceOnLine(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.onDeviceOnLine(new Result<DeviceInfo>() {
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
    @UniJSMethod(uiThread = false)
    @Override
    public void onDeviceOffLine(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.onDeviceOnLine(new Result<DeviceInfo>() {
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
    public void listenToTalk() {
        if (!isConnect){
            showToast();
            return ;
        }

        intercomService.listenToTalk();
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void hideTalkView(Boolean hide) {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.hideTalkView(hide);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void oneKeyCall() {
        if (!isConnect){
            showToast();
            return ;
        }
        intercomService.oneKeyCall();

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
            fingerprintService.init();
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
    public void fingerprintCollect(String id) {
        Log.d(TAG, "fingerprintCollect: ");
        fingerprintService.fingerprintCollect(id);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintFeatureInput(String id, String feature) {
        Log.d(TAG, "fingerprintFeatureInput: ");
        fingerprintService.fingerprintFeatureInput(id,feature);
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setFingerprintFeatureCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureCallBack: ");
        fingerprintService.setFingerprintFeatureCallBack(new Result<CallbackData<FingerprintFeatureResult>>() {
            @Override
            public void onData(CallbackData<FingerprintFeatureResult> callbackData) {
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
    public void setFingerprintFeatureLeftNumCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureLeftNumCallBack: ");
        fingerprintService.setFingerprintLeftNumCallBack(new Result<CallbackData<FingerprintLeftNumResult>>() {
            @Override
            public void onData(CallbackData<FingerprintLeftNumResult> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("leftCounts",String.valueOf(callbackData.getData().getLeftCounts()));
                jsonObject.put("fingerprintBase64Str",callbackData.getData().getFingerprintBase64Str());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = false)
    @Override
    public void setCompareFingerprintCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        fingerprintService.setFingerprintCompareCallBack(new Result<CallbackData<FingerprintCompareResult>>() {
            @Override
            public void onData(CallbackData<FingerprintCompareResult> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",callbackData.getData().getId());
                jsonObject.put("feature",callbackData.getData().getFeature());
                jsonObject.put("fingerprintBase64Str",callbackData.getData().getFingerprintBase64Str());
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
