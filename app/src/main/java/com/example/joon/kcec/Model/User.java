package com.example.joon.kcec.Model;

public class User {

//    private String fullname;
    private String email;
    private long phone_number;
    private String user_id;
    private String username;


    public User() {
    }

    public User(String email, long phone_number, String user_id, String username) {
//        this.fullname = fullname;
        this.email = email;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.username = username;
    }

//    public String getFullname() {
//        return fullname;
//    }
//
//    public void setFullname(String fullname) {
//        this.fullname = fullname;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
