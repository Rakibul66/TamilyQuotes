package com.muththamizh.tamily.ui.activities.admin;

import static com.muththamizh.tamily.utils.Constants.CATEGORIES_COLLECTIONS;
import static com.muththamizh.tamily.utils.Constants.TRENDING_CATEGORIES_COLLECTIONS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.muththamizh.tamily.CustomSpinner;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Category;
import com.muththamizh.tamily.pojo.Trending;
import com.muththamizh.tamily.ui.activities.Categories;
import com.muththamizh.tamily.ui.adapter.TrendingAdapter;
import com.muththamizh.tamily.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.OnClick;

public class AddTrendingCategory extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener, TrendingCatAdapterAdmin.OnCatItemsClicked{

    Button btnAddTrending;
    CustomSpinner spinnerCategory;
    private ArrayList<Category> categories;
    private ArrayList<Trending> trendingCat;
    private FirebaseFirestore firebaseFirestore;

    TrendingCatAdapterAdmin trendingAdapter;
    private String catName;
    private String catId;
    TextView tvTrendingNotAvailable;
    private String docID;

    RecyclerView rvTrending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trending_category);

        spinnerCategory = findViewById(R.id.sp_select_category);
        btnAddTrending = findViewById(R.id.btn_add_trending_category);
        tvTrendingNotAvailable = findViewById(R.id.tv_trending_not_available);
        rvTrending = findViewById(R.id.rv_trending);


        firebaseFirestore = FirebaseFirestore.getInstance();
        categories = new ArrayList<>();
        trendingCat = new ArrayList<>();

        getCategories();
        getTrendingCategories();

        GridLayoutManager manager = new GridLayoutManager(this, 1);
        rvTrending.setLayoutManager(manager);
        trendingAdapter = new TrendingCatAdapterAdmin(this,trendingCat,this);
        rvTrending.setAdapter(trendingAdapter);



        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catName = categories.get(i).getCategoryName();
                catId = categories.get(i).getCategoryId();
                docID = categories.get(i).getCategoryDocumentId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAddTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AddTrendingCategory.this, "cat:"+catName + " catId: "+catId +" docId:"+docID, Toast.LENGTH_SHORT).show();
                createCategory(catName, catId, docID);
                getTrendingCategories();
                trendingAdapter.notifyDataSetChanged();
            }
        });


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

                            TrendingAdapter adapter = new TrendingAdapter(AddTrendingCategory.this,
                                    categories);
                            spinnerCategory.setAdapter(adapter);
                        } else {
                            Toast.makeText(AddTrendingCategory.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTrendingCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getTrendingCategories() {
        trendingCat.clear();
        firebaseFirestore.collection(TRENDING_CATEGORIES_COLLECTIONS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                                trendingCat.add(new Trending(snapshot.getId(),snapshot.getData().get("categoryId").toString(),snapshot.getData().get("categoryName").toString()));
                                Log.d("Data",snapshot.getId()+":"+snapshot.getData());
                            }
                            if (trendingCat.size() != 0){
                                tvTrendingNotAvailable.setVisibility(View.GONE);
                                rvTrending.setVisibility(View.VISIBLE);
                                trendingAdapter.notifyDataSetChanged();
                            }else {
                                tvTrendingNotAvailable.setVisibility(View.VISIBLE);
                                rvTrending.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(AddTrendingCategory.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Data:exc",e.getMessage());
                        Toast.makeText(AddTrendingCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinnerCategory.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        spinnerCategory.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit));
    }



    public void createCategory(String catName, String catId, String docId){
        Map<String, String> documentData = new HashMap<>();
        documentData.put("categoryName",catName);
        documentData.put("categoryId",catId);

        firebaseFirestore.collection(Constants.TRENDING_CATEGORIES_COLLECTIONS)
                .document(docId).set(documentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddTrendingCategory.this, "Category added successfully...!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddTrendingCategory.this, "Failed adding new category...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("errorFireStore",e.toString());
                        Toast.makeText(AddTrendingCategory.this, "Failed adding new category", Toast.LENGTH_SHORT).show();
                    }
                });
        }


    @Override
    public void onItemClicked(Trending category, int pos) {
        startActivity(new Intent(AddTrendingCategory.this, QuotesAdmin.class)
                .putExtra(Constants.CATEGORY_DOC_ID, category.getCategoryDocumentId())
                .putExtra(Constants.CATEGORY_ID, category.getCategoryId())
                .putExtra(Constants.CATEGORY_NAME, category.getCategoryName()));
    }

    @Override
    public void onDeleteItemClicked(Trending category, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTrendingCategory.this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete the category ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseFirestore.collection(TRENDING_CATEGORIES_COLLECTIONS).document(category.getCategoryDocumentId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getTrendingCategories();
                                    Toast.makeText(AddTrendingCategory.this, "Document deleted successfully....!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddTrendingCategory.this, "Error deleting document...!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddTrendingCategory.this, "Error deleting document...!!", Toast.LENGTH_SHORT).show();
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
    public void onEditItemClicked(Trending category, int pos) {
    }
}
