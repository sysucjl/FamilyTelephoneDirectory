
package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.Context;

public class ScreenTools {

    public static int dip2px(float f, Context context) {
        return (int) (0.5D + (double) (f * getDensity(context)));
    }

    public static int dip2px(int i, Context context) {
        return (int) (0.5D + (double) (getDensity(context) * (float) i));
    }

    public static int get480Height(int i, Context context) {
        return (i * getScreenWidth(context)) / 480;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getScal(Context context) {
        return (100 * getScreenWidth(context)) / 480;
    }

    public static int getScreenDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    public static float getXdpi(Context context) {
        return context.getResources().getDisplayMetrics().xdpi;
    }

    public static float getYdpi(Context context) {
        return context.getResources().getDisplayMetrics().ydpi;
    }

    public static int px2dip(float f, Context context) {
        float f1 = getDensity(context);
        return (int) (((double) f - 0.5D) / (double) f1);
    }

    public static int px2dip(int i, Context context) {
        float f = getDensity(context);
        return (int) (((double) i - 0.5D) / (double) f);
    }

}
