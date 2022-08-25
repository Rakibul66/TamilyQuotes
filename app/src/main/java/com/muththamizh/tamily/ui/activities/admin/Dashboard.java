package com.muththamizh.tamily.ui.activities.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Category;
import com.muththamizh.tamily.utils.Constants;
import com.muththamizh.tamily.utils.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.muththamizh.tamily.utils.Constants.CATEGORIES_COLLECTIONS;

public class Dashboard extends AppCompatActivity implements CatAdapterAdmin.OnCatItemsClicked {


    @BindView(R.id.rvCategory)
    RecyclerView rvCategory;
    @BindView(R.id.noDataUI)
    LinearLayout noDataUI;
    CatAdapterAdmin categoriesAdapter;
    Context context;
    LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
    ArrayList<Category> categories;
    PrefManager prefManager;
    FirebaseFirestore firebaseFirestore;

    @Override
    public void onItemClicked(Category category, int pos) {
        startActivity(new Intent(Dashboard.this, QuotesAdmin.class)
                .putExtra(Constants.CATEGORY_DOC_ID, category.getCategoryDocumentId())
                .putExtra(Constants.CATEGORY_ID, category.getCategoryId())
                .putExtra(Constants.CATEGORY_NAME, category.getCategoryName()));
    }

    @Override
    public void onDeleteItemClicked(Category category, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete the category ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseFirestore.collection(CATEGORIES_COLLECTIONS).document(category.getCategoryDocumentId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getCategories();
                            Toast.makeText(Dashboard.this, "Document deleted successfully....!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Dashboard.this, "Error deleting document...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Dashboard.this, "Error deleting document...!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onEditItemClicked(Category category, int pos) {
        startActivity(new Intent(Dashboard.this, AddCategory.class)
                .putExtra(Constants.ADD_OR_EDIT_QUOTE, "edit")
                .putExtra(Constants.CATEGORY_DOC_ID, category.getCategoryDocumentId())
                .putExtra(Constants.CATEGORY_ID, category.getCategoryId())
                .putExtra(Constants.CATEGORY_NAME, category.getCategoryName()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        setStatusBarColor();
        firebaseFirestore = FirebaseFirestore.getInstance();
        categories = new ArrayList<>();
        getCategories();
        prefManager = PrefManager.getInstance(this);
        rvCategory.setLayoutManager(gridLayoutManager);
        rvCategory.setHasFixedSize(true);
        categoriesAdapter = new CatAdapterAdmin(this, categories, this);
        rvCategory.setAdapter(categoriesAdapter);




    }


    @Override
    protected void onStart() {
        super.onStart();
        getCategories();
    }

    private void getCategories() {
        categories.clear();
        firebaseFirestore.collection(CATEGORIES_COLLECTIONS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                categories.add(new Category(snapshot.getId(), snapshot.getData().get("categoryId").toString(), snapshot.getData().get("categoryName").toString()));
                                Log.d("Data", snapshot.getId() + ":" + snapshot.getData());
                            }
                            if (categories.size() != 0) {
                                noDataUI.setVisibility(View.GONE);
                                rvCategory.setVisibility(View.VISIBLE);
                                categoriesAdapter.notifyDataSetChanged();
                            } else {
                                noDataUI.setVisibility(View.VISIBLE);
                                rvCategory.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(Dashboard.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
    }

    @OnClick(R.id.addQuote)
    public void addQuote() {
        startActivity(new Intent(Dashboard.this, AddCategory.class)
                .putExtra(Constants.ADD_OR_EDIT_QUOTE, "add"));
    }

    @OnClick(R.id.add_trending_category)
    public void addTrendingCategory(){
        startActivity(new Intent(Dashboard.this,AddTrendingCategory.class));
    }


    @OnClick(R.id.addImageQuote)
    public void addImageQuote(){
        startActivity(new Intent(Dashboard.this, AddImageQuote.class));
    }

}