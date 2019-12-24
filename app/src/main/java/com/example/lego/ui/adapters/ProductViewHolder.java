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
    public TextView product_name, product_price, product_own;

    ItemClickListener itemClickListener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        product_image = itemView.findViewById(R.id.product_image);
        product_name = itemView.findViewById(R.id.product_name);
        product_price = itemView.findViewById(R.id.product_price);
        product_own = itemView.findViewById(R.id.product_own);
        fav_image = itemView.findViewById(R.id.fav);
        cart_image = itemView.findViewById(R.id.iv_cart);

        itemView.setOnClickListener(this);

    }

    public ImageView getProduct_image() {
        return product_image;
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

    public TextView getProduct_name() {
        return product_name;
    }

    public TextView getProduct_price() {
        return product_price;
    }

    public void setProduct_price(TextView product_price) {
        this.product_price = product_price;
    }

    public TextView getProduct_own() {
        return product_own;
    }

    public void setProduct_own(TextView product_own) {
        this.product_own = product_own;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
