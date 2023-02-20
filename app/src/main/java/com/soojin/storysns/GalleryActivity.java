package com.soojin.storysns;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soojin.storysns.adapter.GalleryAdapter;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = (RecyclerView) findViewById(R.id.gallery_rv);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final int numberOfColums=3;
        recyclerView.setLayoutManager(new GridLayoutManager(this,numberOfColums));
        //어뎁터 연결
       // String[] myDataset={"강아지","고양이","여우","상추"};
        mAdapter=new GalleryAdapter(this,getImagesPath(this));
        recyclerView.setAdapter(mAdapter);

    }



    public  ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;

        //WritePostActivity에서 불러줌.
        Intent intent= getIntent();
        if(intent.getStringExtra("media").equals("video")){
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }
        else{
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }
        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = ((Cursor) cursor).getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}
