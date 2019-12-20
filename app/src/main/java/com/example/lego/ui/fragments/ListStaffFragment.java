package com.example.lego.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lego.R;
import com.example.lego.database.Database;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Order;
import com.example.lego.models.Product;
import com.example.lego.ui.activities.FoodDetail;
import com.example.lego.ui.activities.FoodList;
import com.example.lego.ui.adapters.ProductViewHolderStaff;
import com.example.lego.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListStaffFragment extends Fragment {


    @BindView(R.id.searchBar)
    MaterialSearchBar searchBar;
    @BindView(R.id.rv_product)
    RecyclerView rvProduct;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    StorageReference storageReference;

    DatabaseReference productList;
    DatabaseReference productWaitList;
    FirebaseRecyclerAdapter<Product, ProductViewHolderStaff> adapter;

    // search functionality
    FirebaseRecyclerAdapter<Product, ProductViewHolderStaff> searchAdapter;
    List<String> suggestList = new ArrayList<>();

    public ListStaffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_staff, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        productList = database.getReference("Product");
        productWaitList = database.getReference("ProductWait");
        storageReference = FirebaseStorage.getInstance().getReference();

        // load menu
        layoutManager = new LinearLayoutManager(getContext());
        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(layoutManager);

        // init swipe
        swipeLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListProduct();
            }
        });

        // load for first times
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                loadListProduct();
            }
        });

        loadSuggest();
        searchBar.setHint("Enter your product");
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When user type their text, we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close
                //Restore origin adapter
                if (!enabled)
                    rvProduct.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolderStaff>(
                Product.class,
                R.layout.product_item,
                ProductViewHolderStaff.class,
                productList
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolderStaff viewHolder, final Product model, final int position) {
                viewHolder.getProduct_name().setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());


            }
        };

        rvProduct.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        productList
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Product item = postSnapshot.getValue(Product.class);
                            if (item.getPhone().equals(Util.currentUser.getPhone())) {

                                suggestList.add(item.getName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListProduct() {
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolderStaff>(
                Product.class,
                R.layout.product_item,
                ProductViewHolderStaff.class,
                productList
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolderStaff viewHolder, final Product model, final int position) {
                viewHolder.getProduct_name().setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());

                viewHolder.getProduct_phone().setText("Người bán: " + model.getPhone());
                viewHolder.getProduct_price().setText(model.getPrice());
                viewHolder.getProduct_number().setText("Số sản phẩm: " + model.getRemain() + "/" + model.getTotal());

                viewHolder.getBtnEdit().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                viewHolder.getBtnRemove().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };

        rvProduct.setAdapter(adapter);
        swipeLayout.setRefreshing(false);
    }

}
