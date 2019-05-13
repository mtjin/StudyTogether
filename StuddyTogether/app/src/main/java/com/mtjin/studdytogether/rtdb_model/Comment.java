package com.mtjin.studdytogether.rtdb_model;

public class Comment {
    private String nickName;
    private String age;
    private String image;
    private String date;
    private String message;
    private String uid;

    public  Comment(){}

    public Comment(String nickName, String age, String image, String date, String message, String uid) {
        this.nickName = nickName;
        this.age = age;
        this.image = image;
        this.date = date;
        this.message = message;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImage(String image) {

        this.image = image;
    }
}
