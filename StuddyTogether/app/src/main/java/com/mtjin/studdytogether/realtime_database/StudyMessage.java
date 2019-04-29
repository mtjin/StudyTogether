package com.mtjin.studdytogether.realtime_database;

public class StudyMessage {
    private String title; //글제목
    private String nickName; //닉네임
    private String content; //내용
    private String image; //사용자이미지
    private String photo; // 포스팅하는 사진
    private String age; //나이

    public StudyMessage(){

    }

    public StudyMessage(String title, String nickName, String content, String image, String photo, String age) {
        this.title = title;
        this.nickName = nickName;
        this.content = content;
        this.image = image;
        this.photo = photo;
        this.age =age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}


