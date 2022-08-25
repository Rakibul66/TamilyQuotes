package com.muththamizh.tamily.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.muththamizh.tamily.AdmobNative;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Category;
import com.muththamizh.tamily.pojo.Trending;
import com.muththamizh.tamily.ui.activities.admin.AddTrendingCategory;
import com.muththamizh.tamily.ui.activities.admin.Dashboard;
import com.muththamizh.tamily.ui.adapter.CategoriesAdapter;
import com.muththamizh.tamily.ui.adapter.TrendingCategoriesAdapter;
import com.muththamizh.tamily.ui.fragments.MoreBottomSheet;
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
import static com.muththamizh.tamily.utils.Constants.TRENDING_CATEGORIES_COLLECTIONS;

public class Categories extends AppCompatActivity implements MoreBottomSheet.OnMenuItemsClicked,View.OnClickListener,CategoriesAdapter.OnCategoryItemClicked , TrendingCategoriesAdapter.OnCategoryItemClicked{

    private static final String TAG = "TAG";
    @BindView(R.id.imgFav) ImageView imgFav;
    @BindView(R.id.imgMenu) ImageView imgMenu;
    @BindView(R.id.rvCategory) RecyclerView rvCategory;
    @BindView(R.id.noDataUI)
    LinearLayout noDataUI;
    TextView tvTrendingNotAvailable;
    CategoriesAdapter categoriesAdapter;
    TrendingCategoriesAdapter trendingCategoriesAdapter;
    LinearLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
    ArrayList<Category> categories;
    ArrayList<Trending> trendingCategories;
    MoreBottomSheet moreBottomSheet;
    PrefManager prefManager;

    FirebaseFirestore firebaseFirestore;
    FirebaseFirestore mFirebaseFirestore;

    RecyclerView rvTrending;

    ImageView imageQuote;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);
        setStatusBarColor();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        categories = new ArrayList<>();
        getCategories();
        prefManager = PrefManager.getInstance(this);
        imgFav.setOnClickListener(this);
        imgMenu.setOnClickListener(this);
        rvCategory.setLayoutManager(gridLayoutManager);

        trendingCategories = new ArrayList<>();
        getTrendingCategories();
        rvTrending =  findViewById(R.id.rv_trending);
        tvTrendingNotAvailable =  findViewById(R.id.tv_trending_not_available);
        //GridLayoutManager manager = new GridLayoutManager(this,2);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        rvTrending.setLayoutManager(manager);
        trendingCategoriesAdapter = new TrendingCategoriesAdapter(this,trendingCategories,this);
        rvTrending.setAdapter(trendingCategoriesAdapter);

        rvCategory.setHasFixedSize(true);
        categoriesAdapter = new CategoriesAdapter(this,categories,this);
        rvCategory.setAdapter(categoriesAdapter);

        //load Ad
        AdmobNative.loadAdmobBanner(this,findViewById(R.id.adMobBanner));
        AdmobNative.loadAdmobInterstitial(this);

        firebaseToken();

        imageQuote = findViewById(R.id.image_quote);

        imageQuote.setVisibility(View.INVISIBLE);

        imageQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmobNative.showAdmobInterstitial(Categories.this);

                Intent intent = new Intent(getApplication(),ImageQuotes.class);
                startActivity(intent);
            }
        });
    }

    private void firebaseToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(TAG, token);
                        //Toast.makeText(Categories.this, token, Toast.LENGTH_SHORT).show();
                    }
                });



    }



    private void getCategories() {
        categories.clear();
        firebaseFirestore.collection(CATEGORIES_COLLECTIONS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                               categories.add(new Category(snapshot.getId(),snapshot.getData().get("categoryId").toString(),snapshot.getData().get("categoryName").toString()));
                                Log.d("Data",snapshot.getId()+":"+snapshot.getData());
                            }
                            if (categories.size() != 0){
                                noDataUI.setVisibility(View.GONE);
                                rvCategory.setVisibility(View.VISIBLE);
                                imageQuote.setVisibility(View.VISIBLE);
                                categoriesAdapter.notifyDataSetChanged();
                            }else {
                                noDataUI.setVisibility(View.VISIBLE);
                                rvCategory.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(Categories.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Data:exc",e.getMessage());
                Toast.makeText(Categories.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTrendingCategories() {
        trendingCategories.clear();
        mFirebaseFirestore.collection(TRENDING_CATEGORIES_COLLECTIONS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                                trendingCategories.add(new Trending(snapshot.getId(),snapshot.getData().get("categoryId").toString(),snapshot.getData().get("categoryName").toString()));
                                Log.d("Data",snapshot.getId()+":"+snapshot.getData());
                            }

                            if (trendingCategories.size() != 0){
                                tvTrendingNotAvailable.setVisibility(View.GONE);
                                rvTrending.setVisibility(View.VISIBLE);
                                trendingCategoriesAdapter.notifyDataSetChanged();
                            }else {
                                tvTrendingNotAvailable.setVisibility(View.VISIBLE);
                                rvTrending.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(Categories.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Data:exc",e.getMessage());
                        Toast.makeText(Categories.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgFav:
                makeTapSound();
                //new InterstitialAD(this,getResources().getString(R.string.admob_interstitial_id));
                startActivity(new Intent(Categories.this,FavQuotes.class));
                AdmobNative.showAdmobInterstitial(Categories.this);

                break;
            case R.id.imgMenu:
                makeTapSound();
                moreBottomSheet = new MoreBottomSheet(this);
                moreBottomSheet.show(getSupportFragmentManager(),getResources().getString(R.string.app_name));
                break;
        }
    }

    @Override
    public void onItemClicked(Category category, int pos) {
        makeTapSound();
        //new InterstitialAD(this,getResources().getString(R.string.admob_interstitial_id));
        startActivity(new Intent(Categories.this,Quotes.class)
        .putExtra("cat_id",category.getCategoryId())
        .putExtra("cat_name",category.getCategoryName())
        .putExtra("cat_doc_id",category.getCategoryDocumentId()));

        AdmobNative.showAdmobInterstitial(Categories.this);

    }

    @Override
    public void onItemClicked(Trending category, int pos) {
        makeTapSound();
        //new InterstitialAD(this,getResources().getString(R.string.admob_interstitial_id));
        startActivity(new Intent(Categories.this,Quotes.class)
                .putExtra("cat_id",category.getCategoryId())
                .putExtra("cat_name",category.getCategoryName())
                .putExtra("cat_doc_id",category.getCategoryDocumentId()));

        AdmobNative.showAdmobInterstitial(Categories.this);

    }

    private void makeTapSound(){
        if (prefManager.getBoolean(PrefManager.SOUND_OFF_ON)){
            Constants.playTone(this,R.raw.tap);
        }
    }

    @Override
    public void tapSoundOnOffClicked(boolean isSelected) {
        prefManager.saveBoolean(PrefManager.SOUND_OFF_ON,isSelected);
    }

    @Override
    public void followOnInstaGramClicked() {
        moreBottomSheet.dismiss();
        makeTapSound();
        Constants.redirectToWebPage(this,"http://www.instagram.com/muththamizhoffl");
    }

    @Override
    public void followOnFaceBookClicked() {
        moreBottomSheet.dismiss();
        makeTapSound();
        Constants.redirectToWebPage(this,"https://www.facebook.com/muththamizhsms");
    }

    @Override
    public void rateOurAppClicked() {
        moreBottomSheet.dismiss();
        makeTapSound();
        Constants.rateApp(this);
    }

    @Override
    public void shareOurAppClicked() {
        moreBottomSheet.dismiss();
        makeTapSound();
        Constants.shareApp(this);
    }

}