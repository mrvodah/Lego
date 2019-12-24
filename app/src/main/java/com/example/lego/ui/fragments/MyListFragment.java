package com.example.lego.ui.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.Constant;
import com.example.lego.R;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Category;
import com.example.lego.ui.activities.ProductList;
import com.example.lego.ui.adapters.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment {

    private static final String TAG = "MyListFragment";
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    FirebaseDatabase database;
    DatabaseReference category;
    StorageReference storageReference;

    TextView name;
    FButton select, upload;

    Category newCategory;
    Uri saveUri;
    public final int PICK_IMAGE_REQUEST = 11;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    public MyListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        storageReference = FirebaseStorage.getInstance().getReference();

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
                        intent.putExtra(Constant.MY_LIST, true);
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

    private void loadMenu() {
        rvMenu.setAdapter(adapter);

        // animation
        rvMenu.getAdapter().notifyDataSetChanged();
        rvMenu.scheduleLayoutAnimation();
    }

    @OnClick(R.id.fab_add)
    public void onViewClicked() {
        showDialog();
    }

    private void showDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_menu_layout, null);

        name = v.findViewById(R.id.edtName);
        select = v.findViewById(R.id.btnSelect);
        upload = v.findViewById(R.id.btnUpload);

        new AlertDialog.Builder(getContext())
                .setTitle("Add new Category")
                .setMessage("Please fill full information")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (newCategory != null) {
                            category.push().setValue(newCategory);
                            adapter.notifyDataSetChanged();
                            Snackbar.make(flContainer, "New category: " + newCategory.getName() + " was added!", Snackbar.LENGTH_SHORT).show();
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
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Uploading ...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for newCategory if image upload and we can get download link
                                    newCategory = new Category(
                                            name.getText().toString(),
                                            uri.toString()
                                    );
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Upload failure ... " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK
                && data != null &&
                data.getData() != null) {
            saveUri = data.getData();
            select.setText("Image Selected");
        }
    }
}
