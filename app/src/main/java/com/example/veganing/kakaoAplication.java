package com.example.veganing;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class kakaoAplication extends Application {

    public void onCreate(){
        super.onCreate();
        // kakao SDK 초기화
        KakaoSdk.init(this, "kakao{d1389f930f4c488f8df3754502736280}");
//        KaKaoSdk.init(d1389f930f4c488f8df3754502736280);
    }
}
