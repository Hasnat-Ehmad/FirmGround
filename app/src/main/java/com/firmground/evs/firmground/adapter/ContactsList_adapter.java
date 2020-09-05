package com.firmground.evs.firmground.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.database.MyDatabase;
import com.firmground.evs.firmground.fragment.ChatFragment_No_Match;
import com.firmground.evs.firmground.fragment.ContactListFragment;
import com.firmground.evs.firmground.fragment.SingleChatFragment;
import com.firmground.evs.firmground.model.ContactsList;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import static com.firmground.evs.firmground.activity.MainActivity.activity;
import static com.firmground.evs.firmground.fragment.GroupFragment.ground_Id;
import static com.firmground.evs.firmground.fragment.ContactListFragment.titlebtncheck;

import java.util.ArrayList;
import java.util.List;


public class ContactsList_adapter extends BaseAdapter implements Filterable {
    private Activity mContext;
    static private List<ContactsList> contactsLists = null;
    private LayoutInflater inflater=null;/**/
    private ArrayList<ContactsList> arraylist;
    private SparseBooleanArray mSelectedItemsIds;

    private ArrayList<ContactsList> mOriginalValues;

    public static int button_check=0;

    SharedPreferences sharedPreferences;

    private FragmentActivity fragmentActivity;

    int selected_counter=0;

    AdapterInterface listener;

    public ContactsList_adapter(Activity context, ArrayList<ContactsList> contactsLists1,AdapterInterface listener) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.contactsLists = contactsLists1;
        //inflater = LayoutInflater.from(this.mContext);
        this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arraylist = new ArrayList<ContactsList>();
        this.arraylist.addAll(contactsLists1);
        mSelectedItemsIds = new SparseBooleanArray();
        this.mOriginalValues  = contactsLists1;

        this.listener = listener;

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                contactsLists = (ArrayList<ContactsList>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<ContactsList> FilteredArrList = new ArrayList<ContactsList>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<ContactsList>(contactsLists); // saves the original data in mOriginalValues
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
                        String data = mOriginalValues.get(i).getmContactName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new ContactsList(mOriginalValues.get(i).getmContactPicture(),
                                                                 mOriginalValues.get(i).getmContactId(),
                                                                 mOriginalValues.get(i).getmContactName(),
                                                                 mOriginalValues.get(i).getmContactNumber(),
                                                                 mOriginalValues.get(i).getmContactType(),
                                                                 mOriginalValues.get(i).getmContactExtra(),
                                                                 mOriginalValues.get(i).isSelected(),
                                                                 mOriginalValues.get(i).getmContactBitmap(),
                                                                 mOriginalValues.get(i).getmContactAge(),
                                                                 mOriginalValues.get(i).getmContactGender(),
                                                                 mOriginalValues.get(i).getmContactEmail()));
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

    private class ViewHolder {
        TextView tv_name,tv_number;
        ImageView img;
        CheckBox checkBox;
        //DownloadImageTask downloadImageTask;

        LinearLayout linearLayout;
    }

    @Override
    public int getCount() {
        return contactsLists.size();
    }

    @Override
    public ContactsList getItem(int position) {
        return contactsLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        System.out.println("CreateGroupList "+position);
            final ViewHolder holder;
                  final ContactsList contact = contactsLists.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView         = inflater.inflate(R.layout.list_view_contact,null);
                holder.tv_name      = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_number    = (TextView) convertView.findViewById(R.id.tv_number);
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.contact_view);

                /*
                Bitmap b2 = contact.getmContactBitmap();
                Drawable drawable2 = new BitmapDrawable(b2);
                holder.img.setBackground(drawable2);
                */
                /*
                holder.img.setImageBitmap(null);
                Picasso.with(holder.img.getContext()).load(""+contact.getmContactBitmap()).into(holder.img);
                holder.img.setImageBitmap(contact.getmContactBitmap());
                */

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
            fragmentActivity = (FragmentActivity) mContext;

            //Single chat here call new screen here
            if(titlebtncheck==1){
                //Toast.makeText(mContext,"value adapter = "+sharedPreferences.getInt("titlebtncheck",0), Toast.LENGTH_SHORT).show();
                holder.checkBox = convertView.findViewById(R.id.checkbox);
                holder.checkBox.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        ContactsList _contact = (ContactsList) cb.getTag();
                        if(cb.isChecked()) {
                            cb.setChecked(true);
                            selected_counter++;
                            //Toast.makeText(mContext,"selected_counter = "+selected_counter, Toast.LENGTH_SHORT).show();
                        }else {
                            cb.setChecked(false);
                            selected_counter--;
                            //Toast.makeText(mContext,"selected_counter = "+selected_counter, Toast.LENGTH_SHORT).show();
                        }
                        _contact.setSelected(cb.isChecked());

                        if(listener != null)
                            listener.onClick(selected_counter);

                    }
                });

                for (int j=0;j<contactsLists.size();j++){
                    if(!contactsLists.get(j).isSelected()){
                        holder.checkBox.setChecked(false);
                    }else {
                        holder.checkBox.setChecked(true);
                    }
                }

                holder.tv_name.setText(contact.getmContactName());
                holder.tv_number.setText(contact.getmContactNumber());
                holder.checkBox.setChecked(contact.isSelected());
                holder.checkBox.setTag(contact);

                if(contact.isSelected()){
                    holder.checkBox.setChecked(true);
                }
                else{
                    holder.checkBox.setChecked(false);
                }

                holder.img = (ImageView) convertView.findViewById(R.id.item_image);

                //holder.img.setImageBitmap(null);
                //Picasso.with(holder.img.getContext()).load(""+contact.getmContactBitmap()).into(holder.img);
                //holder.img.setImageBitmap(contact.getmContactBitmap());

                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.checkBox.performClick();
                    }
                });

            }
            else {
                //Toast.makeText(mContext,"value adapter = "+sharedPreferences.getInt("titlebtncheck",0), Toast.LENGTH_SHORT).show();

                holder.checkBox = convertView.findViewById(R.id.checkbox);
                holder.checkBox.setVisibility(View.INVISIBLE);
                notifyDataSetChanged();
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        ContactsList _contact = (ContactsList) cb.getTag();
                        if(cb.isChecked()) {
                            cb.setChecked(true);
                        }else {
                            cb.setChecked(false);
                        }
                        _contact.setSelected(cb.isChecked());

                    }
                });

                for (int j=0;j<contactsLists.size();j++){
                    if(!contactsLists.get(j).isSelected()){
                        holder.checkBox.setChecked(false);
                    }else {
                        holder.checkBox.setChecked(true);
                    }
                }

                holder.tv_name.setText(contact.getmContactName());
                holder.tv_number.setText(contact.getmContactNumber());
                holder.checkBox.setChecked(contact.isSelected());
                holder.checkBox.setTag(contact);

                if(contact.isSelected()){
                    holder.checkBox.setChecked(true);
                }
                else{
                    holder.checkBox.setChecked(false);
                }

                holder.img = (ImageView) convertView.findViewById(R.id.item_image);

                //holder.img.setImageBitmap(null);
                //Picasso.with(holder.img.getContext()).load(""+contact.getmContactBitmap()).into(holder.img);
                //holder.img.setImageBitmap(contact.getmContactBitmap());


                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final MyDatabase mydb = new MyDatabase(mContext);

                        /*
                        String contact_user_id = mydb.getdata_contact_user_id(""+contact.getmContactNumber());
                        Log.d("TAG","contact_user_id = "+contact_user_id);
                        */


                        final String url = mContext.getResources().getString(R.string.url) + "checkUserId?" +
                                "userID="+sharedPreferences.getString("userID","")+
                                "&contactname="+contact.getmContactName()+
                                "&contactnumber="+contact.getmContactNumber()
                                ;
                        final String params = "";
                        final WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                            @Override
                            public String TaskCompletionResult(String result) throws JSONException {

                                JSONObject jsonObject = new JSONObject(result);

                                if(jsonObject.getString("respCode").equals("200")) {

                                    JSONObject responseData = jsonObject.getJSONObject("responseData");

                                    String message = jsonObject.getString("message");
                                    //Toast.makeText(mContext,"message = "+message,Toast.LENGTH_SHORT).show();

                                    if (responseData.length()>1){

                                        String contact_user_id= responseData.getString("id");
                                        String status         = responseData.getString("status");
                                        String phone          = responseData.getString("phone");

                                        ground_Id = mydb.getdata_contact_group_id(contact_user_id);

                                        Bundle bundle = new Bundle();
                                        bundle.putString("contactname"   ,contact.getmContactName());
                                        bundle.putString("contactnumber"    ,contact.getmContactNumber());
                                        // Toast.makeText(mContext,"contactname = "+contact.getmContactName(), Toast.LENGTH_SHORT).show();
                                        // Toast.makeText(mContext,"contactnumber = "+contact.getmContactNumber(), Toast.LENGTH_SHORT).show();

                                        SingleChatFragment tab1= new SingleChatFragment();
                                        tab1.setArguments(bundle);
                                        fragmentActivity.getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.frame_layout, tab1)
                                                .addToBackStack(null)   // this will be added it to the back stack
                                                .commit();

                                        //Toast.makeText(mContext,"last_message = "+last_message,Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    //call dialog box here

                                    TextView textView_title = new TextView(mContext);
                                    textView_title.setText(mContext.getResources().getString(R.string.str_title_message));
                                    textView_title.setGravity(Gravity.START);
                                    textView_title.setPadding(20,10,20,10);
                                    textView_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                                    textView_title.setCompoundDrawablePadding(10);

                                    //textView_title.setCompoundDrawables(null,null,mContext.getResources().getDrawable(R.drawable.ic_warning_colored_24dp),null);
                                    textView_title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_warning_colored_24dp, 0);
                                    new AlertDialog.Builder(mContext)
                                            .setCustomTitle(textView_title)
                                            .setMessage(mContext.getResources().getString(R.string.str_send_message))
                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                            // The dialog is automatically dismissed when a dialog button is clicked.
                                            .setPositiveButton(mContext.getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+contact.getmContactNumber()));
                                                    smsIntent.putExtra("sms_body", "http://evsoft.pk/FirmGround/FirmGround.apk  \n Download this from given link");
                                                    mContext.startActivity(smsIntent);

                                                }
                                            })
                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                            .setNegativeButton(mContext.getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            //.setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                                return result;
                            }
                        });
                        webRequestCall.execute(url,"GET",params);


                      /*  if (!contact_user_id.equals("")){
                                 ground_Id = mydb.getdata_contact_group_id(contact_user_id);

                            //Toast.makeText(mContext,"ground_Id = "+ground_Id, Toast.LENGTH_SHORT).show();

                            Bundle bundle = new Bundle();
                            bundle.putString("contactname"   ,contact.getmContactName());
                            bundle.putString("contactnumber"    ,contact.getmContactNumber());
                            // Toast.makeText(mContext,"contactname = "+contact.getmContactName(), Toast.LENGTH_SHORT).show();
                            // Toast.makeText(mContext,"contactnumber = "+contact.getmContactNumber(), Toast.LENGTH_SHORT).show();

                            SingleChatFragment tab1= new SingleChatFragment();
                            tab1.setArguments(bundle);
                            fragmentActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, tab1)
                                    .addToBackStack(null)   // this will be added it to the back stack
                                    .commit();

                        }
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putString("contactname"   ,contact.getmContactName());
                            bundle.putString("contactnumber"    ,contact.getmContactNumber());
                            // Toast.makeText(mContext,"contactname = "+contact.getmContactName(), Toast.LENGTH_SHORT).show();
                            // Toast.makeText(mContext,"contactnumber = "+contact.getmContactNumber(), Toast.LENGTH_SHORT).show();

                            SingleChatFragment tab1= new SingleChatFragment();
                            tab1.setArguments(bundle);
                            FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.frame_layout, tab1);
                            ft.addToBackStack(null);
                            ft.commitAllowingStateLoss();

                            fragmentActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, tab1)
                                    .addToBackStack(null)   // this will be added it to the back stack
                                    .commit();
                        }*/
                    }
                });

            }

            return convertView;
        }

    public interface AdapterInterface
    {
        void onClick(int value);
    }

    private Bitmap queryContactImage(int imageDataRow) {
        Cursor c = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[] {
                ContactsContract.CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[] {
                Integer.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }

}

/*
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(contact.getmContactNumber()));
        Cursor cursor = mContext.getContentResolver().query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID },
                null, null, null);

        String contactId = "";

        if (cursor.moveToFirst()) {
            do {
                contactId = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.PhoneLookup._ID));
            } while (cursor.moveToNext());
        }

        //Toast.makeText(mContext, "contactId in adapter = "+contactId, Toast.LENGTH_LONG).show();

        Cursor c = mContext.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_ID
                }, ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[] { contactId }, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String number = c.getString(0);
                int type = c.getInt(1);
                String name = c.getString(2);
                int photoId = c.getInt(3);
                Bitmap bitmap = queryContactImage(photoId);

                if (bitmap == null){
                    Drawable[] myImageList = new Drawable[]{mContext.getApplicationContext().getResources().getDrawable(R.drawable.user)};
                    Drawable d = myImageList[0];
                    holder.img.setImageDrawable(d);
                    // holder.img.setVisibility(View.GONE);

                }else {
                    holder.img.setImageBitmap(null);
                    Picasso.with(holder.img.getContext()).load(""+contact.getmContactBitmap()).into(holder.img);
                    holder.img.setImageBitmap(bitmap);
                }
            }
            c.close();
        }*/
