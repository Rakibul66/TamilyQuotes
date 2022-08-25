package com.muththamizh.tamily.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.muththamizh.tamily.ui.adapter.QuotesAdapter;
import com.muththamizh.tamily.ui.adapter.QuotesWithAdsAdapter;
import com.muththamizh.tamily.utils.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.muththamizh.tamily.utils.Constants.CATEGORIES_COLLECTIONS;
import static com.muththamizh.tamily.utils.Constants.QUOTES_COLLECTIONS;

public class Quotes extends AppCompatActivity implements QuotesWithAdsAdapter.DownloadLLClicked,View.OnClickListener {

    @BindView(R.id.navBack) ImageView navBack;
    @BindView(R.id.categoryTitle) TextView categoryTitle;
    @BindView(R.id.rvQuotes) RecyclerView rvQuotes;
    String cat_id,cat_name,cat_doc_id;
    QuotesWithAdsAdapter quotesAdapter;
    ArrayList<Quote> quotesList;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    FirebaseFirestore firebaseFirestore;
    @BindView(R.id.noDataUI)
    LinearLayout noDataUI;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        setStatusBarColor();
        navBack.setOnClickListener(this);
        cat_id = getIntent().getStringExtra("cat_id");
        cat_name = getIntent().getStringExtra("cat_name");
        cat_doc_id = getIntent().getStringExtra("cat_doc_id");
        quotesList = new ArrayList<Quote>();
        getQuotes();
        categoryTitle.setText(cat_name);
        rvQuotes.setLayoutManager(linearLayoutManager);
        rvQuotes.setHasFixedSize(true);

        //iserting ads spaces so that not skip item
       /* ArrayList<Quote> list_Data = quotesList;
        ArrayList<Quote> list_ad_Data = new ArrayList<>();
        ArrayList<Quote> adsAndQoutesList = new ArrayList<>();

        //Toast.makeText(this, "quotes:"+quotesList.size(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < list_Data.size(); i++) {
            if (i > 0 && i %4 == 0 ){
                list_ad_Data.add(null);
            }
            list_ad_Data.add(list_Data.get(i));
        }
        adsAndQoutesList.addAll(list_ad_Data);

        Toast.makeText(this, "list:"+adsAndQoutesList.size(), Toast.LENGTH_SHORT).show();
*/
        quotesAdapter = new QuotesWithAdsAdapter(this,quotesList,0);
        rvQuotes.setAdapter(quotesAdapter);
    }



    private void getQuotes(){
        quotesList.clear();
        Log.d("Dump",CATEGORIES_COLLECTIONS+"\n\n"+cat_doc_id+"\n\n"+QUOTES_COLLECTIONS);
        firebaseFirestore.collection(CATEGORIES_COLLECTIONS).document(cat_doc_id).collection(QUOTES_COLLECTIONS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            int count = 0;
                            for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){

                                //ArrayList<Quote> list_Data = quotesList;
                                //ArrayList<Quote> list_ad_Data = new ArrayList<>();
                                count++;
                                if (count > 0 && count %5 == 0 ){
                                    quotesList.add(null);
                                    //Toast.makeText(Quotes.this, "null added", Toast.LENGTH_SHORT).show();
                                }else{
                                    //Toast.makeText(Quotes.this, "quote added", Toast.LENGTH_SHORT).show();

                                    quotesList.add(new Quote(snapshot.getId(),snapshot.getData().get("quote").toString()));
                                }

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
                            Toast.makeText(Quotes.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Data:exc",e.getMessage());
                Toast.makeText(Quotes.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.navBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void downloadClicked(View vi) {
        checkRunTimePermissions(vi);
    }

    private void checkRunTimePermissions(View view){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the camera
                        //Toast.makeText(Quotes.this, "granted", Toast.LENGTH_SHORT).show();
                        try {
                            FileUtils.downloadFile(view,Quotes.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Quotes.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }else {
                            checkRunTimePermissions(view);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Quotes.this);
        builder.setTitle("Need Permissions");
        builder.setCancelable(false);
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onBackPressed();
            }
        });
        builder.show();

    }

}