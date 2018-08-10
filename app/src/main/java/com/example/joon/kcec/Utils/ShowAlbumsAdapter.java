package com.example.joon.kcec.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joon.kcec.R;

import java.util.ArrayList;
import java.util.List;

public class ShowAlbumsAdapter extends RecyclerView.Adapter<ShowAlbumsAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClicked(int position);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    private Context mContext;
    private List<String> mAlbum_names;

    public ShowAlbumsAdapter(Context context, ArrayList<String> album_names){
        mContext = context;
        mAlbum_names = album_names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_each_album, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mAlbum_name.setText(mAlbum_names.get(position));
    }

    @Override
    public int getItemCount() {
        return mAlbum_names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        /**
         * widgets
         * @param itemView
         */
        private View mView;
        private ImageView mAlbum;
        private TextView mAlbum_name;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mView = itemView;
            mAlbum = mView.findViewById(R.id.each_album);
            mAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position= getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClicked(position);
                        }
                    }
                }
            });
            mAlbum_name = mView.findViewById(R.id.each_album_name);

        }
    }
}
