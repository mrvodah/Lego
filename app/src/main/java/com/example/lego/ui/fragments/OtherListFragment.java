package com.example.lego.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.example.lego.R;
import com.example.lego.database.Database;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Category;
import com.example.lego.ui.activities.Cart;
import com.example.lego.ui.activities.ProductList;
import com.example.lego.ui.adapters.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherListFragment extends Fragment {

    private static final String TAG = "OtherListFragment";
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;
    @BindView(R.id.fab_cart)
    CounterFab fabCart;

    FirebaseDatabase database;
    DatabaseReference category;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    public OtherListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                Log.d(TAG, "populateViewHolder: " + model);
                viewHolder.name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(getContext(), ProductList.class);
                        intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }
        };

        // load menu
        rvMenu.setHasFixedSize(true);
        rvMenu.setLayoutManager(new GridLayoutManager(getContext(), 2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(rvMenu.getContext(), R.anim.layout_fall_down);
        rvMenu.setLayoutAnimation(controller);

        loadMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        fabCart.setCount(new Database(getContext()).getCarts().size());
    }

    private void loadMenu() {
        rvMenu.setAdapter(adapter);

        // animation
        rvMenu.getAdapter().notifyDataSetChanged();
        rvMenu.scheduleLayoutAnimation();
    }

    @OnClick(R.id.fab_cart)
    public void onViewClicked() {
        startActivity(new Intent(getContext(), Cart.class));
    }
}
