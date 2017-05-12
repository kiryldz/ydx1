package com.ydx.test1.contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.ydx.test1.R;
import java.util.ArrayList;
import java.util.List;

public class AllContactsActivity extends Activity {

    RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getString("change_theme","1").equals("1")) {
            super.setTheme(R.style.AppThemeDark);
        } else {
            super.setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_all_contacts);
        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        getAllContacts();
    }

    private void getAllContacts() {
        List<ContactVO> contactVOList = new ArrayList<>();
        ContactVO contactVO;
        ContentResolver contentResolver = getContentResolver();
        try {
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI
                    , null
                    , null
                    , null
                    , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String image_uri = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                        contactVO = new ContactVO();
                        contactVO.setContactName(name);
                        contactVO.setContactImage(image_uri);

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                        if (phoneCursor != null && phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactVO.setContactNumber(phoneNumber);
                        }

                        if (phoneCursor != null) {
                            phoneCursor.close();
                        }
                        Cursor emailCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCursor != null && emailCursor.moveToNext()) {
                            String emailId = emailCursor
                                    .getString(emailCursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Email.DATA));
                            contactVO.setContactEmail(emailId);
                        }
                        if (emailCursor != null) {
                            emailCursor.close();
                        }
                        contactVOList.add(contactVO);
                    }
                }
                cursor.close();
                AllContactsAdapter contactAdapter = new AllContactsAdapter(contactVOList, getApplicationContext());
                rvContacts.setLayoutManager(new LinearLayoutManager(this));
                rvContacts.setAdapter(contactAdapter);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
