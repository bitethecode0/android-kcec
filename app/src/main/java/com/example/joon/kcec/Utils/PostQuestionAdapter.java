package com.example.joon.kcec.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.joon.kcec.Model.QuestionInfo;
import com.example.joon.kcec.QuestionAnswer.QandAActivity;
import com.example.joon.kcec.QuestionAnswer.QuestionViewFragment;
import com.example.joon.kcec.R;

import java.util.ArrayList;
import java.util.List;

public class PostQuestionAdapter extends RecyclerView.Adapter<PostQuestionAdapter.Viewholder> {
    private static final String TAG = "PostQuestionAdapter";

    /**
     * interface
     */
    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener{
        void onItemClicked(int position, QuestionInfo questionInfo);
    }

    public void setOnClickListner(OnItemClickListener listner){
        mItemClickListener = listner;
    }

    private Context mContext;
    private List<QuestionInfo> questionInfo;

    public PostQuestionAdapter(Context context, ArrayList<QuestionInfo> questionInfo){
        mContext = context;
        this.questionInfo = questionInfo;

    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_question,parent, false);
        return new Viewholder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.mQuestionTopic.setText(questionInfo.get(position).getQuestion_topic());
        holder.mPostedDate.setText(questionInfo.get(position).getPosted_date());
        holder.mUsername.setText(questionInfo.get(position).getUser_id());
    }

    @Override
    public int getItemCount() {
        return questionInfo.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        View mView;
        TextView mQuestionTopic;
        TextView mUsername, mPostedDate, mLikeTv, mCommentTv;
        ImageView mProfileImage, mLike, mComment;

        public Viewholder(View itemView, final OnItemClickListener listener ) {
            super(itemView);
            mView = itemView;
            mLikeTv = mView.findViewById(R.id.like_tv);
            mCommentTv = mView.findViewById(R.id.comment_tv);
            mLike = mView.findViewById(R.id.heart_image);
            mComment = mView.findViewById(R.id.comment_image);
            mUsername = mView.findViewById(R.id.username_qanda);
            mPostedDate = mView.findViewById(R.id.date_qanda);
            mQuestionTopic = mView.findViewById(R.id.text_qanda);

            mProfileImage = mView.findViewById(R.id.profile_image_qanda);

            mQuestionTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position= getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClicked(position, questionInfo.get(position));
                        }
                    }
                }
            });
        }
    }
}
