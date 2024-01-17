package com.lonbon.floatunibridging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lb.extend.common.CallbackData;
import com.lb.extend.security.broadcast.AreaDivision;
import com.lb.extend.security.broadcast.IBroadcastService;
import com.lb.extend.security.broadcast.SpeakBroadcastState;
import com.lb.extend.security.card.CardData;
import com.lb.extend.security.card.SwingCardService;
import com.lb.extend.security.education.EducationService;
import com.lb.extend.security.education.EducationTaskStateBean;
import com.lb.extend.security.fingerprint.FingerprintCompareResult;
import com.lb.extend.security.fingerprint.FingerprintFeatureResult;
import com.lb.extend.security.fingerprint.FingerprintLeftNumResult;
import com.lb.extend.security.fingerprint.FingerprintService;
import com.lb.extend.security.intercom.DeviceInfo;
import com.lb.extend.security.intercom.DoorContact;
import com.lb.extend.security.intercom.IntercomService;
import com.lb.extend.security.intercom.LocalDeviceInfo;
import com.lb.extend.security.intercom.MasterDeviceInfo;
import com.lb.extend.security.intercom.TalkEvent;
import com.lb.extend.security.setting.SystemSettingService;
import com.lb.extend.security.temperature.TemperatureData;
import com.lb.extend.security.temperature.TemperatureMeasurementService;
import com.lb.extend.service.ILonbonService;
import com.zclever.ipc.core.Config;
import com.zclever.ipc.core.IpcManager;
import com.zclever.ipc.core.Result;
import com.zclever.ipc.core.client.FrameType;
import com.zclever.ipc.core.client.IPictureCallBack;
import com.zclever.ipc.core.client.IPreviewCallBack;
import com.zclever.ipc.core.client.PictureFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class FloatUniModule extends UniModule implements SettingProviderInterface,IntercomProviderInterface,EducationProviderInterface{

    private final String TAG = "FloatUniModule";

    @UniJSMethod(uiThread = true)
    public void initIPCManager(UniJSCallback uniJsCallback){

//        //首先配置开启媒体服务
        IpcManager.INSTANCE.config(Config.Companion.builder().configOpenMedia(true).build());
        //传入上下文
        Log.d(TAG, "initIPCManager: mUniSDKInstance.getContext()！" + mUniSDKInstance.getContext());
        IpcManager.INSTANCE.init(mUniSDKInstance.getContext());
        IpcManager.INSTANCE.setServerDeath(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                AppStartReceiver.isConnect = false;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",1);
                uniJsCallback.invoke(jsonObject);
                Log.d(TAG, "initIPCManager:serverDeath: 服务链接断开！");
                return null;
            }
        });
        if (!AppStartReceiver.hasStartExecutor) {
            AppStartReceiver.hasStartExecutor = true;
            Log.i(TAG, "initIPCManager:start singleThreadScheduledExecutor");
            //不断重连，每秒重连一次，防止掉线
            AppStartReceiver.singleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "initIPCManager:scheduleWithFixedDelay");
                        if (!AppStartReceiver.isConnect) {
                            Log.i(TAG, "initIPCManager:scheduleWithFixedDelay  1111");
                            //连接服务端，传入的是服务端的包名
                            IpcManager.INSTANCE.open("com.lonbon.lonbon_app", new Function0<Unit>() {
                                @Override
                                public Unit invoke() {
                                    AppStartReceiver.isConnect = true;
                                    initService();
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("code", 0);
                                    uniJsCallback.invoke(jsonObject);
                                    Log.d(TAG, "initIPCManager:invoke: 服务链接成功！");
                                    Toast.makeText(mUniSDKInstance.getContext(), "服务链接成功！", Toast.LENGTH_LONG).show();
                                    return null;
                                }
                            });
                            Log.i(TAG, "initIPCManager:scheduleWithFixedDelay  2222");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "initIPCManager:scheduleWithFixedDelay: 3 " + e.getMessage());
                    }
                }
            }, 0, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * 需要在服务链接成功后才能获取到注册类
     */
    private void initService(){
        AppStartReceiver.iLonbonService = IpcManager.INSTANCE.getService(ILonbonService.class);
        AppStartReceiver.intercomService = IpcManager.INSTANCE.getService(IntercomService.class);
        AppStartReceiver.swingCardService = IpcManager.INSTANCE.getService(SwingCardService.class);
        AppStartReceiver.systemSettingService = IpcManager.INSTANCE.getService(SystemSettingService.class);
        AppStartReceiver.temperatureMeasurementService = IpcManager.INSTANCE.getService(TemperatureMeasurementService.class);
        AppStartReceiver.fingerprintService = IpcManager.INSTANCE.getService(FingerprintService.class);
        AppStartReceiver.educationService = IpcManager.INSTANCE.getService(EducationService.class);
        AppStartReceiver.broadcastService = IpcManager.INSTANCE.getService(IBroadcastService.class);
    }


    //run ui thread
    @UniJSMethod(uiThread = true)
    public void printDeviceInfoTest(UniJSCallback uniJsCallback){
        Log.d(TAG, "printDeviceInfoTest");
        Toast.makeText(mUniSDKInstance.getContext(), TAG, Toast.LENGTH_SHORT).show();
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "printDeviceInfoTest: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.getCurrentDeviceInfo(new Result<LocalDeviceInfo>() {
            @Override
            public void onData(LocalDeviceInfo localDeviceInfo) {
                Toast.makeText(mUniSDKInstance.getContext(), localDeviceInfo.toString(), Toast.LENGTH_SHORT).show();
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

    //run JS thread
    @UniJSMethod(uiThread = true)
    public void printInputTest(String methodName,UniJSCallback uniJsCallback){
        Log.d(TAG, "printInputTest: "+methodName);
        Toast.makeText(mUniSDKInstance.getContext(), methodName, Toast.LENGTH_SHORT).show();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("printInputTest",methodName);
        uniJsCallback.invoke(jsonObject);
    }
    /*********************************************/


    @UniJSMethod(uiThread = true)
    @Override
    public void setTalkViewPosition(int left, int top, int width, int height) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "setTalkViewPosition: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.setTalkViewPosition(left,top,width,height);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void extDoorLampCtrl(int color, int open) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "extDoorLampCtrl: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "extDoorLampCtrl: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.extDoorLampCtrl(color,open);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void onDoorContactValue(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "onDoorContactValue: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "onDoorContactValue: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.onDoorContactValue(new Result<DoorContact>() {
            @Override
            public void onData(DoorContact doorContact) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("doorNum",doorContact.getNum());
                jsonObject.put("isOpen",doorContact.getOpen());
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void asyncGetDeviceListInfo(int areaId, int masterNum, int slaveNum, int devRegType, UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "asyncGetDeviceListInfo: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "asyncGetDeviceListInfo: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.asyncGetDeviceListInfo(areaId,masterNum,slaveNum,devRegType, new Result<ArrayList<DeviceInfo>>() {
            @Override
            public void onData(ArrayList<DeviceInfo> deviceInfos) {
                String gsonString = new Gson().toJson(deviceInfos);
                Log.d(TAG, "onData: "+gsonString);
                uniJsCallback.invokeAndKeepAlive(gsonString);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void updateDeviceTalkState(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "updateDeviceTalkState: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "updateDeviceTalkState: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.updateDeviceTalkState(new Result<DeviceInfo>() {
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
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "deviceClick: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "deviceClick: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.masterClickItem(masterNum,slaveNum,areaId,devRegType);
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
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "nativeCall: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "nativeCall: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.call(masterNum,slaveNum,areaId,devRegType);

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
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "nativeAnswer: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "nativeAnswer: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.answer(masterNum,slaveNum,areaId,devRegType);

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
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "nativeHangup: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "nativeHangup: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.hangup(masterNum,slaveNum,areaId,devRegType);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void openLockCtrl(int num, int open) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "openLockCtrl: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.openLockCtrl(num,open);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getCurrentDeviceInfo(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "getCurrentDeviceInfo: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.getCurrentDeviceInfo(new Result<LocalDeviceInfo>() {
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
    @UniJSMethod(uiThread = true)
    @Override
    public void talkEventCallback(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "talkEventCallback: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.talkEventCallback(new Result<TalkEvent>() {
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
    @UniJSMethod(uiThread = true)
    @Override
    public void onDeviceOnLine(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "onDeviceOnLine: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.onDeviceOnLine(new Result<DeviceInfo>() {
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
    public void onDeviceOffLine(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "onDeviceOffLine: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.onDeviceOffLine(new Result<DeviceInfo>() {
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
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "listenToTalk: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.listenToTalk();
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void hideTalkView(Boolean hide) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "hideTalkView: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.hideTalkView(hide);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void oneKeyCall() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "oneKeyCall: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.oneKeyCall();

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void setLocalVideoViewPosition(int left, int top, int width, int height) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "setLocalVideoViewPosition: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.setPreViewPosition(left,top,width,height);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void hideLocalPreView(Boolean hide) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "hideLocalPreView: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.hidePreView(hide);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void setExtMicEna(Boolean enable) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "setExtMicEna: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.setMicEna(enable);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void openLocalCamera(Boolean isOpen) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "openLocalCamera: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.openLocalCamera(isOpen);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void initFrame() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "openLocalCamera: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.initFrame();
    }

    private int width = 0;
    private int height = 0;
    @UniJSMethod(uiThread = true)
    @Override
    public void setViewWidthHeight(int width, int height) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "openLocalCamera: AppStartReceiver.intercomService is null !");
            return;
        }
        this.width = width;
        this.height = height;
        AppStartReceiver.intercomService.setViewWidthHeight(width,height);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void startTakeFrame() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "startTakeFrame: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.startTakeFrame();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void stopTakeFrame() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        IpcManager.INSTANCE.getMediaService().stopTakeFrame();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void takePicture() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        IpcManager.INSTANCE.getMediaService().takePicture(PictureFormat.JPEG);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void takeFrame() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }

        IpcManager.INSTANCE.getMediaService().takeFrame(FrameType.NV21);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void takePictureCallBack(UniJSCallback uniJsCallback) {
        IpcManager.INSTANCE.getMediaService().setPictureCallBack(new IPictureCallBack() {
            @Override
            public void onPictureTaken(@Nullable byte[] bytes, int i, int i1, @NonNull PictureFormat pictureFormat) {
                Log.i(TAG, "takePictureCallBack: "+Arrays.toString(bytes));
                Log.i(TAG, "takePictureCallBack: "+pictureFormat);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("bytes",getBitmapBase64(bytes,width,height));
                jsonObject.put("pictureFormat",pictureFormat);
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void takeFrameCallBack(UniJSCallback uniJsCallback) {
        IpcManager.INSTANCE.getMediaService().setPreviewCallBack(new IPreviewCallBack() {
            @Override
            public void onPreviewFrame(@Nullable byte[] bytes, int i, int i1, @NonNull FrameType frameType) {
                Log.i(TAG, "takeFrameCallBack: "+ Arrays.toString(bytes));
                Log.i(TAG, "takeFrameCallBack: "+frameType);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("bytes",getBitmapBase64(bytes,width,height));
                jsonObject.put("frameType",frameType);
                uniJsCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    /**
     * 设置通话记录文件存储路径
     * @param path
     * @param uniJsCallback
     */
    @UniJSMethod(uiThread = false)
    @Override
    public void setRecordPath(String path, UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "setRecordPath: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.setRecordPath(path, new Result<String>() {
            @Override
            public void onData(String s) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("url",s);
                uniJsCallback.invoke(jsonObject);
            }
        });
    }

    /**
     * 获取该路径下的文件
     * @param path
     * @param uniJsCallback
     */
    @UniJSMethod(uiThread = false)
    @Override
    public void getFileList(String path, UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "getFileList: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.getFileList(path, new Result<ArrayList<File>>() {
            @Override
            public void onData(ArrayList<File> files) {
                List<JsonFile> jsonList = new ArrayList<>();
                for(File file:files){
                    boolean hasChildFile = file.listFiles() != null && file.listFiles().length > 0;
                    jsonList.add(new JsonFile(
                            file.getPath(),file.getName(),file.length(),file.isDirectory(),hasChildFile
                    ));
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("filesJson",new Gson().toJson(jsonList));
                uniJsCallback.invoke(jsonObject);
            }
        });
    }

    /**
     * 删除文件
     * @param path
     * @param uniJsCallback
     */
    @UniJSMethod(uiThread = false)
    @Override
    public void deleteFile(String path, UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "deleteFile: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.deleteFile(path, new Result<Boolean>() {
            @Override
            public void onData(Boolean isSuccess) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isSuccess",isSuccess);
                uniJsCallback.invoke(jsonObject);
            }
        });

    }

    /**
     * 主机控制分机通话音量
     * @param volume - 范围 0-5
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void setSlaveVolume(int volume) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setSlaveVolume: " + volume);
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "setSlaveVolume: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.setSlaveTalkVolume(volume);
    }

    /**
     * 主机获取分机通话音量（同步方法）
     * @return 0：成功，其它值失败
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void syncGetSlaveVolume(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncGetSlaveVolume: ");
        JSONObject jsonObject = new JSONObject();
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("slaveVolume", 3);
        }else {
            if (AppStartReceiver.intercomService == null){
                Log.d(TAG, "syncGetSlaveVolume: AppStartReceiver.intercomService is null !");
                jsonObject.put("slaveVolume", 3);
                return;
            }
            jsonObject.put("slaveVolume", AppStartReceiver.intercomService.getSlaveTalkVolume());
        }
        uniJSCallback.invoke(jsonObject);
    }

    /**
     * 图片转换为Base64字符串
     * @param curFaceNV21Data
     * @param width
     * @param height
     * @return
     */
    private String getBitmapBase64(byte[] curFaceNV21Data, int width, int height){
        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = getBitmapFromYuv(curFaceNV21Data,width,height);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    /**
     * @param curFaceNV21Data
     * @param width
     * @param height
     * @return
     */
    public Bitmap getBitmapFromYuv(byte[] curFaceNV21Data, int width, int height) {

        Bitmap bmp = null;

        try {
            YuvImage image = new YuvImage(curFaceNV21Data, ImageFormat.NV21, width, height, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);

                bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                //TODO：此处可以对位图进行处理，如显示，保存等

                stream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bmp;
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
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            if (AppStartReceiver.swingCardService == null){
                Log.d(TAG, "syncStartCard: AppStartReceiver.swingCardService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            AppStartReceiver.swingCardService.start();
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
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {

            if (AppStartReceiver.swingCardService == null){
                Log.d(TAG, "syncStopCard: AppStartReceiver.swingCardService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            AppStartReceiver.swingCardService.stop();
        }
        uniJSCallback.invoke(jsonObject);
    }

    /**
     * 刷卡回调
     * @param uniJSCallback
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void setCardDataCallBack(UniJSCallback uniJSCallback){
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setCardDataCallBack: ");
        if (AppStartReceiver.swingCardService == null){
            Log.d(TAG, "setCardDataCallBack: AppStartReceiver.swingCardService is null !");
            return;
        }
        AppStartReceiver.swingCardService.setCardDataCallBack(new Result<CallbackData<CardData>>() {
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
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            if (AppStartReceiver.fingerprintService == null){
                Log.d(TAG, "syncStartFinger: AppStartReceiver.fingerprintService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            AppStartReceiver.fingerprintService.init();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void syncStopFinger(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStopFinger: ");
        JSONObject jsonObject = new JSONObject();
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            if (AppStartReceiver.fingerprintService == null){
                Log.d(TAG, "syncStopFinger: AppStartReceiver.fingerprintService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            AppStartReceiver.fingerprintService.stop();
        }
        uniJSCallback.invoke(jsonObject);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void fingerModuleStop() {
        Log.d(TAG, "fingerModuleStop: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "fingerModuleStop: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.destroy();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintCollect(String id) {
        Log.d(TAG, "fingerprintCollect: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "fingerprintCollect: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.fingerprintCollect(id);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintRecognition() {
        Log.d(TAG, "fingerprintRecognition: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "fingerprintRecognition: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.fingerprintRecognition();

    }


    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintFeatureInput(String id, String feature) {
        Log.d(TAG, "fingerprintFeatureInput: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "fingerprintFeatureInput: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.fingerprintFeatureInput(id,feature);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setFingerprintFeatureCallBack(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureCallBack: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "setFingerprintFeatureCallBack: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.setFingerprintFeatureCallBack(new Result<CallbackData<FingerprintFeatureResult>>() {
            @Override
            public void onData(CallbackData<FingerprintFeatureResult> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(callbackData.getCode()));
                jsonObject.put("msg",callbackData.getMsg());
                jsonObject.put("id", callbackData.getData().getId());
                jsonObject.put("feature",callbackData.getData().getFeature());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setFingerprintFeatureLeftNumCallBack(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureLeftNumCallBack: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "setFingerprintFeatureLeftNumCallBack: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.setFingerprintLeftNumCallBack(new Result<CallbackData<FingerprintLeftNumResult>>() {
            @Override
            public void onData(CallbackData<FingerprintLeftNumResult> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(callbackData.getCode()));
                jsonObject.put("msg",callbackData.getMsg());
                jsonObject.put("leftCounts",String.valueOf(callbackData.getData().getLeftCounts()));
                jsonObject.put("fingerprintBase64Str",callbackData.getData().getFingerprintBase64Str());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setCompareFingerprintCallBack(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.setFingerprintCompareCallBack(new Result<CallbackData<FingerprintCompareResult>>() {
            @Override
            public void onData(CallbackData<FingerprintCompareResult> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(callbackData.getCode()));
                jsonObject.put("msg",callbackData.getMsg());
                jsonObject.put("id",callbackData.getData().getId());
                jsonObject.put("feature",callbackData.getData().getFeature());
                jsonObject.put("fingerprintBase64Str",callbackData.getData().getFingerprintBase64Str());
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    /**
     * 根据人员id清除本地指纹存储信息
     * @param id String
     */

    @UniJSMethod(uiThread = true)
    @Override
    public void clearFingerprintById(String id) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: AppStartReceiver.fingerprintService is null !");
            return;
        }

        AppStartReceiver.fingerprintService.clearFingerprintById(id);
    }

    /**
     * 根据指纹特征值清除本地指纹存储信息
     * @param feature String
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void clearFingerprintByFeature(String feature) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.clearFingerprintByFeature(feature);
    }

    /**
     * 清空本地所有指纹存储信息
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void clearAllFingerprint() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (AppStartReceiver.fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: AppStartReceiver.fingerprintService is null !");
            return;
        }
        AppStartReceiver.fingerprintService.clearAllFingerprint();
    }
    /**********************************************************************************/


    @UniJSMethod(uiThread = true)
    @Override
    public void syncStartTemperature(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStartTemperature: ");
        JSONObject jsonObject = new JSONObject();
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {

            if (AppStartReceiver.temperatureMeasurementService == null){
                Log.d(TAG, "syncStartTemperature: AppStartReceiver.temperatureMeasurementService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            AppStartReceiver.temperatureMeasurementService.start();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void syncStopTemperature(UniJSCallback uniJSCallback) {
        Log.d(TAG, "syncStopTemperature: ");
        JSONObject jsonObject = new JSONObject();
        if (!AppStartReceiver.isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {

            if (AppStartReceiver.temperatureMeasurementService == null){
                Log.d(TAG, "syncStopTemperature: AppStartReceiver.temperatureMeasurementService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            AppStartReceiver.temperatureMeasurementService.stop();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setTemperatureDataCallBack(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setTemperatureDataCallBack: ");
        if (AppStartReceiver.temperatureMeasurementService == null){
            Log.d(TAG, "setTemperatureDataCallBack: AppStartReceiver.temperatureMeasurementService is null !");
            return;
        }
        AppStartReceiver.temperatureMeasurementService.setTemperatureDataCallBack(new Result<CallbackData<TemperatureData>>() {
            @Override
            public void onData(CallbackData<TemperatureData> callbackData) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",String.valueOf(callbackData.getCode()));
                jsonObject.put("msg",callbackData.getMsg());
                jsonObject.put("temperature",String.valueOf(callbackData.getData().getTemperature()));
                Log.d(TAG, "setTemperatureDataCallBack: "+ jsonObject);
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }
    /**********************************************************************************/

    @UniJSMethod(uiThread = true)
    @Override
    public void setSystemTime(long time) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setSystemTime: "+time);
        if (AppStartReceiver.systemSettingService == null){
            Log.d(TAG, "setSystemTime: AppStartReceiver.systemSettingService is null !");
            return;
        }
        AppStartReceiver.systemSettingService.setSystemTime(time);
    }

    @UniJSMethod(uiThread = true)
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

    @UniJSMethod(uiThread = true)
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

    @UniJSMethod(uiThread = true)
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

    @UniJSMethod(uiThread = true)
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

    @UniJSMethod(uiThread = true)
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

    @UniJSMethod(uiThread = true)
    @Override
    public void setStreamMute(int volumeType, boolean isMute){
        AudioManagerHelper audioManagerHelper = new AudioManagerHelper(mUniSDKInstance.getContext());
        audioManagerHelper.setStreamMute(volumeType,isMute);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void rebootSystem() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "rebootSystem: ");
        if (AppStartReceiver.systemSettingService == null){
            Log.d(TAG, "rebootSystem: AppStartReceiver.systemSettingService is null !");
            return;
        }
        AppStartReceiver.systemSettingService.rebootSystem();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void openGuard(int isOpen) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "openGuard: isOpen："+isOpen);
        if (AppStartReceiver.iLonbonService == null){
            Log.d(TAG, "openGuard: AppStartReceiver.iLonbonService is null !");
            return;
        }
        AppStartReceiver.iLonbonService.openGuard(isOpen == 1);

    }

    /***********************************电教相关***********************************************/

    @UniJSMethod(uiThread = true)
    @Override
    public void initEducation() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "initEducation: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "initEducation: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.init();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void enterLiveShow() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "enterLiveShow: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "enterLiveShow: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.showEducation();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void exitLiveShow() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "exitLiveShow: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "exitLiveShow: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.exitEducation();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void syncGetEducationTaskList(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "syncGetEducationTaskList: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "syncGetEducationTaskList: AppStartReceiver.educationService is null !");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("educationTaskList", new Gson().toJson(AppStartReceiver.educationService.getEducationTask()));
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void enterEducationTask() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "enterEducationTask: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "enterEducationTask: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.showEducationTask();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void exitEducationTask() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "exitEducationTask: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "exitEducationTask: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.exitEducationTask();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setEducationStateListener(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setEducationStateListener: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "setEducationStateListener: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.setEducationTaskStateListener(new Result<EducationTaskStateBean>() {
            @Override
            public void onData(EducationTaskStateBean educationTaskStateBean) {
                Log.d(TAG, "educationTaskStateBean: " + educationTaskStateBean);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("educationTaskStateBean", new Gson().toJson(educationTaskStateBean));
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void hdmiOpen(int outputConfigure) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "hdmiOpen: " + outputConfigure);
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "hdmiOpen: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.setHDMIConfigure(outputConfigure);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void audioSyncOutput(int enable) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "audioSyncOutput: " + enable);
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "audioSyncOutput: AppStartReceiver.educationService is null  !");
            return;
        }
        AppStartReceiver.educationService.setAudioSyncOutput(enable);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setHdmiStatusListener(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setHdmiStatusListener: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "setHdmiStatusListener: AppStartReceiver.educationService is null !");
            return;
        }
        AppStartReceiver.educationService.setHdmiStatusListener(new Result<Boolean>() {
            @Override
            public void onData(Boolean aBoolean) {
                Log.d(TAG, "hdmiStatusListener: " + aBoolean);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hdmiStatus", aBoolean);
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void syncGetHdmiStatus(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "syncGetHdmiStatus: ");
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "syncGetHdmiStatus: AppStartReceiver.educationService is null !");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hdmiStatus", AppStartReceiver.educationService.getHdmiStatus());
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void hornControlSwitch(boolean isOpen) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "hornControlSwitch: " + isOpen);
        if (AppStartReceiver.educationService == null){
            Log.d(TAG, "hornControlSwitch: AppStartReceiver.educationService is null  !");
            return;
        }
        AppStartReceiver.educationService.setHornControlSwitch(isOpen);
    }

    /***********************************广播相关***********************************************/

    @UniJSMethod(uiThread = true)
    @Override
    public void initBroadcast() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "initBroadcast: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "initBroadcast: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.broadcastService.initSpeakBroadcast();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setOnIONotifyListener(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setOnIONotifyListener: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "setOnIONotifyListener: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.broadcastService.onIONotifyListener(new Result<Integer>() {
            @Override
            public void onData(Integer data) {
                Log.d(TAG, "onData: " + data);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ioState", data);
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setOnSpeakBroadcastListener(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setOnSpeakBroadcastListener: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "setOnSpeakBroadcastListener: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.broadcastService.onSpeakBroadcastListener(new Result<SpeakBroadcastState>() {
            @Override
            public void onData(SpeakBroadcastState data) {
                Log.d(TAG, "onData: " + data);
                int event = data.getEvent();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event", event);
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setOnToastListener(UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setOnToastListener: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "setOnToastListener: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.broadcastService.onToastListener(new Result<String>() {
            @Override
            public void onData(String s) {
                Log.d(TAG, "onToastListener" + s);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("toast", s);
                uniJSCallback.invokeAndKeepAlive(jsonObject);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void addBroadcastObj(int num, UniJSCallback uniJSCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "addBroadcastObj: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "addBroadcastObj: AppStartReceiver.broadcastService is null !");
            return;
        }

        if (num < 1000){
            Log.d(TAG, "addBroadcastObj: num must bigger than 1000 !");
            return;
        }
        AreaDivision areaDivision = new AreaDivision();
        areaDivision.setDisplayNum(num);
        areaDivision.setMasterNum(num / 1000);
        if (!AppStartReceiver.areaDivisionArrayList.contains(areaDivision)) {
            AppStartReceiver.areaDivisionArrayList.add(areaDivision);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcastDevices", new Gson().toJson(AppStartReceiver.areaDivisionArrayList));
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void clearBroadcastObj() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "addBroadcastObj: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "addBroadcastObj: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.areaDivisionArrayList.clear();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setSpeakBroadcastDevice() {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setSpeakBroadcastDevice: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "setSpeakBroadcastDevice: AppStartReceiver.broadcastService is null !");
            return;
        }
        for (int i = 0; i < AppStartReceiver.areaDivisionArrayList.size(); i++) {
            Log.d(TAG, "setSpeakBroadcastDevice: " + AppStartReceiver.areaDivisionArrayList.get(i));
        }
        AppStartReceiver.broadcastService.setSpeakBroadcastDevice(AppStartReceiver.areaDivisionArrayList);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void startSpeakBroadcast(int data) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "startSpeakBroadcast: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "startSpeakBroadcast: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.broadcastService.startSpeakBroadcast(data);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void stopSpeakBroadcast(int data) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "stopSpeakBroadcast: ");
        if (AppStartReceiver.broadcastService == null){
            Log.d(TAG, "stopSpeakBroadcast: AppStartReceiver.broadcastService is null !");
            return;
        }
        AppStartReceiver.broadcastService.stopSpeakBroadcast(data);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void getMasterDeviceListInfo(UniJSCallback uniJsCallback) {
        if (!AppStartReceiver.isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "getMasterDeviceListInfo: ");
        if (AppStartReceiver.intercomService == null){
            Log.d(TAG, "getMasterDeviceListInfo: AppStartReceiver.intercomService is null !");
            return;
        }
        AppStartReceiver.intercomService.getSubMasterList(new Result<ArrayList<MasterDeviceInfo>>() {
            @Override
            public void onData(ArrayList<MasterDeviceInfo> masterDeviceInfos) {
                String gsonString = new Gson().toJson(masterDeviceInfos);
                Log.d(TAG, "getMasterDeviceListInfo onData: "+gsonString);
                uniJsCallback.invoke(gsonString);
            }
        });
    }

    /**********************************************************************************/

    private void showToast(){
        Toast.makeText(mUniSDKInstance.getContext(), "连接服务中，请稍后！", Toast.LENGTH_LONG).show();
    }
    /**********************************************************************************/
}