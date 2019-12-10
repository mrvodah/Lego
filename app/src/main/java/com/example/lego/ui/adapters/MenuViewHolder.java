package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.R;

/**
 * Created by VietVan on 29/05/2018.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static ImageView image;
    public static TextView name;

    ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.menu_image);
        name = itemView.findViewById(R.id.menu_name);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
