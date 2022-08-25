package com.muththamizh.tamily.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.pojo.Category;
import com.muththamizh.tamily.pojo.Trending;

import java.util.ArrayList;

public class TrendingAdapter extends BaseAdapter {

    Context context;
    ArrayList<Category> trendingList;

    public TrendingAdapter(Context context, ArrayList<Category> trendingList) {
        this.context = context;
        this.trendingList = trendingList;
    }

    @Override
    public int getCount() {
        return trendingList != null ? trendingList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_trending,viewGroup,false);

        TextView tvCatName = rootView.findViewById(R.id.tvNameCategory);
        //tvCatName.setTextColor(Color.parseColor("#000000"));


        tvCatName.setText(trendingList.get(i).getCategoryName());

        return rootView;
    }
}
