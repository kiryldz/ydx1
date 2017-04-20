package com.ydx.test1.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PackageChangeStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction()
                .equals("android.intent.action.PACKAGE_REMOVED")) {
            Toast.makeText(context, " onReceive !!!! "
                    + "PACKAGE_REMOVED"
                    + intent.getData().getSchemeSpecificPart(),
                    Toast.LENGTH_LONG).show();

        }
        else if (intent.getAction()
                .equals("android.intent.action.PACKAGE_ADDED")) {
            Toast.makeText(context, " onReceive !!!!."
                    + "PACKAGE_ADDED "
                    + intent.getData().getSchemeSpecificPart(),
                    Toast.LENGTH_LONG).show();

        }
    }
}
