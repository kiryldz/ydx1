package com.ydx.test1.contacts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ydx.test1.R;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder> {

    private List<ContactVO> contactVOList;
    private Context mContext;

    AllContactsAdapter(List<ContactVO> contactVOList, Context mContext) {
        this.contactVOList = new ArrayList<>(contactVOList);
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view
                , parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, int position) {
        final ContactVO contactVO = contactVOList.get(position);
        holder.tvContactName.setText(contactVO.getContactName());
        holder.tvPhoneNumber.setText(contactVO.getContactNumber());
        if (contactVO.getContactImage() != null) {
            holder.ivContactImage.setImageURI(Uri.parse(contactVO.getContactImage()));
        } else {
            holder.ivContactImage
                    .setImageDrawable(ContextCompat
                            .getDrawable(mContext, R.drawable.ic_action_person));
        }
        holder.lyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactVO.getContactNumber()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    mContext.startActivity(callIntent);
                }
            }
        });
        holder.lyContact.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu
                    , View view
                    , ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(
                        holder.getAdapterPosition()
                        , 0
                        , 0
                        , mContext.getString(R.string.add_to_favorites)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        SharedPreferences.Editor prefs = mContext
                                .getSharedPreferences("contacts_storage", MODE_PRIVATE).edit();
                        String set = "";
                        set = set.concat(contactVO.getContactImage() == null
                                ? "" : contactVO.getContactImage()) + "|";
                        set = set.concat(contactVO.getContactNumber()) + "|";
                        set = set.concat(contactVO.getContactEmail() == null
                                ? "" : contactVO.getContactEmail()) + "|";
                        prefs.putString(contactVO.getContactName()
                                , set.substring(0, set.length()-1)).apply();
                        return false;
                    }
                });
                contextMenu.add(
                        holder.getAdapterPosition()
                        , 0
                        , 0
                        , mContext.getString(R.string.info)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(mContext, contactVO.getContactEmail() == null
                                    ? mContext.getString(R.string.no_email)
                                    : mContext.getString(R.string.email)
                                        + contactVO.getContactEmail()
                                ,Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;
        LinearLayout lyContact;

        ContactViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            lyContact = (LinearLayout) itemView.findViewById(R.id.lyContact);
        }
    }
}
