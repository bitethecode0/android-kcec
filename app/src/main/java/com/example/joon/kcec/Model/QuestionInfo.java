package com.example.joon.kcec.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionInfo implements Parcelable {
    private String user_id;
    private String posted_date;
    private String question_topic;
    private String question_description;

    public QuestionInfo() {
    }

    public QuestionInfo(String user_id, String posted_date, String question_topic, String question_description) {
        this.user_id = user_id;
        this.posted_date = posted_date;
        this.question_topic = question_topic;
        this.question_description = question_description;
    }

    protected QuestionInfo(Parcel in) {
        user_id = in.readString();
        posted_date = in.readString();
        question_topic = in.readString();
        question_description = in.readString();
    }


    public static final Creator<QuestionInfo> CREATOR = new Creator<QuestionInfo>() {
        @Override
        public QuestionInfo createFromParcel(Parcel in) {
            return new QuestionInfo(in);
        }

        @Override
        public QuestionInfo[] newArray(int size) {
            return new QuestionInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(posted_date);
        dest.writeString(question_topic);
        dest.writeString(question_description);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPosted_date() {
        return posted_date;
    }

    public void setPosted_date(String posted_date) {
        this.posted_date = posted_date;
    }

    public String getQuestion_topic() {
        return question_topic;
    }

    public void setQuestion_topic(String question_topic) {
        this.question_topic = question_topic;
    }

    public String getQuestion_description() {
        return question_description;
    }

    public void setQuestion_description(String question_description) {
        this.question_description = question_description;
    }

    @Override
    public String toString() {
        return super.toString();
    }


}
