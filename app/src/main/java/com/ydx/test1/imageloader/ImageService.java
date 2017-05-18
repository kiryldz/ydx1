package com.ydx.test1.imageloader;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.ydx.test1.MainApp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ImageService extends Service {

    private static final String TAG = "TAG";

    private final ImageLoader mImageLoader;
    private SharedPreferences prefs;
    private ScheduledExecutorService scheduler;
    private long updateInterval;

    public ImageService() {
        mImageLoader = new ImageLoader();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyService, onCreate");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        switch (prefs.getString("change_upd_pict_interval","0")) {
            case "0" :
                updateInterval = 15 * 60;
                break;
            case "1" :
                updateInterval = 60 * 60;
                break;
            case "2" :
                updateInterval = 60 * 60 * 8;
                break;
            case "3" :
                updateInterval = 60 * 60 * 24;
                break;
        }

        long delay = 0;
        if (prefs.getLong("start_time",0) != 0) {
            delay = (prefs.getLong("start_time",0)
                    + updateInterval*1000
                    - System.currentTimeMillis()) / 1000;
            if (delay < 0 ) delay = 0;
        }
        Log.d(TAG, "MyService, delay = " + delay);
        prefs.edit()
                .putLong("delay", delay).apply();

    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "MyService, onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent,
                              final int flags,
                              final int startId) {
        Log.d(TAG, "MyService, onStartCommand, loadImage");
        if (intent.getBooleanExtra("ZERO_DELAY", false)) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            prefs.edit()
                    .putLong("delay", 0).apply();
        }
        Runnable uploadImageTask = new Runnable() {
            @Override
            public void run() {
                long dayNow = System.currentTimeMillis() / 86400000;
                Log.d(TAG, "MyService, prev_start_day " + prefs.getLong("start_time",0) / 86400000);
                Log.d(TAG, "MyService, day_now " + dayNow);
                if (prefs.getLong("start_time",0) / 86400000
                        < dayNow ) {
                    prefs.edit().putInt("image_num", 0).apply();
                }
                prefs.edit().putLong("start_time", System.currentTimeMillis()).apply();
                Log.d(TAG, "MyService, started loading " + prefs.getInt("image_num",0));
                final String imageUrl = mImageLoader.getImageUrl(
                        prefs.getInt("image_num",0));
                final String imageUrlContacts = mImageLoader.getImageUrl(
                        prefs.getInt("image_num",0) + 10);
                if (!TextUtils.isEmpty(imageUrl)) {
                    final Bitmap bitmap = mImageLoader.loadBitmap(imageUrl);
                    final Bitmap bitmapContacts = mImageLoader.loadBitmap(imageUrlContacts);
                    final String imageName = "myImage.png";
                    final String imageNameContacts = "myImageContacts.png";
                    ImageSaver.getInstance().saveImage(getApplicationContext()
                            , bitmap
                            , imageName);
                    final Intent intent = new Intent(MainApp.BROADCAST_ACTION);
                    intent.putExtra(MainApp.PARAM_RESULT, imageName);
                    sendBroadcast(intent);
                    ImageSaver.getInstance().saveImage(getApplicationContext()
                            , bitmapContacts
                            , imageNameContacts);
                }
            }
        };

        scheduler.scheduleAtFixedRate(uploadImageTask
                , prefs.getLong("delay",0)
                , updateInterval
                , SECONDS);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MyService, onDestroy");
        scheduler.shutdown();
        super.onDestroy();
    }
}