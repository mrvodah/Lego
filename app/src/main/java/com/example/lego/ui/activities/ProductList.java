package com.example.lego.ui.activities;

import android.annotation.SuppressLint;
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

import com.example.lego.Constant;
import com.example.lego.R;
import com.example.lego.database.Database;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Product;
import com.example.lego.models.Order;
import com.example.lego.ui.adapters.ProductViewHolder;
import com.example.lego.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductList extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.rv_product)
    RecyclerView rvProduct;

    @BindView(R.id.searchBar)
    MaterialSearchBar searchBar;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    StorageReference storageReference;

    DatabaseReference productList;
    DatabaseReference productWaitList;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    // search functionality
    FirebaseRecyclerAdapter<Product, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();

    String categoryId;
    Database localDB;

    TextView name, description, price, discount, number;
    FButton select, upload;

    Product newProduct;
    Uri saveUri;
    public final int PICK_IMAGE_REQUEST = 11;

    @BindView(R.id.fab_add)
    FloatingActionButton fab;

    boolean isMyList = false;
    Calendar endDate = Calendar.getInstance();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView
//        CalligraphyConfig.initDefault(
//                new CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/restaurant_font.otf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        );

        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);

        endDate.add(Calendar.WEEK_OF_YEAR, 1);

        isMyList = getIntent().getBooleanExtra(Constant.MY_LIST, false);
        if(isMyList){
            fab.setVisibility(View.VISIBLE);
        }
        else{
            fab.setVisibility(View.GONE);
        }

        //Init firebase
        database = FirebaseDatabase.getInstance();
        productList = database.getReference("Product");
        productWaitList = database.getReference("ProductWait");
        storageReference = FirebaseStorage.getInstance().getReference();

        //LocalDB
        localDB = new Database(this);

        // load menu
        layoutManager = new LinearLayoutManager(this);
        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(layoutManager);

        // get Intent
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
            loadListProduct();
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

        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_item,
                ProductViewHolder.class,
                productList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
                if(isMyList){

                    if(model.getPhone().equals(Util.currentUser.getPhone())){

                        viewHolder.getProduct_name().setText(model.getName());
                        viewHolder.getProduct_own().setText(model.getPhone());
                        viewHolder.getProduct_price().setText(model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());

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
                                    Toast.makeText(ProductList.this, model.getName() + " was added to Favorites!", Toast.LENGTH_SHORT).show();
                                } else {
                                    localDB.removeFromFavorites(adapter.getRef(position).getKey());
                                    viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                    Toast.makeText(ProductList.this, model.getName() + " was removed from Favorites!", Toast.LENGTH_SHORT).show();
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
                                        model.getDiscount(),
                                        Util.dateFormat.format(new Date()),
                                        Util.dateFormat.format(endDate.getTime())

                                ));
                                Toast.makeText(ProductList.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(ProductList.this, ProductDetail.class);
                                intent.putExtra("ProductId", searchAdapter.getRef(position).getKey());
                                startActivity(intent);
                            }
                        });
                    }
                }
                else{
                    viewHolder.getProduct_name().setText(model.getName());
                    viewHolder.getProduct_own().setText(model.getPhone());
                    viewHolder.getProduct_price().setText(model.getPrice() + "$");
                    Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());

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
                                Toast.makeText(ProductList.this, model.getName() + " was added to Favorites!", Toast.LENGTH_SHORT).show();
                            } else {
                                localDB.removeFromFavorites(adapter.getRef(position).getKey());
                                viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                Toast.makeText(ProductList.this, model.getName() + " was removed from Favorites!", Toast.LENGTH_SHORT).show();
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
                                    model.getDiscount(),
                                    Util.dateFormat.format(new Date()),
                                    Util.dateFormat.format(endDate.getTime())

                            ));
                            Toast.makeText(ProductList.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent intent = new Intent(ProductList.this, ProductDetail.class);
                            intent.putExtra("ProductId", searchAdapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        rvProduct.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        productList.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Product item = postSnapshot.getValue(Product.class);
                            if(item.getPhone().equals(Util.currentUser.getPhone())){

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
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_item,
                ProductViewHolder.class,
                productList.orderByChild("categoryId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, final Product model, final int position) {
                if(isMyList){

                    if(model.getPhone().equals(Util.currentUser.getPhone())){

                        viewHolder.getProduct_name().setText(model.getName());
                        viewHolder.getProduct_own().setText(model.getPhone());
                        viewHolder.getProduct_price().setText(model.getPrice() + "$");
                        Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());

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
                                    Toast.makeText(ProductList.this, model.getName() + " was added to Favorites!", Toast.LENGTH_SHORT).show();
                                } else {
                                    localDB.removeFromFavorites(adapter.getRef(position).getKey());
                                    viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                    Toast.makeText(ProductList.this, model.getName() + " was removed from Favorites!", Toast.LENGTH_SHORT).show();
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
                                        model.getDiscount(),
                                        Util.dateFormat.format(new Date()),
                                        Util.dateFormat.format(endDate.getTime())

                                ));
                                Toast.makeText(ProductList.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(ProductList.this, ProductDetail.class);
                                intent.putExtra("ProductId", adapter.getRef(position).getKey());
                                startActivity(intent);
                            }
                        });
                    }
                }
                else{

                    viewHolder.getProduct_name().setText(model.getName());
                    viewHolder.getProduct_own().setText(model.getPhone());
                    viewHolder.getProduct_price().setText(model.getPrice() + "$");
                    Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());

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
                                Toast.makeText(ProductList.this, model.getName() + " was added to Favorites!", Toast.LENGTH_SHORT).show();
                            } else {
                                localDB.removeFromFavorites(adapter.getRef(position).getKey());
                                viewHolder.getFav_image().setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                Toast.makeText(ProductList.this, model.getName() + " was removed from Favorites!", Toast.LENGTH_SHORT).show();
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
                                    model.getDiscount(),
                                    Util.dateFormat.format(new Date()),
                                    Util.dateFormat.format(endDate.getTime())

                            ));
                            Toast.makeText(ProductList.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent intent = new Intent(ProductList.this, ProductDetail.class);
                            intent.putExtra("ProductId", adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        rvProduct.setAdapter(adapter);
        swipeLayout.setRefreshing(false);
    }

    @OnClick(R.id.fab_add)
    public void onViewClicked() {
        showAddProductDialog();
    }

    private void showAddProductDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_product_layout, null);

        name = v.findViewById(R.id.edtName);
        description = v.findViewById(R.id.edtDescription);
        price = v.findViewById(R.id.edtPrice);
        discount = v.findViewById(R.id.edtDiscount);
        number = v.findViewById(R.id.edtTotal);
        select = v.findViewById(R.id.btnSelect);
        upload = v.findViewById(R.id.btnUpload);

        new AlertDialog.Builder(this)
                .setTitle("Add new Product")
                .setMessage("Please fill full information")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (newProduct != null) {
                            productWaitList.push().setValue(newProduct);
                            Snackbar.make(swipeLayout, "New product: " + newProduct.getName() + " was added to wait list!", Snackbar.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for newCategory if image upload and we can get download link
                                    newProduct = new Product(
                                            name.getText().toString(),
                                            uri.toString(),
                                            description.getText().toString(),
                                            price.getText().toString(),
                                            discount.getText().toString(),
                                            categoryId,
                                            Integer.valueOf(number.getText().toString()),
                                            Util.currentUser.getPhone()
                                    );
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProductList.this, "Upload failure ... " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void showDialogUpdateProduct(final String key, final Product item) {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_product_layout, null);

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
                .setTitle("Add new Product")
                .setMessage("Please fill full information")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // Update product
                        item.setName(name.getText().toString());
                        item.setDescription(description.getText().toString());
                        item.setPrice(price.getText().toString());
                        item.setDiscount(discount.getText().toString());
                        productList.child(key).setValue(item);
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
            showDialogUpdateProduct(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Util.DELETE)){
            deleteProduct(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteProduct(String key) {
        productList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted!", Toast.LENGTH_SHORT).show();
    }


    private void changeImage(final Product item) {
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
                            Toast.makeText(ProductList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ProductList.this, "Upload failure ... " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
