package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/5/4.
 */
public class AvatarDataHelper {
    //数据库名称
    private static String DB_NAME = "contactavatar.db";
    //数据库版本
    private static int DB_VERSION = 2;

    private SQLiteDatabase mDatabase;
    private ContactAvatarSql mContactAvatarHelper;

    public AvatarDataHelper(Context context){
        mContactAvatarHelper = new ContactAvatarSql(context, DB_NAME, null, DB_VERSION);
        mDatabase = mContactAvatarHelper.getWritableDatabase();
    }

    public void close(){
        mContactAvatarHelper.close();
        mDatabase.close();
    }

    //查询是否有某个ID
    public boolean hasContact(String id){
        Cursor cursor = mDatabase.query(ContactAvatarSql.TB_NAME, null, ContactAvatarSql.ID + "=" + id,
                null, null, null, null);
        if(cursor.moveToFirst())
            return true;
        else
            return false;
    }

    public void saveAvatarPath(String id, String path){
        if(hasContact(id)){
            update(id, path);
        }else{
            insert(id, path);
        }
    }

    public void insert(String id, String avatarPath){
        ContentValues values = new ContentValues();
        values.put(ContactAvatarSql.ID, id);
        values.put(ContactAvatarSql.AVATAR_PATH, avatarPath);
        mDatabase.insert(ContactAvatarSql.TB_NAME, null, values);
    }

    public String query(String id){
        Cursor cursor = mDatabase.query(ContactAvatarSql.TB_NAME, null, ContactAvatarSql.ID + "=" + id,
                null, null, null, null);
        if(cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(ContactAvatarSql.AVATAR_PATH));
        }
        return null;
    }

    public void update(String id, String avatarPath){
        ContentValues values = new ContentValues();
        values.put(ContactAvatarSql.AVATAR_PATH, avatarPath);
        mDatabase.update(ContactAvatarSql.TB_NAME, values, ContactAvatarSql.ID+"="+id,
                null);
    }

    public void delete(String id){
        mDatabase.delete(ContactAvatarSql.TB_NAME, ContactAvatarSql.ID+"="+id,
                null);
    }
}
