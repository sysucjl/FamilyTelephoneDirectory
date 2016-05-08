package com.example.sysucjl.familytelephonedirectory.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sysucjl on 16-3-26.
 */
public class RecordItem {
    int id;// ID
    int type;// 类型
    long CallTime;// 打电话时间
    long Duration;//通话时长
    String Number = null;// 电话号码
    List<RecordSegment> recordSegments;
    ContactItem mContactItem = null;

    public RecordItem(int id,int type, long CallTime, long Duration, String Number){
        this.id = id;
        this.type = type;
        this.CallTime = CallTime;
        this.Duration = Duration;
        this.Number = Number;
        this.recordSegments = new ArrayList<>();
        recordSegments.add(new RecordSegment(id,type, CallTime, Duration));
    }

    public ContactItem getmContactItem() {
        return mContactItem;
    }

    public void setmContactItem(ContactItem mContactItem) {
        this.mContactItem = mContactItem;
    }

    public int getId(){
        return id;
    }

    public int getType(){
        return type;
    }

    public long getCallTime(){
        return CallTime;
    }

    public long getDuration(){
        return Duration;
    }

    public String getNumber(){
        return Number;
    }

    public List<RecordSegment> getRecordSegments() {
        return recordSegments;
    }

    public void setRecordSegments(List<RecordSegment> recordSegments) {
        this.recordSegments = recordSegments;
    }

    public void addRecordSegement(int id, int type, long callTime, long duration){
        RecordSegment recordSegment = new RecordSegment(id,type, callTime, duration);
        this.recordSegments.add(recordSegment);
    }

}
