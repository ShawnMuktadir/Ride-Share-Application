package com.example.asifsabir.rideshareapp;

/**
 * Created by asifsabir on 12/19/17.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<DriverReg> MainImageUploadInfoList;

    public RecyclerViewAdapter(Context context, List<DriverReg> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DriverReg driverDetails = MainImageUploadInfoList.get(position);

        holder.tvName.setText(driverDetails.fullName);

        holder.tvMobile.setText(driverDetails.mobile);

        holder.tvNid.setText(driverDetails.nid);

        holder.tvReg.setText(driverDetails.regNo);
        holder.rtDriver.setRating(Float.parseFloat(driverDetails.rating));
        holder.tvType.setText(driverDetails.vehicleType);


    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvMobile;
        public TextView tvNid;
        public TextView tvReg;
        public TextView tvType;
        public RatingBar rtDriver;

        public ViewHolder(View itemView) {

            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvMobile = (TextView) itemView.findViewById(R.id.tv_mobile);
            tvNid = (TextView) itemView.findViewById(R.id.tv_nid);
            tvReg = (TextView) itemView.findViewById(R.id.tv_reg);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            rtDriver = (RatingBar) itemView.findViewById(R.id.rtbar_driver);


        }
    }
}