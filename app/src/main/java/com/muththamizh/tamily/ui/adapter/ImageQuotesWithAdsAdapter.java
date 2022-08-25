package com.muththamizh.tamily.ui.adapter;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muththamizh.tamily.AdmobNative;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Quote;
import com.muththamizh.tamily.utils.Constants;
import com.muththamizh.tamily.utils.FileUtils;
import com.muththamizh.tamily.utils.PrefManager;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageQuotesWithAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //for ads integration
    private static final int LIST_AD_DELTA = 3;
    private static final int CONTENT = 0;
    private static final int AD = 1;

    private Context mContext;
    private ArrayList<ImageQuote> quotesList;
    PrefManager prefManager;
    FavItemFavIconTapped favItemFavIconTapped;
    private int ref = -1;
    DownloadLLClicked downloadClicked;

    FirebaseFirestore firebaseFirestore;

    public ImageQuotesWithAdsAdapter(Context mContext, ArrayList<ImageQuote> quotesList, int ref) {
        this.mContext = mContext;
        this.quotesList = quotesList;
        this.ref = ref;
        if (ref == 1){
            favItemFavIconTapped = (FavItemFavIconTapped)mContext;
        }
        downloadClicked = (DownloadLLClicked)mContext;
        prefManager = PrefManager.getInstance(mContext);
        Hawk.init(mContext).build();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        firebaseFirestore = FirebaseFirestore.getInstance();

        if (viewType == CONTENT) {
            //content
            return new QuotesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_image_quote,parent,false));
        } else {
            //ads
            return new AdsHolder(LayoutInflater.from(mContext).inflate(R.layout.native_ads,parent,false));

        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder quotesHolder, int position) {
        if (getItemViewType(position) == CONTENT) {

            QuotesViewHolder holder = (QuotesViewHolder) quotesHolder;

            final String fileReference = quotesList.get(position).getQuoteId();

            Glide.with(mContext).load(quotesList.get(position).getQuote()).into(holder.imageView);

            if (Hawk.contains(quotesList.get(position).getQuoteId())){
                holder.imgFav.setImageResource(R.drawable.ic_icon_awesome_heart);
            }else {
                holder.imgFav.setImageResource(R.drawable.ic_heart);
            }

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeTapSound();
                    if (Hawk.contains(quotesList.get(position).getQuoteId())){
                        Hawk.delete(quotesList.get(position).getQuoteId());

                        ArrayList<ImageQuote> list =  Hawk.get("image_quotes_list",new ArrayList<ImageQuote>());
                        if(list==null){
                            list = new ArrayList<>();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getQuoteId().equals(quotesList.get(position).getQuoteId())){
                                list.remove(i);
                                i--;
                            }
                        }
                        Hawk.put("image_quotes_list",list);
                    } else {
                        ArrayList<ImageQuote> qList =  Hawk.get("image_quotes_list",new ArrayList<ImageQuote>());
                        if (qList != null){
                            qList.add(quotesList.get(position));
                            Hawk.put("image_quotes_list",qList);
                        }
                        Hawk.put(quotesList.get(position).getQuoteId(),quotesList.get(position));
                    }
                    if (ref ==1){
                        favItemFavIconTapped.refreshFavQuotesList();
                    }else {
                        notifyDataSetChanged();
                    }
                }
            });


            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    makeTapSound();

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imageView.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImage(bitmap);


//                    Constants.shareQuote(mContext,quotesList.get(position).getQuote());
//                    shareItem(quotesList.get(position).getQuote());

                }
            });

            holder.llDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeTapSound();
                    downloadClicked.downloadClicked(holder.cardView);
//                     FileUtils.downloadFile(holder.cardView,mContext);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeTapSound();
                }
            });

        }else{

            // ads binding
            //Toast.makeText(mContext, "loadNative", Toast.LENGTH_SHORT).show();
            AdsHolder adsHolder = (AdsHolder) quotesHolder;

            AdmobNative.loadNativeAd(mContext,adsHolder.fl_adplaceholder,true);


        }
    }

   /* @Override
    public void onBindViewHolder(@NonNull QuotesViewHolder holder, int position) {

    }*/



    private void makeTapSound(){
        if (prefManager.getBoolean(PrefManager.SOUND_OFF_ON)){
            Constants.playTone(mContext,R.raw.tap);
        }
    }

    @Override
    public int getItemCount() {
        return quotesList.size() != 0 ? quotesList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        Log.d("errorlog", ""+quotesList.size());
        if (quotesList.get(position) == null) {
            Log.d("errorlog", ""+quotesList.get(position));

            return AD;
        }
        return CONTENT;
    }


    public class QuotesViewHolder extends RecyclerView.ViewHolder{

        LinearLayout share;

        @BindView(R.id.image) ImageView imageView;
        @BindView(R.id.imgFav) ImageView imgFav;
        @BindView(R.id.llDownload) LinearLayout llDownload;
        @BindView(R.id.llShare) LinearLayout llShare;
        @BindView(R.id.cardView) CardView cardView;

        public QuotesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            share = itemView.findViewById(R.id.llShare);

        }
    }


    public interface FavItemFavIconTapped{
        void refreshFavQuotesList();
    }

    public interface DownloadLLClicked{
        void downloadClicked(View vi);
    }

    public class AdsHolder extends RecyclerView.ViewHolder{

        public FrameLayout fl_adplaceholder;

        public AdsHolder(@NonNull View itemView) {
            super(itemView);
            fl_adplaceholder = itemView.findViewById(R.id.fl_adplaceholder);
        }
    }

    public void shareImage(Bitmap bitmap) {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues newImageDetails = new ContentValues();
        String displayName = "Image." + System.currentTimeMillis() + ".jpg";
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        Uri imageContentUri = contentResolver.insert(contentUri, newImageDetails);

        try (ParcelFileDescriptor fileDescriptor =
                     contentResolver.openFileDescriptor(imageContentUri, "w", null)) {
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            OutputStream outputStream = new FileOutputStream(fd);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap", e);
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageContentUri);
        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.setType("image/*");
        Intent shareIntent = Intent.createChooser(sendIntent, "Share with");
        mContext.startActivity(shareIntent);
    }

}
