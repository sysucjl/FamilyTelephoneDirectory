package com.example.sysucjl.familytelephonedirectory.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.data.ContactItem;
import com.example.sysucjl.familytelephonedirectory.data.RecordItem;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/5/7.
 */
public class AvatarViewGroup extends FrameLayout{

    private final static String TAG = "avatar";

    private final Context mContext;
    private CircleImageView civAvatar;
    private FrameLayout flAvatar;
    private RoundView rvAvatarBackground;
    private ImageView ivAvatarIcon;
    private TextView tvAvatarIcon;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        civAvatar = (CircleImageView) findViewById(R.id.civ_avatar);
        flAvatar = (FrameLayout) findViewById(R.id.fl_avatar);
        rvAvatarBackground = (RoundView) findViewById(R.id.rv_avatar_background);
        ivAvatarIcon = (ImageView) findViewById(R.id.iv_avatar_icon);
        tvAvatarIcon = (TextView) findViewById(R.id.tv_avatar_icon);
    }

    public AvatarViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_avatar, this, true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setRecord(RecordItem record, int avatarSize){
        if(record.getmContactItem() != null){
            setContact(record.getmContactItem(), avatarSize);
        }else{
            ContactItem contactItem = new ContactItem(record.getNumber());
            setContact(contactItem, avatarSize);
        }
    }

    public void setContact(ContactItem contact, int avatarSize){
        if(contact.getmAvatar() != null){
            civAvatar.setVisibility(VISIBLE);
            flAvatar.setVisibility(GONE);
            //System.out.println("头像尺寸:"+ avatarSize);
            Picasso.with(mContext)
                    .load(contact.getmAvatar())
                    .placeholder(new ColorDrawable(contact.getmColor()))
                    .resize(avatarSize, avatarSize)
                    .tag(TAG)
                    .into(civAvatar);
        }else{
            civAvatar.setVisibility(GONE);
            flAvatar.setVisibility(VISIBLE);
            if(contact.getName() != null && contact.getName().length() > 0){
                if (contact.getName().charAt(0) < '9' && contact.getName().charAt(0) > '0') {
                    ivAvatarIcon.setVisibility(View.VISIBLE);
                    tvAvatarIcon.setVisibility(View.GONE);
                } else {
                    ivAvatarIcon.setVisibility(View.GONE);
                    tvAvatarIcon.setVisibility(View.VISIBLE);
                    tvAvatarIcon.setText("" + contact.getName().charAt(0));
                }
                rvAvatarBackground.setColor(contact.getmColor());
            }
        }
    }
}
