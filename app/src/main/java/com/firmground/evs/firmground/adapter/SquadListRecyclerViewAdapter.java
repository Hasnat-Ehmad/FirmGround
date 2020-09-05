package com.firmground.evs.firmground.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.ChatPageActivity;
import com.firmground.evs.firmground.fragment.GroupFragment.OnListFragmentInteractionListener;
import com.firmground.evs.firmground.fragment.dummy.DummyContent.DummyItem;
import com.firmground.evs.firmground.model.Squad_list;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SquadListRecyclerViewAdapter extends RecyclerView.Adapter<SquadListRecyclerViewAdapter.ViewHolder> {

    private final List<Squad_list> mValues;
    private final ChatPageActivity mListener;
    private final Activity activity;

    public SquadListRecyclerViewAdapter(List<Squad_list> items, ChatPageActivity listener, Activity activity) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_squad_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);

        holder.tv_squad_name.setText(mValues.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_squad_name;
        public Squad_list mItem;


        public ViewHolder(final View view) {
            super(view);
            mView = view;

            tv_squad_name = (TextView) view.findViewById(R.id.tv_squad_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_squad_name.getText() + "'";
        }
    }
}
