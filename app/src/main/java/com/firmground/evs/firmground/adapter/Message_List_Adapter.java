package com.firmground.evs.firmground.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.model.Message_List;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;

import static com.firmground.evs.firmground.adapter.MyChatRecyclerViewAdapter.single_screen_check;


/**
 * Created by hp on 2/16/2018.
 */

public class Message_List_Adapter extends BaseAdapter {

    private Activity mContext;
    static private List<Message_List> message_lists = null;

    private LayoutInflater inflater=null;/**/
    private ArrayList<Message_List> arraylist;
    private SparseBooleanArray mSelectedItemsIds;
    private FragmentActivity fragmentActivity;
    private static final int TYPES_COUNT = 2;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    private Handler  handler;

    private boolean     isPlaying = false;

    private static final String TAG = "Adapter";

    private SimpleExoPlayer exoPlayer;

    public static ArrayList<Boolean> positionArray;
    MediaPlayer mPlayer;


    public Message_List_Adapter(Activity context, List<Message_List> messageLists) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.message_lists = messageLists;
        //inflater = LayoutInflater.from(this.mContext);
        this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        positionArray = new ArrayList<Boolean>(messageLists.size());
        for (int i = 0; i < messageLists.size(); i++) {
            positionArray.add(false);
        }

    }

    private class ViewHolder {

        public String id; //Assuming your id is an int, if not, changed it to the correct type

        TextView chatTV, timeTV,tv_username,tv_play_icon,time_current,player_end_time;
        TextView messageStatusIV;
        ImageView img_send,item_image;
        SeekBar mediacontroller_progress;
        View view;
        RelativeLayout relative_layout;

        LinearLayout Layoutmessgaedetail;
       // CircleImageView chatIV;
    }

    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public int getItemViewType (int position) {
        if (getItem(position).getSender().equals("other")) {
            return TYPE_LEFT;
        }
        return TYPE_RIGHT;
    }

    @Override
    public int getCount() {
        return message_lists.size();
    }

    @Override
    public Message_List getItem(int position) {
        return message_lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        //final Message_List item = getItem(position);
        final Message_List item = message_lists.get(position);;

        if (convertView == null  || ((ViewHolder)convertView.getTag()).id != item.getMessage_id()) {

            holder = new Message_List_Adapter.ViewHolder();


            if (item.getSender().equals("other")) {//status check here
                convertView = inflater.inflate(R.layout.item_messsage_incoming, parent, false);

                holder.view   = convertView.findViewById(R.id.view );
                holder.chatTV = convertView.findViewById(R.id.chatTV);
                holder.timeTV = convertView.findViewById(R.id.timeTV);
                holder.img_send    = convertView.findViewById(R.id.img_send);
                holder.tv_username = convertView.findViewById(R.id.tv_username);
                holder.tv_play_icon= convertView.findViewById(R.id.tv_play_icon);
                holder.item_image     = convertView.findViewById(R.id.item_image);
                holder.tv_play_icon   = convertView.findViewById(R.id.tv_play_icon);
                holder.time_current   = convertView.findViewById(R.id.time_current);
                holder.player_end_time= convertView.findViewById(R.id.player_end_time);
                holder.relative_layout= convertView.findViewById(R.id.relative_layout);
                holder.mediacontroller_progress = convertView.findViewById(R.id.mediacontroller_progress);
                holder.Layoutmessgaedetail = convertView.findViewById(R.id.Layoutmessgaedetail);
            }else{
                convertView = inflater.inflate(R.layout.item_messsage_outgoing, parent, false);

                holder.view     = convertView.findViewById(R.id.view );
                holder.chatTV   = convertView.findViewById(R.id.chatTV);
                holder.timeTV   = convertView.findViewById(R.id.timeTV);
                holder.img_send = convertView.findViewById(R.id.img_send);
                holder.item_image      = convertView.findViewById(R.id.item_image);
                holder.tv_play_icon    = convertView.findViewById(R.id.tv_play_icon);
                holder.time_current    = convertView.findViewById(R.id.time_current);
                holder.relative_layout = convertView.findViewById(R.id.relative_layout);
                holder.player_end_time = convertView.findViewById(R.id.player_end_time);
                holder.messageStatusIV = convertView.findViewById(R.id.messageStatusIV);
                holder.mediacontroller_progress= convertView.findViewById(R.id.mediacontroller_progress);
            }

            holder.id = item.getMessage_id();
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        fragmentActivity = (FragmentActivity) mContext;

        mPlayer = new MediaPlayer();

        if (item.getSender().equals("other")) {// other messages

            notifyDataSetChanged();

            if (item.getMessage().equals("")){
                holder.chatTV.setVisibility(View.GONE);
                holder.view.setVisibility(View.VISIBLE);
            }else {
                holder.view.setVisibility(View.GONE);
                holder.chatTV.setVisibility(View.VISIBLE);
                holder.chatTV.setText(item.getMessage());
            }

            notifyDataSetChanged();

            if (single_screen_check == 1 || single_screen_check == 2){
                holder.tv_username.setText(""+item.getName());
                holder.tv_username.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,30,0,0);
                holder.chatTV.setLayoutParams(params);
            }else {
                holder.tv_username.setText("");
                holder.tv_username.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,3,0,0);
                holder.chatTV.setLayoutParams(params);
            }

            String currentString = ""+item.getDate_time();
            String[] separated   = currentString.split(" ");

            // String mytime="Jan 17, 2012";
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");

            Date myDate = null;
            try {
                myDate = dateFormat.parse(""+separated[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
            String finalDate = timeFormat.format(myDate);

            System.out.println(finalDate);

            //separated[0]; // this will contain "Fruit"
            //separated[1]; // this will contain " they taste good"

            holder.timeTV.setText(""+finalDate);
            //holder.timeTV.setText(""+item.getDate_time());
            //holder.timeTV.setText(""+item.getDate_time());

            holder.img_send.setVisibility(View.GONE);holder.tv_play_icon.setVisibility(View.GONE);

                if (item.getImage_check().contains(".jpg")||item.getImage_check().contains(".png")){
                    holder.img_send.setVisibility(View.VISIBLE);

                    holder.tv_play_icon.setVisibility(View.GONE);
                    holder.relative_layout.setVisibility(View.GONE);

                    holder.item_image.setVisibility(View.GONE);

                    Picasso.with(mContext).load(""+item.getImage_check()).into(new Target(){
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            Bitmap myImage = bitmap;
                            int width = myImage.getWidth();
                            int height = myImage.getHeight();

                            Log.e("TAG","width = "+width);
                            Log.e("TAG","height = "+height);

                            if (height>200 && height<350)
                                holder.img_send.getLayoutParams().height = 600;
                            else
                                holder.img_send.getLayoutParams().height = height;

                            if (width>400 && width<650)
                                holder.img_send.getLayoutParams().width = 850;

                            Picasso.with( holder.img_send.getContext()).load(""+item.getImage_check()).into( holder.img_send);

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });

                    // Picasso.with( holder.img_send.getContext()).load(""+item.getImage_check()).into( holder.img_send);
                    //Toast.makeText(mContext," = "+item.getImage_check(),Toast.LENGTH_SHORT).show();
                }
                else {

                    holder.tv_play_icon.setVisibility(View.VISIBLE);
                    holder.relative_layout.setVisibility(View.VISIBLE);
                    holder.item_image.setVisibility(View.VISIBLE);
                    holder.img_send.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);

                    if (item.getImage_check().contains(".3gp")){
                        holder.img_send.setVisibility(View.GONE);
                        holder.tv_play_icon.setVisibility(View.VISIBLE);
                        holder.tv_play_icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                downloadFile(""+item.getImage_check(),holder,item,position);
                                //prepareExoPlayerFromURL(Uri.parse(""+item.getImage_check()),holder,item,position);

                               // Toast.makeText(mContext, "other abc= "+item.getImage_check(), Toast.LENGTH_LONG).show();

                              /*  mPlayer.reset();
                                try {
                                    mPlayer.setDataSource(mContext, Uri.parse(""+item.getImage_check()));
                                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);;
                                    mPlayer.prepare();
                                    mPlayer.start();
                                    //Toast.makeText(mContext, "item.getImage_check() = "+item.getImage_check(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    //Toast.makeText(mContext, ""+e.toString(), Toast.LENGTH_LONG).show();
                                }*/
                            }
                        });
                    }
                }
             if (item.getImage_check().equals("")&&item.getAudio_path().equals(""))  {
                 holder.img_send.setVisibility(View.GONE);
                 holder.tv_play_icon.setVisibility(View.GONE);
                 holder.tv_play_icon.setVisibility(View.GONE);
                 holder.relative_layout.setVisibility(View.GONE);
                 holder.item_image.setVisibility(View.GONE);
             }
            holder.img_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogShow_bus_detail(mContext,position,item);
                }
            });
        }
        else{

            notifyDataSetChanged();

            holder.messageStatusIV.setTag(position);

            if (item.getMessage().equals("")){
                holder.view.setVisibility(View.VISIBLE);
                holder.chatTV.setVisibility(View.GONE);
            }else {
                holder.view.setVisibility(View.GONE);
                holder.chatTV.setVisibility(View.VISIBLE);
                holder.chatTV.setText(item.getMessage());
            }

            notifyDataSetChanged();

            String currentString = ""+item.getDate_time();
            String[] separated   = currentString.split(" ");

           // String mytime="Jan 17, 2012";
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");

            Date myDate = null;
            try {
                 myDate = dateFormat.parse(""+separated[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
            String finalDate = timeFormat.format(myDate);

            System.out.println(finalDate);

            //separated[0]; // this will contain "Fruit"
            //separated[1]; // this will contain " they taste good"

            holder.timeTV.setText(""+finalDate);
            //holder.timeTV.setText(""+item.getDate_time());
            holder.chatTV.setTag(item.getMessage_id());

            holder.img_send.setVisibility(View.GONE);holder.tv_play_icon.setVisibility(View.GONE);

            if (item.getImage_check().contains(".jpg")||item.getImage_check().contains(".png")){
                holder.img_send.setVisibility(View.VISIBLE);

                holder.tv_play_icon.setVisibility(View.GONE);
                holder.relative_layout.setVisibility(View.GONE);
                holder.item_image.setVisibility(View.GONE);

                //Toast.makeText(mContext," = "+item.getImage_check(),Toast.LENGTH_SHORT).show();
                if (item.getImage_check().contains("http://qa.monshiapp.com")){
                    Log.e("TAG","item.getImage_check() = "+item.getImage_check());


                    Picasso.with(mContext).load(""+item.getImage_check()).into(new Target(){
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            Bitmap myImage = bitmap;
                            int width = myImage.getWidth();
                            int height = myImage.getHeight();

                            Log.e("TAG","width = "+width);
                            Log.e("TAG","height = "+height);

                            if (height>200)
                                holder.img_send.getLayoutParams().height = 400;
                            else
                                holder.img_send.getLayoutParams().height = height;

                            if (width>400)
                                holder.img_send.getLayoutParams().width = 500;

                            /*if (height>200 && height<350)
                                holder.img_send.getLayoutParams().height = 300;
                            else
                                holder.img_send.getLayoutParams().height = height;

                            if (width>400 && width<650)
                                holder.img_send.getLayoutParams().width = 450;*/

                            Picasso.with( holder.img_send.getContext()).load(""+item.getImage_check()).into( holder.img_send);

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });

                    }
                else {
                    //Picasso.with(holder.img_send.getContext()).load(""+item.getImage_check()).into( holder.img_send);

                    Picasso.with(mContext).load(""+item.getImage_check()).into(new Target(){
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            Bitmap myImage = bitmap;
                            int width = myImage.getWidth();
                            int height = myImage.getHeight();

                            Log.e("TAG","width = "+width);
                            Log.e("TAG","height = "+height);

                            if (height>200)
                                holder.img_send.getLayoutParams().height = 400;
                            else
                                holder.img_send.getLayoutParams().height = height;

                            if (width>400)
                                holder.img_send.getLayoutParams().width = 500;

                            /* if (height>200)
                                holder.img_send.getLayoutParams().height = 600;
                            else
                                holder.img_send.getLayoutParams().height = height;

                            if (width>400)
                                holder.img_send.getLayoutParams().width = 850;*/

                            Picasso.with( holder.img_send.getContext()).load(""+item.getImage_check()).into( holder.img_send);

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
                    //Picasso.with(holder.img_send.getContext()).load(""+item.getImage_check()).into( holder.img_send);
                }
            }
            else {
                holder.tv_play_icon.setVisibility(View.VISIBLE);
                holder.relative_layout.setVisibility(View.VISIBLE);
                holder.item_image.setVisibility(View.VISIBLE);
                holder.img_send.setVisibility(View.GONE);
                holder.view.setVisibility(View.GONE);

                if (item.getImage_check().contains(".3gp") && item.getImage_check().contains("http://qa.monshiapp.com")){

                    holder.tv_play_icon.setVisibility(View.VISIBLE);

                    holder.tv_play_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //mPlayer.reset();
                            //Toast.makeText(mContext, "item.getImage_check() = "+item.getImage_check(), Toast.LENGTH_LONG).show();
                            Log.i(TAG," = "+item.getImage_check());
                            downloadFile(""+item.getImage_check(), holder, item, position);
                            //prepareExoPlayerFromURL(Uri.parse(""+item.getImage_check()),holder, item, position);
                           /* try {
                                prepareExoPlayerFromURL(Uri.parse(""+item.getImage_check()));
                                mPlayer.setDataSource(mContext, Uri.parse(""+item.getImage_check()));
                                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mPlayer.prepare();
                                mPlayer.start();
                                //Toast.makeText(mContext, "item.getImage_check() = "+item.getImage_check(), Toast.LENGTH_LONG).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, ""+e.toString(), Toast.LENGTH_LONG).show();
                            }*/
                        }
                    });
                }else {

                    holder.tv_play_icon.setVisibility(View.VISIBLE);
                    holder.img_send.setVisibility(View.GONE);
                    holder.tv_play_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //mPlayer.reset();
                            //downloadFile(""+item.getAudio_path(), holder, item, position);
                            prepareExoPlayerFromURL(Uri.parse(""+item.getAudio_path()),holder, item, position);
                           /* try {
                                mPlayer.setDataSource(""+item.getAudio_path());
                                mPlayer.prepare();
                                mPlayer.start();
                                //Toast.makeText(mContext, "Recording Started Playing", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
                        }
                    });
                }
            }
            if (item.getImage_check().equals("")&&item.getAudio_path().equals(""))  {
                holder.img_send.setVisibility(View.GONE);
                holder.tv_play_icon.setVisibility(View.GONE);
                holder.relative_layout.setVisibility(View.GONE);
                holder.item_image.setVisibility(View.GONE);
            }
            holder.img_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogShow_bus_detail(mContext,position,item);
                }
            });

            //Toast.makeText(mContext," = "+item.getMessage_id().length(),Toast.LENGTH_SHORT).show();
            //System.out.println("TAG item.getMessage_id() = "+item.getMessage_id()+" Length = "+item.getMessage_id().length());
            /*
            Uri path_clock = Uri.parse("" + R.drawable.ic_access_time_grey_24dp);
            Uri path_tick  = Uri.parse("" + R.drawable.ic_check_black_24dp);

            holder.messageStatusIV.setTag(""+item.getImage_check());

            if (item.getImage_check().equals("static")){
                System.out.println("position = "+position+" item.getimagecheck = "+item.getImage_check());
                //do nothing
            }else if (item.getImage_check().equals("false")){

                positionArray.set(position,false);
                if (positionArray.get(position).equals(false)){
                    holder.messageStatusIV.setImageResource(Integer.parseInt(String.valueOf(path_clock)));
                }
                System.out.println("position = "+position+" item.getimagecheck = "+item.getImage_check());
            }else if (item.getImage_check().equals("true")){
                positionArray.set(position,true);
                if (positionArray.get(position).equals(true)){
                    holder.messageStatusIV.setImageResource(Integer.parseInt(String.valueOf(path_tick)));
                }
                System.out.println("position = "+position+" item.getimagecheck = "+item.getImage_check());
            }
            */

            holder.messageStatusIV.setText(""+item.getImage());

          /*  if (!item.getImage().equals(null) && !item.getImage().equals("")){

                //holder.messageStatusIV.setImageResource(Integer.parseInt(item.getImage()));
                //holder.messageStatusIV.setImageDrawable(Integer.parseInt(item.getImage()));
            }*/
        }
             //Toast.makeText(mContext,item.getSender(),Toast.LENGTH_SHORT).show();

            //messageStatusIV = (ImageView) v.findViewById(messageStatusIV);
        if (getItemViewType (position) == TYPE_LEFT) {
            //holder.chatIV = (CircleImageView) convertView.findViewById(R.id.chatIV);
            //Picasso.with(holder.chatIV.getContext()).load(""+item.getImage()).into(holder.chatIV);
        }

        //Toast.makeText(mContext,item.getSender(),Toast.LENGTH_SHORT).show();

        /*holder.tv_class_name = convertView.findViewById(R.id.tv_class_name);
        holder.tv_class_name.setText(item.getTeacher_name());

        holder.tv_duration  = convertView.findViewById(R.id.tv_duration);
        holder.tv_duration.setText(""+item.getTo_date_per()+" الی "+""+item.getFrom_date_per());*/

        return convertView;
    }

    public void alertDialogShow_bus_detail(final Context context, final int position, final Message_List item) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_prompt_chat_image,null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        alertDialogBuilder.setView(promptsView);

        final AlertDialog d = alertDialogBuilder.create();

        d.getWindow().getAttributes().windowAnimations =
                R.style.DialogAnimation;
        d.show();
        //d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView img_cross = promptsView.findViewById(R.id.img_cross);
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        ImageView img_back = promptsView.findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        TextView tv_sender = promptsView.findViewById(R.id.tv_sender);
        if (item.getName().equals("")){
            tv_sender.setText("You");
        }else {
            tv_sender.setText(""+item.getName());
        }
        TextView tv_date   = promptsView.findViewById(R.id.tv_date);

        String currentString = ""+item.getDate_time();
        String[] separated   = currentString.split(" ");

        // String mytime="Jan 17, 2012";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date myDate = null;
        try {
            myDate = dateFormat.parse(""+item.getDate_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM, hh:mm");
        String finalDate = timeFormat.format(myDate);

        tv_date.setText(""+finalDate);
        //tv_date.setText(""+item.getDate_time());

        ImageView    img_group_image = promptsView.findViewById(R.id.img_group_image);
        //Toast.makeText(mContext,item.getImage_check(),Toast.LENGTH_SHORT).show();
        Picasso.with(img_group_image.getContext()).load(""+item.getImage_check()).into( img_group_image);
    }
    private int dpToPx(int dp) {
        float density = mContext.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    private void prepareExoPlayerFromURL(Uri uri, final ViewHolder holder, final Message_List item, int position){

        TrackSelector trackSelector = new DefaultTrackSelector();

        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "FirmGround"), null);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);

        //====================

         ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {


            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.i(TAG,"onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.i(TAG,"onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.i(TAG,"onLoadingChanged");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.i(TAG,"onPlayerStateChanged: playWhenReady = "+String.valueOf(playWhenReady)
                        +" playbackState = "+playbackState);
                switch (playbackState){
                    case ExoPlayer.STATE_ENDED:
                        Log.i(TAG,"Playback ended!");
                        //Stop playback and return to start position
                        setPlayPause(false,holder,item);
                        exoPlayer.seekTo(0);
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.i(TAG,"ExoPlayer ready! pos: "+exoPlayer.getCurrentPosition()+" max: "+stringForTime((int)exoPlayer.getDuration()));
                        setProgress(holder,item);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.i(TAG,"Playback buffering!");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.i(TAG,"ExoPlayer idle!");
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.i(TAG,"onPlaybackError: "+error.getMessage());
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.i(TAG,"onPositionDiscontinuity");
            }
        };

        //========================


        exoPlayer.addListener(eventListener);
        exoPlayer.prepare(audioSource);
        initMediaControls(holder, item,position);
    }
    private void initMediaControls(ViewHolder holder, Message_List item,int position) {
        initPlayButton(holder,item,position);
        initSeekBar(holder);
        //initTxtTime();
    }
    private void initPlayButton(ViewHolder holder, Message_List item,int position) {
        setPlayPause(!isPlaying,holder,item);
       /* holder.tv_play_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"isPlaying"+!isPlaying);
                setPlayPause(!isPlaying);
            }
        });*/
    }
    private void setPlayPause(boolean play, ViewHolder holder, Message_List item){
        isPlaying = play;
        //Log.i(TAG," = here"+isPlaying);
        exoPlayer.setPlayWhenReady(play);
        if(!isPlaying){
            //Log.i(TAG," = here1"+isPlaying);
            holder.tv_play_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_24dp, 0, 0, 0);
        }else{
            setProgress(holder,item);
            //Log.i(TAG," = here2"+isPlaying);
            holder.tv_play_icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);
        }
    }
    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    private void setProgress(final ViewHolder holder, final Message_List item) {

        holder.mediacontroller_progress.setMax((int) exoPlayer.getDuration()/1000);

        Log.i(TAG,"holder.mediacontroller_progress = "+holder.mediacontroller_progress.getMax());
        //holder.time_current.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
        //holder.time_current.setText(stringForTime((int)exoPlayer.getDuration()));
        holder.player_end_time.setText(stringForTime((int)exoPlayer.getDuration()));
        holder.player_end_time.setVisibility(View.GONE);

        item.setSeekbar_check(true);


        if(handler == null)
            handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {

                    holder.mediacontroller_progress.setMax((int) exoPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    holder.mediacontroller_progress.setProgress(mCurrentPosition);
                    Log.i(TAG,"mCurrentPosition = "+mCurrentPosition);
                    holder.time_current.setText(stringForTime((int)exoPlayer.getCurrentPosition()));
                    holder.player_end_time.setText(stringForTime((int)exoPlayer.getDuration()));
                    handler.postDelayed(this, 1000);
                }
            }
        });
        //after message
        item.setSeekbar_check(false);
        holder.time_current.setText(stringForTime((int)exoPlayer.getDuration()));

        holder.mediacontroller_progress.setProgress(holder.mediacontroller_progress.getMax());

        holder.mediacontroller_progress.setProgress(0);



    }
    private void initSeekBar(ViewHolder holder) {

         holder.mediacontroller_progress.requestFocus();
         holder.mediacontroller_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        holder.mediacontroller_progress.setMax(0);
        holder.mediacontroller_progress.setMax((int) exoPlayer.getDuration()/1000);

    }

//============================================================================================

    public boolean downloadFile(final String path, ViewHolder holder, Message_List item, int position){
        Log.i("TAG","path = "+path);
    try
    {
      /*  URL url = new URL(path);

        URLConnection ucon = url.openConnection();
        ucon.setReadTimeout(5000);
        ucon.setConnectTimeout(10000);

        InputStream is = ucon.getInputStream();
        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);*/

        //File file = new File(mContext.getDir("", Context.MODE_PRIVATE) + "/"+item.getImage_check());

        File file = new File(Environment.getExternalStorageDirectory(), "MyDirName");

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        String[] separated   = path.split("/");
        String frist_id      = separated[0]; // this will contain "Fruit"
        String second_id     = separated[1];
        String third_id      = separated[2]; // this will contain "Fruit"
        String fourth_id     = separated[3];
        String fifth_id      = separated[4]; // this will contain "Fruit"
        String serverFileName= separated[5]; // this will contain "Fruit"

        Log.i("TAG","path = "+frist_id+" "+second_id+" "+third_id+" "+fourth_id+" "+fifth_id+" "+serverFileName);

        File chkfile = new File(file + "/"+serverFileName);
        if(chkfile.exists()){
            //Do something
            Log.i("TAG","file exist = "+chkfile);

            prepareExoPlayerFromURL(Uri.parse(""+chkfile),holder, item, position);

            //prepareExoPlayerFromURL(Uri.parse(""+chkfile),holder, item, position);
        }
        else{
            // Do something else.
            DownloadManager downloadmanager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(""+path);

            Log.i("TAG","file not exist = "+chkfile);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("My File");
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(false);
            request.setDestinationUri(Uri.parse("file://" + file + "/"+serverFileName));

            downloadmanager.enqueue(request);

            prepareExoPlayerFromURL(Uri.parse(""+chkfile),holder, item, position);

        }
    }
    catch (Exception e)
    {
        e.printStackTrace();
        return false;
    }

    return true;
}


}
