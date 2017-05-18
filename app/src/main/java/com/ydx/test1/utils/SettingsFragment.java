package com.ydx.test1.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;
import com.ydx.test1.R;
import com.ydx.test1.iconlist.AppIconsActivity;
import com.ydx.test1.iconlist.AppIconsFavoritesAdapter;
import com.ydx.test1.iconlist.AppIconsFavoritesFragment;
import com.ydx.test1.imageloader.ImageService;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);
        CheckBoxPreference hideFavs = (CheckBoxPreference) findPreference("hide_favs");
        hideFavs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                FragmentManager fm = getFragmentManager();
                for (Fragment f : fm.getFragments()) {
                    if (f instanceof AppIconsFavoritesFragment) {
                        AppIconsFavoritesAdapter adapter
                                = ((AppIconsFavoritesFragment) f).getAdapter();
                        adapter.setToHide(newValue.toString().equals("true"));
                        adapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });

        CheckBoxPreference hideFavsFragment
                = (CheckBoxPreference) findPreference("hide_favs_fragment");
        hideFavsFragment.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AppIconsActivity.LauncherPagerAdapter adapter
                        =((AppIconsActivity)getActivity()).getmAdapter();
                if (newValue.toString().equals("true")) {
                    adapter.disableFavoriteFragment();
                } else {
                    adapter.enableFavoriteFragment();
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        Preference delFavs = findPreference("del_favs");
        delFavs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getFragmentManager();
                for (Fragment f : fm.getFragments()) {
                    if (f instanceof AppIconsFavoritesFragment) {
                        AppIconsFavoritesAdapter adapter
                                = ((AppIconsFavoritesFragment) f).getAdapter();
                        adapter.clearFavoriteList();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext()
                                , getString(R.string.fav_cleared)
                                , Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        final EditTextPreference URLlim = (EditTextPreference) findPreference("URL_limit");
        URLlim.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String prevValidValue = ((EditTextPreference)preference).getText();
                try {
                    FragmentManager fm = getFragmentManager();
                    for (Fragment f : fm.getFragments()) {
                        if (f instanceof AppIconsFavoritesFragment) {
                            ((AppIconsFavoritesFragment)f)
                                    .setURLlimit(Integer.valueOf(newValue.toString()));
                        }
                    }
                } catch (NumberFormatException e) {
                    ((EditTextPreference)preference).setText(prevValidValue);
                    Toast.makeText(getContext()
                            , getString(R.string.wrong_format)
                            , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        Preference URLclear = findPreference("URL_clear");
        URLclear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getFragmentManager();
                for (Fragment f : fm.getFragments()) {
                    if (f instanceof AppIconsFavoritesFragment) {
                        ((AppIconsFavoritesFragment)f).clearURLinDB();
                        Toast.makeText(getContext()
                                , getString(R.string.URL_cleared)
                                , Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        ListPreference themeSelector = (ListPreference) findPreference("change_theme");
        ListPreference cellsSelector = (ListPreference) findPreference("change_cells");
        themeSelector.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().recreate();
                return true;
            }
        });
        cellsSelector.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().recreate();
                return true;
            }
        });

        Preference updPictNow = findPreference("upd_pict_now");
        updPictNow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Context context = SettingsFragment.this.getActivity();
                Intent i = new Intent(context, ImageService.class);
                i.putExtra("ZERO_DELAY", true);
                context.stopService(i);
                context.startService(i);
                Toast.makeText(getContext()
                        , getString(R.string.updating)
                        , Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ListPreference changeUpdPictInterval = (ListPreference) findPreference("change_upd_pict_interval");
        changeUpdPictInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Context context = SettingsFragment.this.getActivity();
                Intent i = new Intent(context, ImageService.class);
                context.stopService(i);
                context.startService(i);
                return true;
            }
        });
    }
}
