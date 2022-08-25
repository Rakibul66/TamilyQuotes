package com.muththamizh.tamily.ui.activities.admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Quote;
import com.muththamizh.tamily.utils.PrefManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuoteAdapterAdmin extends RecyclerView.Adapter<QuoteAdapterAdmin.QuotesVH> {

    PrefManager prefManager;
    private Context mContext;
    private ArrayList<Quote> quotesList;
    QuoteCardCallbacks quoteCardCallbacks;

    public QuoteAdapterAdmin(Context mContext, ArrayList<Quote> quotesList) {
        this.mContext = mContext;
        this.quotesList = quotesList;
        quoteCardCallbacks = (QuoteCardCallbacks)mContext;
    }

    @NonNull
    @Override
    public QuoteAdapterAdmin.QuotesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuoteAdapterAdmin.QuotesVH(LayoutInflater.from(mContext).inflate(R.layout.item_quote_admin, parent, false));
    }
 
    @Override
    public void onBindViewHolder(@NonNull QuoteAdapterAdmin.QuotesVH holder, int position) {
        holder.tvName.setText(quotesList.get(position).getQuote());

        Log.d("quoteId:>>>",quotesList.get(position).getQuoteId());

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quoteCardCallbacks.onQuoteDeleteClicked(quotesList.get(position),position);
            }
        });

        holder.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quoteCardCallbacks.onQuoteEditClicked(quotesList.get(position),position);
            }
        });




    }




    @Override
    public int getItemCount() {
        return quotesList.size();
    }



    public class QuotesVH extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.llDelete) LinearLayout llDelete;
        @BindView(R.id.llEdit) LinearLayout llEdit;

        public QuotesVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface QuoteCardCallbacks{

        void onQuoteEditClicked(Quote quote,int pos);

        void onQuoteDeleteClicked(Quote quote,int pos);

    }

}
