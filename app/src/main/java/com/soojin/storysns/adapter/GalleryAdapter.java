package com.soojin.storysns.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.soojin.storysns.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView=v;
        }
    }
    public GalleryAdapter(Activity activity,ArrayList<String> myDataset) {
        this.activity = activity;
        mDataset = myDataset;

    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //갤러리 레이아웃만들어서
        CardView cardView=(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery,parent,false);
        final GalleryViewHolder galleryViewHolder=new GalleryViewHolder(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent=new Intent();
                resultIntent.putExtra("profilePath",mDataset.get(galleryViewHolder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
                Log.e("testonBinding","testOnBinding");

            }
        });

       return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        CardView cardView=holder.cardView;


        ImageView imageView=cardView.findViewById(R.id.gallery_iv);
//        Bitmap bmp = BitmapFactory.decodeFile(mDataset.get(position));
//        imageView.setImageBitmap(bmp);
        Glide.with(activity)
                .load(mDataset.get(position))
                .centerCrop()
                .override(500)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
