package com.muththamizh.tamily.ui.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddCategory extends AppCompatActivity {

    private String categoryIncomeType;
    private String categoryID,categoryDocId,categoryName;
    Unbinder unbinder;
    @BindView(R.id.categoryText) EditText categoryText;
    @BindView(R.id.createCategory) Button createCategory;
    FirebaseFirestore mFirebaseFireStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        unbinder = ButterKnife.bind(this);
        mFirebaseFireStore = FirebaseFirestore.getInstance();
        categoryIncomeType = getIntent().getStringExtra(Constants.ADD_OR_EDIT_QUOTE);
        if (categoryIncomeType.equalsIgnoreCase("edit")){
            categoryID = getIntent().getStringExtra(Constants.CATEGORY_ID);
            categoryDocId = getIntent().getStringExtra(Constants.CATEGORY_DOC_ID);
            categoryName = getIntent().getStringExtra(Constants.CATEGORY_NAME);
            createCategory.setText(R.string.update_category);
            categoryText.setText(categoryName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @OnClick(R.id.createCategory)
    public void createCategory(){
        String category = categoryText.getText().toString();

        Map<String, String> documentData = new HashMap<>();
        documentData.put("categoryName",category);
        documentData.put("categoryId",categoryIncomeType.equalsIgnoreCase("add") ? System.currentTimeMillis()+"" : categoryID);
        System.out.println("documentData "+ documentData);
        if (category.isEmpty() || category.equalsIgnoreCase("") || category.length() == 0){
            Toast.makeText(this, "Category title should not be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if (categoryIncomeType.equalsIgnoreCase("add")){
                String dynamicDocID = "QLY_Cat_"+System.currentTimeMillis();
                mFirebaseFireStore.collection(Constants.CATEGORIES_COLLECTIONS)
                        .document().set(documentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddCategory.this, "Category added successfully...!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddCategory.this, "Failed adding new category...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("errorFireStore",e.toString());
                        Toast.makeText(AddCategory.this, "Failed adding new category", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {

                mFirebaseFireStore.collection(Constants.CATEGORIES_COLLECTIONS).document(categoryDocId)
                        .set(documentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddCategory.this, "Category Updated successfully...!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddCategory.this, "Updating category failed...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCategory.this, "Updating category failed...!!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }


}