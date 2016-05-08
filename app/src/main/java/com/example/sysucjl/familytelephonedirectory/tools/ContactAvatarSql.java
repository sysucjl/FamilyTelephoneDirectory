package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/5/4.
 */
public class ContactAvatarSql extends SQLiteOpenHelper{

    public static final String TB_NAME="users_avatar";
    public static final String ID="id";
    public static final String AVATAR_PATH="avatar_path";

    public ContactAvatarSql(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + "(" +
                "id varchar primary key," +   //0
                "avatar_path varchar" +      //1
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
