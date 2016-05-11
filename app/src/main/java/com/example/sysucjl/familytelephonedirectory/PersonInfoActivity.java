package com.example.sysucjl.familytelephonedirectory;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sysucjl.familytelephonedirectory.adapter.ContactInfoAdapter;
import com.example.sysucjl.familytelephonedirectory.adapter.PhoneListAdapter;
import com.example.sysucjl.familytelephonedirectory.data.CityInfo;
import com.example.sysucjl.familytelephonedirectory.data.ContactItem;
import com.example.sysucjl.familytelephonedirectory.data.SerializableMap;
import com.example.sysucjl.familytelephonedirectory.data.WeatherInfo;
import com.example.sysucjl.familytelephonedirectory.tools.BlackListOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.CareSMS;
import com.example.sysucjl.familytelephonedirectory.tools.GetBlurImage;
import com.example.sysucjl.familytelephonedirectory.tools.GetContactInfoById;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;
import com.example.sysucjl.familytelephonedirectory.utils.ColorUtils;
import com.example.sysucjl.familytelephonedirectory.tools.ContactOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;
import com.example.sysucjl.familytelephonedirectory.tools.QueryWeather;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener{

    public final static String PHOTO_URI = "photo_uri";
    public final static String CONTACT_NAME = "contact_name";
    public final static String CONTACT_ID = "contact_id";
    public final static String CONTACT_COLOR = "contact_clor";

    private ImageView ivBackDrop;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private TextView btnSentMessage;
    //private Button btnBlacklist;
    private View vStatusBar;
    private PhoneListAdapter mAdapter;
    DBManager dbHelper;

    private int mColor;
    private ImageView ivBlurImage;
    private CardView cvContactInfo, cvWedther;

    private int mScreenWidth;

    private List<String> mPhonesList = new ArrayList<>();
    private List<String> mEmailList = new ArrayList<>();

    private Map<String, Integer> mPhones = new HashMap<>();
    private Map<String, Integer> mEmails = new HashMap<>();

    private ContactInfoAdapter mPhonesListAdapter;
    private ContactInfoAdapter mEmailsListAdapter;

    private ListView lvPhonesList;
    private ListView lvEmailList;
    private AppBarLayout applAppBarLayout;


    private String mContactId, mContactName, mContactAvatar;

    private TextView tvEdit, tvDelete, tvBlackList;

    /*  显示天气部分 */
    private TextView tvLocationTime, tvWeather, tvAir, tvWeatherRate, tvWeatherState;
    private ProgressBar mProgressBar;
    private ImageButton ibtnRefreshWeather;
    private ImageView ivWeatherIcon;
    private LinearLayout llWeatherInfo, llLoadingWeather;


    private TextView weather, tvWedtherTitle;
    private ImageView weather1;
    private ImageView weather2;
    private ImageView arrow;
    private WeatherInfo weatherInfo;
    private LinearLayout llwedtherIconBackground;
    private CityInfo cityInfo;
    private boolean isWeatherDone = false;
    public static final int SHOW_RESPONSE = 0;
    public static final int NO_CITY = 1;
    public static final int FAIL_WEATHER = 2;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    WeatherInfo response = (WeatherInfo) msg.obj;
                    // 在这里进行UI操作，将结果显示到界面上
                    //  textView.setText(response);
                    String locationAndTime = response.cityName+"  "+"更新时间："+response.date;
                    tvLocationTime.setText(locationAndTime);
                    tvWeatherRate.setText(response.curTem);
                    tvWeather.setText("天气状况："+response.weather);
                    tvAir.setText(response.API);
                    String s = response.cityName + "\n"
                            + response.date + "\n"
                            + response.curTem + "\n"
                            + response.weather;
                    weather.setText(s);
                    Resources res = getResources();
                    int imageid = res.getIdentifier("c"+response.gif1, "drawable", getPackageName());
                    ivWeatherIcon.setImageResource(imageid);

                    weather1.setImageResource(imageid);
                    imageid = res.getIdentifier("c"+response.gif2, "drawable", getPackageName());
                    weather2.setImageResource(imageid);
                    arrow.setImageResource(R.drawable.arrow);
                    //weather2.setImageResource(R.drawable.a_3101);
                    llWeatherInfo.setVisibility(View.VISIBLE);
                    llLoadingWeather.setVisibility(View.GONE);
                    isWeatherDone = true;
                    break;
                case NO_CITY:
                    cvWedther.setVisibility(View.GONE);
                    weather.setText("天气");
                    break;
                case FAIL_WEATHER:
                    tvWeatherState.setText("获取天气失败");
                    mProgressBar.setVisibility(View.GONE);
                    ibtnRefreshWeather.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        //获取传递的数据
        final Intent intent = getIntent();
        String personName = intent.getStringExtra(CONTACT_NAME);
        mContactAvatar = intent.getStringExtra(PHOTO_URI);
        mContactName = personName;
        mContactId = intent.getStringExtra(CONTACT_ID);
        mColor = intent.getIntExtra(CONTACT_COLOR, 0);

        //定义控件
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvWedtherTitle = (TextView) findViewById(R.id.tv_wedther_title);
        cvWedther = (CardView) findViewById(R.id.cv_wedther);

        llwedtherIconBackground = (LinearLayout) findViewById(R.id.ll_wedther_icon_background);
        cvContactInfo = (CardView) findViewById(R.id.cv_contact_info);
        ivBackDrop = (ImageView) findViewById(R.id.iv_backdrop);
        ivBlurImage = (ImageView) findViewById(R.id.iv_blur_image);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbarLayout.setTitle(personName);
        btnSentMessage = (TextView) findViewById(R.id.btn_sent_mesage);
        tvBlackList = (TextView) findViewById(R.id.btn_blacklist);

        tvEdit = (TextView) findViewById(R.id.tv_edit);
        tvEdit.setOnClickListener(this);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvDelete.setOnClickListener(this);
        tvBlackList = (TextView) findViewById(R.id.tv_blacklist);

        mScreenWidth = ScreenTools.getScreenWidth(this);

        lvPhonesList = (ListView)findViewById(R.id.lv_phone_list);
        lvEmailList = (ListView) findViewById(R.id.lv_email_list);

        applAppBarLayout = (AppBarLayout) findViewById(R.id.appl_appbarlayout);
        applAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                System.out.print(verticalOffset + " ");
                int alpha = (int) (Math.abs(verticalOffset)*1.0/500*255);
                alpha = alpha > 255 ? 255 : alpha;
                ivBlurImage.setImageAlpha(alpha);
            }
        });

        setColor();
        setAvatar();

        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonInfoActivity.this,"Hello",Toast.LENGTH_SHORT).show();
            }
        });

        dbHelper=new DBManager(this);
        dbHelper.createDataBase();

        tvLocationTime = (TextView) findViewById(R.id.tv_location_time);
        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvAir = (TextView) findViewById(R.id.tv_air);
        tvWeatherRate = (TextView) findViewById(R.id.tv_weather_rate);
        ivWeatherIcon = (ImageView) findViewById(R.id.iv_weather_icon);
        llWeatherInfo = (LinearLayout) findViewById(R.id.ll_weather_info);
        llLoadingWeather = (LinearLayout) findViewById(R.id.ll_loading_weather);
        ibtnRefreshWeather = (ImageButton) findViewById(R.id.ibtn_refresh_weather);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvWeatherState = (TextView) findViewById(R.id.tv_weather_state);
        ibtnRefreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithHttpURLConnection();
            }
        });


        weather1 = (ImageView)findViewById(R.id.weather1);
        weather2 = (ImageView)findViewById(R.id.weather2);
        arrow = (ImageView)findViewById(R.id.arrow);
        weather = (TextView)findViewById(R.id.weather);
        weather.setText("正在查询天气...");
        //判断是否第一次运行程序
        SharedPreferences pref = getSharedPreferences("city",MODE_PRIVATE);
        boolean first = pref.getBoolean("first", true);
        if(first) {
            SharedPreferences.Editor editor = getSharedPreferences("city", MODE_PRIVATE).edit();
            editor.putBoolean("first", false);
            cityInfo = new CityInfo();
            cityInfo.create(editor);
        }

        //发送关怀短信
        btnSentMessage = (TextView)findViewById(R.id.btn_sent_mesage);
        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWeatherDone)
                    sentCareSMS(weatherInfo);
                else
                    Toast.makeText(PersonInfoActivity.this, "天气未加载完毕，请稍后", Toast.LENGTH_SHORT).show();
            }
        });

        if(intent.getStringExtra("tab_name").equals("contact")) {
            if(mContactId != null)
                getContactInfo();
        }
    }

    private void getContactInfo(){
        GetContactInfoById getInfo = new GetContactInfoById(this){
            @Override
            protected void onPostExecute(ContactItem contactItem) {
                mPhones = contactItem.getmPhones();
                if(mPhones != null) {
                    cvContactInfo.setVisibility(View.VISIBLE);
                    cvWedther.setVisibility(View.VISIBLE);
                    for (String phone : mPhones.keySet()) {
                        System.out.println(phone);
                        mPhonesList.add(phone);
                    }
                    mPhonesListAdapter = new ContactInfoAdapter(PersonInfoActivity.this,
                            R.layout.list_contactinfo_item,
                            mPhonesList, mPhones, mColor, ContactInfoAdapter.TYPE_PHONE);
                    lvPhonesList.setAdapter(mPhonesListAdapter);
                    setListViewHeightBasedOnChildren(lvPhonesList);
                    sendRequestWithHttpURLConnection();
                    handleBackList();
                }else{
                    cvWedther.setVisibility(View.GONE);
                    lvPhonesList.setVisibility(View.GONE);
                }
                mEmails = contactItem.getmEmails();
                if(mEmails != null){
                    cvContactInfo.setVisibility(View.VISIBLE);
                    for(String email : mEmails.keySet()){
                        mEmailList.add(email);
                        System.out.println("取回邮箱："+email);
                    }
                    mEmailsListAdapter = new ContactInfoAdapter(PersonInfoActivity.this,
                            R.layout.list_contactinfo_item,
                            mEmailList, mEmails, mColor, ContactInfoAdapter.TYPE_EMAIL);
                    lvEmailList.setAdapter(mEmailsListAdapter);
                    setListViewHeightBasedOnChildren(lvEmailList);
                }else {
                    lvEmailList.setVisibility(View.GONE);
                }
                if(mPhones == null && mEmails == null){
                    cvContactInfo.setVisibility(View.GONE);
                }
            }
        };
        getInfo.execute(mContactId, mContactName);
    }

    private void setAvatar(){
        if(mContactAvatar != null) {
            System.out.println("设置头像："+mContactAvatar);
            ivBackDrop.setMinimumHeight(mScreenWidth);
            ivBlurImage.setMinimumHeight(mScreenWidth);
            Picasso.with(this)
                    .load(mContactAvatar)
                    .into(ivBackDrop, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            System.out.println("设置头像成功了");

                            BitmapDrawable bitmapDrawable = (BitmapDrawable) ivBackDrop.getDrawable();
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch vibrant = palette.getMutedSwatch();
                                    if (vibrant != null) {
                                        mColor = vibrant.getRgb();
                                        setColor();
                                        System.out.println("vibrant:" + mColor);
                                        if (mPhonesListAdapter != null)
                                            mPhonesListAdapter.setmColor(mColor);
                                        if(mEmailsListAdapter != null)
                                            mEmailsListAdapter.setmColor(mColor);
                                    } else {
                                        System.out.println("vibrant is null");
                                    }
                                    //ivBackDrop.setImageBitmap(bitmap);
                                }
                            });

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                GetBlurImage getBlurImage = new GetBlurImage(PersonInfoActivity.this) {
                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    protected void onPostExecute(Bitmap bitmap) {
                                        ivBlurImage.setImageBitmap(bitmap);
                                        ivBlurImage.setImageAlpha(0);
                                        mToolbarLayout.setContentScrimColor(Color.parseColor("#30000000"));
                                        //mToolbarLayout.setContentScrim(new BitmapDrawable(bitmap));
                                    }
                                };
                                getBlurImage.execute(bitmap);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }else{
            System.out.println("头像为空");
            ivBlurImage.setVisibility(View.GONE);
        }
    }

    private void handleBackList(){
        final BlackListOptionManager blackListOptionManager = new BlackListOptionManager(this);
        final List<String> blacklist = blackListOptionManager.findAll();

        boolean is_all_in_blacklist = true;
        for(int i=0;i<mPhonesList.size();i++){
            if(!blacklist.contains(mPhonesList.get(i))){
                is_all_in_blacklist = false;
                break;
            }
        }
        if(is_all_in_blacklist){
            tvBlackList.setText("移出黑名单");
        }
        else{
            tvBlackList.setText("加入黑名单");
        }
        final boolean[] out_or_in_blacklist = {is_all_in_blacklist};
        tvBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(out_or_in_blacklist[0]){
                    new SweetAlertDialog(PersonInfoActivity.this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("移出黑名单!")
                            .setContentText("该电话将被移出黑名单")
                            .setConfirmText("确认")
                            .setCancelText("取消")
                            .showCancelButton(true)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    for(int i=0;i<mPhonesList.size();i++){
                                        blackListOptionManager.delete(mPhonesList.get(i));
                                        blacklist.remove(mPhonesList.get(i));
                                    }
                                    sweetAlertDialog.setTitleText("移出成功")
                                            .setContentText("该电话已被移出黑名单")
                                            .setConfirmText("确认")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    tvBlackList.setText("加入黑名单");
                                    out_or_in_blacklist[0] = !out_or_in_blacklist[0];
                                }
                            }).show();
                }
                else{
                    new SweetAlertDialog(PersonInfoActivity.this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("加入黑名单!")
                            .setContentText("该电话将被加入黑名单")
                            .setConfirmText("确认")
                            .setCancelText("取消")
                            .showCancelButton(true)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    for(int i=0;i<mPhonesList.size();i++){
                                        blackListOptionManager.add(mPhonesList.get(i));
                                        blacklist.add(mPhonesList.get(i));
                                    }
                                    sweetAlertDialog.setTitleText("加入成功")
                                            .setContentText("该电话已被加入黑名单")
                                            .setConfirmText("确认")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.setTitleText("删除通讯记录")
                                                            .setContentText("该联系人的通讯记录将被删除")
                                                            .setConfirmText("确认")
                                                            .showCancelButton(true)
                                                            .setCancelText("取消")
                                                            .setCancelClickListener(null)
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    ContactOptionManager contactOptionManager = new ContactOptionManager();
                                                                    for (int i = 0; i < mPhonesList.size(); i++) {
                                                                        contactOptionManager.deleteRecordByNumber(PersonInfoActivity.this, mPhonesList.get(i));
                                                                    }
                                                                    sweetAlertDialog.setTitleText("删除成功")
                                                                            .setContentText("该联系人的通讯记录已被删除")
                                                                            .setConfirmText("确认")
                                                                            .showCancelButton(false)
                                                                            .setCancelClickListener(null)
                                                                            .setConfirmClickListener(null)
                                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                                }
                                                            })
                                                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                                }
                                            })
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    tvBlackList.setText("移出黑名单");
                                    out_or_in_blacklist[0] = !out_or_in_blacklist[0];
                                }
                            }).show();
                }

            }
        });
    }

    public void setColor(){
        ivBackDrop.setBackgroundColor(mColor);
        mToolbarLayout.setBackgroundColor(mColor);
        mToolbarLayout.setContentScrimColor(mColor);
        btnSentMessage.setTextColor(mColor);
        tvBlackList.setTextColor(mColor);
        //llwedtherIconBackground.setBackgroundColor(mColor);
        tvWedtherTitle.setTextColor(mColor);
        tvBlackList.setTextColor(mColor);
        tvDelete.setTextColor(mColor);
        tvEdit.setTextColor(mColor);
    }

    private void sentCareSMS(WeatherInfo weatherInfo1){
        String phonenum = mAdapter.getItem(0);
        CareSMS careSMS = new CareSMS();
        String messageContent;
        messageContent = careSMS.getCareSMS(weatherInfo1);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + phonenum));
        sendIntent.putExtra("sms_body", messageContent);
        startActivity(sendIntent);
    }

    //解决ListView在ScrollView中无法显示多列的情况
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    //String num=editText.getText().toString().trim();
                    String phonenum = mPhonesListAdapter.getItem(0);
                    String city = dbHelper.getCityName(phonenum);
                    if(city.toString().equals("本地号码")  ||  city.toString().equals("未知号码"))
                    {
                        Message message = new Message();
                        message.what = NO_CITY;
                        handler.sendMessage(message);
                    }
                    else {
                        //String num = "河源";
                        SharedPreferences pref = getSharedPreferences("city", MODE_PRIVATE);
                        String code = pref.getString(city, "");
                        QueryWeather xmlser = new QueryWeather();
                        if(xmlser == null){
                            Message message = new Message();
                            message.what = FAIL_WEATHER;
                            handler.sendMessage(message);
                        }else {
                            weatherInfo = xmlser.query(code);
                            //Log.i("tag",res);
                            //Result.setText(res);
                            Message message = new Message();
                            message.what = SHOW_RESPONSE;
                            // 将服务器返回的结果存放到Message中
                            message.obj = weatherInfo;
                            handler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void editContact(){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.TAG, EditActivity.TAG_EDIT);
        intent.putExtra(EditActivity.PHOTO_URI, mContactAvatar);
        intent.putExtra(EditActivity.CONTACT_NAME, mContactName);
        intent.putExtra(EditActivity.CONTACT_ID, mContactId);
        if(mPhones != null){
            SerializableMap serializableMap = new SerializableMap();
            serializableMap.setMap(mPhones);
            intent.putExtra(EditActivity.MAP_PHONES, serializableMap);
        }
        if(mEmails != null){
            SerializableMap aserializableMap = new SerializableMap();
            aserializableMap.setMap(mEmails);
            intent.putExtra(EditActivity.MAP_EMAILS, aserializableMap);
        }
        startActivity(intent);
    }

    private void deleteContact(){
        new SweetAlertDialog(PersonInfoActivity.this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("删除联系人!")
                .setContentText("该联系人将被删除")
                .setConfirmText("确认")
                .setCancelText("取消")
                .showCancelButton(true)
                .setCancelClickListener(null)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ContactOptionManager contactOptionManager = new ContactOptionManager();
                        contactOptionManager.deleteContact(PersonInfoActivity.this, mContactName);
                        Intent intent = new Intent(PersonInfoActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra(EditActivity.TAG, EditActivity.TAG_EDIT);
                intent.putExtra(EditActivity.PHOTO_URI, mContactAvatar);
                intent.putExtra(EditActivity.CONTACT_NAME, mContactName);
                intent.putExtra(EditActivity.CONTACT_ID, mContactId);
                if(mPhones != null){
                    SerializableMap serializableMap = new SerializableMap();
                    serializableMap.setMap(mPhones);
                    intent.putExtra(EditActivity.MAP_PHONES, serializableMap);
                }
                if(mEmails != null){
                    SerializableMap aserializableMap = new SerializableMap();
                    aserializableMap.setMap(mEmails);
                    intent.putExtra(EditActivity.MAP_EMAILS, aserializableMap);
                }
                startActivity(intent);
                break;
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(PHOTO_URI, mContactAvatar);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mContactAvatar = savedInstanceState.getString(PHOTO_URI);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edit:
                editContact();
                break;
            case R.id.tv_delete:
                deleteContact();
                break;
        }
    }
}
