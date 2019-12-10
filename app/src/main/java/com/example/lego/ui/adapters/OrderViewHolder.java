package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;

/**
 * Created by VietVan on 13/06/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView tvOrderId, tvOrderStatus, tvOrderPhone, tvOrderAddress;

    public OrderViewHolder(View itemView) {
        super(itemView);

        tvOrderId = itemView.findViewById(R.id.order_id);
        tvOrderStatus = itemView.findViewById(R.id.order_status);
        tvOrderPhone = itemView.findViewById(R.id.order_phone);
        tvOrderAddress = itemView.findViewById(R.id.order_address);

    }

    public TextView getTvOrderId() {
        return tvOrderId;
    }

    public void setTvOrderId(TextView tvOrderId) {
        this.tvOrderId = tvOrderId;
    }

    public TextView getTvOrderStatus() {
        return tvOrderStatus;
    }

    public void setTvOrderStatus(TextView tvOrderStatus) {
        this.tvOrderStatus = tvOrderStatus;
    }

    public TextView getTvOrderPhone() {
        return tvOrderPhone;
    }

    public void setTvOrderPhone(TextView tvOrderPhone) {
        this.tvOrderPhone = tvOrderPhone;
    }

    public TextView getTvOrderAddress() {
        return tvOrderAddress;
    }

    public void setTvOrderAddress(TextView tvOrderAddress) {
        this.tvOrderAddress = tvOrderAddress;
    }
}
