package com.example.sysucjl.familytelephonedirectory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import com.example.sysucjl.familytelephonedirectory.adapter.PhoneListAdapter;
import com.example.sysucjl.familytelephonedirectory.data.CityInfo;
import com.example.sysucjl.familytelephonedirectory.data.WeatherInfo;
import com.example.sysucjl.familytelephonedirectory.tools.ColorUtils;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;
import com.example.sysucjl.familytelephonedirectory.tools.QueryWeather;

import java.net.HttpURLConnection;

public class PersonInfoActivity extends AppCompatActivity {

    private ImageView ivBackDrop;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private Button btnSentMessage;
    private View vStatusBar;
    private PhoneListAdapter mAdapter;
    DBManager dbHelper;
    /*  显示天气部分 */
    private TextView weather;
    private WeatherInfo weatherInfo;
    private CityInfo cityInfo;
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

        dbHelper=new DBManager(this);
        dbHelper.createDataBase();

        weather = (TextView)findViewById(R.id.weather);
        //判断是否第一次运行程序
        SharedPreferences pref = getSharedPreferences("city",MODE_PRIVATE);
        boolean first = pref.getBoolean("first", true);
        if(first) {
            SharedPreferences.Editor editor = getSharedPreferences("city", MODE_PRIVATE).edit();
            editor.putBoolean("first", false);
            cityInfo = new CityInfo();
            cityInfo.create(editor);
        }

        BitmapDrawable bd = (BitmapDrawable) ivBackDrop.getDrawable();
        Palette.from(bd.getBitmap()).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if(vibrant != null){
                    mToolbarLayout.setContentScrimColor(vibrant.getRgb());
                    btnSentMessage.setBackgroundColor(vibrant.getRgb());
                    mAdapter.setmColor(vibrant.getRgb());
                }
            }
        });

        if(intent.getStringExtra("tab_name").equals("contact")){
            mAdapter = new PhoneListAdapter(this,R.layout.list_phone_item,intent.getStringArrayListExtra("phonelist"), color);
            final ListView phoneList = (ListView)findViewById(R.id.phone_list);

            phoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAdapter.getView(position, view, phoneList);
                }
            });

            phoneList.setAdapter(mAdapter);
            setListViewHeightBasedOnChildren(phoneList);
        }
        sendRequestWithHttpURLConnection();
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
