package com.mtjin.studdytogether.realtime_database;

public class Comment {
    private String nickName;
    private String age;
    private String image;

    public  Comment(){}

    public Comment(String nickName, String age, String image) {
        this.nickName = nickName;
        this.age = age;
        this.image = image;
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
