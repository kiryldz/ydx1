package com.ydx.test1.iconlist;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ydx.test1.MainApp;
import com.ydx.test1.R;
import com.ydx.test1.utils.Constants;
import com.ydx.test1.utils.SquareLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class AppIconsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppIcon> iconList;
    private List<AppIcon> iconListNew;
    private List<AppIcon> iconListFrequent;
    private List<AppIcon> iconBuffer;
    private int cellsInLineCount;

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
                    iconListNew.add(new AppIcon(Constants.EMPTY_ICON,0));
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
            iconListFrequent.get(i).setAppNamePopular(Integer.toHexString(i + 1));
        }
        if (iconListFrequent.size() < cellsInLineCount) {
            int sz = iconListFrequent.size();
            for (int i=0;i<cellsInLineCount-sz;i++) {
                iconListFrequent.add(new AppIcon(Constants.EMPTY_ICON,0));
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
                tmpIcon = iconList.get((k+1)%Constants.TOTAL_ICONS_COUNT);
                iconListNew.set(i,tmpIcon);
            }
            tmpIcon.setAppNameNew(Integer.toHexString(i+1));
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

    AppIconsAdapter(final List<AppIcon> iconList
            , final  List<AppIcon> iconListNew
            , int cellsInLineCount) {
        this.iconList = iconList;
        this.cellsInLineCount = cellsInLineCount;
        this.iconListNew = iconListNew;
        iconListFrequent = new ArrayList<>(iconListNew);
        iconBuffer = new ArrayList<>();
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

    private int calcRealPosition(int position) {
        int realPos;
        if (position > 0 && position < cellsInLineCount + 1) {
            realPos = position - 1;
        } else if (position > cellsInLineCount + 1 && position < (cellsInLineCount * 2 + 2)){
            realPos = (position - 2) - cellsInLineCount;
        }
        else {
            realPos = position - (2 * cellsInLineCount + 3) ;
        }
        return realPos;
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
            if (icon.getAppName()[0].equals(Constants.EMPTY_ICON)) {
                iconViewHolder.layout.setVisibility(View.INVISIBLE);
                iconViewHolder.layout.setClickable(false);
            } else {
                iconViewHolder.title.setText(icon.getAppName()[1]);
            }
        }
        else if (position > cellsInLineCount + 1 && position < (cellsInLineCount * 2 + 2)){
            currentBlockPos = (position - 2) - cellsInLineCount;
            icon = iconListNew.get(currentBlockPos);
            if (icon.getAppName()[0].equals(Constants.EMPTY_ICON)) {
                iconViewHolder.layout.setVisibility(View.INVISIBLE);
                iconViewHolder.layout.setClickable(false);
            } else {
                iconViewHolder.title.setText(icon.getAppName()[2]);
            }
        }
        else {
            currentBlockPos = position - (2 * cellsInLineCount + 3) ;
            icon = iconList.get(currentBlockPos);
            iconViewHolder.title.setText(icon.getAppName()[0]);
        }
        ImageView iconImage = iconViewHolder.icon;
        iconImage.setImageResource(icon.getAppDrawable());
        final Context context = iconImage.getContext();
        iconViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                icon.appClicked();
                MainApp.setAtLeastOneItemClicked(true);
                Toast.makeText(context, icon.getAppName()[0], Toast.LENGTH_SHORT)
                        .show();
            }
        });
        iconViewHolder.layout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu
                    , View view
                    , ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(
                        iconViewHolder.getAdapterPosition()
                        , 0
                        , 0
                        , context.getString(R.string.info)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder
                                .setTitle(context.getString(R.string.info))
                                .setMessage(context.getString(R.string.info_about
                                        , icon.getAppName()[0], icon.getAppClickNum()))
                                .setCancelable(true)
                                .setNeutralButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        return false;
                    }
                });
                if ((iconViewHolder.getAdapterPosition() >= (cellsInLineCount * 2 + 2))) {
                    contextMenu.add(
                            iconViewHolder.getAdapterPosition()
                            , 1
                            , 0
                            , context.getString(R.string.delete))
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    removeIcon(iconViewHolder.getAdapterPosition());
                                    return false;
                                }
                            });
                }
            }
        });
    }

    private void removeIcon(int pos) {
        AppIcon a = iconList.get(calcRealPosition(pos));
        iconList.remove(a);
        iconBuffer.add(a);
        notifyItemRemoved(pos);
    }

    @Override
    public int getItemCount() {
        return iconList.size()
                + cellsInLineCount * 2
                + 3;
    }
}

