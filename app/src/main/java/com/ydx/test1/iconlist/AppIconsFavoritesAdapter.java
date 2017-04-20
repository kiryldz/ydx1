package com.ydx.test1.iconlist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ydx.test1.MainApp;
import com.ydx.test1.R;
import com.ydx.test1.utils.SquareLayout;
import java.util.ArrayList;
import java.util.List;

public class AppIconsFavoritesAdapter extends RecyclerView.Adapter<AppIconsFavoritesAdapter.IconViewHolder> {

    private List<AppIcon> favoriteList = new ArrayList<>();
    private PackageManager pm;
    private boolean toHide;

    public void setToHide(boolean flag) {
        toHide = flag;
    }

    AppIconsFavoritesAdapter(PackageManager pm, List<AppIcon> list, boolean toHide) {
        this.pm = pm;
        favoriteList = list;
        this.toHide = toHide;
    }

    void addIconToFavorites(AppIcon appIcon) {
        if (!getFavoritePackages().contains(appIcon.getAppPackage())) {
            favoriteList.add(appIcon);
            notifyItemInserted(favoriteList.size() - 1);
        }
    }

    private ArrayList<String> getFavoritePackages() {
        ArrayList<String> favPackagesList = new ArrayList<>();
        for (AppIcon a : favoriteList) {
            favPackagesList.add(a.getAppPackage());
        }
        return favPackagesList;
    }

    List<AppIcon> getFavoriteList() {
        return this.favoriteList;
    }

    void removeFromFavorites(AppIcon a) {
        String packageName = a.getAppPackage();
        for (int i=0; i<favoriteList.size(); i++) {
            AppIcon aa = favoriteList.get(i);
            if (packageName.equals(aa.getAppPackage())) {
                int pos = favoriteList.indexOf(aa);
                favoriteList.remove(aa);
                notifyItemRemoved(pos);
            }
        }
    }

    public void clearFavoriteList() {
        favoriteList.clear();
    }

    class IconViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        SquareLayout layout;

        IconViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.app_title);
            icon = (ImageView) view.findViewById(R.id.app_icon);
            layout = (SquareLayout) view.findViewById(R.id.app_icon_whole);
        }
    }

    @Override
    public IconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IconViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_element, parent, false));
    }

    @Override
    public void onBindViewHolder(final IconViewHolder holder, int position) {
        if (toHide) {
            holder.layout.setVisibility(View.GONE);
            return;
        } else {
            holder.layout.setVisibility(View.VISIBLE);
        }
        final AppIcon icon = favoriteList.get(position);
        ImageView iconImage = holder.icon;
        iconImage.setImageDrawable(icon.getAppDrawable());
        holder.title.setText(icon.getAppName());
        final Context context = iconImage.getContext();
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icon.appClicked();
                MainApp.setAtLeastOneItemClicked(true);
                Intent i = pm.getLaunchIntentForPackage(icon.getAppPackage());
                context.startActivity(i);
            }
        });
        holder.layout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu
                    , View view
                    , ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(
                        holder.getAdapterPosition()
                        , 1
                        , 0
                        , context.getString(R.string.delete_from_favorites))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                int pos = holder.getAdapterPosition();
                                favoriteList.remove(pos);
                                notifyItemRemoved(pos);
                                return false;
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }
}
