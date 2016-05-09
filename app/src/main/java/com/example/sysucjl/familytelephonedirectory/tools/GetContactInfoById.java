package com.example.sysucjl.familytelephonedirectory.tools;

import android.content.Context;
import android.os.AsyncTask;

import com.example.sysucjl.familytelephonedirectory.data.ContactItem;

/**
 * Created by Administrator on 2016/5/7.
 */
public class GetContactInfoById extends AsyncTask<String, Void, ContactItem>{

    private Context mContext;

    public GetContactInfoById(Context context){
        mContext = context;
    }

    @Override
    protected ContactItem doInBackground(String... params) {
        ContactOptionManager manager = new ContactOptionManager();
        ContactItem contactItem = manager.getDetailFromContactID(mContext, params[0], params[1]);
        return contactItem;
    }
}
