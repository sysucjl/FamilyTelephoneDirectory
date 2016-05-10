package com.example.sysucjl.familytelephonedirectory.adapter;

/**
 * Created by sysucjl on 16-3-31.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sysucjl.familytelephonedirectory.data.ContactItem;
import com.example.sysucjl.familytelephonedirectory.PersonInfoActivity;
import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.tools.ContactOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;
import com.example.sysucjl.familytelephonedirectory.view.AvatarViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PersonViewAdapter extends RecyclerView.Adapter {

    private List<ContactItem> mPersons;
    private Context mContext;
    private int mAvatarSize;

    public PersonViewAdapter(List<ContactItem> persons, Context context) {
        this.mPersons = persons;
        this.mContext = context;
        mAvatarSize = ScreenTools.dip2px(40, context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_person_item, parent, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        mContext.getContentResolver().registerContentObserver(ContactsContract.RawContacts.CONTENT_URI,true,new contactObserver(new Handler(),mContext));
        return new PersonHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonHolder personHolder = (PersonHolder)holder;
        ContactItem person = mPersons.get(position);
        personHolder.tvPersonName.setText(person.getName());
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
            if (sortStr!=null && sortStr.equals(section)) {
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
        public AvatarViewGroup mAvatarViewGroup;
        public TextView tvPersonName, tvSection;

        public PersonHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvPersonName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvSection = (TextView) itemView.findViewById(R.id.tv_section);
            mAvatarViewGroup = (AvatarViewGroup) itemView.findViewById(R.id.avatar_group_view);
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
            mContext.startActivity(intent);
        }
    }

    class contactObserver extends ContentObserver {
        private Context myContext;

        public contactObserver(Handler handler,Context context){
            super(handler);
            this.myContext = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            Toast.makeText(myContext, "contact database has changed!", Toast.LENGTH_SHORT).show();
            super.onChange(selfChange);
            ContactOptionManager contactOptionManager = new ContactOptionManager();
            mPersons = contactOptionManager.getBriefContactInfor(myContext);
            notifyDataSetChanged();
            myContext.getContentResolver().unregisterContentObserver(this);
        }
    }


}
