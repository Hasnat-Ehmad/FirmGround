package com.firmground.evs.firmground.model;

public class Message_List {

    private String  message_id;
    private String  image;
    private String  name;
    private String  date_time;
    private String  message;
    private String  sender;
    private String  image_check;
    private String  audio_path;
    private boolean seekbar_check;

    public Message_List(String message_id,String image  , String  name, String date_time, String message, String sender, String image_check,String audio_path,boolean seekbar_check) {

        this.message_id   = message_id;
        this.image        = image;
        this.name         = name;
        this.date_time    = date_time;
        this.message      = message;
        this.sender       = sender;
        this.image_check  = image_check;
        this.audio_path   = audio_path;
        this.seekbar_check= seekbar_check;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getImage_check() {
        return image_check;
    }

    public void setImage_check(String image_check) {
        this.image_check = image_check;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }

    public boolean isSeekbar_check() {
        return seekbar_check;
    }

    public void setSeekbar_check(boolean seekbar_check) {
        this.seekbar_check = seekbar_check;
    }
}
