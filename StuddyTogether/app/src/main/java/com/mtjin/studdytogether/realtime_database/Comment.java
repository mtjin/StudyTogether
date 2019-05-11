package com.mtjin.studdytogether.realtime_database;

public class Comment {
    private String nickName;
    private String age;
    private String image;
    private String date;
    private String message;

    public  Comment(){}

    public Comment(String nickName, String age, String image, String date, String message) {
        this.nickName = nickName;
        this.age = age;
        this.image = image;
        this.date = date;
        this.message = message;
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

    public void setImage(String image) {
        this.image = image;
    }
}
