package com.mtjin.studdytogether;

public class studyMessage {
    private String title; //글제목
    private String nickName; //닉네임
    private String content; //내용
    private String image; //사용자이미지
    private String photo; // 포스팅하는 사진

    public studyMessage(String title, String nickName, String content, String image, String photo) {
        this.title = title;
        this.nickName = nickName;
        this.content = content;
        this.image = image;
        this.photo = photo;
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
}


