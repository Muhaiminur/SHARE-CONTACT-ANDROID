package com.sharecontact.conactshare.ADAPTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sharecontact.conactshare.DATABASE.CONTACT;
import com.sharecontact.conactshare.R;

import java.util.ArrayList;
import java.util.List;

public class CONTACT_LIST_ADAPTER extends RecyclerView.Adapter<CONTACT_LIST_ADAPTER.MyViewHolder> {
    Context context;
    List<CONTACT> product_list;


    public CONTACT_LIST_ADAPTER(List<CONTACT> notification, Context c) {
        product_list = notification;
        context = c;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.contact_name);
            number = view.findViewById(R.id.contact_number);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CONTACT bodyResponse = product_list.get(position);
        holder.name.setText(bodyResponse.getName());
        holder.number.setText(bodyResponse.getNummber());
    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    public void filterList(ArrayList<CONTACT> filterdNames) {
        this.product_list = filterdNames;
        notifyDataSetChanged();
    }
}