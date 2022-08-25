package com.muththamizh.tamily.ui.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muththamizh.tamily.AdmobNative;
import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Quote;
import com.muththamizh.tamily.utils.Constants;
import com.muththamizh.tamily.utils.PrefManager;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuotesWithAdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //for ads integration
    private static final int LIST_AD_DELTA = 3;
    private static final int CONTENT = 0;
    private static final int AD = 1;

    private Context mContext;
    private ArrayList<Quote> quotesList;
    PrefManager prefManager;
    FavItemFavIconTapped favItemFavIconTapped;
    private int ref = -1;
    DownloadLLClicked downloadLLClicked;

    public QuotesWithAdsAdapter(Context mContext, ArrayList<Quote> quotesList, int ref) {
        this.mContext = mContext;
        this.quotesList = quotesList;
        this.ref = ref;
        if (ref == 1){
            favItemFavIconTapped = (FavItemFavIconTapped)mContext;
        }
        downloadLLClicked = (DownloadLLClicked)mContext;
        prefManager = PrefManager.getInstance(mContext);
        Hawk.init(mContext).build();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CONTENT) {
            //content
            return new QuotesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_quote,parent,false));

        } else {
            //ads
            return new AdsHolder(LayoutInflater.from(mContext).inflate(R.layout.native_ads,parent,false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder quotesHolder, int position) {
        if (getItemViewType(position) == CONTENT) {

            QuotesViewHolder holder = (QuotesViewHolder) quotesHolder;

            holder.tvName.setText(quotesList.get(position).getQuote());

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

                        ArrayList<Quote> list =  Hawk.get("quotes_list",new ArrayList<Quote>());
                        if(list==null){
                            list = new ArrayList<>();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getQuote().equals(quotesList.get(position).getQuote())){
                                list.remove(i);
                                i--;
                            }
                        }
                        Hawk.put("quotes_list",list);
                    } else {
                        ArrayList<Quote> qList =  Hawk.get("quotes_list",new ArrayList<Quote>());
                        if (qList != null){
                            qList.add(quotesList.get(position));
                            Hawk.put("quotes_list",qList);
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


            holder.llShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeTapSound();
                    Constants.shareQuote(mContext,quotesList.get(position).getQuote());
                }
            });

            holder.llDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeTapSound();
                    downloadLLClicked.downloadClicked(holder.cardView);
                    // FileUtils.downloadFile(holder.cardView,mContext);
                }
            });

            holder.llCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeTapSound();
                    Constants.copyQuote2ClipBoard(mContext,quotesList.get(position).getQuote());
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

        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.imgFav) ImageView imgFav;
        @BindView(R.id.llDownload) LinearLayout llDownload;
        @BindView(R.id.llCopy) LinearLayout llCopy;
        @BindView(R.id.llShare) LinearLayout llShare;
        @BindView(R.id.cardView) CardView cardView;

        public QuotesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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

}
