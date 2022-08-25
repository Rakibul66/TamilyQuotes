package com.muththamizh.tamily.ui.activities.admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Quote;
import com.muththamizh.tamily.utils.Constants;
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
import static com.muththamizh.tamily.utils.Constants.QUOTES_COLLECTIONS;

public class QuotesAdmin extends AppCompatActivity implements View.OnClickListener,QuoteAdapterAdmin.QuoteCardCallbacks {

    @BindView(R.id.navBack) ImageView navBack;
    @BindView(R.id.categoryTitle) TextView categoryTitle;
    @BindView(R.id.rvQuotes) RecyclerView rvQuotes;
    String cat_id,cat_name,cat_doc_id;
    QuoteAdapterAdmin quotesAdapter;
    ArrayList<Quote> quotesList;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    FirebaseFirestore firebaseFirestore;
    @BindView(R.id.noDataUI) LinearLayout noDataUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_admin);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        setStatusBarColor();
        navBack.setOnClickListener(this);
        cat_id = getIntent().getStringExtra(Constants.CATEGORY_ID);
        cat_name = getIntent().getStringExtra(Constants.CATEGORY_NAME);
        cat_doc_id = getIntent().getStringExtra(Constants.CATEGORY_DOC_ID);
        quotesList = new ArrayList<Quote>();
        getQuotes();
        categoryTitle.setText(cat_name);
        rvQuotes.setLayoutManager(linearLayoutManager);
        rvQuotes.setHasFixedSize(true);
        quotesAdapter = new QuoteAdapterAdmin(this,quotesList);
        rvQuotes.setAdapter(quotesAdapter);
    }


    @Override
    public void onQuoteEditClicked(Quote quote, int pos) {
        startActivity(new Intent(QuotesAdmin.this, AddQuote.class)
                .putExtra(Constants.ADD_OR_EDIT_QUOTE, "edit")
                .putExtra(Constants.CATEGORY_DOC_ID, cat_doc_id)
                .putExtra(Constants.CATEGORY_ID, cat_id)
                .putExtra(Constants.CATEGORY_NAME, cat_name)
                .putExtra(Constants.QUOTE_ID,quote.getQuoteId())
                .putExtra(Constants.QUOTE_NAME,quote.getQuote()));
    }

    @Override
    public void onQuoteDeleteClicked(Quote quote, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuotesAdmin.this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete the quote ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseFirestore.collection(CATEGORIES_COLLECTIONS)
                        .document(cat_doc_id)
                        .collection(QUOTES_COLLECTIONS).document(quote.getQuoteId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getQuotes();
                            Toast.makeText(QuotesAdmin.this, "Quote deleted successfully....!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QuotesAdmin.this, "Error deleting quote...!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuotesAdmin.this, "Error deleting quote...!!", Toast.LENGTH_SHORT).show();
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

    private void getQuotes(){
        quotesList.clear();
        firebaseFirestore.collection(CATEGORIES_COLLECTIONS).document(cat_doc_id).collection(QUOTES_COLLECTIONS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                                quotesList.add(new Quote(snapshot.getId(),snapshot.getData().get("quote").toString()));
                                Log.d("Data",snapshot.getId()+":"+snapshot.getData());
                            }
                            if (quotesList.size() != 0){
                                noDataUI.setVisibility(View.GONE);
                                rvQuotes.setVisibility(View.VISIBLE);
                                quotesAdapter.notifyDataSetChanged();
                            }else {
                                noDataUI.setVisibility(View.VISIBLE);
                                rvQuotes.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(QuotesAdmin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Data:exc",e.getMessage());
                Toast.makeText(QuotesAdmin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getQuotes();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.navBack:
                onBackPressed();
                break;
        }
    }

    @OnClick(R.id.addQuote)
    public void addQuote(){
        startActivity(new Intent(QuotesAdmin.this,AddQuote.class)
                .putExtra(Constants.ADD_OR_EDIT_QUOTE,"add")
                .putExtra(Constants.CATEGORY_DOC_ID, cat_doc_id)
                .putExtra(Constants.CATEGORY_ID, cat_id)
                .putExtra(Constants.CATEGORY_NAME, cat_name));
    }

}