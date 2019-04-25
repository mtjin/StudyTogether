package com.mtjin.studdytogether.realtime_database;

public class Profile {
    private String email;
    private String nickName;
    private String sex;

    public Profile(String email, String nickName, String sex) {
        this.email = email;
        this.nickName = nickName;
        this.sex = sex;
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

    @Override
    public String toString() {
        return email;
    }


}
