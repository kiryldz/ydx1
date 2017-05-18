package com.ydx.test1.welcomescreen;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.ydx.test1.utils.Constants;
import com.ydx.test1.MainApp;
import com.ydx.test1.R;
import java.util.ArrayList;
import java.util.List;

import static com.ydx.test1.utils.Constants.NUM_WELCOME_PAGES;
import static com.ydx.test1.welcomescreen.WelcomeScreenFragment.fetchAccentColor;


class WelcomeScreenAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<WelcomeScreenInstance> mInstanseList = new ArrayList<>();
    private View.OnClickListener mListener;

    WelcomeScreenAdapter(Context context
            , List<WelcomeScreenInstance> mList
            , View.OnClickListener listener) {
        mContext = context;
        mInstanseList = (ArrayList<WelcomeScreenInstance>) mList;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mInstanseList.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void restartActivityForNewTheme(boolean flag) {
        MainApp.setThemeFlag(flag);
        ((Activity)mContext).recreate();
    }

    private void setSolidColorFromTheme(Button btn) {
        if (btn instanceof ToggleButton) {
            StateListDrawable gradientDrawable = (StateListDrawable) btn.getBackground();
            DrawableContainer.DrawableContainerState drawableContainerState
                    = (DrawableContainer.DrawableContainerState) gradientDrawable.getConstantState();
            Drawable[] children =
                    drawableContainerState != null
                            ? drawableContainerState.getChildren()
                            : new Drawable[0];
            GradientDrawable selectedDrawable = (GradientDrawable) children[0];
            selectedDrawable.setColor(fetchAccentColor(btn.getContext()));
        }
    }

    private void applySettings(final ViewGroup layout) {
        final ToggleButton light = (ToggleButton) layout.findViewById(R.id.lighThemeChosen);
        final ToggleButton dark = (ToggleButton) layout.findViewById(R.id.darkThemeChosen);
        final ToggleButton opt1 = (ToggleButton) layout.findViewById(R.id.cellsCountOpt1);
        final ToggleButton opt2 = (ToggleButton) layout.findViewById(R.id.cellsCountOpt2);
        if (MainApp.getThemeFlag()) {
            dark.setChecked(true);
            setSolidColorFromTheme(dark);
            dark.setClickable(false);
            light.setClickable(true);
            light.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dark.setChecked(false);
                    restartActivityForNewTheme(false);
                }
            });
        } else {
            light.setChecked(true);
            setSolidColorFromTheme(light);
            light.setClickable(false);
            dark.setClickable(true);
            dark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    light.setChecked(false);
                    restartActivityForNewTheme(true);
                }
            });
        }
        if (MainApp.getIsCellsOpt2()) {
            opt2.setChecked(true);
            setSolidColorFromTheme(opt2);
            opt2.setClickable(false);
            opt1.setClickable(true);
            opt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainApp.setIsCellsOpt2(false);
                    opt2.setChecked(false);
                    applySettings(layout);
                }
            });
        } else {
            opt1.setChecked(true);
            setSolidColorFromTheme(opt1);
            opt1.setClickable(false);
            opt2.setClickable(true);
            opt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainApp.setIsCellsOpt2(true);
                    opt1.setChecked(false);
                    applySettings(layout);
                }
            });
        }
    }

    @Override
    public Object instantiateItem (ViewGroup collection,int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout;
        if (position == NUM_WELCOME_PAGES) {
            layout = (ViewGroup)
                    inflater.inflate(R.layout.welcome_page_layout_settings, collection, false);
            applySettings(layout);
        } else {
            WelcomeScreenInstance screenInstance = mInstanseList.get(position);
            layout = (ViewGroup)
                    inflater.inflate(R.layout.welcome_page_layout, collection, false);
            TextView textView = (TextView) layout.findViewById(R.id.welcomeScreenText);
            ImageView imageView= (ImageView) layout.findViewById(R.id.welcomeScreenImage);
            imageView.setImageResource(screenInstance.getImgRes());
            textView.setText(screenInstance.getText());
        }
        Button next = (Button) layout.findViewById(R.id.welcomeNextBtn);
        next.setOnClickListener(mListener);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
