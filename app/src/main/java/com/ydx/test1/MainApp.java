package com.ydx.test1;

import android.app.Application;


public class MainApp extends Application {
    private static boolean isDark = false;
    private static boolean isCellsOpt2 = false;
    private static boolean isFirstLaunch = true;
    private static boolean atLeastOneItemClicked = false;

    public static boolean isAtLeastOneItemClicked() {
        return atLeastOneItemClicked;
    }

    public static void setAtLeastOneItemClicked(boolean atLeastOneItemClicked) {
        MainApp.atLeastOneItemClicked = atLeastOneItemClicked;
    }

    public static boolean isFirstLaunch() {
        return isFirstLaunch;
    }

    public static void setIsFirstLaunch(boolean isFirstLaunch) {
        MainApp.isFirstLaunch = isFirstLaunch;
    }

    public static void setThemeFlag(boolean flag) {
        isDark = flag;
    }

    public static boolean getThemeFlag() {
        return isDark;
    }

    public static void setIsCellsOpt2(boolean flag) {
        isCellsOpt2 = flag;
    }

    public static boolean getIsCellsOpt2() {
        return isCellsOpt2;
    }
}
