package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.sysucjl.familytelephonedirectory.data.ContactItem;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2016/4/16.
 */
public class ReadFromFile {
    public List<ContactItem> read(Context context){
        List<ContactItem> list = new ArrayList<>();;

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "log.txt";
        String strFilePath = filePath+ "/" + fileName;

   //     String test = converfile(strFilePath);

        try {
            ContactItem item;
            File file = new File(strFilePath);
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte [] buffer = new byte[length];
            fis.read(buffer);
            String str = EncodingUtils.getString(buffer, "UTF-8");
            String lineSet[] = str.split("\r\n");

            for (String line : lineSet){
                item = stringToItem(line);
                list.add(item);
            }

            fis.close();
        }catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }

        return list;
    }

    private ContactItem stringToItem(String line) {
        //ContactItem item = new ContactItem(null);
        String s[] = line.split(" ");
        ContactItem item = new ContactItem(s[1]);
        item.setmContactId(s[0]);
        item.setmPhoneCount(Integer.parseInt(s[2]));
        List<String> phone = new ArrayList<>();
        for(int i = 3; i < 3+item.getmPhoneCount(); i++){
            phone.add(s[i]);
        }
        item.setPhoneNumber(phone);
        return item;
    }

    public String converfile(String filepath){
        System.out.println("ConvertFileCode--------->"+filepath);
        File file=new File(filepath);
        FileInputStream fis=null;
        BufferedInputStream bis=null;
        BufferedReader reader=null;
        String text="";
        try {
            fis=new FileInputStream(file);
            bis=new BufferedInputStream(fis);
            bis.mark(4);
            byte[] first3bytes=new byte[3];
//   System.out.println("");
            //找到文档的前三个字节并自动判断文档类型。
            bis.read(first3bytes);
            bis.reset();
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                    && first3bytes[2] == (byte) 0xBF) {// utf-8

                reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));

            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFE) {

                reader = new BufferedReader(
                        new InputStreamReader(bis, "unicode"));
            } else if (first3bytes[0] == (byte) 0xFE
                    && first3bytes[1] == (byte) 0xFF) {

                reader = new BufferedReader(new InputStreamReader(bis,
                        "utf-16be"));
            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFF) {

                reader = new BufferedReader(new InputStreamReader(bis,
                        "utf-16le"));
            } else {

                reader = new BufferedReader(new InputStreamReader(bis, "GBK"));
            }
            String str = reader.readLine();

            while (str != null) {
                text = text + str + "/n";
                str = reader.readLine();
            }
            System.out.println("text"+text);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if (fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bis!=null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return text;

    }
}
