package com.app.hroshidozarplatni.utils;

import android.text.TextUtils;
import android.util.Log;

import com.app.hroshidozarplatni.BuildConfig;
import com.orhanobut.hawk.Hawk;

import java.util.UUID;

public class Const {
    public static final String KEY_SUB_ID = "sub_id";

    public static String getSubId() {
        String subId = Hawk.get(KEY_SUB_ID);
        if (TextUtils.isEmpty(subId)) {
            Log.w("ERROR", "subId was null");
            subId = UUID.randomUUID().toString().replaceAll("-", "");
            Hawk.put(KEY_SUB_ID, subId);
        }
        return subId;
    }

    public static String getAppId(){
        return BuildConfig.APPLICATION_ID;
    }

    public static String getRedirectUrl(String redirectUrl) {
        if (!TextUtils.isEmpty(redirectUrl)) {
            try {
                return redirectUrl.replaceAll("\\{subid\\}", getSubId());
            } catch (Exception e) {
            }
        }

        return null;
    }
}
