package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.sysucjl.familytelephonedirectory.data.ContactItem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/16.
 */
public class SaveToFile {

    public String save(Context context){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "log.txt";

        // 每次保存之前先删除原来的文件
        try {
            File file = null;
            file = new File(filePath + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 生成新文件
        makeFilePath(filePath,fileName);



        // 获取手机通讯录
        ContactOptionManager contactOptionManager = new ContactOptionManager();
        List<ContactItem> list = contactOptionManager.getBriefContactInfor(context);
        for(ContactItem contactItem : list)
        {
            ContactItem contactItem2 = contactOptionManager.getDetailFromContactID(context, contactItem);
            initData(contactItem2, filePath, fileName);
        }

        return filePath + "/" + fileName;
    }


    public void initData(ContactItem item, String filePath, String fileName) {
        //boolean b = Environment.isExternalStorageRemovable();
        //String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //String filePath = "/sdcard1/Test/";
        //String fileName = "log.txt";

        ArrayList<String> list = item.getmPhoneList();
        String line = item.getmContactId() + " " + item.getName() + " " + list.size() + " ";
        for(String phone : list)
            line += phone + " ";
//        line += item.getmPinYin() + " " + item.getmContactId() + " " + item.getmSection();
        writeTxtToFile(line, filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        //makeFilePath(filePath, fileName);

        String strFilePath = filePath+ "/" + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
           // File file = new File(strFilePath);
           // if (!file.exists()) {
            //    Log.d("TestFile", "Create the file:" + strFilePath);
             //   file.getParentFile().mkdirs();
              //  file.createNewFile();
           // }
            File file = new File(strFilePath);
            FileOutputStream fos = new FileOutputStream(file);
            byte [] bytes = strContent.getBytes();
            fos.write(bytes);
            fos.close();
           // RandomAccessFile raf = new RandomAccessFile(file, "rwd");
           // raf.seek(file.length());
           // raf.write(strContent.getBytes());
          //  raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + "/" + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }
}
