package com.app.hroshidozarplatni.models;

import android.text.TextUtils;

import com.app.hroshidozarplatni.utils.Const;

public class CRCUaProposition implements AdapterModel {
    String image;
    String name;
    int verified;
    int favorite;
    String url;
    String redirect_url;
    String score;
    String credit;
    String credit_repeat;
    String days;
    int appSort;
    String description;
    String stavka;
    String time2accept;

    CRCUaProposition() {

    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public boolean isVerified() {
        return verified == 1;
    }

    public boolean isFavorite() {
        return favorite == 1;
    }

    public String getUrl() {
        return url;
    }

    public float getScore() {
        float s = 0;
        try {
            s = TextUtils.isEmpty(score) ? 0 : Float.parseFloat(score);
        } catch (Exception e) {
        }
        return s;
    }

    public String getCredit() {
        return credit;
    }

    public String getCreditRepeat() {
        return credit_repeat;
    }

    public String getDays() {
        return days;
    }

    public String getRedirectUrl() {
        String rUrl = Const.getRedirectUrl(redirect_url);
        return !TextUtils.isEmpty(rUrl) ? rUrl : url;
    }

    public int getAppSort() {
        return appSort;
    }

    public String getDescription() {
        return description;
    }

    public boolean test = false;

    @Override
    public Type getType() {
        return isFavorite() || test ? Type.TYPE_FAV_ITEM : Type.TYPE_DEF_ITEM;
    }

    public String getRates() {
        return (!TextUtils.isEmpty(stavka) ? stavka : "0.01") + "%";
    }

    public String getTime2Accept() {
        return (!TextUtils.isEmpty(time2accept) ? time2accept : "10 мин.");
    }
}
