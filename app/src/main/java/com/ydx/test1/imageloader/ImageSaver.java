package com.ydx.test1.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ydx.test1.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class ImageSaver {

    private static final Object mLock = new Object();
    private static volatile ImageSaver sInstance;

    private static final String DIRECTORY_NAME = "images";

    public static ImageSaver getInstance() {
        if (null == sInstance) {
            synchronized (mLock) {
                if (null == sInstance) {
                    sInstance = new ImageSaver();
                }
            }
        }

        return sInstance;
    }

    private ImageSaver() {
    }

    void saveImage(final Context context, final Bitmap bitmap, final String fileName) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile(context, fileName, false));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private File createFile(final Context context, final String fileName, final boolean external) {
        File directory;
        if (external){
            directory = getAlbumStorageDir(DIRECTORY_NAME);
        }
        else {
            directory = context.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        }

        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created");
        }
        return file;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public void loadImage(final Activity context, final String fileName) {
        AsyncTask<Void, Bitmap, Bitmap> asyncTask = new AsyncTask<Void, Bitmap, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(createFile(context, fileName, false));
                    return BitmapFactory.decodeStream(inputStream);
                }
                catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                final Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
                bitmapDrawable.setAlpha(128);
                ViewGroup mainLayout = (ViewGroup) context.findViewById(R.id.mainLayout);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mainLayout.setBackground(bitmapDrawable);
                } else {
                    mainLayout.setBackgroundDrawable(bitmapDrawable);
                }
                Log.d(TAG, "MainActivity or ContactsActivity, image loaded");

            }
        };
        asyncTask.execute();
    }
}
