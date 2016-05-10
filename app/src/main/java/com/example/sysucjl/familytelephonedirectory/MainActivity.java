package com.example.sysucjl.familytelephonedirectory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sysucjl.familytelephonedirectory.adapter.ViewPagerAdapter;
import com.example.sysucjl.familytelephonedirectory.tools.ContactInfo;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;

import java.io.File;


public class MainActivity extends AppCompatActivity{

    private RelativeLayout rlFloatBtn;
    private int mSrollWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("  家庭电话簿");
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.ic_logo);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() == 0) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(viewPager.getCurrentItem() == 1){
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", "add");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        rlFloatBtn = (RelativeLayout) findViewById(R.id.rl_floatBtn);

        mSrollWidth = ScreenTools.getScreenWidth(this)/2 -
                ScreenTools.dip2px(56, this)/2 - ScreenTools.dip2px(16, this);
        //mSrollWidth = s.getScreenWidth()/2


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.TabLayoutOnPageChangeListener tabLayoutListener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout);
        viewPager.addOnPageChangeListener(tabLayoutListener);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //System.out.println("" + position + " " + positionOffset);
                if(position == 1){
                    rlFloatBtn.scrollTo((int) (-mSrollWidth), 0);
                    return;
                }
                rlFloatBtn.scrollTo((int) (-positionOffset*mSrollWidth), 0);
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    fab.setImageResource(R.drawable.ic_phone);
                if(position == 1)
                    fab.setImageResource(android.R.drawable.ic_input_add);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.save) {
            save();
            return true;
        }
        else if(id == R.id.restore){
            restore();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "contacts.vcf";
                String strFilePath = filePath + "/" + fileName;
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                Looper.prepare();
                try {
                    boolean ifSucceed = ContactInfo.outPut(MainActivity.this);
                    if (ifSucceed) {
                        dialog.setTitle("通讯录导出");

                        dialog.setMessage("导出成功,保存到" + filePath + "/" + fileName);
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    } else
                        Toast.makeText(MainActivity.this, "导出联系人信息失败!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "导出联系人信息失败!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

    private void restore(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "contacts.vcf";
                String strFilePath = filePath + "/" + fileName;
                Looper.prepare();
                try {
                    File file = new File(strFilePath);
                    if(file.exists()) {
                        String type = "text/x-vcard";
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(MainActivity.this, filePath + "下不存在文件" + fileName + ",请先导出", Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Toast.makeText(MainActivity.this, "导入联系人信息失败!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

    @Override
    protected void onDestroy(){
        //android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }



}
