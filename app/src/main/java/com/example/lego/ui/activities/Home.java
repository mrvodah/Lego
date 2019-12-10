package com.example.lego.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.andremion.counterfab.CounterFab;
import com.example.lego.R;
import com.example.lego.utils.Util;
import com.example.lego.database.Database;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Category;
import com.example.lego.Service.ListenDialog;
import com.example.lego.ui.adapters.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TAG";
    FirebaseDatabase database;
    DatabaseReference category;
    DatabaseReference user;

    @BindView(R.id.sw_layout)
    SwipeRefreshLayout swLayout;
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;

    TextView tvFullName;
    CounterFab fab;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/restaurant_font.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        user = database.getReference("User");

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(Home.this, FoodList.class);
                        intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }
        };

        //init paper
        Paper.init(this);

        // init swift
        swLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMenu();
            }
        });

        // load for first times
        swLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMenu();
            }
        });

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Cart.class));
            }
        });
        fab.setCount(new Database(Home.this).getCarts().size());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set name for user
        View headerView = navigationView.getHeaderView(0);
        tvFullName = headerView.findViewById(R.id.tvfullname);
        tvFullName.setText(Util.currentUser.getName());

        // load menu
        rvMenu.setHasFixedSize(true);
        rvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(rvMenu.getContext(), R.anim.layout_fall_down);
        rvMenu.setLayoutAnimation(controller);

        // register service
        Intent service = new Intent(Home.this, ListenDialog.class);
        startService(service);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(Home.this).getCarts().size());
    }

    private void loadMenu() {
        rvMenu.setAdapter(adapter);
        swLayout.setRefreshing(false);

        // animation
        rvMenu.getAdapter().notifyDataSetChanged();
        rvMenu.scheduleLayoutAnimation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(Home.this, Cart.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(Home.this, OrderStatus.class));
        } else if (id == R.id.nav_changpw) {

            View v = LayoutInflater.from(Home.this).inflate(R.layout.change_password_layout, null);

            final MaterialEditText pw, npw, rpnpw;
            pw = v.findViewById(R.id.edtPw);
            npw = v.findViewById(R.id.edtnPw);
            rpnpw = v.findViewById(R.id.edtrpnPw);

            new AlertDialog.Builder(Home.this)
                    .setTitle("CHANGE PASSWORD")
                    .setMessage("Please fill all information")
                    .setView(v)
                    .setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (pw.getText().toString().equals(Util.currentUser.getPassword()) &&
                                    npw.getText().toString().equals(rpnpw.getText().toString())) {

//                                Util.currentUser.setPassword(npw.getText().toString());
//                                user.child(Util.currentUser.getPhone()).setValue(
//                                        new User(
//                                                Util.currentUser.getName(),
//                                                Util.currentUser.getPassword(),
//                                                Util.currentUser.getIsStaff(),
//                                                Util.currentUser.getSecureCode()
//                                        )
//                                );
//
//                                Toast.makeText(Home.this, "Password changed!", Toast.LENGTH_SHORT).show();

                                Map<String, Object> map = new HashMap<>();
                                map.put("password", npw.getText().toString());

                                user.child(Util.currentUser.getPhone()).updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Home.this, "Password was updated!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Home.this, "Update Password get error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {

                                Toast.makeText(Home.this, "Wrong Password or New Password or Repeat new Password not same! Please check again~", Toast.LENGTH_SHORT).show();

                            }

                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (id == R.id.nav_log_out) {

            //Delete rememeber user & pwd
            Paper.book().destroy();

            Intent intent = new Intent(Home.this, SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
