package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;
import com.example.lego.interfaces.ItemClickListener;

/**
 * Created by VietVan on 06/06/2018.
 */

public class ProductWaitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView product_image, product_delete;
    public TextView product_name, product_price;

    ItemClickListener itemClickListener;

    public ProductWaitViewHolder(View itemView) {
        super(itemView);

        product_image = itemView.findViewById(R.id.product_image);
        product_name = itemView.findViewById(R.id.product_name);
        product_price = itemView.findViewById(R.id.product_price);
        product_delete = itemView.findViewById(R.id.iv_delete);

        itemView.setOnClickListener(this);

    }

    public ImageView getProduct_image() {
        return product_image;
    }

    public TextView getProduct_name() {
        return product_name;
    }

    public ImageView getProduct_delete() {
        return product_delete;
    }

    public void setProduct_delete(ImageView product_delete) {
        this.product_delete = product_delete;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
