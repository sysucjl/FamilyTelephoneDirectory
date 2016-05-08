package com.example.sysucjl.familytelephonedirectory.view;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.internal.view.SupportSubMenu;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/5/8.
 */
public class MyCollapsingToolbarLayout extends CollapsingToolbarLayout{

    public MyCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public MyCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println(h);
    }
}
