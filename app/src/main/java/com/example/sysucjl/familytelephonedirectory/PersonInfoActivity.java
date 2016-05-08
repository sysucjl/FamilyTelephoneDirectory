package com.example.sysucjl.familytelephonedirectory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sysucjl.familytelephonedirectory.adapter.PhoneListAdapter;
import com.example.sysucjl.familytelephonedirectory.data.CityInfo;
import com.example.sysucjl.familytelephonedirectory.data.WeatherInfo;
import com.example.sysucjl.familytelephonedirectory.tools.BlackListOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.CareSMS;
import com.example.sysucjl.familytelephonedirectory.tools.ColorUtils;
import com.example.sysucjl.familytelephonedirectory.tools.ContactOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;
import com.example.sysucjl.familytelephonedirectory.tools.QueryWeather;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PersonInfoActivity extends AppCompatActivity {

    private ImageView ivBackDrop;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private Button btnSentMessage;
    private Button btnBlacklist;
    private View vStatusBar;
    private PhoneListAdapter mAdapter;
    DBManager dbHelper;
    /*  显示天气部分 */
    private TextView weather;
    private ImageView weather1;
    private ImageView weather2;
    private ImageView arrow;
    private WeatherInfo weatherInfo;
    private CityInfo cityInfo;
    private boolean isWeatherDone = false;
    public static final int SHOW_RESPONSE = 0;
    public static final int NO_CITY = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    WeatherInfo response = (WeatherInfo) msg.obj;
                    // 在这里进行UI操作，将结果显示到界面上
                    //  textView.setText(response);
                    String s = response.cityName + "  " + response.date + "   " + response.curTem + "   " + response.weather;
                    weather.setText(s);
                    Resources res = getResources();
                    int imageid = res.getIdentifier("c"+response.gif1, "drawable", getPackageName());
                    weather1.setImageResource(imageid);
                    imageid = res.getIdentifier("c"+response.gif2, "drawable", getPackageName());
                    weather2.setImageResource(imageid);
                    arrow.setImageResource(R.drawable.arrow);
                    //weather2.setImageResource(R.drawable.a_3101);
                    isWeatherDone = true;
                    break;
                case NO_CITY:
                    weather.setText("天气");
            }
        }
    };
    Button sentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        final Intent intent = getIntent();
        String personName = intent.getStringExtra("name");
        int color = Color.parseColor(ColorUtils.getColor(personName.hashCode()));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ivBackDrop = (ImageView) findViewById(R.id.iv_backdrop);
        ivBackDrop.setBackgroundColor(color);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbarLayout.setBackgroundColor(color);
        mToolbarLayout.setContentScrimColor(color);
        mToolbarLayout.setTitle(personName);
        btnSentMessage = (Button) findViewById(R.id.btn_sent_mesage);
        btnSentMessage.setBackgroundColor(color);
        btnBlacklist = (Button)findViewById(R.id.btn_blacklist);
        btnBlacklist.setBackgroundColor(color);

        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonInfoActivity.this,"Hello",Toast.LENGTH_SHORT).show();
            }
        });

        dbHelper=new DBManager(this);
        dbHelper.createDataBase();

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
        sentMessage = (Button)findViewById(R.id.btn_sent_mesage);
        sentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWeatherDone)
                    sentCareSMS(weatherInfo);
                else
                    Toast.makeText(PersonInfoActivity.this, "天气未加载完毕，请稍后", Toast.LENGTH_SHORT).show();
            }
        });

        BitmapDrawable bd = (BitmapDrawable) ivBackDrop.getDrawable();
        Palette.from(bd.getBitmap()).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    mToolbarLayout.setContentScrimColor(vibrant.getRgb());
                    btnSentMessage.setBackgroundColor(vibrant.getRgb());
                    mAdapter.setmColor(vibrant.getRgb());
                }
            }
        });

        if(intent.getStringExtra("tab_name").equals("contact")) {
            final ArrayList<String> phones = intent.getStringArrayListExtra("phonelist");
            mAdapter = new PhoneListAdapter(this, R.layout.list_phone_item, phones, color);
            final ListView phoneList = (ListView) findViewById(R.id.phone_list);

            phoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAdapter.getView(position, view, phoneList);
                }
            });

            final BlackListOptionManager blackListOptionManager = new BlackListOptionManager(this);
            final List<String> blacklist = blackListOptionManager.findAll();
            /*
            btnBlacklist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonInfoActivity.this,"Hello",Toast.LENGTH_SHORT).show();
                }
            });
            */

            boolean is_all_in_blacklist = true;
            for(int i=0;i<phones.size();i++){
                if(!blacklist.contains(phones.get(i))){
                    is_all_in_blacklist = false;
                    break;
                }
            }
            if(is_all_in_blacklist){
                btnBlacklist.setText("移出黑名单");
            }
            else{
                btnBlacklist.setText("加入黑名单");
            }
            final boolean[] out_or_in_blacklist = {is_all_in_blacklist};
            btnBlacklist.setOnClickListener(new View.OnClickListener() {
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
                                        for(int i=0;i<phones.size();i++){
                                            blackListOptionManager.delete(phones.get(i));
                                            blacklist.remove(phones.get(i));
                                        }
                                        sweetAlertDialog.setTitleText("移出成功")
                                                        .setContentText("该电话已被移出黑名单")
                                                        .setConfirmText("确认")
                                                        .showCancelButton(false)
                                                        .setCancelClickListener(null)
                                                        .setConfirmClickListener(null)
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        btnBlacklist.setText("加入黑名单");
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
                                        for(int i=0;i<phones.size();i++){
                                            blackListOptionManager.add(phones.get(i));
                                            blacklist.add(phones.get(i));
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
                                                                        for (int i = 0; i < phones.size(); i++) {
                                                                            contactOptionManager.deleteRecordByNumber(PersonInfoActivity.this, phones.get(i));
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
                                        btnBlacklist.setText("移出黑名单");
                                        out_or_in_blacklist[0] = !out_or_in_blacklist[0];
                                    }
                                }).show();
                    }

                }
            });

            phoneList.setAdapter(mAdapter);
            setListViewHeightBasedOnChildren(phoneList);
        }
        sendRequestWithHttpURLConnection();
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
                    String phonenum = mAdapter.getItem(0);
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
}
