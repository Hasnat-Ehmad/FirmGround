package com.firmground.evs.firmground.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MyDatabase extends SQLiteOpenHelper {
    SharedPreferences sharedPreferences;

    //general use data
    private static final String UID              ="_id";
    private static final String COULMN_GROUP_ID  ="group_id";
    private static final String COULMN_USER_ID   ="user_id";

    //first table data (local messages)
    private static final String TABLE_NAME       ="tbl_messages";
    private static final String COULMN_MESSAGE_ID="message_id";
    private static final String COULMN_MESSAGE   ="message";

    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+ " ("
               +UID              +" INTEGER PRIMARY KEY AUTOINCREMENT, "
               +COULMN_GROUP_ID  +" INTEGER, "
               +COULMN_USER_ID   +" INTEGER, "
               +COULMN_MESSAGE_ID+" INTEGER, "
               +COULMN_MESSAGE   +" TEXT );"
               ;

    //second table data (group details)
    private static final String TABLE_NAME_GROUP           ="tbl_groups";
    private static final String COULMN_GROUP_NAME          ="group_name";
    private static final String COULMN_GROUP_TYPE          ="group_type";
    private static final String COULMN_GROUP_LAST_MESSAGE  ="last_message";
    private static final String COULMN_GROUP_LAST_MESSAGE_TYPE ="last_message_type";
    private static final String COULMN_GROUP_DATE          ="date";
    private static final String COULMN_GROUP_MEMBERROLE    ="memberrole";
    private static final String COULMN_GROUP_GAME_ID       ="game_id";
    private static final String COULMN_GROUP_IMAGE         ="group_image";
    private static final String COULMN_GROUP_IN_OUT_STATUS ="in_out_status";
    private static final String COULMN_GROUP_SECONDUSERDATA="secondUserData";
    private static final String COULMN_GROUP_LASTMSGID     ="lastmsgid";

    private static final String CREATE_GROUP_TBL = "CREATE TABLE "+TABLE_NAME_GROUP+ " ("
                  +UID                       +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                  +COULMN_GROUP_ID           +" INTEGER, "
                  +COULMN_USER_ID            +" INTEGER, "
                  +COULMN_GROUP_NAME         +" VARCHAR(255), "
                  +COULMN_GROUP_TYPE         +" VARCHAR(255), "
                  +COULMN_GROUP_LAST_MESSAGE +" VARCHAR(255), "
                  +COULMN_GROUP_LAST_MESSAGE_TYPE +" VARCHAR(255), "
                  +COULMN_GROUP_DATE         +" VARCHAR(255), "
                  +COULMN_GROUP_MEMBERROLE   +" VARCHAR(255), "
                  +COULMN_GROUP_GAME_ID      +" INTEGER, "
                  +COULMN_GROUP_IMAGE        +" VARCHAR(255), "
                  +COULMN_GROUP_IN_OUT_STATUS+" VARCHAR(255),"
                  +COULMN_GROUP_SECONDUSERDATA+" VARCHAR(255), "
                  +COULMN_GROUP_LASTMSGID    +" VARCHAR(255) );"
              ;

    //third table data (chat details)
    private static final String TABLE_NAME_CHAT          ="tbl_chat";
    private static final String COULMN_CHAT_MESSAGE_ID   ="msg_id";
    private static final String COULMN_CHAT_MESSAGE_IMAGE="sent_status";//✓✓
    private static final String COULMN_CHAT_FULLNAME     ="fullname";
    private static final String COULMN_CHAT_SENTTIME     ="senttime";
    private static final String COULMN_CHAT_MESSAGE      ="message";
    private static final String COULMN_CHAT_USER_CHECK   ="user_check";
    private static final String COULMN_CHAT_READ         ="read_status";

    final String CREATE_CHAT_TBL = "CREATE TABLE "+TABLE_NAME_CHAT+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_GROUP_ID+          " INTEGER, "
            +COULMN_USER_ID+           " INTEGER, "
            /*+COULMN_GROUP_NAME+      " VARCHAR(255), "*/
            +COULMN_CHAT_MESSAGE_ID+   " INTEGER, "
            +COULMN_CHAT_MESSAGE_IMAGE+" VARCHAR(255), "
            +COULMN_CHAT_FULLNAME+     " VARCHAR(255), "
            +COULMN_CHAT_SENTTIME+     " VARCHAR(255), "
            +COULMN_CHAT_MESSAGE+      " VARCHAR(255), "
            +COULMN_CHAT_USER_CHECK+   " VARCHAR(255), "
            +COULMN_CHAT_READ+         " VARCHAR(255) );"
            ;


    //fourth table data (match details)
    private static final String TABLE_NAME_MATCH            ="tbl_match";
    private static final String COULMN_MATCH_ID             ="match_id";
    private static final String COULMN_COMPLETE_MATCH_DETAIL="match_detail";

    private static final String CREATE_MATCH_DETAIL_TBL = "CREATE TABLE "+TABLE_NAME_MATCH+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_GROUP_ID+             " VARCHAR(255), "
            +COULMN_MATCH_ID+             " VARCHAR(255), "
            +COULMN_COMPLETE_MATCH_DETAIL+"  TEXT );"
            ;

    //fifth table data (squad details)
    private static final String TABLE_NAME_SQUAD   ="tbl_squad";
    private static final String COULMN_SQUAD_DETAIL="squad_detail";

    private static final String CREATE_SQUAD_TBL = "CREATE TABLE "+TABLE_NAME_SQUAD+ " ("
            +UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "
            +COULMN_GROUP_ID+    " VARCHAR(255), "
            +COULMN_SQUAD_DETAIL+"  TEXT );"
            ;

    //sixth table data (contact details)
    private static final String TABLE_NAME_CONTACT    ="tbl_contact";
    private static final String COULMN_CONTACT_NAME   ="contact_name";
    private static final String COULMN_CONTACT_NUMBER ="contact_number";
    private static final String COULMN_CONTACT_USER_ID="contact_user_id";
    private static final String COULMN_CONTACT_STATUS ="contact_status";

    private static final String CREATE_CONTACT_TBL = "CREATE TABLE "+TABLE_NAME_CONTACT+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_USER_ID         + " VARCHAR(255), "
            +COULMN_CONTACT_NAME    + " VARCHAR(255), "
            +COULMN_CONTACT_NUMBER  + " VARCHAR(255) UNIQUE , "
            +COULMN_CONTACT_USER_ID + " VARCHAR(255), "
            +COULMN_CONTACT_STATUS  + " VARCHAR(255) );"
            ;


    //sevevnth table data (boots details)
    private static final String TABLE_NAME_BOOTS    ="tbl_boots";
    private static final String COULMN_BOOTS_BID    ="bID";
    private static final String COULMN_BOOTS_BName  ="bName";
    private static final String COULMN_BOOTS_BSTATUS="bStatus";

    private static final String CREATE_BOOTS_TBL = "CREATE TABLE "+TABLE_NAME_BOOTS+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_BOOTS_BID      + " VARCHAR(255), "
            +COULMN_BOOTS_BName    + " VARCHAR(255), "
            +COULMN_BOOTS_BSTATUS  + " VARCHAR(255) );"
            ;

    //eigth table data (facilities details)
    private static final String TABLE_NAME_FACILITIES     ="tbl_facilities";
    private static final String COULMN_FACILITIES_FID     ="fID";
    private static final String COULMN_FACILITIES_FNAME   ="fName";
    private static final String COULMN_FACILITIES_FSTATUS ="fStatus";

    private static final String CREATE_FACILITIES_TBL = "CREATE TABLE "+TABLE_NAME_FACILITIES+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_FACILITIES_FID      + " VARCHAR(255), "
            +COULMN_FACILITIES_FNAME    + " VARCHAR(255), "
            +COULMN_FACILITIES_FSTATUS  + " VARCHAR(255) );"
            ;

    //ninth table data (restrictions details)
    private static final String TABLE_NAME_RESTRICTIONS    ="tbl_restrictions";
    private static final String COULMN_RESTRICTIONS_RID    ="id";
    private static final String COULMN_RESTRICTIONS_RNAME  ="restriction";
    private static final String COULMN_RESTRICTIONS_RSTATUS="resStatus";

    private static final String CREATE_RESTRICTIONS_TBL = "CREATE TABLE "+TABLE_NAME_RESTRICTIONS+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_RESTRICTIONS_RID      + " VARCHAR(255), "
            +COULMN_RESTRICTIONS_RNAME    + " VARCHAR(255), "
            +COULMN_RESTRICTIONS_RSTATUS  + " VARCHAR(255) );"
            ;

    //tenth table data (locationImages details)
    private static final String TABLE_NAME_LOCATIONIMAGES="tbl_locationImages";
    private static final String COULMN_LOCATION_ID       ="locationID";
    private static final String COULMN_LOCATION_IMAGE    ="locationImage";
    private static final String COULMN_LOCATION_IMAGETYPE="ImageType";


    private static final String CREATE_LOCATIONIMAGES_TBL = "CREATE TABLE "+TABLE_NAME_LOCATIONIMAGES+ " ("
            /*+UID+" INTEGER PRIMARY KEY AUTOINCREMENT , "*/
            +COULMN_LOCATION_ID      + " VARCHAR(255), "
            +COULMN_LOCATION_IMAGE    + " VARCHAR(255), "
            +COULMN_LOCATION_IMAGETYPE  + " VARCHAR(255) );"
            ;

    private static final String DROP_TABLE               ="DROP TABLE IF EXISTS "+TABLE_NAME;
    private static final String DROP_TABLE_GROUP         ="DROP TABLE IF EXISTS "+TABLE_NAME_GROUP;
    private static final String DROP_TABLE_CHAT          ="DROP TABLE IF EXISTS "+TABLE_NAME_CHAT;
    private static final String DROP_TABLE_MATCH         ="DROP TABLE IF EXISTS "+TABLE_NAME_MATCH;
    private static final String DROP_TABLE_SQUAD         ="DROP TABLE IF EXISTS "+TABLE_NAME_SQUAD;
    private static final String DROP_TABLE_CONTACT       ="DROP TABLE IF EXISTS "+TABLE_NAME_CONTACT;
    private static final String DROP_TABLE_BOOTS         ="DROP TABLE IF EXISTS "+TABLE_NAME_BOOTS;
    private static final String DROP_TABLE_FACILITIES    ="DROP TABLE IF EXISTS "+TABLE_NAME_FACILITIES;
    private static final String DROP_TABLE_RESTRICTIONS  ="DROP TABLE IF EXISTS "+TABLE_NAME_RESTRICTIONS;
    private static final String DROP_TABLE_LOCATIONIMAGES="DROP TABLE IF EXISTS "+TABLE_NAME_LOCATIONIMAGES;

    public MyDatabase(Context context) {
        super(context, "MyDB", null, 1);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_GROUP_TBL);
            db.execSQL(CREATE_CHAT_TBL);
            db.execSQL(CREATE_MATCH_DETAIL_TBL);
            db.execSQL(CREATE_SQUAD_TBL);
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_CONTACT_TBL);
            db.execSQL(CREATE_BOOTS_TBL);
            db.execSQL(CREATE_FACILITIES_TBL);
            db.execSQL(CREATE_RESTRICTIONS_TBL);
            db.execSQL(CREATE_LOCATIONIMAGES_TBL);
            } catch (Exception e) {
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            db.execSQL(DROP_TABLE_GROUP);
            db.execSQL(DROP_TABLE_CHAT);
            db.execSQL(DROP_TABLE_MATCH);
            db.execSQL(DROP_TABLE_SQUAD);
            db.execSQL(DROP_TABLE_CONTACT);
            db.execSQL(DROP_TABLE_BOOTS);
            db.execSQL(DROP_TABLE_FACILITIES);
            db.execSQL(DROP_TABLE_RESTRICTIONS);
            db.execSQL(DROP_TABLE_LOCATIONIMAGES);
            onCreate(db);
        }catch (Exception e) {
        }
    }

    public void insertdata(String group_id,String user_id,String message_id,String message) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_GROUP_ID,group_id);
        cv.put(COULMN_USER_ID,user_id);
        cv.put(COULMN_MESSAGE_ID,message_id);
        cv.put(COULMN_MESSAGE,message);
        database.insert(TABLE_NAME,null,cv);
    }
    public void insertdata_groups(String group_id,String user_id,String group_name,String group_type,String last_message,String last_message_type,String date,String memberrole,String game_id,String image,String status,String secondUserData,String lastmsgid) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_GROUP_ID,group_id);
        cv.put(COULMN_USER_ID,user_id);
        cv.put(COULMN_GROUP_NAME,group_name);
        cv.put(COULMN_GROUP_TYPE,group_type);
        cv.put(COULMN_GROUP_LAST_MESSAGE,last_message);
        cv.put(COULMN_GROUP_LAST_MESSAGE_TYPE,last_message_type);
        cv.put(COULMN_GROUP_DATE,date);
        cv.put(COULMN_GROUP_MEMBERROLE,memberrole);
        cv.put(COULMN_GROUP_GAME_ID,game_id);
        cv.put(COULMN_GROUP_IMAGE,image);
        cv.put(COULMN_GROUP_IN_OUT_STATUS,status);
        cv.put(COULMN_GROUP_SECONDUSERDATA,secondUserData);
        cv.put(COULMN_GROUP_LASTMSGID,lastmsgid);
        database.insert(TABLE_NAME_GROUP,null,cv);
    }
    public void insertdata_chat(String group_id,String user_id,String message_id,String message_image,String fullname,String senttime,String message,String user_check,String read_status) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_GROUP_ID          ,group_id);
        cv.put(COULMN_USER_ID           ,user_id);
        /*cv.put(COULMN_GROUP_NAME        ,group_name);*/
        cv.put(COULMN_CHAT_MESSAGE_ID   ,message_id);
        cv.put(COULMN_CHAT_MESSAGE_IMAGE,message_image);//✓✓
        cv.put(COULMN_CHAT_FULLNAME     ,fullname);
        cv.put(COULMN_CHAT_SENTTIME     ,senttime);
        cv.put(COULMN_CHAT_MESSAGE      ,message);
        cv.put(COULMN_CHAT_USER_CHECK   ,user_check);
        cv.put(COULMN_CHAT_READ         ,read_status);
        database.insert(TABLE_NAME_CHAT ,null,cv);
    }
    public void insertdata_match_details(String group_id,String match_id,String match_details) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_GROUP_ID,group_id);
        cv.put(COULMN_MATCH_ID,match_id);
        cv.put(COULMN_COMPLETE_MATCH_DETAIL,match_details);
        if (getdata_match_details_check(group_id).equals("0")){
            database.insert(TABLE_NAME_MATCH,null,cv);
        }else {
            getdata_match_details_dlt(group_id);
            database.insert(TABLE_NAME_MATCH,null,cv);
        }
    }
    public void insertdata_squad_details(String group_id,String match_details) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_GROUP_ID,group_id);
        cv.put(COULMN_SQUAD_DETAIL,match_details);
        if (getdata_squad_details_check(group_id).equals("0")){
            database.insert(TABLE_NAME_SQUAD,null,cv);
        }else {
            getdata_squad_details_dlt(group_id);
            database.insert(TABLE_NAME_SQUAD,null,cv);
        }
    }
    public void insertdata_contact(String user_id,String contactname,String contactnumber,String contact_user_id,String contactstatus) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_USER_ID,user_id);
        cv.put(COULMN_CONTACT_NAME,contactname);
        cv.put(COULMN_CONTACT_NUMBER ,contactnumber);
        cv.put(COULMN_CONTACT_USER_ID ,contact_user_id);
        cv.put(COULMN_CONTACT_STATUS,contactstatus);
        database.insert(TABLE_NAME_CONTACT,null,cv);
    }
    public void insertdata_boots(String bID,String bName,String bStatus) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_BOOTS_BID,bID);
        cv.put(COULMN_BOOTS_BName,bName);
        cv.put(COULMN_BOOTS_BSTATUS,bStatus);
        database.insert(TABLE_NAME_BOOTS,null,cv);
    }
    public void insertdata_facilities(String fID,String fName,String fStatus) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_FACILITIES_FID,fID);
        cv.put(COULMN_FACILITIES_FNAME,fName);
        cv.put(COULMN_FACILITIES_FSTATUS,fStatus);
        database.insert(TABLE_NAME_FACILITIES,null,cv);
    }
    public void insertdata_restriction(String rid,String rName,String rStatus) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_RESTRICTIONS_RID,rid);
        cv.put(COULMN_RESTRICTIONS_RNAME,rName);
        cv.put(COULMN_RESTRICTIONS_RSTATUS,rStatus);
        database.insert(TABLE_NAME_RESTRICTIONS,null,cv);
    }
    public void insertdata_locationImages(String locationID,String locationImage,String ImageType) {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COULMN_LOCATION_ID,locationID);
        cv.put(COULMN_LOCATION_IMAGE,locationImage);
        cv.put(COULMN_LOCATION_IMAGETYPE,ImageType);
        database.insert(TABLE_NAME_LOCATIONIMAGES,null,cv);
    }

    public Cursor getdata()        {
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME,null);
        return cursor;
    }
    public Cursor getdata_groups() {
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_GROUP,null);
        return cursor;
    }
    public Cursor getdata_groups_msg_id() {
        SQLiteDatabase database=this.getReadableDatabase();

        Cursor cursor=database.rawQuery("SELECT tbl_groups.*, tbl_chat.msg_id, tbl_chat.senttime from tbl_groups, tbl_chat where tbl_chat.msg_id = (SELECT MAX(tbl_chat.msg_id) FROM tbl_chat WHERE tbl_chat.group_id = tbl_groups.group_id GROUP BY group_id ORDER BY msg_id DESC LIMIT 1) GROUP BY tbl_groups.group_id ORDER BY tbl_chat.msg_id DESC;",null);

        /* Cursor cursor=database.rawQuery("SELECT tbl_groups.*, tbl_chat.msg_id, tbl_chat.senttime from tbl_groups, tbl_chat where \n" +
                "          tbl_chat.msg_id = (SELECT MAX(tbl_chat.msg_id) FROM tbl_chat WHERE \n" +
                "          tbl_chat.group_id = tbl_groups.group_id GROUP BY group_id ORDER BY msg_id DESC LIMIT 1) \n" +
                "          GROUP BY tbl_groups.group_id ORDER BY tbl_chat.msg_id DESC;",null);*/

        /*
          SELECT tbl_groups.*, tbl_chat.msg_id, tbl_chat.senttime from tbl_groups, tbl_chat where
          tbl_chat.msg_id = (SELECT MAX(tbl_chat.msg_id) FROM tbl_chat WHERE
          tbl_chat.group_id = tbl_groups.group_id GROUP BY group_id ORDER BY msg_id DESC LIMIT 1)
          GROUP BY tbl_groups.group_id ORDER BY tbl_chat.msg_id DESC;
          */

        return cursor;
    }
    public Cursor getdata_chat(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_CHAT+" where group_id = "+group_id,null);
        return cursor;
    }
    public String getdata_chat_check_data(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("select msg_id from "+TABLE_NAME_CHAT+" where group_id = "+group_id+" order by msg_id desc limit 1",null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("msg_id"));
        }
        if (result.equals(""))
            result="0";
        cursor.close();
        return result;
    }
    public String getdata_match_details_check(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("select count(*) AS chk_match from tbl_match where group_id = "+group_id,null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("chk_match"));
        }
        if (result.equals(""))
            result="0";
        cursor.close();
        return result;
    }
    public String getdata_match_details_dlt(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("delete from tbl_match where group_id = "+group_id,null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        cursor.close();
        return result;
    }
    public String setdata_contact_user_id(String contactname,String contactnumber,String db_user_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";

        ContentValues cv=new ContentValues();
        cv.put(COULMN_CONTACT_USER_ID,db_user_id);

        database.update(TABLE_NAME_CONTACT,cv,COULMN_CONTACT_NUMBER + " = ?",new String[]{ String.valueOf(contactnumber) });

        return result;
    }

    public String setdata_chat_msg_read_status(String groupd_id,String user_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";

        ContentValues cv=new ContentValues();
        cv.put(COULMN_CHAT_READ,"read");

        database.update(TABLE_NAME_CHAT,cv,COULMN_GROUP_ID + " = ? and "+COULMN_USER_ID+" = ?",new String[]{ String.valueOf(groupd_id),String.valueOf(user_id)});

        return result;
    }

    public String getdata_contact_user_id(String contactnumber){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("SELECT contact_user_id from "+TABLE_NAME_CONTACT+" where contact_number = '"+contactnumber+"' ",null);
        Log.d("TAG", "File...:::: query = " + "select contact_user_id from "+TABLE_NAME_CONTACT+" where contact_number = '"+contactnumber+"'");
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("contact_user_id"));
        }
        if (result.equals(""))
            result="";
        cursor.close();
        return result;
    }
    public String getdata_contact_user_name(String contact_user_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("select contact_name from "+TABLE_NAME_CONTACT+" where contact_user_id = "+contact_user_id,null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("contact_name"));
        }
        if (result.equals(""))
            result="";
        cursor.close();
        return result;
    }
    public String getdata_contact_group_id(String contact_user_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";

        String groupnamef = sharedPreferences.getString("userID","")+"-"+contact_user_id;
        String groupnames = contact_user_id+"-"+sharedPreferences.getString("userID","");

        Cursor cursor=database.rawQuery("select group_id from "+TABLE_NAME_GROUP+" where group_name = '"+groupnamef+"' or group_name = '"+groupnames+"'",null);
        Log.d("TAG", "File...:::: query = " + "select group_id from "+TABLE_NAME_GROUP+" where group_name = "+groupnamef+" or group_name ="+groupnames);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("group_id"));
        }
        if (result.equals(""))
            result="";
        cursor.close();
        return result;
    }
    public Cursor getdata_match_details(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_MATCH+" where group_id = '"+group_id+"' Limit 1",null);
        return cursor;
    }
    public String getdata_squad_details_check(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("select count(*) AS chk_squad from tbl_squad where group_id = "+group_id,null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("chk_squad"));
        }
        if (result.equals(""))
            result="0";
        cursor.close();
        return result;
    }
    public String getdata_squad_details_dlt(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        Cursor cursor=database.rawQuery("delete from tbl_squad where group_id = "+group_id,null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        cursor.close();
        return result;
    }
    public String getdata_last_msg_id_user_wise(String user_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        //SELECT msg_id FROM tbl_chat where user_id = 91 order by msg_id DESC LIMIT 1
        Cursor cursor=database.rawQuery("SELECT msg_id FROM tbl_chat where user_id = "+user_id+" order by msg_id DESC LIMIT 1",null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("msg_id"));
        }
        if (result.equals(""))
            result="0";
        cursor.close();
        return result;
    }

    public String getdata_last_msg_id_group_wise(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        //SELECT msg_id FROM tbl_chat where user_id = 91 order by msg_id DESC LIMIT 1
        Cursor cursor=database.rawQuery("SELECT msg_id FROM tbl_chat where group_id = "+group_id+" order by msg_id DESC LIMIT 1",null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("msg_id"));
        }
        if (result.equals(""))
            result="0";
        cursor.close();
        return result;
    }

    public String getdata_last_msg_id_read_status(String msg_id){
        SQLiteDatabase database=this.getReadableDatabase();
        String result="";
        //SELECT msg_id FROM tbl_chat where user_id = 91 order by msg_id DESC LIMIT 1
        Cursor cursor=database.rawQuery("SELECT read_status FROM tbl_chat where msg_id = "+msg_id+" order by msg_id DESC LIMIT 1",null);
        //select msg_id from tbl_chat where group_id = 167 order by msg_id desc limit 1;
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("read_status"));
        }
        if (result.equals(""))
            result="0";
        cursor.close();
        return result;
    }

    public Cursor getdata_squad_details(String group_id){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_SQUAD+" where group_id = "+group_id+" limit 1",null);
        return cursor;
    }
    public Cursor getdata_contact(){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_CONTACT,null);
        return cursor;
    }
    public Cursor getdata_contact_user_id_wise_contact(String user_id){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_CONTACT+" where user_id = '"+user_id +"' and contact_user_id = null",null);
        Log.d("TAG", "select * from "+TABLE_NAME_CONTACT+" where user_id = '"+user_id +"' and contact_user_id = null");
        return cursor;
    }
    public Cursor getdata_boots(){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_BOOTS,null);
        return cursor;
    }
    public Cursor getdata_facilities(){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_FACILITIES,null);
        return cursor;
    }
    public Cursor getdata_restriction(){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_RESTRICTIONS,null);
        return cursor;
    }
    public Cursor getdata_locationImages(){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from "+TABLE_NAME_LOCATIONIMAGES,null);
        return cursor;
    }
    public Cursor getdata_gameids(){
        SQLiteDatabase database=this.getReadableDatabase();
        //select game_id from tbl_groups where game_id !="";
        Cursor cursor=database.rawQuery("select game_id from tbl_groups where game_id !=''",null);
        return cursor;
    }

    public void delete_record(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
    public void delete_record_groups(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_GROUP);
    }
    public void delete_record_chat(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_CHAT);
    }
    public void delete_record_match_detail(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_MATCH);
    }
    public void delete_record_squad_detail(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_SQUAD);
    }
    public void delete_contact(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_CONTACT);
    }
    public void delete_boots(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_BOOTS);
    }
    public void delete_facilities(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_FACILITIES);
    }
    public void delete_restriction(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_RESTRICTIONS);
    }
    public void delete_locationImages(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_LOCATIONIMAGES);
    }

    /*
        public void updatedata(String newName,String oldName){
        Gettersetter gtst =new Gettersetter();
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        "update TABLE_NAME_CONTACT SET contact_user_id="+db_user_id +" where contact_name="+contactname+" and contact_number= "+contactnumber
        String query="UPDATE " + TABLE_NAME_CONTACT + " SET " + COULMN_CONTACT_USER_ID + " = '" + newName + "' WHERE " + NAME + " = '" + oldName + "' AND " + NAME + " = '" + oldName + ";
        database.execSQL(query);
        }
    */
}
