package com.ydx.test1.iconlist;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.ydx.test1.MainApp;
import com.ydx.test1.R;
import com.ydx.test1.contacts.AllContactsActivity;
import com.ydx.test1.imageloader.ImageSaver;
import com.ydx.test1.imageloader.ImageService;
import com.ydx.test1.utils.SettingsFragment;
import com.ydx.test1.welcomescreen.WelcomeScreenActivity;
import java.util.ArrayList;
import java.util.List;

import static com.ydx.test1.MainApp.PARAM_RESULT;

public class AppIconsActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private SharedPreferences sp;
    private LauncherPagerAdapter mAdapter;
    private MyBroadcastReceiver mBroadcastReceiver;
    private Intent serviceIntent;
    private static final String TAG = "TAG";

    
    public LauncherPagerAdapter getmAdapter() {
        return mAdapter;
    }

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS_CALL = 100;

    private void showContacts() {
        startActivity(new Intent(this, AllContactsActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS_CALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this
                        , getString(R.string.grant_permission)
                        , Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void syncGlobalSettings() {
        if (sp.getString("change_theme","-1").equals("-1")) {
            sp.edit().putString("change_theme", MainApp.getThemeFlag() ? "1" : "0").apply();
        } else {
            MainApp.setThemeFlag(sp.getString("change_theme","1").equals("1"));
        }
        if (sp.getString("change_cells","-1").equals("-1")) {
            sp.edit().putString("change_cells", MainApp.getIsCellsOpt2() ? "1" : "0").apply();
        } else {
            MainApp.setIsCellsOpt2(sp.getString("change_cells","1").equals("1"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, ImageService.class);
        serviceIntent.putExtra("isMainActivity", true);
        if (getSharedPreferences("prefs", Context.MODE_PRIVATE)
                .getBoolean("first_run",true)) {
            startActivity(new Intent(this, WelcomeScreenActivity.class));
            finish();
            return;
        }
        sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        syncGlobalSettings();
        if (sp.getString("change_theme","1").equals("1")) {
            super.setTheme(R.style.AppThemeDark);
        } else {
            super.setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_main);
        ImageSaver.getInstance().loadImage(this, sp.getString("image_name",null));
        mBroadcastReceiver = new MyBroadcastReceiver();
        ViewPager pager = (ViewPager) findViewById(R.id.mainViewPager);
        mAdapter = new LauncherPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void applyFragment(boolean showSettings, MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (showSettings) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.mainLayout, SettingsFragment.newInstance(), "SETTINGS")
                    .commit();
        } else {
            exitSettings(fragmentManager);
        }
        menuItem.setChecked(true);
        mDrawer.closeDrawers();
    }

    private void openContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]
                            {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}
                    , PERMISSIONS_REQUEST_READ_CONTACTS_CALL);
        } else {
            showContacts();
        }
        mDrawer.closeDrawers();
    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.all_icons:
                applyFragment(false, menuItem);
                break;
            case R.id.nav_contacts:
                openContacts();
                break;
            case R.id.nav_settings:
                applyFragment(true, menuItem);
                break;
            default:
                applyFragment(false, menuItem);
        }
    }



    public class LauncherPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> list;

        LauncherPagerAdapter(FragmentManager fm) {
            super(fm);
            list = new ArrayList<>();
            list.add(0, AppIconsFragment.newInstance());
            if (!sp.getBoolean("hide_favs_fragment",false)) {
                list.add(1, AppIconsFavoritesFragment.newInstance());
            }
        }

        @Override
        public Fragment getItem(int pos) {
            return this.list.get(pos);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public void enableFavoriteFragment() {
            list.add(1, AppIconsFavoritesFragment.newInstance());
        }

        public void disableFavoriteFragment() {
            list.remove(1);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void exitSettings(FragmentManager fragmentManager) {
        if (fragmentManager.findFragmentByTag("SETTINGS") != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(fragmentManager.findFragmentByTag("SETTINGS"))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        exitSettings(getSupportFragmentManager());
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "MyBroadcastReceiver, onReceive");
            final String imageName = intent.getStringExtra(PARAM_RESULT);
            if (!TextUtils.isEmpty(imageName)) {
                sp.edit().putString("image_name", imageName).apply();
                sp.edit().putInt("image_num", sp.getInt("image_num",0) + 1).apply();
                ImageSaver.getInstance().loadImage(AppIconsActivity.this, imageName);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity, onResume, registerReceiver");
        registerReceiver(mBroadcastReceiver, new IntentFilter(MainApp.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "MainActivity, onPause, unregisterReceiver");
        unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(serviceIntent);
    }
}
