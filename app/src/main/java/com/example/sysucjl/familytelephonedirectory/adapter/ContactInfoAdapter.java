package com.example.sysucjl.familytelephonedirectory.adapter;

/**
 * Created by sysucjl on 16-3-31.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sysucjl.familytelephonedirectory.EditActivity;
import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;

import java.util.List;
import java.util.Map;

public class ContactInfoAdapter extends ArrayAdapter<String> {

    public final static int TYPE_EMAIL = 1;
    public final static int TYPE_PHONE = 0;

    private int mType = 0;
    private int mResourceId;
    private int mColor;
    private Map<String, Integer > mData;

    public ContactInfoAdapter(Context context, int textViewResourceId,
                              List<String> objects, Map<String, Integer> data, int color, int type){
        super(context,textViewResourceId,objects);
        this.mResourceId = textViewResourceId;
        this.mColor = color;
        this.mData = data;
        this.mType = type;
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
            phoneListHolder.tvData = (TextView) convertView.findViewById(R.id.tv_phone_number);
            phoneListHolder.tvLocation = (TextView) convertView.findViewById(R.id.tv_location);
            phoneListHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            phoneListHolder.tvType = (TextView) convertView.findViewById(R.id.tv_type);
            convertView.setTag(phoneListHolder);
        }else{
            phoneListHolder = (PhoneListHolder) convertView.getTag();
        }

        if(mType == TYPE_PHONE) {
            final String item = getItem(position).replace("-", "");
            phoneListHolder.tvData.setText(item);
            for(Map.Entry<String, Integer> entry : EditActivity.PHONETYPE.entrySet()){
                if(entry.getValue() == mData.get(getItem(position))){
                    phoneListHolder.tvType.setText(entry.getKey());
                    break;
                }
            }
            //address
            DBManager dbHelper;
            dbHelper = new DBManager(getContext());
            dbHelper.createDataBase();
            phoneListHolder.tvLocation.setText(dbHelper.getResult(item));

            if (position == 0) {
                phoneListHolder.ivIcon.setVisibility(View.VISIBLE);
            } else {
                phoneListHolder.ivIcon.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phoneListHolder.ivbtnSetMessage.setImageTintList(ColorStateList.valueOf(mColor));
                phoneListHolder.ivIcon.setImageTintList(ColorStateList.valueOf(mColor));
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

            phoneListHolder.tvData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    String data = "tel:" + item;
                    intent.setData(Uri.parse(data));
                    getContext().startActivity(intent);
                }
            });
        }else if(mType == TYPE_EMAIL){
            phoneListHolder.ivbtnSetMessage.setVisibility(View.GONE);
            if(position == 0)
                phoneListHolder.ivIcon.setImageResource(R.drawable.ic_account_messages_inbox);
            else
                phoneListHolder.ivIcon.setVisibility(View.INVISIBLE);
            phoneListHolder.tvData.setText(getItem(position));
            for(Map.Entry<String, Integer> entry : EditActivity.PHONETYPE.entrySet()){
                if(entry.getValue() == mData.get(getItem(position))){
                    System.out.println("号码类型："+mData.get(getItem(position)));
                    phoneListHolder.tvType.setText(entry.getKey());
                    break;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                phoneListHolder.ivIcon.setImageTintList(ColorStateList.valueOf(mColor));
            }
        }
        if(TextUtils.isEmpty(phoneListHolder.tvType.getText().toString()))
            phoneListHolder.tvType.setVisibility(View.GONE);
        return convertView;
    }

    class PhoneListHolder{
        public ImageView ivIcon;
        public ImageButton ivbtnSetMessage;
        public TextView tvData, tvLocation, tvType;
    }
}
