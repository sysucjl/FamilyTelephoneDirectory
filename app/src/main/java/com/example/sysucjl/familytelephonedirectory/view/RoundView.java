package com.example.sysucjl.familytelephonedirectory.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.example.sysucjl.familytelephonedirectory.R;

/**
 * Created by Administrator on 2016/5/7.
 */
public class RoundView extends View{

    private Paint mPaint;

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mPaint.setAntiAlias(true);
    }

    public void setColor(int color){
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(new RectF(0, 0, getMeasuredWidth(), getMeasuredWidth()), mPaint);
    }
}
