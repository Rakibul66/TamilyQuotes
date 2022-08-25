package com.muththamizh.tamily.ui.activities.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class CatAdapterAdmin extends RecyclerView.Adapter<CatAdapterAdmin.CatViewHolder> {

    private Context mContext;
    private ArrayList<Category> categoriesList;
    CatAdapterAdmin.OnCatItemsClicked listener;

    public CatAdapterAdmin(Context mContext, ArrayList<Category> categoriesList, CatAdapterAdmin.OnCatItemsClicked itemClicked) {
        this.mContext = mContext;
        this.categoriesList = categoriesList;
        this.listener = itemClicked;
    }

    @NonNull
    @Override
    public CatAdapterAdmin.CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CatAdapterAdmin.CatViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cats_admin, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull CatAdapterAdmin.CatViewHolder holder, int position) {
        if (position >= 10) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, Constants.getColorsList(position % 10)));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, Constants.getColorsList(position)));
        }
        holder.tvNameCategory.setText(categoriesList.get(position).getCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    listener.onItemClicked(categoriesList.get(position), position);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        holder.editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditItemClicked(categoriesList.get(position),position);
            }
        });

        holder.deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteItemClicked(categoriesList.get(position),position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoriesList.size();
    }


    public class CatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cvParent) CardView cardView;
        @BindView(R.id.tvNameCategory) TextView tvNameCategory;
        @BindView(R.id.editCategory) ImageView editCategory;
        @BindView(R.id.deleteCategory) ImageView deleteCategory;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface OnCatItemsClicked {

        void onItemClicked(Category category, int pos);
        void onDeleteItemClicked(Category category,int pos);
        void onEditItemClicked(Category category,int pos);

    }
}