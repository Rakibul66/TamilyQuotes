package com.muththamizh.tamily.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Category;
import com.muththamizh.tamily.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>{

    private Context mContext;
    private ArrayList<Category> categoriesList;
    OnCategoryItemClicked listener;

    public CategoriesAdapter(Context mContext, ArrayList<Category> categoriesList,OnCategoryItemClicked itemClicked) {
        this.mContext = mContext;
        this.categoriesList = categoriesList;
        this.listener = itemClicked;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        if (position >= 10){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext,Constants.getColorsList(position % 10)));
        }else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext,Constants.getColorsList(position)));
        }
        holder.tvNameCategory.setText(categoriesList.get(position).getCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(categoriesList.get(position),position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoriesList.size();
    }


    public class CategoriesViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.cvParent) CardView cardView;
        @BindView(R.id.tvNameCategory) TextView tvNameCategory;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public interface OnCategoryItemClicked{

        void onItemClicked(Category category,int pos);

    }

}

