package com.firmground.evs.firmground.model;

/**
 * Created by hp on 3/19/2018.
 */

public class Out_list {

    private String name;
    private String UserID;


    public Out_list(String name, String UserID) {

        this.name = name;
        this.UserID = UserID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}