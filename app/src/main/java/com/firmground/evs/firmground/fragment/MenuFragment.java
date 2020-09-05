package com.firmground.evs.firmground.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.LoginActivity;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;

import org.json.JSONException;
import org.json.JSONObject;


public class MenuFragment extends Fragment {

    TextView tv_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        ImageView search_img = getActivity().findViewById(R.id.search_img);
        search_img.setVisibility(View.GONE);

        ImageView add_group_img = getActivity().findViewById(R.id.add_group_img);
        add_group_img.setVisibility(View.GONE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        tv_logout = rootView.findViewById(R.id.tv_logout);

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView_title = new TextView(getActivity());
                textView_title.setText("Log Out");
                textView_title.setGravity(Gravity.START);
                textView_title.setPadding(20,10,20,10);
                textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                textView_title.setCompoundDrawablePadding(10);

                //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);

                new AlertDialog.Builder(getActivity())
                        .setCustomTitle(textView_title)
                        .setMessage("Would you want logout?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(getActivity().getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                final String url = "http://qa.monshiapp.com/FirmGround/REST/logout";
                                final String params = "token="+sharedPreferences.getString("token_id","");
                                final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                                    @Override
                                    public String TaskCompletionResult(String result) throws JSONException {

                                        JSONObject jsonObject = new JSONObject(result);

                                        if(jsonObject.getString("respCode").equals("200")) {

                                            MyDatabase mydb = new MyDatabase(getActivity());

                                            //mydb.delete_record_chat();
                                            mydb.delete_record_match_detail();
                                            mydb.delete_record_squad_detail();

                                            mydb.close();

                                            //mydb.delete_contact();
                                            SavePreferences("yes_login"     ,"");
                                            Intent i = new Intent(getActivity(), LoginActivity.class);
                                            startActivity(i);
                                            //close this activity
                                            getActivity().finish();

                                        }else {

                                        }
                                        return result;
                                    }
                                });
                                webRequestCall.execute(url,"POST",params);

                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(getActivity().getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //http://192.168.100.14/FirmGround/REST/userParticipationStatusChange?gameId=1&status=pending&gId=6&userID=2
                            }
                        })

                        .show();
            }
        });

       return rootView;
    }
    SharedPreferences sharedPreferences;
    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
