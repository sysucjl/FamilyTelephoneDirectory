package com.example.sysucjl.familytelephonedirectory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.sysucjl.familytelephonedirectory.adapter.ViewPagerAdapter;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;

import java.util.Collection;


public class MainActivity extends AppCompatActivity{

    private RelativeLayout rlFloatBtn;
    private int mSrollWidth;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("  家庭电话簿");
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.ic_logo);

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

        ScreenTools s = ScreenTools.instance(getApplicationContext());
        mSrollWidth = s.getScreenWidth()/2 - s.dip2px(56)/2 - s.dip2px(16);
        //mSrollWidth = s.getScreenWidth()/2
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
