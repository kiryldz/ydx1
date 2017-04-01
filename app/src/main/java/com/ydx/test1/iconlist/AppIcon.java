package com.ydx.test1.iconlist;

import android.os.Parcel;
import android.os.Parcelable;
import static com.ydx.test1.utils.Constants.imageList;


class AppIcon implements Parcelable {
    private String[] appName = new String [3];
    private int appDrawable;
    private int appClickNum;

    AppIcon(String appName, int appDrawable) {
        this.appName[0] = appName;
        this.appName[1] = "";
        this.appName[2] = "";
        this.appDrawable = imageList[appDrawable];
        appClickNum = 0;
    }

    private AppIcon(Parcel in) {
        in.readStringArray(appName);
        appDrawable = in.readInt();
        appClickNum = in.readInt();
    }

    public static final Creator<AppIcon> CREATOR = new Creator<AppIcon>() {
        @Override
        public AppIcon createFromParcel(Parcel in) {
            return new AppIcon(in);
        }

        @Override
        public AppIcon[] newArray(int size) {
            return new AppIcon[size];
        }
    };

    String[] getAppName() {
        return appName;
    }

    void setAppNamePopular(String appName) { this.appName[1] = appName; }

    void setAppNameNew(String appName) { this.appName[2] = appName; }

    int getAppDrawable() {
        return appDrawable;
    }

    int getAppClickNum() {
        return appClickNum;
    }

    void appClicked() {
        this.appClickNum++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(appName);
        parcel.writeInt(appDrawable);
        parcel.writeInt(appClickNum);
    }

}

