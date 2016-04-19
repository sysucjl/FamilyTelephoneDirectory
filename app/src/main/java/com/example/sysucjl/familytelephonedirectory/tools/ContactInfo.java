package com.example.sysucjl.familytelephonedirectory.tools;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.ContactStruct.ContactMethod;
import a_vcard.android.syncml.pim.vcard.ContactStruct.PhoneData;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;


/**
 * 联系人信息包装类
 *
 * @author LW
 *
 */
public class ContactInfo {

    /** MUST exist */
    private String name; // 姓名

    /** 联系人电话信息 */
    public static class PhoneInfo{
        /** 联系电话类型 */
        public int type;
        /** 联系电话 */
        public String number;
    }

    /** 联系人邮箱信息 */
    public static class EmailInfo{
        /** 邮箱类型 */
        public int type;
        /** 邮箱 */
        public String email;
    }

    private List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>(); // 联系号码
    private List<EmailInfo> email = new ArrayList<EmailInfo>(); // Email

    /**
     * 构造联系人信息
     * @param name 联系人姓名 
     */
    public ContactInfo(String name) {
        this.name = name;
    }

    /** 姓名 */
    public String getName() {
        return name;
    }
    /** 姓名 */
    public ContactInfo setName(String name) {
        this.name = name;
        return this;
    }
    /** 联系电话信息 */
    public List<PhoneInfo> getPhoneList() {
        return phoneList;
    }
    /** 联系电话信息 */
    public ContactInfo setPhoneList(List<PhoneInfo> phoneList) {
        this.phoneList = phoneList;
        return this;
    }
    /** 邮箱信息 */
    public List<EmailInfo> getEmail() {
        return email;
    }
    /** 邮箱信息 */
    public ContactInfo setEmail(List<EmailInfo> email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "{name: "+name+", number: "+phoneList+", email: "+email+"}";
    }

    /**
     * 联系人
     *         备份/还原操作
     * @author LW
     *
     */
    public static class ContactHandler {

        private static ContactHandler instance_ = new ContactHandler();

        /** 获取实例 */
        public static ContactHandler getInstance(){
            return instance_;
        }

        /**
         * 获取联系人指定信息
         * @param projection 指定要获取的列数组, 获取全部列则设置为null
         * @return
         * @throws Exception
         */
        public Cursor queryContact(Activity context, String[] projection){
            // 获取联系人的所需信息
            Cursor cur = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
            return cur;
        }

        /**
         * 获取联系人信息
         * @param context
         * @return
         */
        public List<ContactInfo> getContactInfo(Activity context){
            List<ContactInfo> infoList = new ArrayList<ContactInfo>();

            Cursor cur = queryContact(context, null);

            if(cur.moveToFirst()){
                do{

                    // 获取联系人id号
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    // 获取联系人姓名
                    String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    ContactInfo info = new ContactInfo(displayName);// 初始化联系人信息

                    // 查看联系人有多少电话号码, 如果没有返回0
                    int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if(phoneCount>0){

                        Cursor phonesCursor = context.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=" + id , null, null);

                        if(phonesCursor.moveToFirst()) {
                            List<PhoneInfo> phoneNumberList = new ArrayList<PhoneInfo>();
                            do{
                                // 遍历所有电话号码
                                String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(Phone.NUMBER));
                                // 对应的联系人类型
                                int type = phonesCursor.getInt(phonesCursor.getColumnIndex(Phone.TYPE));

                                // 初始化联系人电话信息
                                ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
                                phoneInfo.type=type;
                                phoneInfo.number=phoneNumber;

                                phoneNumberList.add(phoneInfo);
                            }while(phonesCursor.moveToNext());
                            // 设置联系人电话信息
                            info.setPhoneList(phoneNumberList);
                        }
                    }

                    // 获得联系人的EMAIL
                    Cursor emailCur = context.getContentResolver().query(Email.CONTENT_URI, null, Email.CONTACT_ID+"="+id, null, null);

                    if(emailCur.moveToFirst()){
                        List<EmailInfo> emailList = new ArrayList<EmailInfo>();
                        do{
                            // 遍历所有的email
                            String email = emailCur.getString(emailCur.getColumnIndex(Email.DATA1));
                            int type = emailCur.getInt(emailCur.getColumnIndex(Email.TYPE));

                            // 初始化联系人邮箱信息
                            ContactInfo.EmailInfo emailInfo=new ContactInfo.EmailInfo();
                            emailInfo.type=type;    // 设置邮箱类型
                            emailInfo.email=email;    // 设置邮箱地址

                            emailList.add(emailInfo);
                        }while(emailCur.moveToNext());

                        info.setEmail(emailList);
                    }

                    //Cursor postalCursor = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + "=" + id, null, null);
                    infoList.add(info);
                }while(cur.moveToNext());
            }
            return infoList;
        }

        /**
         * 备份联系人
         */
        public void backupContacts(Activity context, List<ContactInfo> infos){

            try {

                String path = Environment.getExternalStorageDirectory() + "/contacts.vcf";

                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");

                VCardComposer composer = new VCardComposer();

                for (ContactInfo info : infos)
                {
                    ContactStruct contact = new ContactStruct();
                    contact.name = info.getName();
                    // 获取联系人电话信息, 添加至 ContactStruct
                    List<PhoneInfo> numberList = info
                            .getPhoneList();
                    for (ContactInfo.PhoneInfo phoneInfo : numberList)
                    {
                        contact.addPhone(phoneInfo.type, phoneInfo.number,
                                null, true);
                    }
                    // 获取联系人Email信息, 添加至 ContactStruct
                    List<EmailInfo> emailList = info.getEmail();
                    for (ContactInfo.EmailInfo emailInfo : emailList)
                    {
                        contact.addContactmethod(Contacts.KIND_EMAIL,
                                emailInfo.type, emailInfo.email, null, true);
                    }
                    String vcardString = composer.createVCard(contact,
                            VCardComposer.VERSION_VCARD30_INT);
                    writer.write(vcardString);
                    writer.write("\n");

                    writer.flush();
                }
                writer.close();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (VCardException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Toast.makeText(context, "备份成功！", Toast.LENGTH_SHORT).show();
        }


        /**
         * 获取vCard文件中的联系人信息
         * @return
         */
        public List<ContactInfo> restoreContacts() throws Exception {
            List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

            VCardParser parse = new VCardParser();
            VDataBuilder builder = new VDataBuilder();
            String file = Environment.getExternalStorageDirectory() + "/contacts.vcf";

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            String vcardString = "";
            String line;
            while((line = reader.readLine()) != null) {
                vcardString += line + "\n";
            }
            reader.close();

            boolean parsed = parse.parse(vcardString, "UTF-8", builder);

            if(!parsed){
                throw new VCardException("Could not parse vCard file: "+ file);
            }

            List<VNode> pimContacts = builder.vNodeList;

            for (VNode contact : pimContacts) {

                ContactStruct contactStruct= ContactStruct.constructContactFromVNode(contact, 1);
                // 获取备份文件中的联系人电话信息
                List<PhoneData> phoneDataList = contactStruct.phoneList;
                List<PhoneInfo> phoneInfoList = new ArrayList<PhoneInfo>();
                for(PhoneData phoneData : phoneDataList){
                    ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
                    phoneInfo.number=phoneData.data;
                    phoneInfo.type=phoneData.type;
                    phoneInfoList.add(phoneInfo);
                }

                // 获取备份文件中的联系人邮箱信息
                List<ContactMethod> emailList = contactStruct.contactmethodList;
                List<EmailInfo> emailInfoList = new ArrayList<EmailInfo>();
                // 存在 Email 信息
                if (null!=emailList)
                {
                    for (ContactMethod contactMethod : emailList)
                    {
                        if (Contacts.KIND_EMAIL == contactMethod.kind)
                        {
                            ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
                            emailInfo.email = contactMethod.data;
                            emailInfo.type = contactMethod.type;
                            emailInfoList.add(emailInfo);
                        }
                    }
                }
                ContactInfo info = new ContactInfo(contactStruct.name).setPhoneList(phoneInfoList).setEmail(emailInfoList);
                contactInfoList.add(info);
            }

            return contactInfoList;
        }


        /**
         * 向手机中录入联系人信息
         * @param info 要录入的联系人信息
         */
        public void addContacts(Activity context, ContactInfo info){
            ContentValues values = new ContentValues();
            //首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
            Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);

            //往data表入姓名数据
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.GIVEN_NAME, info.getName());
            context.getContentResolver().insert(
                    ContactsContract.Data.CONTENT_URI, values);

            // 获取联系人电话信息
            List<PhoneInfo> phoneList = info.getPhoneList();
            /** 录入联系电话 */
            for (ContactInfo.PhoneInfo phoneInfo : phoneList) {
                values.clear();
                values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                // 设置录入联系人电话信息
                values.put(Phone.NUMBER, phoneInfo.number);
                values.put(Phone.TYPE, phoneInfo.type);
                // 往data表入电话数据
                context.getContentResolver().insert(
                        ContactsContract.Data.CONTENT_URI, values);
            }

            // 获取联系人邮箱信息
            List<EmailInfo> emailList = info.getEmail();

            /** 录入联系人邮箱信息 */
            for (ContactInfo.EmailInfo email : emailList) {
                values.clear();
                values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
                values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
                // 设置录入的邮箱信息
                values.put(Email.DATA, email.email);
                values.put(Email.TYPE, email.type);
                // 往data表入Email数据
                context.getContentResolver().insert(
                        ContactsContract.Data.CONTENT_URI, values);
            }

        }

    }

    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER };
    /** 联系人显示名称 **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /** 电话号码 **/
    private static final int PHONES_NUMBER_INDEX = 1;

    public static boolean outPut(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) // 判断存储卡是否存在
        {
            OutputStreamWriter writer;
            File file = new File(Environment.getExternalStorageDirectory(),
                    "contacts.vcf");
            // 得到存储卡的根路径，将example.vcf写入到根目录下
            try {
                writer = new OutputStreamWriter(new FileOutputStream(file),
                        "UTF-8");

                VCardComposer composer = new VCardComposer();

                /** 得到手机通讯录联系人信息 **/

                ContentResolver resolver = context.getContentResolver();

                // 获取手机联系人
                Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {

                        ContactStruct contact1 = new ContactStruct();
                        // 得到手机号码
                        String phoneNumber = phoneCursor
                                .getString(PHONES_NUMBER_INDEX);

                        // 得到联系人名称
                        String contactName = phoneCursor
                                .getString(PHONES_DISPLAY_NAME_INDEX);

                        contact1.name = contactName;

                        contact1.addPhone(Contacts.Phones.TYPE_MOBILE,
                                phoneNumber, null, true);
                        String vcardString = composer.createVCard(contact1,
                                VCardComposer.VERSION_VCARD30_INT);
                        writer.write(vcardString);
                        writer.write("\n");

                        writer.flush();
                    }

                    phoneCursor.close();
                }

                writer.close();
                return true;
                //Toast.makeText(MainActivity.this, "已成功导入SD卡中！", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (VCardException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            //Toast.makeText(MainActivity.this, "写入失败，SD卡不存在！", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}