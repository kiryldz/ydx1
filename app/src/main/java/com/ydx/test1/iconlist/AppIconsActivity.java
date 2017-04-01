package com.ydx.test1.iconlist;

import android.support.v4.app.Fragment;

import com.ydx.test1.utils.SingleFragmentActivity;

public class AppIconsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AppIconsFragment();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
