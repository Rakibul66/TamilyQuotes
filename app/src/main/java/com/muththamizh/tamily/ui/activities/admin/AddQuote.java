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

public class AddQuote extends AppCompatActivity {

    private String quoteIncomeType;
    private String categoryID,categoryDocId,categoryName,quoteId,quoteName;
    Unbinder unbinder;
    @BindView(R.id.quoteText) EditText quoteText;
    @BindView(R.id.createQuote) Button createQuote;
    FirebaseFirestore mFirebaseFireStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);
        unbinder = ButterKnife.bind(this);
        mFirebaseFireStore = FirebaseFirestore.getInstance();
        quoteIncomeType = getIntent().getStringExtra(Constants.ADD_OR_EDIT_QUOTE);
        categoryID = getIntent().getStringExtra(Constants.CATEGORY_ID);
        categoryDocId = getIntent().getStringExtra(Constants.CATEGORY_DOC_ID);
        categoryName = getIntent().getStringExtra(Constants.CATEGORY_NAME);

        if (quoteIncomeType.equalsIgnoreCase("edit")){
            quoteName = getIntent().getStringExtra(Constants.QUOTE_NAME);
            quoteId = getIntent().getStringExtra(Constants.QUOTE_ID);
            createQuote.setText(R.string.update_quote);
            quoteText.setText(quoteName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @OnClick(R.id.createQuote)
    public void createQuote(){
        String quote = quoteText.getText().toString();

        Map<String, String> documentData = new HashMap<>();
        documentData.put("quote",quote);

        if (quote.isEmpty() || quote.equalsIgnoreCase("") || quote.length() == 0){
            Toast.makeText(this, "Quote should not be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if (quoteIncomeType.equalsIgnoreCase("add")){
                String dynamicDocID = "QLY_Cat_"+System.currentTimeMillis();

                mFirebaseFireStore.collection(Constants.CATEGORIES_COLLECTIONS)
                        .document(categoryDocId)
                        .collection(Constants.QUOTES_COLLECTIONS)
                        .document()
                        .set(documentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddQuote.this, "Quote added successfully...!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddQuote.this, "Failed adding new quote...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("errorFireStore",e.toString());
                        Toast.makeText(AddQuote.this, "Failed adding new quote", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {

                mFirebaseFireStore.collection(Constants.CATEGORIES_COLLECTIONS).document(categoryDocId)
                        .collection(Constants.QUOTES_COLLECTIONS).document(quoteId)
                        .set(documentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddQuote.this, "Quote Updated successfully...!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddQuote.this, "Updating quote failed...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuote.this, "Updating quote failed...!!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

}