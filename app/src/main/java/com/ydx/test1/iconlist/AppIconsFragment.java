package com.ydx.test1.iconlist;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ydx.test1.utils.Constants;
import com.ydx.test1.MainApp;
import com.ydx.test1.R;
import static com.ydx.test1.utils.Constants.TOTAL_ICONS_COUNT;
import static com.ydx.test1.utils.Constants.imageList;


public class AppIconsFragment extends Fragment {

    private List<AppIcon> iconList = new ArrayList<>();
    private List<AppIcon> iconListNew = new ArrayList<>();
    private AppIconsAdapter mAdapter;
    private final String ARRAY_STATE_KEY = "ARRAY_STATE";
    private final String ARRAY_NEW_STATE_KEY = "ARRAY_STATE_NEW";

    private void generateNewList() {
        iconListNew = new ArrayList<>();
        Random r = new Random();
        int startPos = r.nextInt(TOTAL_ICONS_COUNT) - (Constants.ICON_LIST_SIZE_LARGE[1]+1);
        if (startPos < 0) startPos = 0;
        for (int i=startPos;i<Constants.ICON_LIST_SIZE_LARGE[1]+startPos;i++) {
            if (i-startPos<iconList.size()) {
                iconListNew.add(iconList.get(i));
            } else {
                iconListNew.add(new AppIcon(Constants.EMPTY_ICON,0));
            }
            iconListNew.get(i-startPos).setAppNameNew(Integer.toHexString(i-startPos+1));
            iconListNew.get(i-startPos).setAppNamePopular(Integer.toHexString(i-startPos+1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icon_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (savedInstanceState == null) {
            generateFakeData();
            generateNewList();
        } else {
            iconList = savedInstanceState.getParcelableArrayList(ARRAY_STATE_KEY);
            iconListNew = savedInstanceState.getParcelableArrayList(ARRAY_NEW_STATE_KEY);
        }

        final int cellsInLineCount;
        if (MainApp.getIsCellsOpt2()) {
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
        mAdapter = new AppIconsAdapter(iconList, iconListNew, cellsInLineCount);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private void generateFakeData() {
        for (int i=0; i<TOTAL_ICONS_COUNT; i++) {
            AppIcon icon = new AppIcon(Integer.toHexString(i+1)
                    , (i + (i / imageList.length))
                        % imageList.length);
            iconList.add(icon);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelableArrayList(ARRAY_STATE_KEY
                , (ArrayList<? extends Parcelable>) iconList);
        state.putParcelableArrayList(ARRAY_NEW_STATE_KEY
                , (ArrayList<? extends Parcelable>) iconListNew);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainApp.isFirstLaunch()) {
            MainApp.setIsFirstLaunch(false);
            return;
        }
        if (mAdapter != null) mAdapter.updateHeaders();
    }

}
