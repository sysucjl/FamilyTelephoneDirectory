package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by Administrator on 2016/5/8.
 */
public class GetBlurImage extends AsyncTask<Bitmap, Void, Bitmap>{

    private Context mContext;

    public GetBlurImage(Context context){
        this.mContext = context;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        return Blur.fastblur(mContext, params[0], 25);
    }
}
