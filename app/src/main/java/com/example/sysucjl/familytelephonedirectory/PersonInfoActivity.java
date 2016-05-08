package com.example.sysucjl.familytelephonedirectory;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sysucjl.familytelephonedirectory.adapter.ContactInfoAdapter;
import com.example.sysucjl.familytelephonedirectory.data.CityInfo;
import com.example.sysucjl.familytelephonedirectory.data.ContactItem;
import com.example.sysucjl.familytelephonedirectory.data.SerializableMap;
import com.example.sysucjl.familytelephonedirectory.data.WeatherInfo;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;
import com.example.sysucjl.familytelephonedirectory.tools.GetBlurImage;
import com.example.sysucjl.familytelephonedirectory.tools.GetContactInfoById;
import com.example.sysucjl.familytelephonedirectory.tools.QueryWeather;
import com.example.sysucjl.familytelephonedirectory.view.MyCollapsingToolbarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.ScreenUtils;

public class PersonInfoActivity extends AppCompatActivity {

    public final static String PHOTO_URI = "photo_uri";
    public final static String CONTACT_NAME = "contact_name";
    public final static String CONTACT_ID = "contact_id";
    public final static String CONTACT_COLOR = "contact_clor";

    private ImageView ivBackDrop;
    private Toolbar mToolbar;
    private MyCollapsingToolbarLayout mToolbarLayout;
    private Button btnSentMessage;
    private ImageView ivBlurImage;

    private ListView lvPhonesList;
    private ListView lvEmailList;

    private AppBarLayout applAppBarLayout;
    public static int mScreenWidth;

    private NestedScrollView nswScroll;


    DBManager dbHelper;

    /*  显示天气部分 */
    private TextView weather;
    private WeatherInfo weatherInfo;
    private CityInfo cityInfo;
    public static final int SHOW_RESPONSE = 0;
    public static final int NO_CITY = 1;

    private String mContactId, mContactName, mContactAvatar;
    private int mColor;
    private List<String> mPhonesList = new ArrayList<>();
    private List<String> mEmailList = new ArrayList<>();

    private Map<String, Integer> mPhones = new HashMap<>();
    private Map<String, Integer> mEmails = new HashMap<>();

    private ContactInfoAdapter mPhonesListAdapter;
    private ContactInfoAdapter mEmailsListAdapter;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    WeatherInfo response = (WeatherInfo) msg.obj;
                    // 在这里进行UI操作，将结果显示到界面上
                    //  textView.setText(response);
                    String s = response.cityName + "  " +
                            response.date + "   " +
                            response.curTem + "   " +
                            response.weather;
                    weather.setText(s);break;
                case NO_CITY:
                    weather.setText("天气");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        final Intent intent = getIntent();
        mContactName = intent.getStringExtra(CONTACT_NAME);
        mContactAvatar = intent.getStringExtra(PHOTO_URI);
        mContactId = intent.getStringExtra(CONTACT_ID);
        mColor = intent.getIntExtra(CONTACT_COLOR, 0);
        System.out.println(mColor);

        mScreenWidth = ScreenUtils.getScreenSize(this).x;
        init();

        GetContactInfoById getInfo = new GetContactInfoById(this){
            @Override
            protected void onPostExecute(ContactItem contactItem) {
                mPhones = contactItem.getmPhones();
                if(mPhones != null) {
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
                }
                mEmails = contactItem.getmEmails();
                if(mEmails != null){
                    for(String email : mEmails.keySet()){
                        mEmailList.add(email);
                        System.out.println("取回邮箱："+email);
                    }
                    mEmailsListAdapter = new ContactInfoAdapter(PersonInfoActivity.this,
                            R.layout.list_contactinfo_item,
                            mEmailList, mEmails, mColor, ContactInfoAdapter.TYPE_EMAIL);
                    lvEmailList.setAdapter(mEmailsListAdapter);
                    setListViewHeightBasedOnChildren(lvEmailList);
                }
            }
        };
        getInfo.execute(mContactId, mContactName);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void init() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivBackDrop = (ImageView) findViewById(R.id.iv_backdrop);
        mToolbarLayout = (MyCollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        btnSentMessage = (Button) findViewById(R.id.btn_sent_mesage);
        weather = (TextView)findViewById(R.id.weather);
        mToolbarLayout.setTitle(mContactName);
        ivBlurImage = (ImageView) findViewById(R.id.iv_blur_image);
        lvPhonesList = (ListView)findViewById(R.id.lv_phone_list);
        lvEmailList = (ListView) findViewById(R.id.lv_email_list);

        applAppBarLayout = (AppBarLayout) findViewById(R.id.appl_appbarlayout);
        applAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                System.out.print(verticalOffset + " ");
                int alpha = (int) (Math.abs(verticalOffset)*1.0/500*255);
                alpha = alpha > 255 ? 255 : alpha;
                ivBlurImage.setImageAlpha(alpha);
            }
        });


        setColor();

        if(mContactAvatar != null) {
            ivBackDrop.setMinimumHeight(ScreenUtils.getScreenSize(this).x);
            ivBlurImage.setMinimumHeight(ScreenUtils.getScreenSize(PersonInfoActivity.this).x);
            Picasso.with(this)
                    .load(mContactAvatar)
                    .resize(mScreenWidth, mScreenWidth)
                    .skipMemoryCache()
                    .into(new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch vibrant = palette.getMutedSwatch();

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

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
                            ivBackDrop.setImageBitmap(bitmap);
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
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }else{
            ivBlurImage.setVisibility(View.GONE);
        }

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

        dbHelper=new DBManager(this);
        dbHelper.createDataBase();
    }

    public void setColor(){
        ivBackDrop.setBackgroundColor(mColor);
        mToolbarLayout.setBackgroundColor(mColor);
        mToolbarLayout.setContentScrimColor(mColor);
        btnSentMessage.setBackgroundColor(mColor);
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
                        weatherInfo = xmlser.query(code);
                        //Log.i("tag",res);
                        //Result.setText(res);
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        // 将服务器返回的结果存放到Message中
                        message.obj = weatherInfo;
                        handler.sendMessage(message);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
}
