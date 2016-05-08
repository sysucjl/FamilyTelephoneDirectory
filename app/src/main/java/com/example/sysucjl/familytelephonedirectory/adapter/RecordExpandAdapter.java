package com.example.sysucjl.familytelephonedirectory.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.data.RecordItem;
import com.example.sysucjl.familytelephonedirectory.data.RecordSegment;
import com.example.sysucjl.familytelephonedirectory.tools.ColorUtils;
import com.example.sysucjl.familytelephonedirectory.tools.ContactOptionManager;
import com.example.sysucjl.familytelephonedirectory.tools.DBManager;
import com.example.sysucjl.familytelephonedirectory.tools.DateTools;
import com.example.sysucjl.familytelephonedirectory.tools.ScreenTools;
import com.example.sysucjl.familytelephonedirectory.view.AvatarViewGroup;
import com.example.sysucjl.familytelephonedirectory.view.RoundView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/4/7.
 */
public class RecordExpandAdapter extends BaseExpandableListAdapter {

    private final int mAvatarSize;
    private Context mContext;
    private List<RecordItem> mRecordItems;
    public RecordExpandAdapter(Context context, List<RecordItem> recordItems){
        this.mContext = context;
        this.mRecordItems = recordItems;

        ScreenTools screenTools = ScreenTools.instance(context);
        mAvatarSize = screenTools.dip2px(40);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        if(mRecordItems == null)
            return 0;
        return mRecordItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mRecordItems.get(groupPosition).getRecordSegments().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public RecordSegment getChild(int groupPosition, int childPosition) {
        return mRecordItems.get(groupPosition).getRecordSegments().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        RecordGroupHolder recordGroupHolder = null;
        if(convertView == null){
            recordGroupHolder = new RecordGroupHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_record_item, parent, false);
            recordGroupHolder.tvRecordName = (TextView) convertView.findViewById(R.id.tv_record_name);
            recordGroupHolder.tvRecordDate = (TextView) convertView.findViewById(R.id.tv_record_date);
            recordGroupHolder.llBackground = (LinearLayout) convertView.findViewById(R.id.ll_background);
            recordGroupHolder.vRecordDivide = (View) convertView.findViewById(R.id.v_recordd_divide);
            recordGroupHolder.llRecordType = (LinearLayout) convertView.findViewById(R.id.ll_record_type);
            recordGroupHolder.llChoose = (LinearLayout) convertView.findViewById(R.id.ll_choose);
            recordGroupHolder.tvCallBack = (TextView) convertView.findViewById(R.id.tv_callback);
            recordGroupHolder.tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
            recordGroupHolder.tvPhoneNum = (TextView) convertView.findViewById(R.id.tv_phonenum);
            recordGroupHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_record_address);
            recordGroupHolder.mAvatarViewGroup = (AvatarViewGroup) convertView.findViewById(R.id.avatar_group);
            convertView.setTag(recordGroupHolder);
        }else{
            recordGroupHolder = (RecordGroupHolder) convertView.getTag();
        }
        final RecordItem recordItem = mRecordItems.get(groupPosition);
        //address
        DBManager dbHelper;
        dbHelper=new DBManager(mContext);
        dbHelper.createDataBase();
        String address = dbHelper.getResult(recordItem.getNumber());

        recordGroupHolder.mAvatarViewGroup.setRecord(recordItem, mAvatarSize);

        if(!TextUtils.isEmpty(address)){
            recordGroupHolder.tvAddress.setVisibility(View.VISIBLE);
            recordGroupHolder.tvAddress.setText(address);
        }else{
            recordGroupHolder.tvAddress.setVisibility(View.GONE);
        }

        if(isExpanded) {
            recordGroupHolder.llBackground.setBackgroundColor(Color.parseColor("#ffffff"));
            recordGroupHolder.vRecordDivide.setVisibility(View.VISIBLE);
            recordGroupHolder.llChoose.setVisibility(View.VISIBLE);
            if(recordItem.getmContactItem() != null){
                if(!TextUtils.isEmpty(recordItem.getNumber())) {
                    recordGroupHolder.tvPhoneNum.setVisibility(View.VISIBLE);
                    recordGroupHolder.tvPhoneNum.setText(recordItem.getNumber());
                }else{
                    recordGroupHolder.tvPhoneNum.setVisibility(View.GONE);
                }
            }else{
                recordGroupHolder.tvPhoneNum.setVisibility(View.GONE);
            }

            if(TextUtils.isEmpty(recordItem.getNumber())){
                recordGroupHolder.tvCallBack.setVisibility(View.GONE);
            }else{
                recordGroupHolder.tvCallBack.setVisibility(View.VISIBLE);
                recordGroupHolder.tvCallBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + recordItem.getNumber()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        mAdapterListener.collapseGroup(groupPosition);
                    }
                });
            }

            recordGroupHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //SweetAlertDialog是导入的第三方dialog
                    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("要删除吗?")
                            .setContentText("系统将删除该组通话记录。")
                            .setCancelText("取消")
                            .setConfirmText("删除")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    ContactOptionManager tool = new ContactOptionManager();
                                    for(int i=0;i<recordItem.getRecordSegments().size();i++){
                                        tool.deleteRecord(mContext,recordItem.getRecordSegments().get(i).getId());
                                    }
                                    mRecordItems.remove(groupPosition);
                                    mAdapterListener.mynotifyDataSetChanged(groupPosition);

                                    sDialog.setTitleText("已删除!")
                                            .setContentText("该组通话记录已被删除!")
                                            .setConfirmText("确认")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                                }

                            })
                            .show();
                }
            });
        }
        else {
            recordGroupHolder.llBackground.setBackgroundColor(Color.parseColor("#00000000"));
            recordGroupHolder.vRecordDivide.setVisibility(View.GONE);
            recordGroupHolder.llChoose.setVisibility(View.GONE);
        }
        recordGroupHolder.llBackground.setTag(groupPosition);

        //设置姓名
        if(recordItem.getmContactItem() != null) {
            recordGroupHolder.tvRecordName.setText(recordItem.getmContactItem().getName());
        }
        else {
            recordGroupHolder.tvPhoneNum.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(recordItem.getNumber())){
                recordGroupHolder.tvRecordName.setText(DateTools.getNumberFormat(recordItem.getNumber()));
            }else{
                recordGroupHolder.tvRecordName.setText("未知");
            }
        }


        //设置来电图标
        recordGroupHolder.llRecordType.removeAllViews();
        for(int i = 0; i < recordItem.getRecordSegments().size(); i++){
            if(i == 3){
                TextView textView = new TextView(mContext);
                textView.setTextSize(12);
                textView.setText("("+recordItem.getRecordSegments().size()+")");
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                recordGroupHolder.llRecordType.addView(textView);
                break;
            }
            int type = recordItem.getRecordSegments().get(i).getType();
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(25, 15));
            switch (type){
                case CallLog.Calls.OUTGOING_TYPE:
                    imageView.setImageResource(R.drawable.ic_outgoing);
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    imageView.setImageResource(R.drawable.ic_incoming);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    imageView.setImageResource(R.drawable.ic_missed);
                    break;
            }
            recordGroupHolder.llRecordType.addView(imageView);
        }
        recordGroupHolder.tvRecordDate.setText(DateTools.getRecordItemData(recordItem.getCallTime()));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RecordChildHolder recordChildHolder = null;
        if(convertView == null){
            recordChildHolder = new RecordChildHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.record_segment_item, parent, false);
            recordChildHolder.imgRecordType = (ImageView) convertView.findViewById(R.id.img_record_type);
            recordChildHolder.tvRecordType = (TextView) convertView.findViewById(R.id.tv_record_type);
            recordChildHolder.tvRecordDate = (TextView) convertView.findViewById(R.id.tv_record_date);
            recordChildHolder.tvRecordDuration = (TextView) convertView.findViewById(R.id.tv_record_duration);
            recordChildHolder.imgShadow = (ImageView) convertView.findViewById(R.id.img_shadow);
            convertView.setTag(recordChildHolder);
        }else{
            recordChildHolder = (RecordChildHolder) convertView.getTag();
        }
        RecordSegment recordSegment = getChild(groupPosition, childPosition);
        int callcode = recordSegment.getType();
        switch (callcode) {
            case CallLog.Calls.OUTGOING_TYPE:
                recordChildHolder.imgRecordType.setBackgroundResource(R.drawable.ic_outgoing);
                recordChildHolder.tvRecordType.setText("外拨电话");
                recordChildHolder.tvRecordDuration.setVisibility(View.VISIBLE);
                break;
            case CallLog.Calls.INCOMING_TYPE:
                recordChildHolder.imgRecordType.setBackgroundResource(R.drawable.ic_incoming);
                recordChildHolder.tvRecordDuration.setVisibility(View.VISIBLE);
                recordChildHolder.tvRecordType.setText("来电");
                break;
            case CallLog.Calls.MISSED_TYPE:
                recordChildHolder.imgRecordType.setBackgroundResource(R.drawable.ic_missed);
                recordChildHolder.tvRecordType.setText("未接电话");
                recordChildHolder.tvRecordDuration.setVisibility(View.GONE);
                break;
        }
        recordChildHolder.tvRecordDate.setText(String.valueOf(DateTools.getRecordSegmentDate(recordSegment.getCallTime())));
        recordChildHolder.tvRecordDuration.setText(DateTools.getDuration(recordSegment.getDuration()));
        if(childPosition == getChildrenCount(groupPosition) - 1){
            recordChildHolder.imgShadow.setVisibility(View.VISIBLE);
        }else{
            recordChildHolder.imgShadow.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    class RecordGroupHolder{
        public View vRecordDivide;
        public TextView tvRecordName, tvCallBack, tvDelete, tvPhoneNum,tvAddress;
        public TextView tvRecordDate;
        public LinearLayout llBackground;
        public LinearLayout llRecordType;
        public LinearLayout llChoose;
        public AvatarViewGroup mAvatarViewGroup;
    }

    class RecordChildHolder{
        public ImageView imgRecordType;
        public TextView tvRecordType;
        public TextView tvRecordDate;
        public TextView tvRecordDuration;
        public ImageView imgShadow;
    }

    private RecordAdapterListener mAdapterListener;

    public void setOnRecordAdapterListener(RecordAdapterListener adapterListener){
        this.mAdapterListener = adapterListener;
    }

    public interface RecordAdapterListener{
        public void collapseGroup(int groupPosition);
        public void mynotifyDataSetChanged(int groupPosition);
    }
}

