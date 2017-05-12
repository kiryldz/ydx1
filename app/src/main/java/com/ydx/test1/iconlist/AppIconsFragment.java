package com.ydx.test1.iconlist;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ydx.test1.utils.Constants;
import com.ydx.test1.MainApp;
import com.ydx.test1.R;
import static android.content.Context.MODE_PRIVATE;

public class AppIconsFragment extends Fragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private PackageManager manager;
    private List<AppIcon> iconList = new ArrayList<>();
    private List<AppIcon> iconListNew = new ArrayList<>();
    private AppIconsAdapter mAdapter;
    private int cellsInLineCount;
    private SharedPreferences prefs;

    public static AppIconsFragment newInstance() {
        return new AppIconsFragment();
    }

    AppIconsAdapter getAdapter() {
        return mAdapter;
    }

    private AppIcon addApp(String packageName) {
        AppIcon app = null;
        try {
            ApplicationInfo appInfo = manager.getApplicationInfo(packageName, 0);
            if (manager.getLaunchIntentForPackage(appInfo.packageName) == null)
                return null;
            app = new AppIcon(manager.getApplicationLabel(appInfo).toString()
                , packageName
                , manager.getApplicationIcon(appInfo));
            long installTime = manager.getPackageInfo(packageName,0).firstInstallTime;
            app.setInstallTime(installTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return app;
    }

    private void notifyInstallDelete() {
        List<AppIcon> addedApps = new ArrayList<>();
        List<Integer> deletedApps = new ArrayList<>();
        Map<String,?> keys = prefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            if (entry.getValue().toString().equals("1")) {
                AppIcon app = addApp(entry.getKey());
                if (app != null) addedApps.add(app);
            }
            if (entry.getValue().toString().equals("-1")) {
                deletedApps.add(getPositionOfDeletedApp(entry.getKey()));
            }
        }
        if (!deletedApps.isEmpty()) {
           for (Integer pos : deletedApps) {
               mAdapter.removeIcon(pos);
           }
        }
        if (!addedApps.isEmpty()) {
            Collections.sort(addedApps, new Comparator<AppIcon>() {
                @Override
                public int compare(AppIcon t1, AppIcon t2) {
                    return t1.getInstallTime()<t2.getInstallTime() ? 1 : -1;
                }
            });
            iconListNew.addAll(0, addedApps);
            iconList.addAll(addedApps);
        }
        if (!addedApps.isEmpty() || !deletedApps.isEmpty()) {
            prefs.edit().clear().apply();
            mAdapter.updateHeaders();
            mAdapter.notifyDataSetChanged();
            addedApps.clear();
            deletedApps.clear();
        }
    }


    private int getPositionOfDeletedApp(String packageToDelete) {
        for (int i=0; i<iconList.size(); i++) {
            if (iconList.get(i).getAppPackage().equals(packageToDelete)) {
                return i;
            }
        }
        return -1;
    }

    private void generateNewList() {
        iconListNew = new ArrayList<>(iconList);
        Collections.sort(iconListNew, new Comparator<AppIcon>() {
            @Override
            public int compare(AppIcon t1, AppIcon t2) {
                return t1.getInstallTime()<t2.getInstallTime() ? 1 : -1;
            }
        });
        for (int i=0;i<Constants.ICON_LIST_SIZE_LARGE[1];i++) {
            if (i>=iconList.size()) {
                iconListNew.add(new AppIcon(Constants.EMPTY_ICON, null));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icon_list,
                container, false);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        prefs = getActivity().getApplicationContext().getSharedPreferences("apps", MODE_PRIVATE);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        manager = getActivity().getPackageManager();
        if (sp.getString("change_cells","1").equals("1")) {
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                cellsInLineCount = Constants.ICON_LIST_SIZE_LARGE[1];
            } else {
                cellsInLineCount = Constants.ICON_LIST_SIZE_LARGE[0];
            }
        } else {
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                cellsInLineCount = Constants.ICON_LIST_SIZE_STANDART[1];
            } else {
                cellsInLineCount = Constants.ICON_LIST_SIZE_STANDART[0];
            }
        }

        GridLayoutManager mLayoutManager
                = new GridLayoutManager(getActivity().getApplicationContext()
                , cellsInLineCount);
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mAdapter.getItemViewType(position)){
                    case 1:
                        return cellsInLineCount;
                    case 0:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        generateApplicationList();
        notifyInstallDelete();
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private void generateApplicationListFirstTime() {
        List<ApplicationInfo> packages
                = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        String currentPackage = getActivity().getApplicationContext().getPackageName();
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(currentPackage)
                    || manager.getLaunchIntentForPackage(packageInfo.packageName) == null) {
                continue;
            }
            AppIcon app = new AppIcon(manager.getApplicationLabel(packageInfo).toString()
                    , packageInfo.packageName
                    , manager.getApplicationIcon(packageInfo));
            try {
                long installTime = manager
                        .getPackageInfo(packageInfo.packageName,0)
                        .firstInstallTime;
                app.setInstallTime(installTime);
            } catch (PackageManager.NameNotFoundException e) {
                app.setInstallTime(-1);
                e.printStackTrace();
            }
            iconList.add(app);
        }
        generateNewList();
        mAdapter = new AppIconsAdapter(this, manager, iconList
                , iconListNew, cellsInLineCount, true);
    }

    private void generateApplicationList() {
        iconList.clear();
        prefs.edit().clear().apply();
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("apps_storage", MODE_PRIVATE);
        String json = prefs.getString("common_list", "");
        if (json.equals("")) {
            generateApplicationListFirstTime();
            return;
        }
        Type type = new TypeToken< List < String[] >>() {}.getType();
        List<String[]> iconListFromStorage = gson.fromJson(json, type);
        generateAppListFromJson(iconListFromStorage);
        generateNewList();
        mAdapter = new AppIconsAdapter(this, manager, iconList
                , iconListNew, cellsInLineCount, false);
    }

    private void generateAppListFromJson(List<String[]> list) {
        for (String[] jsonSet : list) {
            AppIcon app ;
            try {
                ApplicationInfo appInfo = manager.getApplicationInfo(jsonSet[0], 0);
                app = new AppIcon(manager.getApplicationLabel(appInfo).toString()
                        , jsonSet[0]
                        , manager.getApplicationIcon(appInfo));
                app.setAppClicks(Integer.valueOf(jsonSet[1]));
                app.setInstallTime(Long.valueOf(jsonSet[2]));
                iconList.add(app);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
        if (MainApp.isFirstLaunch()) {
            MainApp.setIsFirstLaunch(false);
            return;
        }
        if (mAdapter != null) {
            mAdapter.updateHeaders();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("apps_storage", MODE_PRIVATE);
        Gson gson = new Gson();
        List<String[]> list = new ArrayList<>();
        for (AppIcon a : getAdapter().getIconList()) {
            list.add(a.getGSONset(false));
        }
        String json = gson.toJson(list);
        prefs.edit().putString("common_list", json).apply();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
        notifyInstallDelete();
    }
}
