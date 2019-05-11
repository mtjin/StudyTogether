package com.mtjin.studdytogether.realtime_database;

public class Profile {
    private String email;
    private String nickName;
    private String sex;
    private String age;
    private String image;

    public Profile(String email, String nickName, String sex, String age, String image) {
        this.email = email;
        this.nickName = nickName;
        this.sex = sex;
        this.age = age;
        if(image == null || image.equals("")) { //만약 이미지를 설정하면 basic이란 문자열을 갖게한다.
         this.image = "basic";
        }else {
            this.image = image;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    @Override
    public String toString() {
        return email;
    }


}
