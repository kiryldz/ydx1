package com.ydx.test1.iconlist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
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
import com.ydx.test1.utils.Constants;
import com.ydx.test1.utils.SquareLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class AppIconsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private PackageManager manager;
    private AppIconsFragment fragment;
    private List<AppIcon> iconList;
    private List<AppIcon> iconListNew;
    private List<AppIcon> iconListFrequent;
    private List<AppIcon> iconBuffer;
    private int cellsInLineCount;

    List<AppIcon> getIconList() {
        return iconList;
    }

    void updateHeaders() {
        int bufSize = iconBuffer.size();
        for (int i=0;i<iconBuffer.size();i++) {
            AppIcon a = iconBuffer.get(i);
            if (iconListFrequent.contains(a)) {
                iconListFrequent.remove(a);
            }
            if (iconListNew.contains(a)) {
                iconListNew.remove(a);
                if (iconList.size() < Constants.ICON_LIST_SIZE_LARGE[1]) {
                    iconListNew.add(new AppIcon(Constants.EMPTY_ICON,null));
                } else {
                    iconListNew.add(null);
                }
            }
        }
        iconBuffer.clear();
        if (MainApp.isAtLeastOneItemClicked() || bufSize > 0) {
            updateHeaderLists();
        }
    }

    private void updateHeaderLists() {
        List<AppIcon> tmp = new ArrayList<>(iconList);
        Collections.sort(tmp, new Comparator<AppIcon>() {
            @Override
            public int compare(AppIcon a1, AppIcon a2) {
                return a1.getAppClickNum() < a2.getAppClickNum() ? 1 : -1;
            }
        });
        iconListFrequent = new ArrayList<>();
        for (int i = 0; i < Constants.MIN(cellsInLineCount,tmp.size()); i++) {
            iconListFrequent.add(tmp.get(i));
        }
        if (iconListFrequent.size() < cellsInLineCount) {
            int sz = iconListFrequent.size();
            for (int i=0;i<cellsInLineCount-sz;i++) {
                iconListFrequent.add(new AppIcon(Constants.EMPTY_ICON,null));
            }
        }
        AppIcon tmpIcon = null;
        for (int i=0;i<Constants.ICON_LIST_SIZE_LARGE[1];i++) {
            if (iconListNew.get(i) != null) {
                tmpIcon = iconListNew.get(i);
            } else {
                int k;
                if (tmpIcon != null) {
                    k = iconList.indexOf(tmpIcon);
                } else {
                    k = 0;
                }
                tmpIcon = iconList.get((k+1)%iconList.size());
                iconListNew.set(i,tmpIcon);
            }
        }
        notifyItemRangeChanged(1, 2 * cellsInLineCount + 1);
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.headerId);
        }
    }

    private class IconViewHolder extends RecyclerView.ViewHolder {
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

    AppIconsAdapter(final AppIconsFragment fragment
            , final PackageManager manager
            , final List<AppIcon> iconList
            , final  List<AppIcon> iconListNew
            , int cellsInLineCount
            , boolean isInitial) {
        this.fragment = fragment;
        this.manager = manager;
        this.iconList = iconList;
        this.cellsInLineCount = cellsInLineCount;
        this.iconListNew = iconListNew;
        iconBuffer = new ArrayList<>();
        if (isInitial) {
            iconListFrequent = new ArrayList<>(iconListNew);
        } else {
            updateHeaderLists();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0
                || position == cellsInLineCount+1
                || position == (cellsInLineCount+1)*2) {
            return 1;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.icon_element, parent, false);
            return new IconViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header, parent, false);
            return new HeaderHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            TextView headerText = ((HeaderHolder)holder).title;
            if (position == 0) {
                headerText.setText(headerText.getContext().getString(R.string.header_popular));
            } else if (position == cellsInLineCount+1) {
                headerText.setText(headerText.getContext().getString(R.string.header_new));
            } else {
                headerText.setVisibility(View.GONE);
            }
            return;
        }
        final int currentBlockPos;
        final AppIcon icon;
        final IconViewHolder iconViewHolder = (IconViewHolder) holder;
        if (position > 0 && position < cellsInLineCount + 1) {
            currentBlockPos = position - 1;
            icon = iconListFrequent.get(currentBlockPos);
            if (icon.getAppName().equals(Constants.EMPTY_ICON)) {
                iconViewHolder.layout.setVisibility(View.INVISIBLE);
                iconViewHolder.layout.setClickable(false);
            }
        }
        else if (position > cellsInLineCount + 1 && position < (cellsInLineCount * 2 + 2)){
            currentBlockPos = (position - 2) - cellsInLineCount;
            icon = iconListNew.get(currentBlockPos);
            if (icon.getAppName().equals(Constants.EMPTY_ICON)) {
                iconViewHolder.layout.setVisibility(View.INVISIBLE);
                iconViewHolder.layout.setClickable(false);
            }
        }
        else {
            currentBlockPos = position - (2 * cellsInLineCount + 3) ;
            icon = iconList.get(currentBlockPos);
        }
        iconViewHolder.title.setText(icon.getAppName());
        ImageView iconImage = iconViewHolder.icon;
        iconImage.setImageDrawable(icon.getAppDrawable());
        final Context context = iconImage.getContext();
        iconViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icon.appClicked();
                MainApp.setAtLeastOneItemClicked(true);
                Intent i = manager.getLaunchIntentForPackage(icon.getAppPackage());
                context.startActivity(i);
            }
        });
        iconViewHolder.layout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu
                    , View view
                    , ContextMenu.ContextMenuInfo contextMenuInfo) {
                if ((iconViewHolder.getAdapterPosition() >= (cellsInLineCount * 2 + 2))) {
                    contextMenu.add(
                            iconViewHolder.getAdapterPosition()
                            , 1
                            , 0
                            , context.getString(R.string.delete))
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                                    intent.setData(Uri.parse("package:"
                                        + icon.getAppPackage()));
                                    fragment.startActivity(intent);
                                    return false;
                                }
                            });
                    contextMenu.add(
                            iconViewHolder.getAdapterPosition()
                            , 0
                            , 0
                            , context.getString(R.string.info)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Intent i = new Intent(android.provider.Settings
                                    .ACTION_APPLICATION_DETAILS_SETTINGS);
                            i.addCategory(Intent.CATEGORY_DEFAULT);
                            i.setData(Uri.parse("package:" + icon.getAppPackage()));
                            fragment.startActivity(i);
                            return false;
                        }
                    });
                }
                contextMenu.add(
                        iconViewHolder.getAdapterPosition()
                        , 0
                        , 0
                        , context.getString(R.string.add_to_favorites)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        for (Fragment f : fragment.getFragmentManager().getFragments()) {
                            if (f instanceof AppIconsFavoritesFragment) {
                                ((AppIconsFavoritesFragment)f)
                                        .getAdapter().addIconToFavorites(icon);
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    void removeIcon(int pos) {
        AppIcon a = iconList.get(pos);
        iconList.remove(a);
        iconBuffer.add(a);
        try {
            for (Fragment f : fragment.getFragmentManager().getFragments()) {
                if (f instanceof AppIconsFavoritesFragment) {
                    ((AppIconsFavoritesFragment) f)
                            .getAdapter().removeFromFavorites(a);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        notifyItemRemoved(pos + (2 * cellsInLineCount + 3));
    }

    @Override
    public int getItemCount() {
        return iconList.size()
                + cellsInLineCount * 2
                + 3;
    }
}

