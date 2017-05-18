package com.ydx.test1.welcomescreen;


public class WelcomeScreenInstance {
    private String mText;
    private int mImgRes;

    public WelcomeScreenInstance(String mText, int mImgRes) {
        this.mText = mText;
        this.mImgRes = mImgRes;
    }

    public String getText() {
        return mText;
    }

    public int getImgRes() {
        return mImgRes;
    }
}
