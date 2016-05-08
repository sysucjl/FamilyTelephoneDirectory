package com.example.sysucjl.familytelephonedirectory.tools;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by Administrator on 2016/5/7.
 */
public class MyApplication extends Application{

    public static int mAvatarSize;

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenTools screenTools = ScreenTools.instance(this);
        mAvatarSize = screenTools.dip2px(40);
        System.out.println("头像大小："+mAvatarSize);
    }
}
