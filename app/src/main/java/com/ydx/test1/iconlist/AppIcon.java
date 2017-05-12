package com.ydx.test1.iconlist;

import android.graphics.drawable.Drawable;

class AppIcon {
    private String appName;
    private String appPackage;
    private Drawable appDrawable;
    private int appClickNum;
    private long installTime;
    private boolean isContact = false;
    private String contactNumber;
    private String contactImageUri;
    private String contactEmail;


    AppIcon(String appName, String appPackage, Drawable appDrawable) {
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

    AppIcon (String contactName
            , String contactImage
            , String contactNumber
            , String contactEmail) {
        this.appName = contactName;
        this.contactImageUri = contactImage;
        this.contactNumber = contactNumber;
        this.appPackage = contactName;
        this.contactEmail = contactEmail;
        appClickNum = 0;
        this.isContact = true;
    }

    String[] getGSONset(boolean isContact) {
        if (!isContact) {
            return new String[]{appPackage
                    , String.valueOf(appClickNum)
                    , String.valueOf(installTime)
                    , String.valueOf("")
                    , String.valueOf(false)};
        } else {
            return new String[]{appName
                    , String.valueOf(contactImageUri)
                    , String.valueOf(contactNumber)
                    , String.valueOf(contactEmail)
                    , String.valueOf(true)};
        }
    }

    String getAppPackage() {return appPackage;}

    String getAppName() {
        return appName;
    }

    Drawable getAppDrawable() {
        return appDrawable;
    }

    boolean getIsContact() {return isContact; }

    String getContactNumber() {return contactNumber; }

    int getAppClickNum() {
        return appClickNum;
    }

    void appClicked() { if (!isContact) this.appClickNum++; }

    void setAppClicks(int clicks) {this.appClickNum = clicks; }

    void setAppDrawable(Drawable drawable) {this.appDrawable = drawable; }

    long getInstallTime() {
        return installTime;
    }

    void setInstallTime(long installTime) {
        this.installTime = installTime;
    }

    public String getContactEmail() {
        return contactEmail;
    }

}

