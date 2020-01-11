package com.example.lego.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;
import com.example.lego.ui.adapters.OrderDetailAdapter;
import com.example.lego.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetail extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.order_id)
    TextView orderId;
    @BindView(R.id.order_phone)
    TextView orderPhone;
    @BindView(R.id.order_total)
    TextView orderTotal;
    @BindView(R.id.order_address)
    TextView orderAddress;
    @BindView(R.id.rv_orderDetail)
    RecyclerView rvOrderDetail;
    @BindView(R.id.order_comments)
    TextView orderComments;

    public String id = "013679";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);

        if(getIntent() != null){
            id = getIntent().getStringExtra("OrderId");
        }

        orderId.setText("#" + id);
        orderPhone.setText(Util.currentRequest.getPhone());
        orderAddress.setText(Util.currentRequest.getAddress());
        orderTotal.setText(Util.currentRequest.getTotal());
        orderComments.setText(Util.currentRequest.getStartDate());

        rvOrderDetail.setHasFixedSize(true);
        rvOrderDetail.setLayoutManager(new LinearLayoutManager(this));
        rvOrderDetail.setAdapter(new OrderDetailAdapter(Util.currentRequest.getList()));

    }
}
