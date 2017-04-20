package com.ydx.test1.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PackageChangeStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences("apps", Context.MODE_PRIVATE);
        if (intent.getAction()
                .equals("android.intent.action.PACKAGE_ADDED")) {
            preferences.edit()
                    .putString(intent.getData().getSchemeSpecificPart(), "1")
                    .apply();
        }
        if (intent.getAction()
                .equals("android.intent.action.PACKAGE_REMOVED")) {
            preferences.edit()
                    .putString(intent.getData().getSchemeSpecificPart(), "-1")
                    .apply();
        }
    }
}
