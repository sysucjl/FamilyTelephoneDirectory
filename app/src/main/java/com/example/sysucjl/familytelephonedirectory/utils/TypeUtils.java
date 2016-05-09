package com.example.sysucjl.familytelephonedirectory.utils;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/9.
 */
public class TypeUtils {

    public final static Map<String, Integer> PHONETYPE = new HashMap<>();
    private final static ArrayList<String> PHONETYPELIST = new ArrayList<>();
    public final static Map<String, Integer> EMAILTYPE = new HashMap<>();
    private final static ArrayList<String> EMAILTYPELIST = new ArrayList<>();

    private final static Map<Integer, Integer> TYPETOPHONE = new HashMap<>();
    private final static Map<Integer, Integer> TYPETOEMAIL = new HashMap<>();

    static {
        PHONETYPE.put("手机", ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        PHONETYPE.put("工作", ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        PHONETYPE.put("住宅", ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        PHONETYPE.put("总机", ContactsContract.CommonDataKinds.Phone.TYPE_MAIN);
        PHONETYPE.put("单位传真", ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK);
        PHONETYPE.put("住宅传真", ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME);
        PHONETYPE.put("其他", ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);

        PHONETYPELIST.add("手机");
        PHONETYPELIST.add("工作");
        PHONETYPELIST.add("单位");
        PHONETYPELIST.add("总机");
        PHONETYPELIST.add("单位传真");
        PHONETYPELIST.add("住宅传真");
        PHONETYPELIST.add("其他");

        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, 0);
        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK, 1);
        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_HOME, 2);
        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN, 3);
        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK, 4);
        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME, 5);
        TYPETOPHONE.put(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER, 6);

        EMAILTYPE.put("个人", ContactsContract.CommonDataKinds.Email.TYPE_HOME);
        EMAILTYPE.put("手机", ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
        EMAILTYPE.put("工作", ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        EMAILTYPE.put("其他", ContactsContract.CommonDataKinds.Email.TYPE_OTHER);

        EMAILTYPELIST.add("个人");
        EMAILTYPELIST.add("手机");
        EMAILTYPELIST.add("工作");
        EMAILTYPELIST.add("其他");

        TYPETOEMAIL.put(ContactsContract.CommonDataKinds.Email.TYPE_HOME, 0);
        TYPETOEMAIL.put(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE, 1);
        TYPETOEMAIL.put(ContactsContract.CommonDataKinds.Email.TYPE_WORK, 2);
        TYPETOEMAIL.put(ContactsContract.CommonDataKinds.Email.TYPE_OTHER, 3);
    }

    public static ArrayList<String> getPhonetypelist(){
        return PHONETYPELIST;
    }

    public static ArrayList<String> getEmailtypelist(){
        return EMAILTYPELIST;
    }

    public static int getTypeToPhoneOrder(int phoneType){
        return TYPETOPHONE.get(phoneType);
    }

    public static int getTypeToEmailOrder(int emailType){
        return TYPETOEMAIL.get(emailType);
    }

    public static String getPhoneTypeByPosition(int position){
        return PHONETYPELIST.get(position);
    }

    public static int getPhoneTypeCode(String phoneType){
        return PHONETYPE.get(phoneType);
    }

    public static int getPhoneTypeCodeByPosition(int position){
        return getPhoneTypeCode(getPhoneTypeByPosition(position));
    }

    public static String getEmailTypeByPosition(int position){
        return EMAILTYPELIST.get(position);
    }

    public static int getEmailTypeCode(String emailType){
        return EMAILTYPE.get(emailType);
    }

    public static int getEmailTypeCodeByPosition(int position){
        return getEmailTypeCode(getEmailTypeByPosition(position));
    }
}
