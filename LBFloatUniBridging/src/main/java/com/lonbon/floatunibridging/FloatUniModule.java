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
import com.lb.extend.security.intercom.TalkEvent;
import com.lb.extend.security.setting.SystemSettingService;
import com.lb.extend.security.temperature.TemperatureData;
import com.lb.extend.security.temperature.TemperatureMeasurementService;
import com.lb.extend.service.ILonbonService;
import com.lb.extend.service.SystemSetService;
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

import javax.security.auth.callback.Callback;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

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
    private boolean isConnect = false;

    private ArrayList<AreaDivision> areaDivisionArrayList = new ArrayList<>();
    /**
     * 获取服务类
     */
    private ILonbonService iLonbonService;
    private IntercomService intercomService ;
    private SwingCardService swingCardService ;
    private SystemSettingService systemSettingService ;
    private TemperatureMeasurementService temperatureMeasurementService ;
    private FingerprintService fingerprintService ;
    private EducationService educationService;
    private IBroadcastService broadcastService;

    @UniJSMethod(uiThread = true)
    public void initIPCManager(UniJSCallback uniJsCallback){

//        //首先配置开启媒体服务
        IpcManager.INSTANCE.config(Config.Companion.builder().configOpenMedia(true).build());
        //传入上下文
        IpcManager.INSTANCE.init(mUniSDKInstance.getContext());
        //连接服务端，传入的是服务端的包名
        IpcManager.INSTANCE.open("com.lonbon.lonbon_app", new Function0<Unit>() {
            @Override
            public Unit invoke() {
                isConnect = true;
                initService();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",0);
                uniJsCallback.invoke(jsonObject);
                Log.d(TAG, "initIPCManager:invoke: 服务链接成功！");
                Toast.makeText(mUniSDKInstance.getContext(), "服务链接成功！", Toast.LENGTH_LONG).show();
                return null;
            }
        });

    }

    /**
     * 需要在服务链接成功后才能获取到注册类
     */
    private void initService(){
        iLonbonService = IpcManager.INSTANCE.getService(ILonbonService.class);
        intercomService = IpcManager.INSTANCE.getService(IntercomService.class);
        swingCardService = IpcManager.INSTANCE.getService(SwingCardService.class);
        systemSettingService = IpcManager.INSTANCE.getService(SystemSettingService.class);
        temperatureMeasurementService = IpcManager.INSTANCE.getService(TemperatureMeasurementService.class);
        fingerprintService = IpcManager.INSTANCE.getService(FingerprintService.class);
        educationService = IpcManager.INSTANCE.getService(EducationService.class);
        broadcastService = IpcManager.INSTANCE.getService(IBroadcastService.class);
    }


    //run ui thread
    @UniJSMethod(uiThread = true)
    public void printDeviceInfoTest(UniJSCallback uniJsCallback){
        Log.d(TAG, "printDeviceInfoTest");
        Toast.makeText(mUniSDKInstance.getContext(), TAG, Toast.LENGTH_SHORT).show();
        if (intercomService == null){
            Log.d(TAG, "printDeviceInfoTest: intercomService is null !");
            return;
        }
        intercomService.getCurrentDeviceInfo(new Result<LocalDeviceInfo>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "setTalkViewPosition: intercomService is null !");
            return;
        }
        intercomService.setTalkViewPosition(left,top,width,height);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void extDoorLampCtrl(int color, int open) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "extDoorLampCtrl: ");
        if (intercomService == null){
            Log.d(TAG, "extDoorLampCtrl: intercomService is null !");
            return;
        }
        intercomService.extDoorLampCtrl(color,open);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void onDoorContactValue(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "onDoorContactValue: ");
        if (intercomService == null){
            Log.d(TAG, "onDoorContactValue: intercomService is null !");
            return;
        }
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

    @UniJSMethod(uiThread = true)
    @Override
    public void asyncGetDeviceListInfo(int areaId, int masterNum, int slaveNum, int devRegType, UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "asyncGetDeviceListInfo: ");
        if (intercomService == null){
            Log.d(TAG, "asyncGetDeviceListInfo: intercomService is null !");
            return;
        }
        intercomService.asyncGetDeviceListInfo(areaId,masterNum,slaveNum,devRegType, new Result<ArrayList<DeviceInfo>>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "updateDeviceTalkState: ");
        if (intercomService == null){
            Log.d(TAG, "updateDeviceTalkState: intercomService is null !");
            return;
        }
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
        if (intercomService == null){
            Log.d(TAG, "deviceClick: intercomService is null !");
            return;
        }
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
        if (intercomService == null){
            Log.d(TAG, "nativeCall: intercomService is null !");
            return;
        }
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
        if (intercomService == null){
            Log.d(TAG, "nativeAnswer: intercomService is null !");
            return;
        }
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
        if (intercomService == null){
            Log.d(TAG, "nativeHangup: intercomService is null !");
            return;
        }
        intercomService.hangup(masterNum,slaveNum,areaId,devRegType);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void openLockCtrl(int num, int open) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "openLockCtrl: intercomService is null !");
            return;
        }
        intercomService.openLockCtrl(num,open);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void getCurrentDeviceInfo(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "getCurrentDeviceInfo: intercomService is null !");
            return;
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
    @UniJSMethod(uiThread = true)
    @Override
    public void talkEventCallback(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "talkEventCallback: intercomService is null !");
            return;
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
    @UniJSMethod(uiThread = true)
    @Override
    public void onDeviceOnLine(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "onDeviceOnLine: intercomService is null !");
            return;
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
    public void onDeviceOffLine(UniJSCallback uniJsCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "onDeviceOffLine: intercomService is null !");
            return;
        }
        intercomService.onDeviceOffLine(new Result<DeviceInfo>() {
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
        if (intercomService == null){
            Log.d(TAG, "listenToTalk: intercomService is null !");
            return;
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
        if (intercomService == null){
            Log.d(TAG, "hideTalkView: intercomService is null !");
            return;
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
        if (intercomService == null){
            Log.d(TAG, "oneKeyCall: intercomService is null !");
            return;
        }
        intercomService.oneKeyCall();

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void setLocalVideoViewPosition(int left, int top, int width, int height) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "setLocalVideoViewPosition: intercomService is null !");
            return;
        }
        intercomService.setPreViewPosition(left,top,width,height);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void hideLocalPreView(Boolean hide) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "hideLocalPreView: intercomService is null !");
            return;
        }
        intercomService.hidePreView(hide);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void setExtMicEna(Boolean enable) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "setExtMicEna: intercomService is null !");
            return;
        }
        intercomService.setMicEna(enable);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void openLocalCamera(Boolean isOpen) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "openLocalCamera: intercomService is null !");
            return;
        }
        intercomService.openLocalCamera(isOpen);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void initFrame() {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "openLocalCamera: intercomService is null !");
            return;
        }
        intercomService.initFrame();
    }

    private int width = 0;
    private int height = 0;
    @UniJSMethod(uiThread = true)
    @Override
    public void setViewWidthHeight(int width, int height) {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "openLocalCamera: intercomService is null !");
            return;
        }
        this.width = width;
        this.height = height;
        intercomService.setViewWidthHeight(width,height);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void startTakeFrame() {
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "startTakeFrame: intercomService is null !");
            return;
        }
        intercomService.startTakeFrame();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void stopTakeFrame() {
        if (!isConnect){
            showToast();
            return ;
        }
        IpcManager.INSTANCE.getMediaService().stopTakeFrame();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void takePicture() {
        if (!isConnect){
            showToast();
            return ;
        }
        IpcManager.INSTANCE.getMediaService().takePicture(PictureFormat.JPEG);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void takeFrame() {
        if (!isConnect){
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
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "setRecordPath: intercomService is null !");
            return;
        }
        intercomService.setRecordPath(path, new Result<String>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "getFileList: intercomService is null !");
            return;
        }
        intercomService.getFileList(path, new Result<ArrayList<File>>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        if (intercomService == null){
            Log.d(TAG, "deleteFile: intercomService is null !");
            return;
        }
        intercomService.deleteFile(path, new Result<Boolean>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setSlaveVolume: " + volume);
        if (intercomService == null){
            Log.d(TAG, "setSlaveVolume: intercomService is null !");
            return;
        }
        intercomService.setSlaveTalkVolume(volume);
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
        if (!isConnect){
            showToast();
            jsonObject.put("slaveVolume", 3);
        }else {
            if (intercomService == null){
                Log.d(TAG, "syncGetSlaveVolume: intercomService is null !");
                jsonObject.put("slaveVolume", 3);
                return;
            }
            jsonObject.put("slaveVolume", intercomService.getSlaveTalkVolume());
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
        if (!isConnect){
            showToast();
            jsonObject.put("code",-1);
        }else {
            if (swingCardService == null){
                Log.d(TAG, "syncStartCard: swingCardService is null !");
                jsonObject.put("code",-1);
                return;
            }
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

            if (swingCardService == null){
                Log.d(TAG, "syncStopCard: swingCardService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            swingCardService.stop();
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setCardDataCallBack: ");
        if (swingCardService == null){
            Log.d(TAG, "setCardDataCallBack: swingCardService is null !");
            return;
        }
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
            if (fingerprintService == null){
                Log.d(TAG, "syncStartFinger: fingerprintService is null !");
                jsonObject.put("code",-1);
                return;
            }
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
            if (fingerprintService == null){
                Log.d(TAG, "syncStopFinger: fingerprintService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            fingerprintService.stop();
        }
        uniJSCallback.invoke(jsonObject);
    }
    @UniJSMethod(uiThread = true)
    @Override
    public void fingerModuleStop() {
        Log.d(TAG, "fingerModuleStop: ");
        if (fingerprintService == null){
            Log.d(TAG, "fingerModuleStop: fingerprintService is null !");
            return;
        }
        fingerprintService.destroy();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintCollect(String id) {
        Log.d(TAG, "fingerprintCollect: ");
        if (fingerprintService == null){
            Log.d(TAG, "fingerprintCollect: fingerprintService is null !");
            return;
        }
        fingerprintService.fingerprintCollect(id);

    }
    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintRecognition() {
        Log.d(TAG, "fingerprintRecognition: ");
        if (fingerprintService == null){
            Log.d(TAG, "fingerprintRecognition: fingerprintService is null !");
            return;
        }
        fingerprintService.fingerprintRecognition();

    }


    @UniJSMethod(uiThread = true)
    @Override
    public void fingerprintFeatureInput(String id, String feature) {
        Log.d(TAG, "fingerprintFeatureInput: ");
        if (fingerprintService == null){
            Log.d(TAG, "fingerprintFeatureInput: fingerprintService is null !");
            return;
        }
        fingerprintService.fingerprintFeatureInput(id,feature);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setFingerprintFeatureCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureCallBack: ");
        if (fingerprintService == null){
            Log.d(TAG, "setFingerprintFeatureCallBack: fingerprintService is null !");
            return;
        }
        fingerprintService.setFingerprintFeatureCallBack(new Result<CallbackData<FingerprintFeatureResult>>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetFingerprintFeatureLeftNumCallBack: ");
        if (fingerprintService == null){
            Log.d(TAG, "setFingerprintFeatureLeftNumCallBack: fingerprintService is null !");
            return;
        }
        fingerprintService.setFingerprintLeftNumCallBack(new Result<CallbackData<FingerprintLeftNumResult>>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: fingerprintService is null !");
            return;
        }
        fingerprintService.setFingerprintCompareCallBack(new Result<CallbackData<FingerprintCompareResult>>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: fingerprintService is null !");
            return;
        }

        fingerprintService.clearFingerprintById(id);
    }

    /**
     * 根据指纹特征值清除本地指纹存储信息
     * @param feature String
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void clearFingerprintByFeature(String feature) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: fingerprintService is null !");
            return;
        }
        fingerprintService.clearFingerprintByFeature(feature);
    }

    /**
     * 清空本地所有指纹存储信息
     */
    @UniJSMethod(uiThread = true)
    @Override
    public void clearAllFingerprint() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setGetCompareFingerprintCallBack: ");
        if (fingerprintService == null){
            Log.d(TAG, "setCompareFingerprintCallBack: fingerprintService is null !");
            return;
        }
        fingerprintService.clearAllFingerprint();
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

            if (temperatureMeasurementService == null){
                Log.d(TAG, "syncStartTemperature: temperatureMeasurementService is null !");
                jsonObject.put("code",-1);
                return;
            }
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

            if (temperatureMeasurementService == null){
                Log.d(TAG, "syncStopTemperature: temperatureMeasurementService is null !");
                jsonObject.put("code",-1);
                return;
            }
            jsonObject.put("code",0);
            temperatureMeasurementService.stop();
        }
        uniJSCallback.invoke(jsonObject);

    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setTemperatureDataCallBack(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setTemperatureDataCallBack: ");
        if (temperatureMeasurementService == null){
            Log.d(TAG, "setTemperatureDataCallBack: temperatureMeasurementService is null !");
            return;
        }
        temperatureMeasurementService.setTemperatureDataCallBack(new Result<CallbackData<TemperatureData>>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setSystemTime: "+time);
        if (systemSettingService == null){
            Log.d(TAG, "setSystemTime: systemSettingService is null !");
            return;
        }
        systemSettingService.setSystemTime(time);
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "rebootSystem: ");
        if (systemSettingService == null){
            Log.d(TAG, "rebootSystem: systemSettingService is null !");
            return;
        }
        systemSettingService.rebootSystem();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void openGuard(int isOpen) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "openGuard: isOpen："+isOpen);
        if (iLonbonService == null){
            Log.d(TAG, "openGuard: iLonbonService is null !");
            return;
        }
        iLonbonService.openGuard(isOpen == 1);

    }

    /***********************************电教相关***********************************************/

    @UniJSMethod(uiThread = true)
    @Override
    public void initEducation() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "initEducation: ");
        if (educationService == null){
            Log.d(TAG, "initEducation: educationService is null !");
            return;
        }
        educationService.init();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void enterLiveShow() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "enterLiveShow: ");
        if (educationService == null){
            Log.d(TAG, "enterLiveShow: educationService is null !");
            return;
        }
        educationService.showEducation();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void exitLiveShow() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "exitLiveShow: ");
        if (educationService == null){
            Log.d(TAG, "exitLiveShow: educationService is null !");
            return;
        }
        educationService.exitEducation();
    }

    @Override
    public void syncGetEducationTaskList(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "syncGetEducationTaskList: ");
        if (educationService == null){
            Log.d(TAG, "syncGetEducationTaskList: educationService is null !");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("educationTaskList", new Gson().toJson(educationService.getEducationTask()));
        uniJSCallback.invoke(jsonObject);
    }

    @Override
    public void enterEducationTask() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "enterEducationTask: ");
        if (educationService == null){
            Log.d(TAG, "enterEducationTask: educationService is null !");
            return;
        }
        educationService.showEducationTask();
    }

    @Override
    public void exitEducationTask() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "exitEducationTask: ");
        if (educationService == null){
            Log.d(TAG, "exitEducationTask: educationService is null !");
            return;
        }
        educationService.exitEducationTask();
    }

    @Override
    public void setEducationStateListener(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setEducationStateListener: ");
        if (educationService == null){
            Log.d(TAG, "setEducationStateListener: educationService is null !");
            return;
        }
        educationService.setEducationTaskStateListener(new Result<EducationTaskStateBean>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "hdmiOpen: " + outputConfigure);
        if (educationService == null){
            Log.d(TAG, "hdmiOpen: educationService is null !");
            return;
        }
        educationService.setHDMIConfigure(outputConfigure);
    }

    /***********************************广播相关***********************************************/

    @UniJSMethod(uiThread = true)
    @Override
    public void initBroadcast() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "initBroadcast: ");
        if (broadcastService == null){
            Log.d(TAG, "initBroadcast: broadcastService is null !");
            return;
        }
        broadcastService.initSpeakBroadcast();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setOnIONotifyListener(UniJSCallback uniJSCallback) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setOnIONotifyListener: ");
        if (broadcastService == null){
            Log.d(TAG, "setOnIONotifyListener: broadcastService is null !");
            return;
        }
        broadcastService.onIONotifyListener(new Result<Integer>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setOnSpeakBroadcastListener: ");
        if (broadcastService == null){
            Log.d(TAG, "setOnSpeakBroadcastListener: broadcastService is null !");
            return;
        }
        broadcastService.onSpeakBroadcastListener(new Result<SpeakBroadcastState>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setOnToastListener: ");
        if (broadcastService == null){
            Log.d(TAG, "setOnToastListener: broadcastService is null !");
            return;
        }
        broadcastService.onToastListener(new Result<String>() {
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
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "addBroadcastObj: ");
        if (broadcastService == null){
            Log.d(TAG, "addBroadcastObj: broadcastService is null !");
            return;
        }

        if (num < 1000){
            Log.d(TAG, "addBroadcastObj: num must bigger than 1000 !");
            return;
        }
        AreaDivision areaDivision = new AreaDivision();
        areaDivision.setDisplayNum(num);
        areaDivision.setMasterNum(num % 1000);
        if (!areaDivisionArrayList.contains(areaDivision)) {
            areaDivisionArrayList.add(areaDivision);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcastDevices", new Gson().toJson(areaDivisionArrayList));
        uniJSCallback.invoke(jsonObject);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void clearBroadcastObj() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "addBroadcastObj: ");
        if (broadcastService == null){
            Log.d(TAG, "addBroadcastObj: broadcastService is null !");
            return;
        }
        areaDivisionArrayList.clear();
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void setSpeakBroadcastDevice() {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "setSpeakBroadcastDevice: ");
        if (broadcastService == null){
            Log.d(TAG, "setSpeakBroadcastDevice: broadcastService is null !");
            return;
        }
        for (int i = 0; i < areaDivisionArrayList.size(); i++) {
            Log.d(TAG, "setSpeakBroadcastDevice: " + areaDivisionArrayList.get(i));
        }
        broadcastService.setSpeakBroadcastDevice(areaDivisionArrayList);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void startSpeakBroadcast(int data) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "startSpeakBroadcast: ");
        if (broadcastService == null){
            Log.d(TAG, "startSpeakBroadcast: broadcastService is null !");
            return;
        }
        broadcastService.startSpeakBroadcast(data);
    }

    @UniJSMethod(uiThread = true)
    @Override
    public void stopSpeakBroadcast(int data) {
        if (!isConnect){
            showToast();
            return ;
        }
        Log.d(TAG, "stopSpeakBroadcast: ");
        if (broadcastService == null){
            Log.d(TAG, "stopSpeakBroadcast: broadcastService is null !");
            return;
        }
        broadcastService.stopSpeakBroadcast(data);
    }

    /**********************************************************************************/

    private void showToast(){
        Toast.makeText(mUniSDKInstance.getContext(), "连接服务中，请稍后！", Toast.LENGTH_LONG).show();
    }
    /**********************************************************************************/
}