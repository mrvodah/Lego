package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.R;

/**
 * Created by VietVan on 06/06/2018.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView product_image, fav_image, cart_image;
    public TextView product_name, product_price;

    ItemClickListener itemClickListener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        product_image = itemView.findViewById(R.id.product_image);
        product_name = itemView.findViewById(R.id.product_name);
        product_price = itemView.findViewById(R.id.product_price);
        fav_image = itemView.findViewById(R.id.fav);
        cart_image = itemView.findViewById(R.id.iv_cart);

        itemView.setOnClickListener(this);

    }

    public ImageView getFood_image() {
        return product_image;
    }

    public void setFood_image(ImageView product_image) {
        this.product_image = product_image;
    }

    public ImageView getFav_image() {
        return fav_image;
    }

    public void setFav_image(ImageView fav_image) {
        this.fav_image = fav_image;
    }

    public ImageView getCart_image() {
        return cart_image;
    }

    public void setCart_image(ImageView cart_image) {
        this.cart_image = cart_image;
    }

    public TextView getFood_name() {
        return product_name;
    }

    public void setFood_name(TextView product_name) {
        this.product_name = product_name;
    }

    public TextView getFood_price() {
        return product_price;
    }

    public void setFood_price(TextView product_price) {
        this.product_price = product_price;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
