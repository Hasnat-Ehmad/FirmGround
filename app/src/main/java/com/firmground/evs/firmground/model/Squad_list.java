package com.firmground.evs.firmground.model;

/**
 * Created by hp on 3/19/2018.
 */

public class Squad_list {

    private String membershipId;
    private String name;
    private String memberRole;
    private String number;
    private String screen;


    public Squad_list(String membershipId,String name, String memberRole,String number,String screen) {

        this.membershipId = membershipId;
        this.name = name;
        this.memberRole = memberRole;
        this.number = number;
        this.screen = screen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }
}