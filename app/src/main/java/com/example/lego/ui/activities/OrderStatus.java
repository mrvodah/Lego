package com.example.lego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;
import com.example.lego.utils.Util;
import com.example.lego.models.Request;
import com.example.lego.ui.adapters.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.widget.FButton;

public class OrderStatus extends AppCompatActivity {

    @BindView(R.id.rv_hire)
    RecyclerView rvOrder;

    FirebaseDatabase database;
    DatabaseReference requests;

    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        ButterKnife.bind(this);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        layoutManager = new LinearLayoutManager(this);
        rvOrder.setLayoutManager(layoutManager);
        rvOrder.setHasFixedSize(true);

        loadOrders();
    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout_staff,
                OrderViewHolder.class,
                requests.orderByChild("status").equalTo("0")
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.getTvOrderId().setText("#" + adapter.getRef(position).getKey());
                viewHolder.getTvOrderStatus().setText(convertCodeToStatus(model.getStatus()));
                viewHolder.getTvOrderPhone().setText(model.getPhone());
                viewHolder.getTvOrderAddress().setText(model.getAddress());

                viewHolder.getFbAccept().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Request item = adapter.getItem(position);
                        item.setStatus("1");
                        requests.child(adapter.getRef(position).getKey()).setValue(item);
                    }
                });

                viewHolder.getFbRemove().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requests.child(adapter.getRef(position).getKey()).removeValue();
                    }
                });

                viewHolder.getLnContent().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.currentRequest = model;
                        Intent intent = new Intent(OrderStatus.this, OrderDetail.class);
                        startActivity(intent);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rvOrder.setAdapter(adapter);
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Waiting";

        return "Accept";
    }
}
