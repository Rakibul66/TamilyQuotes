package com.muththamizh.tamily.ui.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.ui.adapter.ImageQuote;
import com.muththamizh.tamily.ui.adapter.ImageQuoteAdapter;
import com.muththamizh.tamily.ui.adapter.ImageQuoteRecyclerDecoration;

public class AddImageQuote extends AppCompatActivity {

    CardView upload;
    RecyclerView recyclerview;

    private FirebaseFirestore db;
    private CollectionReference quotes;

    ImageQuoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_quote);

        upload = findViewById(R.id.btn_upload);
        recyclerview = findViewById(R.id.recyclerview);
        int sidePadding = getResources().getDimensionPixelSize(R.dimen.sidePadding);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerview.addItemDecoration(new ImageQuoteRecyclerDecoration(sidePadding, topPadding, bottomPadding));

        db = FirebaseFirestore.getInstance();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddImageQuote.this,UploadImageQuote.class);
                startActivity(intent);
                finish();
            }
        });

        loadData();
    }

    private void loadData() {

        quotes = db.collection("image_quote");

        Query query = quotes.orderBy("timestamp", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<ImageQuote> options = new FirestoreRecyclerOptions.Builder<ImageQuote>()
                .setQuery(query, ImageQuote.class)
                .build();

        adapter = new ImageQuoteAdapter(options);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview.setAdapter(adapter);
        adapter.startListening();

    }
}