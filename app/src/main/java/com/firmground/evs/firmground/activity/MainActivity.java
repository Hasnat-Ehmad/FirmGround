package com.firmground.evs.firmground.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.fragment.GroupFragment;
import com.firmground.evs.firmground.fragment.ContactListFragment;
import com.firmground.evs.firmground.fragment.MenuFragment;
import com.firmground.evs.firmground.fragment.ProfileFragment;
import com.firmground.evs.firmground.fragment.dummy.DummyContent;
import com.firmground.evs.firmground.service.Incoming_Chat_Service;
import com.firmground.evs.firmground.service.backendApi_Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GroupFragment.OnListFragmentInteractionListener,ProfileFragment.OnFragmentInteractionListener {

    //private TextView mTextMessage;

    EditText ed_search;
    LinearLayout toolbar_layout;

    Fragment fragment = null;

    int numberoffile=0;

    FileOutputStream output = null;

    public int counter_fragment_friens=0;

    public static Activity activity;

    public static ProgressDialog progressDialog;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    //mTextMessage.setText(R.string.title_home);
                    fragment = new GroupFragment();
                    break;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.title_dashboard);
                    //Intent intent = new Intent(MainActivity.this,ChatPageActivity.class);
                    //startActivity(intent);
                    fragment = new ProfileFragment();
                    break;
                case R.id.navigation_groups:
                    //mTextMessage.setText(R.string.title_notifications);
                   // return true;
                    break;
                /*case R.id.navigation_list:
                    //mTextMessage.setText(R.string.title_notifications);
                    //return true;
                    break;*/
                case R.id.navigation_more:
                    //mTextMessage.setText(R.string.title_notifications);
                    fragment = new MenuFragment();
                    break;
            }
            if(fragment != null){
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentTransaction.commit();

                //tv_title.setText(""+title);
            }
            return true;
        }
    };


    private FragmentActivity fragmentActivity;

    public static ImageView add_group_img;

    public Intent backendApi_service;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backendApi_service = new Intent(MainActivity.this, backendApi_Service.class);
        stopService(backendApi_service);
        startService(backendApi_service);

        activity = MainActivity.this;

        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new GroupFragment());
        transaction.commit();

        //mTextMessage = (TextView) findViewById(R.id.message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            Drawable background = getResources().getDrawable(R.drawable.toolbar_background);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            //window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_toolbar_title.setText(Html.fromHtml("<b>f</b>g", Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv_toolbar_title.setText(Html.fromHtml("<b>f</b>g"));
        }

        fragmentActivity = (FragmentActivity) activity;

        final EditText ed_search = findViewById(R.id.ed_search);
        final LinearLayout toolbar_layout = findViewById(R.id.toolbar_layout);

        final ImageView cross_img = findViewById(R.id.cross_img);
        ImageView search_img = findViewById(R.id.search_img);
        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
                ed_search.setVisibility(View.VISIBLE);
                cross_img.setVisibility(View.VISIBLE);
                toolbar_layout.setVisibility(View.GONE);
                cross_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolbar_layout.setVisibility(View.VISIBLE);
                        ed_search.setVisibility(View.GONE);
                        cross_img.setVisibility(View.GONE);
                    }
                });
            }
        });

        ImageView add_match_img = findViewById(R.id.add_match_img);
        add_match_img.setVisibility(View.GONE);
        add_group_img = findViewById(R.id.add_group_img);
        add_group_img.setVisibility(View.VISIBLE);
        add_group_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
                ContactListFragment tab1 = new ContactListFragment();
                fragmentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, tab1)
                        .addToBackStack(null)   // this will be it to the back stack
                        .commit();

            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void write1(String contact) {
        try
        {
            numberoffile++;
            String mediaState = Environment.getExternalStorageState();
            if(mediaState.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            {
                if (counter_fragment_friens==0){

                    counter_fragment_friens++;

                    SharedPreferences.Editor editor = getSharedPreferences("check_counter", MODE_PRIVATE).edit();

                    editor.putInt("counter_fragment_friens",counter_fragment_friens);
                    editor.commit();

                    //Toast.makeText(getApplicationContext(),"file created",Toast.LENGTH_SHORT).show();
                    output = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/FG_Contact_List.txt"));
                }
            }
            else
            {
                // error

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (counter_fragment_friens>0){

                    if (output != null){
                       // Toast.makeText(getApplicationContext(),"this is inside write data",Toast.LENGTH_SHORT).show();

                        output.write(contact.getBytes());
                        output.flush();
                    }

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("check_counter", MODE_PRIVATE);
        if (prefs != null) {
            counter_fragment_friens   = prefs.getInt("counter_fragment_friens"   ,0);//"No name defined" is the default value.
            //0 is the default value.
        }
    }
}
