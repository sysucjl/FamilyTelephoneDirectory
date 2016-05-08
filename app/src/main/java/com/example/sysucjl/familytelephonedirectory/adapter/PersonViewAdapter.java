package com.example.sysucjl.familytelephonedirectory.adapter;

/**
 * Created by sysucjl on 16-3-31.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sysucjl.familytelephonedirectory.data.ContactItem;
import com.example.sysucjl.familytelephonedirectory.PersonInfoActivity;
import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.tools.ColorUtils;
import com.example.sysucjl.familytelephonedirectory.tools.ContactOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;
import com.example.sysucjl.familytelephonedirectory.view.AvatarViewGroup;
import com.example.sysucjl.familytelephonedirectory.view.RoundView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonViewAdapter extends RecyclerView.Adapter {

    private ArrayList<ContactItem> mPersons;
    private Context mContext;
    public final static String TAG = "PersonViewAdapter";
    private int mAvatarSize;

    public PersonViewAdapter(List<ContactItem> persons, Context context) {
        this.mPersons = (ArrayList<ContactItem>) persons;
        this.mContext = context;
        ScreenTools screenTools = ScreenTools.instance(context);
        mAvatarSize = screenTools.dip2px(40);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_person_item, parent, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new PersonHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonHolder personHolder = (PersonHolder)holder;
        ContactItem person = mPersons.get(position);
        personHolder.tvPersonName.setText(person.getName());
        //设置头像
        personHolder.mAvatarViewGroup.setContact(person, mAvatarSize);

        int resultPositon = getPositionForSection(mPersons.get(position).getmSection());
        if(position == resultPositon){
            personHolder.tvSection.setText(mPersons.get(position).getmSection());
        }else{
            personHolder.tvSection.setText(" ");
        }
    }

    public int getPositionForSection(String section) {
        for (int i = 0; i < mPersons.size(); i++) {
            String sortStr = mPersons.get(i).getmSection();
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvPersonName, tvSection;
        public AvatarViewGroup mAvatarViewGroup;

        public PersonHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvSection = (TextView) itemView.findViewById(R.id.tv_section);
            mAvatarViewGroup = (AvatarViewGroup) itemView.findViewById(R.id.avatar_group);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            ContactItem item = mPersons.get(getPosition());
            Intent intent = new Intent();
            intent.setClass(mContext, PersonInfoActivity.class);
            intent.putExtra("tab_name","contact");
            intent.putExtra(PersonInfoActivity.CONTACT_ID, item.getmContactId());
            intent.putExtra(PersonInfoActivity.CONTACT_NAME, item.getName());
            intent.putExtra(PersonInfoActivity.PHOTO_URI, item.getmAvatar());
            intent.putExtra(PersonInfoActivity.CONTACT_COLOR, item.getmColor());
            System.out.println(item.getmColor());
            mContext.startActivity(intent);
        }
    }
}
