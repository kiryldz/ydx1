package com.ydx.test1.welcomescreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;
import com.ydx.test1.utils.Constants;
import com.ydx.test1.R;
import com.ydx.test1.iconlist.AppIconsActivity;

import java.util.ArrayList;

import static com.ydx.test1.utils.Constants.NUM_WELCOME_PAGES;
import static com.ydx.test1.utils.Constants.welcomeScreenPictures;
import static com.ydx.test1.utils.Constants.welcomeScreenText;


public class WelcomeScreenFragment extends Fragment {

    private ViewPager mViewPager;
    private ArrayList<WelcomeScreenInstance> mList = new ArrayList<>();

    private void generateFakeData() {
        for (int i = 0; i < NUM_WELCOME_PAGES; i++) {
            if (i == 0) {
                String tmp = getString(R.string.app_name)
                        + System.getProperty("line.separator")
                        + getString(welcomeScreenText[i]);
                mList.add(new WelcomeScreenInstance(tmp,welcomeScreenPictures[i]));
            } else {
                mList.add(new WelcomeScreenInstance(
                        getString(welcomeScreenText[i])
                        ,welcomeScreenPictures[i]));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int ind_to_start = getActivity().getIntent().getIntExtra("num_to_start",0);
        View view = inflater.inflate(R.layout.fragment_welcome_screen, container, false);
        generateFakeData();
        mViewPager = (ViewPager) view.findViewById(R.id.welcomeScreenViewPager);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NUM_WELCOME_PAGES == mViewPager.getCurrentItem()) {
                    startActivity(new Intent(getActivity(),AppIconsActivity.class));
                    getActivity().finish();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }
        };
        WelcomeScreenAdapter mAdapter = new WelcomeScreenAdapter(getContext(), mList, listener);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(ind_to_start);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)
                view.findViewById(R.id.titles);
        circlePageIndicator.setRadius(20f);
        circlePageIndicator.setFillColor(Constants.fetchAccentColor(getContext()));
        circlePageIndicator.setViewPager(mViewPager);
        return view;
    }

}
