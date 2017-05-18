package com.ydx.test1.welcomescreen;

import android.support.v4.app.Fragment;

import com.ydx.test1.utils.SingleFragmentActivity;

public class WelcomeScreenActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new WelcomeScreenFragment();
    }
}
