package com.lonbon.floatunibridging;

import io.dcloud.feature.uniapp.bridge.UniJSCallback;

public interface SipProviderInterface {
    //获取sip注册账号
    void getSipUsername(UniJSCallback uniJSCallback);

    //获取sip账号 显示名
    void getSipDisplayName(UniJSCallback uniJSCallback);

    // -1 注销成功 0 注册失败 1 注册成功
    void isSipRegisterState(UniJSCallback uniJSCallback);

    void onSipEvent(UniJSCallback uniJSCallback);

    void sipCall(String sipNum, int dataType, UniJSCallback uniJSCallback);

    void sipAnswer(String sipNum, int dataType, UniJSCallback uniJSCallback);

    void sipHangup(String sipNum, int dataType, UniJSCallback uniJSCallback);

    void sipAudioCall(String sipNum, int dataType, UniJSCallback uniJSCallback);

}
