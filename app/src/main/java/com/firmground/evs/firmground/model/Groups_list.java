package com.firmground.evs.firmground.model;

/**
 * Created by hp on 3/19/2018.
 */

public class Groups_list {

    private String membershipId;
    private String UserID;
    private String groupId;
    private String GroupName;
    private String GroupType;
    private String memberRole;
    private String dateAdded;
    private String image;
    private String fname;
    private String lname;
    private String phone;
    private String gameId;
    private String participentStatus;
    private String last_message;
    private String last_message_type;
    private String last_message_id;

    public Groups_list(String membershipId,String UserID,String groupId,String GroupName,String GroupType,String memberRole,String dateAdded,String image,String fname,String lname,String phone,String gameId,String participentStatus,String last_message,String last_message_type,String last_message_id) {

        this.membershipId = membershipId;
        this.UserID       = UserID;
        this.groupId      = groupId;
        this.GroupName    = GroupName;
        this.GroupType    = GroupType;
        this.memberRole   = memberRole;
        this.dateAdded    = dateAdded;
        this.image  = image;
        this.fname  = fname;
        this.lname  = lname;
        this.phone  = phone;
        this.gameId = gameId;
        this.participentStatus = participentStatus;
        this.last_message      = last_message;
        this.last_message_type = last_message_type;
        this.last_message_id   = last_message_id;


    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getParticipentStatus() {
        return participentStatus;
    }

    public void setParticipentStatus(String participentStatus) {
        this.participentStatus = participentStatus;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_message_type() {
        return last_message_type;
    }

    public void setLast_message_type(String last_message_type) {
        this.last_message_type = last_message_type;
    }

    public String getGroupType() {
        return GroupType;
    }

    public void setGroupType(String groupType) {
        GroupType = groupType;
    }

    public String getLast_message_id() {
        return last_message_id;
    }

    public void setLast_message_id(String last_message_id) {
        this.last_message_id = last_message_id;
    }
}
