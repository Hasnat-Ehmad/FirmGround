package com.firmground.evs.firmground.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.adapter.SquadList_adapter;
import com.firmground.evs.firmground.model.Squad_list;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupDetailActivity extends AppCompatActivity {

    String  group_Id,gameId;
    ArrayList<Squad_list> All_squad =new ArrayList();
    SquadList_adapter adapter;
    ListView listView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Bundle bundle = getIntent().getExtras();

        final TextView  ed_group_name  = findViewById(R.id.ed_group_name);
        final ImageView img_group_icon = findViewById(R.id.img_group_icon);
        listView = findViewById(R.id.list_view);

        group_Id    = bundle.getString("group_Id"  );

        if (bundle.containsKey("gameId")){
            gameId = bundle.getString("gameId"  );
        }else {
            gameId="";
        }

        //http://192.168.100.14/FirmGround/REST/groupsMembers?gId=2
        final String url = getResources().getString(R.string.url) + "groupsMembers?gId="+group_Id;
        final String params = "";
        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
            @Override
            public String TaskCompletionResult(String result) throws JSONException {

                JSONObject jsonObject = new JSONObject(result);

                if(jsonObject.getString("respCode").equals("200")) {
                    JSONArray results = jsonObject.getJSONArray("results");
                    All_squad.clear();
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject c = results.getJSONObject(i);

                        String membershipId = c.getString("membershipId");
                        String UserID = c.getString("UserID");
                        String groupId = c.getString("gId");
                        String memberRole = c.getString("memberRole");
                        String dateAdded = c.getString("dateAdded");
                        String full_name = c.getString("full_name");
                        String fname = c.getString("fname");
                        String lname = c.getString("lname");
                        String phone = c.getString("phone");

                        //Toast.makeText(UpdateGroupActivity.this,fname+" "+UserID,Toast.LENGTH_SHORT).show();
                        if (fname.equals("")){

                            if(full_name.contains(" ")){
                                fname= full_name.substring(0, full_name.indexOf(" "));
                                System.out.println(fname);
                            }else
                                fname=full_name;
                        }

                        Squad_list squad_list = new Squad_list(""+membershipId,"" + fname, ""+memberRole,""+phone,"2");
                        All_squad.add(squad_list);
                    }

                    adapter = new SquadList_adapter(GroupDetailActivity.this,All_squad);
                    listView.setAdapter(adapter);

                    JSONObject GroupInfo = jsonObject.getJSONObject("GroupInfo");

                    String gId        = GroupInfo.getString("gId");
                    String GroupName  = GroupInfo.getString("GroupName");
                    ed_group_name.setText(""+GroupName);
                    String UserID     = GroupInfo.getString("UserID");
                    String image      = getResources().getString(R.string.imageBaseUrl)+GroupInfo.getString("image");
                    Picasso.with(img_group_icon.getContext()).load(""+image).into(img_group_icon);
                    String CreatedDate= GroupInfo.getString("CreatedDate");
                    String status     = GroupInfo.getString("status");
                }

                return result;
            }
        });
        webRequestCall.execute(url,"GET",params);

        final Button btn_update_group = findViewById(R.id.btn_update_group);
        btn_update_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //http://192.168.100.14/FirmGround/REST/userGroupStatus?gameId=1
                final String url = getResources().getString(R.string.url) + "userGroupStatus?" +
                        "UserID="+sharedPreferences.getString("userID","")+
                        "&gId="+group_Id+
                        "&NewStatus=deleted";
                final String params = "";
                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) throws JSONException {

                        JSONObject jsonObject = new JSONObject(result);

                        if(jsonObject.getString("respCode").equals("200")) {


                        }else {
                        }

                        return result;
                    }
                });
                webRequestCall.execute(url,"GET",params);

            }
        });

    }
}
