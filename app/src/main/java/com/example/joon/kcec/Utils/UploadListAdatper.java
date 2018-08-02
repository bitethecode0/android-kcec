package com.example.joon.kcec.Utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.joon.kcec.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UploadListAdatper extends RecyclerView.Adapter<UploadListAdatper.Viewholder> {
    private static final String TAG = "UploadListAdatper";

    public List<String> filenameList;
    public static List<String> fileUploadedList;
    public static List<String> imgfileUrls;


    public UploadListAdatper(List<String> filenameList, List<String> fileUploadedList
            , List<String> imgfileUrls){
        this.filenameList = filenameList;
        UploadListAdatper.fileUploadedList = fileUploadedList;
        UploadListAdatper.imgfileUrls = imgfileUrls;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {
        Log.d(TAG, "onBindViewHolder: each item will be loaded here.");
        String filename = filenameList.get(position);
//        holder.filenameView.setText(filename);

        String url = imgfileUrls.get(position);

        Log.d(TAG, "onBindViewHolder: image url : "+url);

        Log.d(TAG, "setImage: setup images.");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url, holder.postImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }
        });


        //didn't work in this way
//        Bitmap convertedBm = getBitmapFromUrl(imgfileUrls.get(position));
//        Log.d(TAG, "onBindViewHolder: bitmap converted : "+convertedBm);


        //before change
//        new AsynTaskLoadImage(holder.postImage).execute(url);
        
        
//
//        if(fileUploadedList.equals("uploading")){
//
//
//
//        } else {
//
//            holder.fileUploaedView.setImageResource(R.drawable.ic_after_upload);
//
//        }


    }



    @Override
    public int getItemCount() {
        return filenameList.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        View mView;
        public ImageView postImage;
        public ProgressBar mProgressBar;
//        public TextView filenameView;
//        public ImageView fileUploaedView;

        public Viewholder(View itemView) {
            super(itemView);
            mView = itemView;

            postImage = mView.findViewById(R.id.uploaded_image);
            mProgressBar = mView.findViewById(R.id.progressBar);
//            filenameView = mView.findViewById(R.id.file_description);
//            fileUploaedView = mView.findViewById(R.id.upload_checkmark);


        }
    }

//  /*  public static Bitmap getBitmapFromUrl(String src){
//        try{
//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            return BitmapFactory.decodeStream((InputStream)url.getContent());
//
//
//
//        } catch (IOException e){
//            Log.e(TAG, "getBitmapFromUrl: IOException "+e.getMessage() );
//            return null;
//
//        } catch (Exception e){
//            Log.e(TAG, "getBitmapFromUrl: Exception"+e.getMessage() );
//            return null;
//        }
//    }*/

    public class AsynTaskLoadImage extends AsyncTask<String, Void, Bitmap>{
        private static final String TAG = "AsynTaskLoadImage";

        private ImageView imageView;

        public AsynTaskLoadImage(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bm =null;
            try{
                URL url = new URL(strings[0]);
                bm = BitmapFactory.decodeStream((InputStream)url.getContent());

            } catch (IOException e) {
                Log.e(TAG, "doInBackground: IOException" + e.getMessage() );
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
