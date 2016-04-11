package com.example.sysucjl.familytelephonedirectory.adapter;

/**
 * Created by sysucjl on 16-3-31.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;

import java.util.List;

public class PhoneListAdapter extends ArrayAdapter<String> {

    private int mResourceId;
    private int mColor;

    public PhoneListAdapter(Context context, int textViewResourceId, List<String> objects, int color){
        super(context,textViewResourceId,objects);
        mResourceId = textViewResourceId;
        this.mColor = color;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        PhoneListHolder phoneListHolder = null;
        if (convertView == null) {
            phoneListHolder = new PhoneListHolder();
            convertView = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            phoneListHolder.ivbtnSetMessage = (ImageButton) convertView.findViewById(R.id.imgbtn_sent_message);
            phoneListHolder.tvPhoneNum = (TextView) convertView.findViewById(R.id.tv_phone_number);
            phoneListHolder.tvLocation = (TextView) convertView.findViewById(R.id.tv_location);
            phoneListHolder.ivPhone = (ImageView) convertView.findViewById(R.id.iv_phone);

            convertView.setTag(phoneListHolder);
        }else{
            phoneListHolder = (PhoneListHolder) convertView.getTag();
        }
        final String item = getItem(position);
        //address
        DBManager dbHelper;
        dbHelper=new DBManager(getContext());
        dbHelper.createDataBase();
        phoneListHolder.tvLocation.setText(dbHelper.getResult(item));

        if(position == 0){
            phoneListHolder.ivPhone.setVisibility(View.VISIBLE);
        }else{
            phoneListHolder.ivPhone.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneListHolder.ivbtnSetMessage.setImageTintList(ColorStateList.valueOf(mColor));
            phoneListHolder.ivPhone.setImageTintList(ColorStateList.valueOf(mColor));
        }

        phoneListHolder.ivbtnSetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String data = "smsto:" + item;
                intent.setData(Uri.parse(data));
                getContext().startActivity(intent);
            }
        });

        phoneListHolder.tvPhoneNum.setText(item);
        phoneListHolder.tvPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String data = "tel:" + item;
                intent.setData(Uri.parse(data));
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    class PhoneListHolder{
        public ImageView ivPhone;
        public ImageButton ivbtnSetMessage;
        public TextView tvPhoneNum, tvLocation;
    }
}
