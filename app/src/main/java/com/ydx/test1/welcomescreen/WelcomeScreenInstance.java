package com.ydx.test1.welcomescreen;


class WelcomeScreenInstance {
    private String mText;
    private int mImgRes;

    WelcomeScreenInstance(String mText, int mImgRes) {
        this.mText = mText;
        this.mImgRes = mImgRes;
    }

    String getText() {
        return mText;
    }

    int getImgRes() {
        return mImgRes;
    }
}
