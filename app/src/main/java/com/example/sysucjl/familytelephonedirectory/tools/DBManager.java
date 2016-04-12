package com.example.sysucjl.familytelephonedirectory.tools;

/**
 * Created by Administrator on 2016/4/11.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 16  * @author Joshua
 17  * 用法：
 18  * DBHelper dbHelper = new DBHelper(this);
 19  * dbHelper.createDataBase();
 20  * SQLiteDatabase db = dbHelper.getWritableDatabase();
 21  * Cursor cursor = db.query()
 22  * db.execSQL(sqlString);
 23  * 注意：execSQL不支持带;的多条SQL语句，只能一条一条的执行，晕了很久才明白
 24  * 见execSQL的源码注释 (Multiple statements separated by ;s are not supported.)
 25  * 将把assets下的数据库文件直接复制到DB_PATH，但数据库文件大小限制在1M以下
 26  * 如果有超过1M的大文件，则需要先分割为N个小文件，然后使用copyBigDatabase()替换copyDatabase()
 27  */
/* 将把assets下的数据库文件直接复制到DB_PATH，但数据库文件大小限制在1M以下
        * 如果有超过1M的大文件，则需要先分割为N个小文件，然后使用copyBigDatabase()替换copyDatabase()
        */
public class DBManager extends SQLiteOpenHelper {
    //用户数据库文件的版本
    private static final int DB_VERSION = 1;
    //数据库文件目标存放路径为系统默认位置，com.rys.lb 是你的包名
    private static String DB_PATH = "/data/data/com.example.sysucjl.familytelephonedirectory/databases/";

//如果你想把数据库文件存放在SD卡的话
// private static String DB_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
// + "/arthurcn/drivertest/packfiles/";

    private static String DB_NAME = "callhome.db";
    private static String ASSETS_NAME = "callhomedb_db";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    //private static String DB_PATH;
    /**
     * 如果数据库文件较大，使用FileSplit分割为小于1M的小文件
     * 此例中分割为 data.db.100 data.db.101 data.db.102....
     */
//第一个文件名后缀
    private static final int ASSETS_SUFFIX_BEGIN = 101;
    //最后一个文件名后缀
    private static final int ASSETS_SUFFIX_END = 112;

    /**
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数
     *
     * @param context 上下文对象
     * @param name    数据库名称
     * @param factory 一般都是null
     * @param version 当前数据库的版本，值必须是整数并且是递增的状态
     */
    public DBManager(Context context, String name, CursorFactory factory, int version) {
//必须通过super调用父类当中的构造函数
        super(context, name, null, version);
        this.myContext = context;
        //DB_PATH = myContext.getFilesDir().getParentFile().getAbsolutePath()+"/databases";

    }

    public DBManager(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DBManager(Context context, String name) {
        this(context, name, DB_VERSION);
    }

    public DBManager(Context context) {
        this(context, DB_PATH + DB_NAME);
    }

    public void createDataBase()  {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //数据库已存在，do nothing.
            System.out.println("数据库已经存在");
        } else {
            //创建数据库
            try {
                File dir = new File(DB_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dbf = new File(DB_PATH + DB_NAME);
                if (dbf.exists()) {
                    dbf.delete();
                }
                SQLiteDatabase.openOrCreateDatabase(dbf, null);
                // 复制asseets中的db文件到DB_PATH下
                //copyDataBase();
                copyBigDataBase();
            } catch (IOException e) {
                throw new Error("数据库创建失败");
            }
        }
    }

    //检查数据库是否有效
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
//database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
            System.out.println("关闭");
        }
        return checkDB != null ? true : false;
    }

    public DBManager open1() {
        String myPath = DB_PATH + DB_NAME;
        System.out.println("数据库已经...");
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        System.out.println("数据库打开");
        return this;

    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
//Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(ASSETS_NAME);
// Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
//Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
//transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
//Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    //复制assets下的大数据库文件时用这个
    private void copyBigDataBase() throws IOException {
        InputStream myInput;
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        for (int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END + 1; i++) {
            myInput = myContext.getAssets().open(ASSETS_NAME + "." + i);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myInput.close();
        }
        myOutput.close();
        System.out.println("数据库已经复制");
    }
    //
//    //用于得到归属地的数据库
//    public DBManager getInstance(Context context){
//        DBManager dbHelper = new DBManager(context,DB_NAME, null, 1);
//        return dbHelper;
//    }
    public String getResult(String num){
        DBManager dbHelper2=new DBManager(myContext,"callhome.db",null,1);
        SQLiteDatabase db=dbHelper2.getReadableDatabase();
        String number=num;
        String result=null;
        if (number.length() > 7) {
            String firstNum = number.substring(0, 1);
            if (number.length() >= 10) {
                if ("0".equals(firstNum)) {
                    String s1 = number.substring(1);
                    String s2 = s1;
                    String second = s1.substring(0, 1);
                    if (second.equals("1") || second.equals("2")) {
                        s2 = s1.substring(0, 2);
                    } else {
                        s2 = s1.substring(0, 3);
                    }
                    String sql = "select location from tel_location where _id = ? ";
                    String[] param = new String[] { s2 };
                    if (db != null && db.isOpen()) {
                        Cursor cursor = db.rawQuery(sql, param);
                        if (cursor.moveToNext()) {
                            result = cursor.getString(0)+"固话";
                        }
                        cursor.close();
                    }
                } else {
                    if (number.indexOf("+86") == 0) {
                        number = number.substring(3);
                    }
                    if (number.indexOf("86") == 0) {
                        number = number.substring(2);
                    }
                    String s1 = number.substring(0, 7);
                    String sql = "select location from mob_location where _id = ? ";
                    String[] param = new String[] { s1 };
                    if (db != null && db.isOpen()) {
                        Cursor cursor = db.rawQuery(sql, param);
                        if (cursor.moveToNext()) {
                            int middleIndex=0;
                            String temp = cursor.getString(0);
                            for(int i=0;i<temp.length();i++){
                                if(temp.charAt(i)==' '){
                                    middleIndex=i;
                                    break;
                                }
                            }
                            result=temp.substring(0,middleIndex)+temp.substring(middleIndex+1);
                            //result=temp;
                        }
                        cursor.close();
                    }
                }
            } else {
                result = "本地号码";
            }
        } else {
            if (number.length() < 4) {
                result = "未知号码";
            } else {
                result = "本地号码";
            }
        }
        return result;
    }



    public String getCityName(String num){
        DBManager dbHelper2=new DBManager(myContext,"callhome.db",null,1);
        SQLiteDatabase db=dbHelper2.getReadableDatabase();
        String number=num;
        String result=null;
        if (number.length() > 7) {
            String firstNum = number.substring(0, 1);
            if (number.length() >= 10) {
                if ("0".equals(firstNum)) {
                    String s1 = number.substring(1);
                    String s2 = s1;
                    String second = s1.substring(0, 1);
                    if (second.equals("1") || second.equals("2")) {
                        s2 = s1.substring(0, 2);
                    } else {
                        s2 = s1.substring(0, 3);
                    }
                    String sql = "select location from tel_location where _id = ? ";
                    String[] param = new String[] { s2 };
                    if (db != null && db.isOpen()) {
                        Cursor cursor = db.rawQuery(sql, param);
                        if (cursor.moveToNext()) {
                            //result = cursor.getString(0)+"固话";
                            result = cursor.getString(0).substring(2);
                        }
                        cursor.close();
                    }
                } else {
                    if (number.indexOf("+86") == 0) {
                        number = number.substring(3);
                    }
                    if (number.indexOf("86") == 0) {
                        number = number.substring(2);
                    }
                    String s1 = number.substring(0, 7);
                    String sql = "select location from mob_location where _id = ? ";
                    String[] param = new String[] { s1 };
                    if (db != null && db.isOpen()) {
                        Cursor cursor = db.rawQuery(sql, param);
                        if (cursor.moveToNext()) {
                            int middleIndex=0;
                            String temp = cursor.getString(0);
                            for(int i=0;i<temp.length();i++){
                                if(temp.charAt(i)==' '){
                                    middleIndex=i;
                                    break;
                                }
                            }
                           // result=temp.substring(0,middleIndex)+temp.substring(middleIndex+1);
                            result=temp.substring(2,middleIndex);
                            //result=temp;
                        }
                        cursor.close();
                    }
                }
            } else {
                result = "本地号码";
            }
        } else {
            if (number.length() < 4) {
                result = "未知号码";
            } else {
                result = "本地号码";
            }
        }
        return result;
    }


    @Override
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
            System.out.println("关闭成功1");
        }
        super.close();
        System.out.println("关闭成功2");
    }

    /**
     * 该函数是在第一次创建的时候执行，
     * 实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * 数据库表结构有变化时采用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void open() {
        SQLiteDatabase DataBase = this.openOrCreateDatabase("data.db",

                null);
    }

    private SQLiteDatabase openOrCreateDatabase(String string, Object object) {
// TODO Auto-generated method stub
        return null;
    }
}
