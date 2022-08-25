package com.muththamizh.tamily.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Quote;
import com.muththamizh.tamily.utils.Constants;
import com.muththamizh.tamily.utils.PrefManager;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuotesViewHolder>{

    private Context mContext;
    private ArrayList<Quote> quotesList;
    PrefManager prefManager;
    FavItemFavIconTapped favItemFavIconTapped;
    private int ref = -1;
    DownloadLLClicked downloadLLClicked;

    public QuotesAdapter(Context mContext, ArrayList<Quote> quotesList,int ref) {
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
    public QuotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuotesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_quote,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuotesViewHolder holder, int position) {
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
    }



    private void makeTapSound(){
        if (prefManager.getBoolean(PrefManager.SOUND_OFF_ON)){
            Constants.playTone(mContext,R.raw.tap);
        }
    }

    @Override
    public int getItemCount() {
        return quotesList.size() != 0 ? quotesList.size():0;
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

}
