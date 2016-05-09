package com.example.sysucjl.familytelephonedirectory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sysucjl.familytelephonedirectory.data.SerializableMap;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;
import com.example.sysucjl.familytelephonedirectory.utils.FileUtils;
import com.example.sysucjl.familytelephonedirectory.utils.TypeUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity{

    public final static String TAG = "tag";
    public final static String TAG_EDIT = "edit";
    public final static String TAG_ADD = "add";

    public final static String CONTACT_NAME = "contact_name";
    public final static String PHOTO_URI = "photo_uri";
    public final static String CONTACT_ID = "contact_id";

    public final static String MAP_PHONES = "map_phones";
    public final static String MAP_EMAILS = "map_emails";

    private Spinner spPhoneType, spEmaiType;
    private ArrayAdapter<String> mPhoneTypeAdapter, mEmailTypeAdapter;
    private LinearLayout llExtraPhones;
    private LinearLayout llExtraEmails;

    private ImageView ivClearPhone, ivAvatar, ivClearEmail;
    private TextView tvAddPhone, tvSave, tvCancle, tvAddEmail;
    private Bitmap mAvatar;


    private EditText edName, edPhone, edMail;

    private Map<String, Integer> mPhones = new LinkedHashMap<>();
    private Map<String, Integer> mEmails = new LinkedHashMap<>();
    private String mContactName, mContactAvatar, mContactId;

    private static final String KEY_TEMP_CROP_FILE = "key_temp_crop_file";

    private static final int PHOTO_REQUEST = 0;// 选择图片
    private static final int PHOTO_REQUEST_CUT = 1;// 剪裁
    private File mTmpCropImageFile;

    private String mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setToolbar();

        init();

        if(mTag.equals(TAG_EDIT)){
            getIntentExtra();
            if(mContactAvatar != null){
                ivAvatar.setMinimumHeight(ScreenTools.getScreenWidth(this));
                Picasso.with(this)
                        .load(mContactAvatar)
                        .into(ivAvatar);
            }
            edName.setText(mContactName);
            handleData();
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTag = getIntent().getStringExtra(TAG);
        if(mTag.equals(TAG_EDIT)){
            toolbar.setTitle("编辑联系人");
        }else if(mTag.equals(TAG_ADD)){
            toolbar.setTitle("新建联系人");
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init(){

        edName = (EditText) findViewById(R.id.ed_name);
        edPhone = (EditText) findViewById(R.id.ed_phone_num);
        edMail = (EditText) findViewById(R.id.ed_email_address);

        ivClearPhone = (ImageView) findViewById(R.id.iv_clear_phone);
        ivClearPhone.setVisibility(View.GONE);
        ivClearEmail = (ImageView) findViewById(R.id.iv_clear_email);
        ivClearEmail.setVisibility(View.GONE);

        llExtraPhones = (LinearLayout) findViewById(R.id.ll_extra_phones);
        llExtraEmails = (LinearLayout) findViewById(R.id.ll_extra_emails);

        spPhoneType = (Spinner) findViewById(R.id.sp_phone_type);
        mPhoneTypeAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_item,
                TypeUtils.getPhonetypelist());
        spPhoneType.setAdapter(mPhoneTypeAdapter);

        spEmaiType = (Spinner) findViewById(R.id.sp_email_type);
        mEmailTypeAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_item,
                TypeUtils.getEmailtypelist());
        spEmaiType.setAdapter(mEmailTypeAdapter);

        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(EditActivity.this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                startActivityForResult(intent, PHOTO_REQUEST);*/
            }
        });

        tvAddPhone = (TextView) findViewById(R.id.tv_add_phone);
        tvAddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhone(null, 0);
            }
        });

        tvAddEmail = (TextView) findViewById(R.id.tv_add_email);
        tvAddEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmail(null, 0);
            }
        });

        tvSave = (TextView) findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });

        tvCancle = (TextView) findViewById(R.id.tv_cancle);
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getIntentExtra() {
        mContactName = getIntent().getStringExtra(CONTACT_NAME);
        mContactAvatar = getIntent().getStringExtra(PHOTO_URI);
        mContactId = getIntent().getStringExtra(CONTACT_ID);

        System.out.println("联系人id:"+mContactId);

        SerializableMap as = (SerializableMap) getIntent().getSerializableExtra(MAP_PHONES);
        if(as != null)
            mPhones = as.getMap();
        SerializableMap bs = (SerializableMap) getIntent().getSerializableExtra(MAP_EMAILS);
        if(bs != null)
            mEmails = bs.getMap();
    }

    private void handleData() {
        if(mPhones != null){
            int i = 0;
            for(Map.Entry<String, Integer> entry : mPhones.entrySet()){
                if(i == 0){
                    edPhone.setText(entry.getKey());
                    i++;
                }else if(i > 0){
                    addPhone(entry.getKey(), entry.getValue());
                    i++;
                }
            }
        }
        if(mEmails != null){
            int j = 0;
            for(Map.Entry<String, Integer> entry : mEmails.entrySet()){
                if(j == 0){
                    edMail.setText(entry.getKey());
                    j++;
                }else if(j > 0){
                    addEmail(entry.getKey(), entry.getValue());
                    j++;
                }
            }
        }
    }


    private void addPhone(String phoneNum, int phoneType){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_edit_phone, null);
        llExtraPhones.addView(view);
        ((Spinner)view.findViewById(R.id.sp_phone_type)).setAdapter(mPhoneTypeAdapter);
        view.findViewById(R.id.iv_clear_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llExtraPhones.removeView((View) v.getParent());
            }
        });
        if(phoneNum != null){
            ((TextView)view.findViewById(R.id.ed_phone_num)).setText(phoneNum);
            ((Spinner)view.findViewById(R.id.sp_phone_type)).setSelection(
                    TypeUtils.getTypeToPhoneOrder(phoneType));
        }
    }

    private void addEmail(String emailAddress, int emailType){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_edit_email, null);
        llExtraEmails.addView(view);
        ((Spinner)view.findViewById(R.id.sp_email_type)).setAdapter(mEmailTypeAdapter);
        view.findViewById(R.id.iv_clear_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llExtraEmails.removeView((View) v.getParent());
            }
        });
        if(emailAddress != null){
            ((TextView)view.findViewById(R.id.ed_email_address)).setText(emailAddress);
            ((Spinner)view.findViewById(R.id.sp_email_type)).setSelection(
                    TypeUtils.getTypeToEmailOrder(emailType));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TEMP_CROP_FILE, mTmpCropImageFile);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            mTmpCropImageFile = (File) savedInstanceState.getSerializable(KEY_TEMP_CROP_FILE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            System.out.println("返回图片失败");
            switch (resultCode){
                case RESULT_CANCELED:
                    System.out.println("取消");
                    break;
                case RESULT_FIRST_USER:
                    System.out.print("RESULT_FIRST_USER");
                    break;
                default:
                    System.out.println(resultCode);
                    break;
            }
            return;
        }

        switch (requestCode){
            case PHOTO_REQUEST:
                ArrayList<String> imagePaths = data.getStringArrayListExtra("");
                String imagePath = imagePaths.get(0);
                System.out.println(imagePath);
                startPhotoZoom(Uri.fromFile(new File(imagePath)));
                break;
            case PHOTO_REQUEST_CUT:
                System.out.println("剪裁返回");
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpCropImageFile)));
                Picasso.with(this).load(mTmpCropImageFile).skipMemoryCache().into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mAvatar = bitmap;
                        ivAvatar.setImageBitmap(mAvatar);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                System.out.println(mTmpCropImageFile.getAbsolutePath());
                break;
        }
    }

    private void startPhotoZoom(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");

        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        File dir = FileUtils.getCacheDirectory(this, true);

        mTmpCropImageFile = new File(dir.getAbsolutePath()+"/"+getPhotoFileName());
        System.out.println(mTmpCropImageFile.getAbsolutePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpCropImageFile));
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", ScreenTools.getScreenWidth(this));
        intent.putExtra("outputY", ScreenTools.getScreenWidth(this));
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private void saveContact(){
        String name = edName.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        //if(mContactId != null){
        //getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
        //        ContactsContract.Data.RAW_CONTACT_ID + "=" +
        //    ContactOptionManager co = new ContactOptionManager();
        //    co.deleteContact(this, mContactId);
        //}

        //存储联系人姓名
        //获取rawContactId
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI
                , values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, edName.getText().toString());
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
        System.out.println("存储联系人姓名："+name);
        values.clear();

        //存储电话号码
        if(!TextUtils.isEmpty(edPhone.getText().toString())) {
            savePhoneNum(edPhone.getText().toString(), rawContactId,
                    spPhoneType.getSelectedItemPosition());
        }

        for(int i = 0; i < llExtraPhones.getChildCount(); i++){
            View view = llExtraPhones.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.ed_phone_num);
            Spinner sp = (Spinner) view.findViewById(R.id.sp_phone_type);
            if(!TextUtils.isEmpty(editText.getText().toString())){
                savePhoneNum(editText.getText().toString(), rawContactId, sp.getSelectedItemPosition());
            }
        }

        //存储邮箱
        if(!TextUtils.isEmpty(edMail.getText().toString())){
            saveEmail(edMail.getText().toString(), rawContactId,
                    spEmaiType.getSelectedItemPosition());
        }

        for(int i = 0; i < llExtraEmails.getChildCount(); i++){
            View aview = llExtraEmails.getChildAt(i);
            EditText editText = (EditText) aview.findViewById(R.id.ed_email_address);
            Spinner sp = (Spinner) aview.findViewById(R.id.sp_email_type);
            if(!TextUtils.isEmpty(editText.getText().toString())){
                saveEmail(editText.getText().toString(), rawContactId, sp.getSelectedItemPosition());
            }
        }

        if(mAvatar != null){
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 将Bitmap压缩成PNG编码，质量为100%存储
            mAvatar.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, avatar);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
                    values);
        }

        finish();

    }

    private void savePhoneNum(String s, long rawContactId, int position) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER,
                s);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE,
                TypeUtils.getPhoneTypeCodeByPosition(position));
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI,
                values);
        values.clear();
    }

    private void saveEmail(String address, long rawContactId, int position){
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.TYPE,
                TypeUtils.getEmailTypeByPosition(position));
        values.put(ContactsContract.CommonDataKinds.Email.DATA, address);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI,
                values);
        values.clear();
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
}
