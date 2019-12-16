package com.example.lego.ui.activities;

import android.os.Bundle;
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
    @BindView(R.id.order_total)
    TextView orderTotal;
    @BindView(R.id.order_btnPlaceOrder)
    FButton orderBtnPlaceOrder;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    int sum;

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

        if(getIntent().getStringExtra("userPhone") == null)
            loadOrders(Util.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {
        sum = 0;
        Locale locale = new Locale("en", "US");
        final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.getTvOrderId().setText("#" + adapter.getRef(position).getKey());
                viewHolder.getTvOrderStatus().setText(convertCodeToStatus(model.getStatus()));
                viewHolder.getTvOrderPhone().setText(model.getPhone());
                viewHolder.getTvOrderAddress().setText(model.getAddress());

                sum += Integer.valueOf(model.getTotal());
                orderTotal.setText(format.format(sum));
            }
        };

        adapter.notifyDataSetChanged();
        rvOrder.setAdapter(adapter);
    }


    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
