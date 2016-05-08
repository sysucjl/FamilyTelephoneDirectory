package com.example.sysucjl.familytelephonedirectory.data;

import android.graphics.Color;

import com.example.sysucjl.familytelephonedirectory.tools.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sysucjl on 16-3-23.
 */

public class ContactItem {
    private String mDisplayName;
    private String mPinYin;
    private String mSection;
    private String mContactId;
    private String mAvatar;
    private int mPhoneCount;
    int mColor;

    private Map<String, Integer> mPhones;
    private Map<String, Integer> mEmails;

    public ContactItem(String name){
        mDisplayName = name;
        mColor = Color.parseColor(ColorUtils.getColor(name.hashCode()));
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public Map<String, Integer> getmEmails() {
        return mEmails;
    }

    public void setmEmails(Map<String, Integer> mEmails) {
        this.mEmails = mEmails;
    }

    public Map<String, Integer> getmPhones() {
        return mPhones;
    }

    public void setmPhones(Map<String, Integer> mPhones) {
        this.mPhones = mPhones;
    }

    public void setmContactId(String id){
        mContactId = id;
    }

    public void setmPhoneCount(int count){
        mPhoneCount = count;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public String getName(){
        return mDisplayName;
    }

    public String getmContactId(){
        return mContactId;
    }

    public int getmPhoneCount(){
        return mPhoneCount;
    }

    public String getmPinYin() {
        return mPinYin;
    }

    public void setmPinYin(String mPinYin) {
        this.mPinYin = mPinYin;
    }

    public String getmSection() {
        return mSection;
    }

    public void setmSection(String mSection) {
        this.mSection = mSection;
    }
}
