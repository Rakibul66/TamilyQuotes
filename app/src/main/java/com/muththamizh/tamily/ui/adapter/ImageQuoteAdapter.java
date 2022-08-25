package com.muththamizh.tamily.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.muththamizh.tamily.R;

public class ImageQuoteAdapter extends FirestoreRecyclerAdapter<ImageQuote, ImageQuoteAdapter.ImageHolder> {

    private Context mContext;
    private FirebaseFirestore firebaseFirestore;
    StorageReference mStorageRef;

    public ImageQuoteAdapter(@NonNull FirestoreRecyclerOptions<ImageQuote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageHolder holder, int position, @NonNull ImageQuote model) {

        final String fileReference = getItem(position).getQuoteId();

        Glide.with(mContext).load(model.getQuote()).into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("image_quote").document(fileReference).delete();
                mStorageRef.child(fileReference).delete();
                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_quote_layout,
                parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("image_quote");

        return new ImageHolder(view);
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout delete;

        public ImageHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);

            mContext = itemView.getContext();

        }
    }
}