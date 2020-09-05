package com.firmground.evs.firmground.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.model.ContactsList_updateGroup;

import java.util.ArrayList;
import java.util.List;


public class ContactsListupdateGroup_adapter extends BaseAdapter {
    private Activity mContext;
    static private List<ContactsList_updateGroup> contactsList_updateGroups = null;
    private LayoutInflater inflater=null;/**/
    private ArrayList<ContactsList_updateGroup> arraylist;
    private SparseBooleanArray mSelectedItemsIds;

    public static int button_check=0;

    public ContactsListupdateGroup_adapter(Activity context, List<ContactsList_updateGroup> contactsListUpdateGroups) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.contactsList_updateGroups = contactsListUpdateGroups;
        //inflater = LayoutInflater.from(this.mContext);
        this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arraylist = new ArrayList<ContactsList_updateGroup>();
        this.arraylist.addAll(contactsListUpdateGroups);
        mSelectedItemsIds = new SparseBooleanArray();

    }

    private class ViewHolder {
        TextView name;
        ImageView img;
        CheckBox checkBox;
        //DownloadImageTask downloadImageTask;

        LinearLayout linearLayout;
    }

    @Override
    public int getCount() {
        return contactsList_updateGroups.size();
    }

    @Override
    public ContactsList_updateGroup getItem(int position) {
        return contactsList_updateGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("CreateGroupList "+position);
            final ViewHolder holder;
        ContactsList_updateGroup contact = contactsList_updateGroups.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_contact, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.contact_view);


                holder.checkBox = convertView.findViewById(R.id.checkbox);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        ContactsList_updateGroup _contact = (ContactsList_updateGroup) cb.getTag();
                        if(cb.isChecked()) {
                            cb.setChecked(true);
                        }else {
                            cb.setChecked(false);
                        }
                        _contact.setSelected(cb.isChecked());

                    }
                });

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            for (int j=0;j<contactsList_updateGroups.size();j++){
                if(!contactsList_updateGroups.get(j).isSelected()){
                    holder.checkBox.setChecked(false);
                }else {
                    holder.checkBox.setChecked(true);
                }
            }

            holder.img = (ImageView) convertView.findViewById(R.id.item_image);

        //  Picasso.with(holder.img.getContext()).load(""+contact.getmContactBitmap()).into(holder.img);
            holder.img.setImageBitmap(contact.getmContactBitmap());

            holder.name.setText(contact.getmContactName()+"\n"+contact.getmContactNumber());
            holder.checkBox.setChecked(contact.isSelected());
            holder.checkBox.setTag(contact);

        if(contact.isSelected()){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }
            return convertView;
        }

}
