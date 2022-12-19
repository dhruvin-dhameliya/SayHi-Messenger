package com.group_project.chatapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_Contact extends RecyclerView.Adapter<Adapter_Contact.ViewHolder> {

    Activity activity;
    ArrayList<Model_Contact> arrayList;

    public Adapter_Contact(Activity activity, ArrayList<Model_Contact> arrayList) {
        this.activity= activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_number = itemView.findViewById(R.id.tv_number);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_Contact model_contact = arrayList.get(position);
        holder.tv_name.setText(model_contact.getContact_name());
        holder.tv_number.setText(model_contact.getContact_number());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
