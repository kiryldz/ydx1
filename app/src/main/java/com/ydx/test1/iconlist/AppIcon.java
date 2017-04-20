package com.ydx.test1.iconlist;

import android.graphics.drawable.Drawable;


class AppIcon {
    private String appName;
    private String appPackage;
    private Drawable appDrawable;
    private int appClickNum;
    private long installTime;

    AppIcon(String appName, String appPackage,Drawable appDrawable) {
        this.appName = appName;
        this.appDrawable = appDrawable;
        this.appPackage = appPackage;
        appClickNum = 0;
    }

    AppIcon (String appName, Drawable appDrawable) {
        this.appName = appName;
        this.appDrawable = appDrawable;
        appClickNum = 0;
    }

    String[] getGSONset() {
        return new String[]{appPackage
                , String.valueOf(appClickNum)
                , String.valueOf(installTime)};
    }

    String getAppPackage() {return appPackage;}

    String getAppName() {
        return appName;
    }

    Drawable getAppDrawable() {
        return appDrawable;
    }

    int getAppClickNum() {
        return appClickNum;
    }

    void appClicked() {
        this.appClickNum++;
    }

    void setAppClicks(int clicks) {this.appClickNum = clicks; }

    long getInstallTime() {
        return installTime;
    }

    void setInstallTime(long installTime) {
        this.installTime = installTime;
    }

}

