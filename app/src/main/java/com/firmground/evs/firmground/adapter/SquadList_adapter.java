package com.firmground.evs.firmground.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.model.Squad_list;
import com.firmground.evs.firmground.webservice.TaskDelegate;
import com.firmground.evs.firmground.webservice.WebRequestCall;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SquadList_adapter extends BaseAdapter {
    private Activity mContext;

    static private List<Squad_list> squad_lists = null;
    private LayoutInflater inflater=null;/**/

    public SquadList_adapter(Activity context, List<Squad_list> squadLists) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.squad_lists = squadLists;
        //inflater = LayoutInflater.from(this.mContext);
        this.inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    private class ViewHolder {
        TextView name,tv_set_admin,tv_role;
        ImageView img_cross,item_image;
        CheckBox checkBox;
        //DownloadImageTask downloadImageTask;

        LinearLayout linearLayout;
    }

    @Override
    public int getCount() {
        return squad_lists.size();
    }

    @Override
    public Squad_list getItem(int position) {
        return squad_lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        System.out.println("CreateGroupList "+position);
        System.out.println("SquadList_count "+getCount());

        final SquadList_adapter.ViewHolder holder;
        final Squad_list item =  getItem(position);

            if (convertView == null) {

                holder = new SquadList_adapter.ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_members, parent, false);
                convertView.setTag(holder);

            }else{
                    holder = (SquadList_adapter.ViewHolder) convertView.getTag();
                }

                System.out.println("SquadList_name "+item.getName());
                System.out.println("SquadList_getMemberRole"+item.getMemberRole());

                holder.name        =convertView.findViewById(R.id.item_title);
                holder.tv_set_admin=convertView.findViewById(R.id.tv_set_admin);
                holder.tv_role     =convertView.findViewById(R.id.tv_role);
                holder.img_cross   =convertView.findViewById(R.id.img_cross);
                holder.item_image   =convertView.findViewById(R.id.item_image);
                Picasso.with(holder.item_image.getContext()).load(""+item.getScreen()).into(holder.item_image);



                holder.img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,""+item.getMembershipId(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext,"position = "+position,Toast.LENGTH_SHORT).show();


                String url    =  mContext.getResources().getString(R.string.url)+"deleteMembership";
                //Toast.makeText(getActivity(),"users="+prams.toString(),Toast.LENGTH_LONG).show();

                final String params = "membershipId="+item.getMembershipId();

                WebRequestCall webRequestCall = new WebRequestCall(new TaskDelegate() {
                    @Override
                    public String TaskCompletionResult(String result) {

                        try {

                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.getString("respCode").equals("200")) {
                                //Toast.makeText(mContext,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();

                            squad_lists.remove(position);
                            notifyDataSetChanged();

                            }
                            else{
                                Toast.makeText(mContext,jsonObject.getString("status_alert") , Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return result;
                    }
                });
                 webRequestCall.execute(url, "POST", params);
            }
        });

                holder.tv_set_admin.setVisibility(View.INVISIBLE);

                holder.tv_role.setText(""+item.getMemberRole());
                holder.name.setText(""+item.getName());

        if (item.getMemberRole().equals("SuperAdmin")){

            holder.img_cross.setVisibility(View.INVISIBLE);

        }else {

            if (item.getScreen().equals("2")){
                holder.img_cross.setVisibility(View.INVISIBLE);
            }else {
                holder.img_cross.setVisibility(View.VISIBLE);
            }

        }

            return convertView;
        }
}
