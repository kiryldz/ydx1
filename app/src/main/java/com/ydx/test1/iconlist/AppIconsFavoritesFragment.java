package com.ydx.test1.iconlist;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ydx.test1.R;
import com.ydx.test1.utils.Constants;
import com.ydx.test1.utils.UrlInstance;
import com.ydx.test1.utils.UrlReaderDbHelper;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AppIconsFavoritesFragment extends Fragment {

    private AppIconsFavoritesAdapter mAdapter;
    private List<AppIcon> favoriteList = new ArrayList<>();
    private UrlReaderDbHelper helper;
    private PackageManager packageManager;
    private int URLlimit;

    public void setURLlimit(int URLlimit) {
        this.URLlimit = URLlimit;
    }

    public static AppIconsFavoritesFragment newInstance() {
        return new AppIconsFavoritesFragment();
    }

    public AppIconsFavoritesAdapter getAdapter() {
        return mAdapter;
    }

    public void clearURLinDB() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(UrlInstance.UrlEntry.TABLE_NAME, null, null);
        db.close();
    }

    private void insertURLtoDB(String s) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UrlInstance.UrlEntry.COLUMN_NAME_URL, s);
        values.put(UrlInstance.UrlEntry.COLUMN_NAME_TIMESTAMP, System.currentTimeMillis());
        db.insert(
                UrlInstance.UrlEntry.TABLE_NAME,
                null,
                values);
        db.close();
    }

    private CharSequence[] getURLfromDB() {
        ArrayList<String> urls = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] projection = {
                UrlInstance.UrlEntry.COLUMN_NAME_URL
        };
        String sortOrder =
                UrlInstance.UrlEntry.COLUMN_NAME_TIMESTAMP + " DESC";
        Cursor c = db.query(
                true,
                UrlInstance.UrlEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                String.valueOf(URLlimit)
        );
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    for (String cn : c.getColumnNames()) {
                        urls.add(c.getString(c.getColumnIndex(cn)));
                    }

                } while (c.moveToNext());
            }
            c.close();
        }
        db.close();
        return urls.toArray(new CharSequence[urls.size()]);
    }

    private void startURLIntent(String text, String http, boolean toDB) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW
                , Uri.parse(http + text));
        startActivity(browserIntent);
        if (toDB) {
            insertURLtoDB(text);
        }
    }

    private void openURL(String enteredText, boolean toDB) {
        try {
            startURLIntent(enteredText, "", toDB);
        } catch (ActivityNotFoundException ex1) {
            try {
                startURLIntent(enteredText, "http://", toDB);
            } catch (ActivityNotFoundException ex2) {
                Toast.makeText(getContext()
                        , getContext().getString(R.string.invalid_uri)
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initButtons(View view) {
        Button enterBtn = (Button) view.findViewById(R.id.enterURLbtn);
        final EditText editText = (EditText) view.findViewById(R.id.editTextURL);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredText = editText.getText().toString();
                if (enteredText.length() > 0) {
                    openURL(enteredText, true);
                }
            }
        });
        Button histBtn = (Button) view.findViewById(R.id.histURLbtn);
        histBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.choose_history));
                final CharSequence[] urls = getURLfromDB();
                builder.setItems(urls, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openURL(urls[which].toString(), false);
                        dialog.dismiss();
                    }
                });
                AlertDialog d = builder.show();
                d.getListView().setFastScrollEnabled(true);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icon_list_favorites,
                container, false);

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        URLlimit = Integer.valueOf(PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString("URL_limit","5"));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        helper = new UrlReaderDbHelper(getContext());

        final int cellsInLineCount;
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
        packageManager = getActivity().getPackageManager();

        GridLayoutManager mLayoutManager
                = new GridLayoutManager(getActivity().getApplicationContext()
                , cellsInLineCount);
        recyclerView.setLayoutManager(mLayoutManager);
        restoreFavoriteList();
        mAdapter = new AppIconsFavoritesAdapter(packageManager
                , favoriteList
                , PreferenceManager
                    .getDefaultSharedPreferences(getContext())
                    .getBoolean("hide_favs",false));
        recyclerView.setAdapter(mAdapter);
        initButtons(view);
        return view;
    }

    private void restoreFavoriteList() {
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("apps_storage", MODE_PRIVATE);
        String json = prefs.getString("favorite_list", "");
        if (json.equals("")) {
            return;
        }
        Type type = new TypeToken< List < String[] >>() {}.getType();
        List<String[]> iconListFromStorage = gson.fromJson(json, type);
        generateAppListFromJson(iconListFromStorage);
    }

    private void generateAppListFromJson(List<String[]> list) {
        for (String[] jsonSet : list) {
            AppIcon app ;
            try {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(jsonSet[0], 0);
                app = new AppIcon(packageManager.getApplicationLabel(appInfo).toString()
                        , jsonSet[0]
                        , packageManager.getApplicationIcon(appInfo));
                app.setAppClicks(Integer.valueOf(jsonSet[1]));
                app.setInstallTime(Long.valueOf(jsonSet[2]));
                favoriteList.add(app);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("apps_storage", MODE_PRIVATE);
        Gson gson = new Gson();
        List<String[]> list = new ArrayList<>();
        for (AppIcon a : getAdapter().getFavoriteList()) {
            list.add(a.getGSONset());
        }
        String json = gson.toJson(list);
        prefs.edit().putString("favorite_list", json).apply();
    }
}
