package com.example.sysucjl.familytelephonedirectory.tools;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.sysucjl.familytelephonedirectory.data.ContactItem;
import com.example.sysucjl.familytelephonedirectory.data.RecordItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sysucjl on 16-4-4.
 */
public class ContactOptionManager {

    public static Map<String, String> mNumAndId = new HashMap<>();
    public static Map<String, String> mNumAndPhoto = new HashMap<>();

    //读取通讯记录
    public List<RecordItem> getCallLog(Context context) {
        /* Query the CallLog Content Provider */
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int id_index = cursor.getColumnIndex(CallLog.Calls._ID);
        int number_index = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type_index = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date_index = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration_index = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int name_index = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        List<RecordItem> recordItems = new ArrayList<>();
        String lastphNum = "";
        while (cursor.moveToNext()) {
            //通话记录ID
            int callid = cursor.getInt(id_index);
            //号码
            String phNum = cursor.getString(number_index);
            //通话类型
            int callcode = Integer.parseInt(cursor.getString(type_index));
            //通话日期
            long callDate = Long.valueOf(cursor.getString(date_index));
            //通话时间
            long callDuration = Long.valueOf(cursor.getString(duration_index));
            //联系人姓名
            String name = cursor.getString(name_index);

            if (phNum.equals(lastphNum) && recordItems.size() > 0) {
                recordItems.get(recordItems.size() - 1).addRecordSegement(callid,callcode, callDate, callDuration);
            } else {
                RecordItem item = new RecordItem(
                        callid, callcode, callDate, callDuration, phNum);
                if(name != null) {
                    ContactItem contactItem = new ContactItem(name);
                    String photo_uri = mNumAndPhoto.get(phNum);
                    String contact_id = mNumAndId.get(phNum);
                    if(contact_id != null){
                        contactItem.setmContactId(contact_id);
                        contactItem.setmAvatar(photo_uri);
                        item.setmContactItem(contactItem);
                        recordItems.add(item);
                    }else{
                        contact_id = getContactIdByNumber(phNum, context);
                        if(contact_id != null){
                            contactItem.setmContactId(contact_id);
                            mNumAndId.put(phNum, contact_id);
                            photo_uri = getContactPhotoUriById(contact_id, context);
                            if(photo_uri != null){
                                contactItem.setmAvatar(photo_uri);
                                mNumAndPhoto.put(phNum, photo_uri);
                            }
                            item.setmContactItem(contactItem);
                            recordItems.add(item);
                        }
                    }
                }else{
                    recordItems.add(item);
                }
            }
            lastphNum = phNum;
        }
        cursor.close();
        return recordItems;
    }

    private String getContactIdByNumber(String phoneNumber, Context context){
        String result = null;
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        try {
            Cursor cursor = contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

                    result = contactId;
                }
                cursor.close();
            }
        }catch (Exception e){

        }
        return result;
    }

    private String getContactPhotoUriById(String id, Context context){
        String result = null;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI;
        ContentResolver contentResolver = context.getContentResolver();
        // 获取手机联系人
        Cursor cursor = contentResolver.query(
                Uri.parse("content://com.android.contacts/contacts"),
                null,
                ID +"="+ id, null, "sort_key");
        if(cursor.getCount() > 0 && cursor != null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex(PHOTO_URI));
        }
        return result;
    }


    //删除通讯记录
    public void deleteRecord(Context context, int id) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + "=?", new String[]{id + ""});
    }

    //读取联系人列表
    public List<ContactItem> getBriefContactInfor(Context context) {
        //定义常量，节省重复引用的时间
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI;
        //临时变量
        String contactId;
        String displayName;
        //生成ContentResolver对象
        ContentResolver contentResolver = context.getContentResolver();
        // 获取手机联系人
        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/contacts"), null, null, null, "sort_key");
        List<ContactItem> friendList = new ArrayList<>();
        // 无联系人直接返回
        if (!cursor.moveToFirst()) {//moveToFirst定位到第一行
            return null;
        }
        do {
            // 获得联系人的ID：String类型  列名-->列数-->列内容
            contactId = cursor.getString(cursor.getColumnIndex(ID));
            // 获得联系人姓名：String类型
            displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
            if(displayName != null) {
                // 查看联系人有多少个号码，如果没有号码，返回0
                int phoneCount = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));

                String avatar_uri = cursor.getString(cursor.getColumnIndex(PHOTO_URI));

                ContactItem item;
                item = new ContactItem(displayName);
                item.setmContactId(contactId);
                item.setmPhoneCount(phoneCount);
                if(avatar_uri != null) {
                    item.setmAvatar(avatar_uri);
                    //System.out.println("头像:"+avatar_uri);
                }
                System.out.println(displayName+" "+contactId);
                friendList.add(item);
            }
        } while (cursor.moveToNext());
        return friendList;
    }

    public void deleteContact(Context context, String id){
        //定义常量，节省重复引用的时间
        String ID = ContactsContract.Contacts._ID;
        //生成ContentResolver对象
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(Uri.parse("content://com.android.contacts/contacts"),
                ID+"="+ id, null);
    }

    //读取单个联系人详细信息
    public ContactItem getDetailFromContactID(Context context, String id, String name) {
        Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

        String CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;

        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String PHONR_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

        String EMAIL = ContactsContract.CommonDataKinds.Email.ADDRESS;
        String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;


        ContentResolver contentResolver = context.getContentResolver();
        List<String> phoneList = new ArrayList<>();
        ContactItem item = new ContactItem(name);
        if (id == null) {
            return null;
        }

        //获取电话号码
        Cursor phoneCursor;
        Map<String, Integer> phones = new HashMap<>();
        phoneCursor = contentResolver.query(PHONE_CONTENT_URI, null,
                CONTACT_ID + "=" + id, null, null);
        if (phoneCursor.moveToFirst()) {
            do {
                String str_phone = phoneCursor.getString(
                        phoneCursor.getColumnIndex(NUMBER));
                int int_phone_type = phoneCursor.getInt(
                        phoneCursor.getColumnIndex(PHONR_TYPE));

                if (str_phone != null) {
                    phones.put(str_phone, int_phone_type);
                }
            } while (phoneCursor.moveToNext());
            item.setmPhones(phones);
        }

        //获取邮箱
        Cursor emailCursor;
        Map<String, Integer> emails = new HashMap<>();
        emailCursor = contentResolver.query(EMAIL_CONTENT_URI, null,
                CONTACT_ID + "=" + id, null, null);
        if(emailCursor.moveToFirst()){
            do{
                String str_email = emailCursor.getString(
                        emailCursor.getColumnIndex(EMAIL));
                int int_email_type = emailCursor.getInt(
                        emailCursor.getColumnIndex(EMAIL_TYPE));
                if(str_email != null) {
                    emails.put(str_email, int_email_type);
                    System.out.println(str_email);
                }
            }while (emailCursor.moveToNext());
            item.setmEmails(emails);
        }
        return item;
    }
}
