package com.example.joon.kcec.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.joon.kcec.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgUrls){
        super(context, layoutResource, imgUrls);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgUrls;

    }

    public static class Viewholder  {
        SquareImageView iamge;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Viewholder viewholder;

        if(convertView==null){
            convertView= mInflater.inflate(layoutResource, parent, false);
            viewholder = new Viewholder();
            viewholder.iamge= convertView.findViewById(R.id.each_grid_image);
            viewholder.mProgressBar = convertView.findViewById(R.id.grid_imageview_progressBar);
            convertView.setTag(viewholder);
        } else{
            viewholder= (Viewholder)convertView.getTag();
        }

        String imgUrl = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgUrl, viewholder.iamge, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(viewholder.mProgressBar!=null){
                    viewholder.mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(viewholder.mProgressBar!=null){
                    viewholder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(viewholder.mProgressBar!=null){
                    viewholder.mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(viewholder.mProgressBar!=null){
                    viewholder.mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        return  convertView;
    }
}
