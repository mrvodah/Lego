package com.example.lego.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lego.R;
import com.example.lego.database.Database;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Food;
import com.example.lego.models.Order;
import com.example.lego.ui.adapters.FoodViewHolder;
import com.example.lego.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodList extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.rv_foodlist)
    RecyclerView rvFoodlist;

    @BindView(R.id.searchBar)
    MaterialSearchBar searchBar;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    StorageReference storageReference;

    DatabaseReference foodList;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    // search functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();

    String categoryId;
    Database localDB;

    TextView name, description, price, discount;
    FButton select, upload;

    Food newFood;
    Uri saveUri;
    public final int PICK_IMAGE_REQUEST = 11;

    @BindView(R.id.fab)
    FloatingActionButton fab;

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

        setContentView(R.layout.activity_food_list);
        ButterKnife.bind(this);

        //Init firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Product");
        storageReference = FirebaseStorage.getInstance().getReference();

        //LocalDB
        localDB = new Database(this);

        // load menu
        layoutManager = new LinearLayoutManager(this);
        rvFoodlist.setHasFixedSize(true);
        rvFoodlist.setLayoutManager(layoutManager);

        // get Intent
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
            loadListFood();
        }

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
                loadListFood();
            }
        });

        // load for first times
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                loadListFood();
            }
        });

        loadSuggest();
        searchBar.setHint("Enter your food");
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
                    rvFoodlist.setAdapter(adapter);
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

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.getFood_name().setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.getFood_image());

                //Add Favorites
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_black_24dp);
                else
                    viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);

                //Click to change state of Favorites
                viewHolder.getFav_image().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " was added to Favorites!", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " was removed from Favorites!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.getCart_image().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                "1",
                                model.getPrice(),
                                model.getDiscount()

                        ));
                        Toast.makeText(FoodList.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FoodList.this, FoodDetail.class);
                        intent.putExtra("ProductId", searchAdapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        rvFoodlist.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood() {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("categoryId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.getFood_name().setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.getFood_image());

                //Add Favorites
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_black_24dp);
                else
                    viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);

                //Click to change state of Favorites
                viewHolder.getFav_image().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " was added to Favorites!", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " was removed from Favorites!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.getCart_image().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                "1",
                                model.getPrice(),
                                model.getDiscount()

                        ));
                        Toast.makeText(FoodList.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FoodList.this, FoodDetail.class);
                        intent.putExtra("ProductId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        rvFoodlist.setAdapter(adapter);
        swipeLayout.setRefreshing(false);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        showAddProductDialog();
    }

    private void showAddProductDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_food_layout, null);

        name = v.findViewById(R.id.edtName);
        description = v.findViewById(R.id.edtDescription);
        price = v.findViewById(R.id.edtPrice);
        discount = v.findViewById(R.id.edtDiscount);
        select = v.findViewById(R.id.btnSelect);
        upload = v.findViewById(R.id.btnUpload);

        new AlertDialog.Builder(this)
                .setTitle("Add new Food")
                .setMessage("Please fill full information")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (newFood != null) {
                            foodList.push().setValue(newFood);
                            Snackbar.make(swipeLayout, "New category: " + newFood.getName() + " was added!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading ...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for newCategory if image upload and we can get download link
                                    newFood = new Food(
                                            name.getText().toString(),
                                            uri.toString(),
                                            description.getText().toString(),
                                            price.getText().toString(),
                                            discount.getText().toString(),
                                            categoryId
                                    );
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this, "Upload failure ... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded: " + progress + "%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK
                && data != null &&
                data.getData() != null) {
            saveUri = data.getData();
            select.setText("Image Selected");
        }
    }

    private void showDialogUpdateFood(final String key, final Food item) {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_food_layout, null);

        name = v.findViewById(R.id.edtName);
        description = v.findViewById(R.id.edtDescription);
        price = v.findViewById(R.id.edtPrice);
        discount = v.findViewById(R.id.edtDiscount);
        select = v.findViewById(R.id.btnSelect);
        upload = v.findViewById(R.id.btnUpload);

        name.setText(item.getName());
        description.setText(item.getDescription());
        price.setText(item.getPrice());
        discount.setText(item.getDiscount());

        new AlertDialog.Builder(this)
                .setTitle("Add new Food")
                .setMessage("Please fill full information")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // Update food
                        item.setName(name.getText().toString());
                        item.setDescription(description.getText().toString());
                        item.setPrice(price.getText().toString());
                        item.setDiscount(discount.getText().toString());
                        foodList.child(key).setValue(item);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Util.UPDATE)){
            showDialogUpdateFood(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Util.DELETE)){
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show();
    }


    private void changeImage(final Food item) {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading ...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for newCategory if image upload and we can get download link
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this, "Upload failure ... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded: " + progress + "%");
                        }
                    });
        }
    }

}
