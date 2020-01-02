package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;

import info.hoang8f.widget.FButton;

/**
 * Created by VietVan on 13/06/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView tvOrderId, tvOrderStatus, tvOrderPhone, tvOrderAddress, tvOrderDate;
    public LinearLayout lnContainer;
    public FButton fbAccept, fbRemove;

    public OrderViewHolder(View itemView) {
        super(itemView);

        tvOrderId = itemView.findViewById(R.id.order_id);
        tvOrderStatus = itemView.findViewById(R.id.order_status);
        tvOrderPhone = itemView.findViewById(R.id.order_phone);
        tvOrderAddress = itemView.findViewById(R.id.order_address);
        tvOrderDate = itemView.findViewById(R.id.order_date);
        lnContainer = itemView.findViewById(R.id.ln_container);

        fbAccept= itemView.findViewById(R.id.btnAccept);
        fbRemove= itemView.findViewById(R.id.btnRemove);

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

    public TextView getTvOrderDate() {
        return tvOrderDate;
    }

    public void setTvOrderDate(TextView tvOrderDate) {
        this.tvOrderDate = tvOrderDate;
    }

    public LinearLayout getLnContainer() {
        return lnContainer;
    }

    public void setLnContainer(LinearLayout lnContainer) {
        this.lnContainer = lnContainer;
    }

    public FButton getFbAccept() {
        return fbAccept;
    }

    public void setFbAccept(FButton fbAccept) {
        this.fbAccept = fbAccept;
    }

    public FButton getFbRemove() {
        return fbRemove;
    }

    public void setFbRemove(FButton fbRemove) {
        this.fbRemove = fbRemove;
    }
}
