package com.example.connecthost;

import android.graphics.Bitmap;

public class Contact {
    private String id;
    private String pic_filename;
    private Bitmap pic;
    private String name;
    private String email;
    private String phoneNum;
    private String birthday;

    public Contact(String id, String pic_filename, Bitmap pic, String name, String email, String phoneNum, String birthday) {
        this.id = id;
        this.pic_filename = pic_filename;
        this.pic = pic;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
    }

    public String getPic_filename() {
        return pic_filename;
    }

    public void setPic_filename(String pic_filename) {
        this.pic_filename = pic_filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
