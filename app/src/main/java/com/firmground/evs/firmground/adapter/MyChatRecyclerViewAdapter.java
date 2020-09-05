package com.firmground.evs.firmground.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.ChatPageActivity;
import com.firmground.evs.firmground.circle_image.CircleImageView;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.fragment.GroupFragment.OnListFragmentInteractionListener;
import com.firmground.evs.firmground.fragment.ChatFragment_No_Match;
import com.firmground.evs.firmground.fragment.SingleChatFragment;
import com.firmground.evs.firmground.fragment.dummy.DummyContent.DummyItem;
import com.firmground.evs.firmground.helpingclasses.TimeAgo;
import com.firmground.evs.firmground.model.Groups_list;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.firmground.evs.firmground.fragment.GroupFragment.ground_Id;


/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<MyChatRecyclerViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<Groups_list> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private FragmentActivity fragmentActivity;

    SharedPreferences sharedPreferences;

    public static int single_screen_check = 0;


    private ArrayList<Groups_list> mOriginalValues; // Original Values
    //private ArrayList<Groups_list> mDisplayedValues;    // Values to be displayed

    public MyChatRecyclerViewAdapter(ArrayList<Groups_list> items, OnListFragmentInteractionListener listener, Activity activity) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
        this.mOriginalValues  = items;
        this.mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat, parent, false);

        fragmentActivity = (FragmentActivity) activity;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        //holder.mIdView.setText(mValues.get(position).id);
        if(holder.mItem.getParticipentStatus().equals("in")){
            holder.circleImageView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.circle_border_green));
        }else if(holder.mItem.getParticipentStatus().equals("out") || holder.mItem.getParticipentStatus().equals("partial") ){
            holder.circleImageView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.circle_border_red));
        }else if (holder.mItem.getParticipentStatus().equals("pending")){
            holder.circleImageView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.circle_border_orange));
        }else {
            holder.circleImageView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.circle_border_transparent));
        }

        final MyDatabase mydb = new MyDatabase(activity);

        if (mValues.get(position).getLast_message().length()>59){
            if (mValues.get(position).getLast_message_type().equals("image")){
                holder.tv_description.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_grey_24dp, 0, 0, 0);
                CharSequence foo = mValues.get(position).getLast_message();
                String bar = foo.toString();
                String desiredString = bar.substring(0,56);
                desiredString = desiredString+"...";

                Log.i("groupmsgid", "group_id = "+mValues.get(position).getGroupId()+" msg_id = "+mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId()));

                String msg_id_group_wise = mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId());
                String read_status = mydb.getdata_last_msg_id_read_status(msg_id_group_wise);

                if (read_status.equals("unread")){
                    holder.tv_description.setTypeface(holder.tv_description.getTypeface(), Typeface.BOLD);
                    holder.tv_description.setText(""+desiredString);
                }else {
                    holder.tv_description.setText(""+desiredString);
                }
            }else {
                CharSequence foo = mValues.get(position).getLast_message();
                String bar = foo.toString();
                String desiredString = bar.substring(0,56);

                String msg_id_group_wise = mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId());
                String read_status = mydb.getdata_last_msg_id_read_status(msg_id_group_wise);

                if (read_status.equals("unread")){
                    holder.tv_description.setTypeface(holder.tv_description.getTypeface(), Typeface.BOLD);
                    desiredString = desiredString+"...";
                    holder.tv_description.setText(""+desiredString);
                }else {
                    desiredString = desiredString+"...";
                    holder.tv_description.setText(""+desiredString);
                }
            }
        }
        else {
            if (mValues.get(position).getLast_message().equals(""))
            {
                if (mValues.get(position).getLast_message_type().equals("image")){
                    holder.tv_description.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_grey_24dp, 0, 0, 0);

                    String msg_id_group_wise = mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId());
                    String read_status = mydb.getdata_last_msg_id_read_status(msg_id_group_wise);

                    if (read_status.equals("unread")){
                        holder.tv_description.setTypeface(holder.tv_description.getTypeface(), Typeface.BOLD);
                        holder.tv_description.setText("Photo");
                    }else {
                        holder.tv_description.setText("Photo");
                    }

                }else  {
                    holder.tv_description.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_voice_black_24dp, 0, 0, 0);
                    String msg_id_group_wise = mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId());
                    String read_status = mydb.getdata_last_msg_id_read_status(msg_id_group_wise);

                    if (read_status.equals("unread")){
                        holder.tv_description.setTypeface(holder.tv_description.getTypeface(), Typeface.BOLD);
                        holder.tv_description.setText("Audio");
                    }else {
                        holder.tv_description.setText("Audio");
                    }
                }
            }
            else{
                if (mValues.get(position).getLast_message_type().equals("image")){
                    holder.tv_description.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_grey_24dp, 0, 0, 0);

                    String msg_id_group_wise = mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId());
                    String read_status       = mydb.getdata_last_msg_id_read_status(msg_id_group_wise);

                    if (read_status.equals("unread")){

                        holder.tv_description.setText(""+mValues.get(position).getLast_message());
                    }else {
                        holder.tv_description.setTypeface(holder.tv_description.getTypeface(), Typeface.BOLD);
                        holder.tv_description.setText(""+mValues.get(position).getLast_message());
                    }

                }
                else {
                        String msg_id_group_wise = mydb.getdata_last_msg_id_group_wise(mValues.get(position).getGroupId());
                        String read_status = mydb.getdata_last_msg_id_read_status(msg_id_group_wise);

                    if (read_status.equals("unread")){
                        Log.i("checking","read_status = "+read_status);

                        holder.tv_description.setTypeface(holder.tv_description.getTypeface(), Typeface.BOLD);
                        holder.tv_description.setText(""+mValues.get(position).getLast_message());
                    }else {


                        holder.tv_description.setText(""+mValues.get(position).getLast_message());
                    }

                }
            }
        }
        //DateUtils.getRelativeTimeSpanString();
        //TimeAgo.getTimeAgo(Long.parseLong(mValues.get(position).getDateAdded()));
        holder.date_nav.setText(""+TimeAgo.getTimeAgo(Long.parseLong(mValues.get(position).getDateAdded())));

        Picasso.with( holder.circleImageView.getContext()).load(""+mValues.get(position).getImage()).into( holder.circleImageView);

        holder.tv_title.setText(mValues.get(position).getGroupName());
        final int temp_pos = position;
        holder.row_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(holder.row_chat.getContext(),"AA BB "+temp_pos,Toast.LENGTH_SHORT).show();
            }
        });

        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(holder.row_chat.getContext(),"AA BB ", Toast.LENGTH_SHORT).show();
                alertDialogShow_bus_detail(activity,position,holder.mItem);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    // Toast.makeText(holder.row_chat.getContext(),"AA BB "+temp_pos,Toast.LENGTH_SHORT).show();

                    /*if (wifi_checker==1){}else{}*/

                    if (!mValues.get(position).getGameId().equals("") && mValues.get(position).getGroupType().equals("one2many")){

                        single_screen_check = 1;

                        ground_Id = mValues.get(position).getGroupId();

                        Bundle bundle = new Bundle();
                        bundle.putString("group_Id"   ,mValues.get(position).getGroupId());
                        bundle.putString("game_Id"    ,mValues.get(position).getGameId());
                        bundle.putString("memberRole" ,mValues.get(position).getMemberRole());
                        bundle.putString("group_Image",mValues.get(position).getImage());
                        bundle.putString("memberRole" ,mValues.get(position).getMemberRole());
                        bundle.putString("group_name" ,mValues.get(position).getGroupName());

                        SavePreferences("group_Image" ,mValues.get(position).getImage());

                        Log.i("TAG", "group_Image : " +sharedPreferences.getString("group_Image",""));

                           /*
                           Toast.makeText(holder.row_chat.getContext(),"group_Id = "+mValues.get(position).getGroupId(), Toast.LENGTH_SHORT).show();
                           Toast.makeText(holder.row_chat.getContext(),"game_Id = "+mValues.get(position).getGameId(), Toast.LENGTH_SHORT).show();
                           Toast.makeText(holder.row_chat.getContext(),"game_Image ="+mValues.get(position).getImage(), Toast.LENGTH_SHORT).show();
                           */

                        Intent intent = new Intent(activity,ChatPageActivity.class);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                    else if (mValues.get(position).getGameId().equals("") && mValues.get(position).getGroupType().equals("one2many")) {
                        //calling the fragment here

                        single_screen_check = 2;

                        Bundle bundle = new Bundle();
                        bundle.putString("group_Id",mValues.get(position).getGroupId());
                        bundle.putString("memberRole",mValues.get(position).getMemberRole());
                        bundle.putString("group_name",mValues.get(position).getGroupName());

                        SavePreferences("group_Image" ,mValues.get(position).getImage());

                        ImageView search_img =activity.findViewById(R.id.search_img);
                        search_img.setVisibility(View.GONE);

                        ground_Id = mValues.get(position).getGroupId();

                        ChatFragment_No_Match tab1= new ChatFragment_No_Match();
                        tab1.setArguments(bundle);
                        fragmentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, tab1)
                                .addToBackStack(null)   // this will be it to the back stack
                                .commit();
                    }
                    else {

                        single_screen_check = 3;

                        Bundle bundle = new Bundle();
                        bundle.putString("contactname",mValues.get(position).getGroupName());

                        ground_Id = mValues.get(position).getGroupId();


                        SingleChatFragment tab1= new SingleChatFragment();
                        tab1.setArguments(bundle);
                        fragmentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, tab1)
                                .addToBackStack(null)   // this will be it to the back stack
                                .commit();

                    }
                   // mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void alertDialogShow_bus_detail(final Context context, final int position, final Groups_list item) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_prompt_group_image,null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        alertDialogBuilder.setView(promptsView);

        final AlertDialog d = alertDialogBuilder.show();
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView img_group_image = promptsView.findViewById(R.id.img_group_image);
        Picasso.with( img_group_image.getContext()).load(""+mValues.get(position).getImage()).into( img_group_image);
    }

    private void SavePreferences(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mValues = (ArrayList<Groups_list>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Groups_list> FilteredArrList = new ArrayList<Groups_list>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Groups_list>(mValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getGroupName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Groups_list(mOriginalValues.get(i).getMembershipId()  , mOriginalValues.get(i).getUserID(),
                                                                mOriginalValues.get(i).getGroupId()       , mOriginalValues.get(i).getGroupName(),
                                                                mOriginalValues.get(i).getGroupName(),      mOriginalValues.get(i).getMemberRole(),
                                                                mOriginalValues.get(i).getDateAdded(),      mOriginalValues.get(i).getImage()         ,
                                                                mOriginalValues.get(i).getFname(),
                                                                mOriginalValues.get(i).getLname()         , mOriginalValues.get(i).getPhone(),
                                                                mOriginalValues.get(i).getGameId()        , mOriginalValues.get(i).getParticipentStatus(),
                                                                mOriginalValues.get(i).getLast_message()  , mOriginalValues.get(i).getLast_message_type(),
                                                                mOriginalValues.get(i).getLast_message_id())
                            );
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView tv_title;
        public TextView tv_description,date_nav;
        public Groups_list mItem;
        public CircleImageView circleImageView;
        public ConstraintLayout row_chat;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            row_chat = (ConstraintLayout) view.findViewById(R.id.row_chat);
            circleImageView = (CircleImageView) view.findViewById(R.id.circleImageView);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_description = (TextView) view.findViewById(R.id.tv_description);
            date_nav = (TextView) view.findViewById(R.id.date_nav);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_title.getText() + "'";
        }
    }
}
