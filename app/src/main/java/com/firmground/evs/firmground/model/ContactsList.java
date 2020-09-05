package com.firmground.evs.firmground.model;

import android.graphics.Bitmap;

/**
 * Created by HP on 11/15/2017.
 */

public class ContactsList {
    private String mContactPicture;
    private String mContactName;
    private String mContactId;
    private String mContactNumber;
    private String mContactGender;
    private String mContactAge;
    private String mContactEmail;
    private String mContactType;
    private String mContactExtra;
    private Boolean selected;
    private Bitmap mContactBitmap;


    public ContactsList(String  mContactPicture, String mContactId    , String mContactName,
                        String  mContactNumber , String mContactType  , String mContactExtra,
                        Boolean select         , Bitmap mContactBitmap, String mContactAge,
                        String  mContactGender , String mContactEmail){

        this.mContactPicture= mContactPicture;
        this.mContactName   = mContactName;
        this.mContactId     = mContactId;
        this.mContactNumber = mContactNumber;
        this.mContactType   = mContactType;
        this.mContactExtra  = mContactExtra;
        this.selected       = select;
        this.mContactBitmap = mContactBitmap;
        this.mContactAge    = mContactAge;
        this.mContactGender = mContactGender;
        this.mContactEmail  = mContactEmail;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getmContactPicture() {
        return mContactPicture;
    }
    public void setmContactPicture(String mContactPicture) {
        this.mContactPicture = mContactPicture;
    }

    public String getmContactName() {
        return mContactName;
    }
    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public String getmContactId() {
        return mContactId;
    }
    public void setmContactId(String mContactId) {
        this.mContactId = mContactId;
    }

    public String getmContactNumber() {
        return mContactNumber;
    }
    public void setmContactNumber(String mContactNumber) {
        this.mContactNumber = mContactNumber;
    }

    public String getmContactGender() {
        return mContactGender;
    }
    public void setmContactGender(String mContactGender) {
        this.mContactGender = mContactGender;
    }

    public String getmContactAge() {
        return mContactAge;
    }
    public void setmContactAge(String mContactAge) {
        this.mContactAge = mContactAge;
    }

    public String getmContactEmail() {
        return mContactEmail;
    }
    public void setmContactEmail(String mContactEmail) {
        this.mContactEmail = mContactEmail;
    }

    public String getmContactType() {
        return mContactType;
    }
    public void setmContactType(String mContactType) {
        this.mContactType = mContactType;
    }

    public String getmContactExtra() {
        return mContactExtra;
    }
    public void setmContactExtra(String mContactExtra) {
        this.mContactExtra = mContactExtra;
    }

    public Bitmap getmContactBitmap() {
        return mContactBitmap;
    }
    public void setmContactBitmap(Bitmap mContactBitmap) {
        this.mContactBitmap = mContactBitmap;
    }
}
