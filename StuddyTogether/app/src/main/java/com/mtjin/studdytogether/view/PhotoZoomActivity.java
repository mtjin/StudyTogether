package com.mtjin.studdytogether.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.mtjin.studdytogether.R;

public class PhotoZoomActivity extends AppCompatActivity {

    private PhotoView mPhotoView;
    private String mPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_zoom);

        mPhotoView = findViewById(R.id.photoView);
        processIntent(); //인텐트처리
    }

    public void processIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra("zoomProfilePhoto")){ //프로필사진인경우
            mPhoto= intent.getExtras().getString("zoomProfilePhoto");
            if (mPhoto.equals("basic")) { //프로필사진이 없는경우
                Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(mPhotoView);
            } else {
                Glide.with(this).load(mPhoto).into(mPhotoView);
            }
        }else if(intent.hasExtra("zoomMessagePhoto")){ //게시물사진인 경우
            mPhoto = intent.getExtras().getString("zoomMessagePhoto");
            if (mPhoto != "basic") {
                Glide.with(this).load(mPhoto).into(mPhotoView);
            } else {
                //사진첨부안했으니 안올림
            }
        }
    }
}
