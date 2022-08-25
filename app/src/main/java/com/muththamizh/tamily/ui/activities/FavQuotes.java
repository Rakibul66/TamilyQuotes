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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muththamizh.tamily.AdmobNative;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Quote;
import com.muththamizh.tamily.ui.adapter.QuotesAdapter;
import com.muththamizh.tamily.ui.adapter.QuotesWithAdsAdapter;
import com.muththamizh.tamily.utils.FileUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavQuotes extends AppCompatActivity implements QuotesWithAdsAdapter.DownloadLLClicked,View.OnClickListener, QuotesWithAdsAdapter.FavItemFavIconTapped {

    @BindView(R.id.navBack)
    ImageView navBack;
    @BindView(R.id.categoryTitle)
    TextView categoryTitle;
    @BindView(R.id.rvQuotes)
    RecyclerView rvQuotes;
    @BindView(R.id.noDataUI)
    LinearLayout noDataUI;
    QuotesWithAdsAdapter quotesAdapter;
    ArrayList<Quote> quotesList;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_quotes);
        ButterKnife.bind(this);
        setStatusBarColor();
        navBack.setOnClickListener(this);
        getDataFromDB();
        rvQuotes.setLayoutManager(linearLayoutManager);
        rvQuotes.setHasFixedSize(true);
        rvQuotes.setAdapter(quotesAdapter);

        //load Ad
        AdmobNative.loadAdmobBanner(this,findViewById(R.id.adMobBanner));

    }



    private void getDataFromDB(){
        ArrayList<Quote> list = new ArrayList<>();
        quotesList = new ArrayList<>();
        list  = Hawk.get("quotes_list");

        ArrayList<Quote> list_ad_Data = new ArrayList<>();

        //quotesList  =  Hawk.get("quotes_list",new ArrayList<Quote>());;
        if (list!=null){

            for (int i = 0; i < list.size(); i++) {
                if (i > 0 && i %4 == 0 ){
                    list_ad_Data.add(null);
                }
                list_ad_Data.add(list.get(i));
            }
            quotesList.addAll(list_ad_Data);
            quotesAdapter =new QuotesWithAdsAdapter(this,quotesList,1);
            rvQuotes.setAdapter(quotesAdapter);
            noDataUI.setVisibility(View.GONE);
            rvQuotes.setVisibility(View.VISIBLE);
        }else {
            noDataUI.setVisibility(View.VISIBLE);
            rvQuotes.setVisibility(View.GONE);
        }

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
    public void refreshFavQuotesList() {
        getDataFromDB();
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
                        try {
                            FileUtils.downloadFile(view,FavQuotes.this);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FavQuotes.this);
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