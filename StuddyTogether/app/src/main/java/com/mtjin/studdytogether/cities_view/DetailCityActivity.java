package com.mtjin.studdytogether.cities_view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.view.PhotoZoomActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailCityActivity extends AppCompatActivity {
    private TextView mTitleTextView;//제목
    private TextView mNickNameTextView; //닉네임
    private TextView mAgeTextView; //나이
    private ImageView mMessageImageView; //게시물업로드사진
    private TextView mMessageTextView; //게시물 내용
    private CircleImageView mPhotoImageView; //내프로필사진
    private TextView mDatesTextView; //날짜
    private TextView mCommentTextView; //댓글

    //게시물id, 도시
    private String mId;
    private String mCity;
    //게시물
    private String mNickName;
    private String mAge; //나이대
    private String mImage; //프로필사진
    private String mTitle; //제목
    private String mContent; //내용
    private String mMessagePhoto; //게시물에 업로드한 사진
    private String mDate; //날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_city);
        mTitleTextView = findViewById(R.id.detail_tv_title);
        mNickNameTextView = findViewById(R.id.detail_tv_name);
        mAgeTextView = findViewById(R.id.detail_tv_age);
        mMessageImageView = findViewById(R.id.detail_iv_photo);
        mPhotoImageView = findViewById(R.id.detail_iv_profile);
        mDatesTextView = findViewById(R.id.detail_tv_date);
        mMessageTextView = findViewById(R.id.detail_tv_message);
        mCommentTextView = findViewById(R.id.detail_tv_comment);

        //인텐트처리
        processIntent();
        //프로필사진클릭시 줌인줌아웃가능
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCityActivity.this , PhotoZoomActivity.class);
                intent.putExtra("zoomProfilePhoto", mImage);
                startActivity(intent);
            }
        });
        //게시물사진 클릭시 줌인줌아웃가능
        mMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCityActivity.this , PhotoZoomActivity.class);
                intent.putExtra("zoomMessagePhoto", mMessagePhoto);
                startActivity(intent);
            }
        });
    }

    public void processIntent(){

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle.getString("id") != null) {
            mId = bundle.getString("id");
        }
        if(bundle.getString("city") != null){
            mCity = bundle.getString("city");
        }
        //값 get
        mTitle= bundle.getString("messageTitle");
        mDate = bundle.getString("messageDate");
        mNickName =  bundle.getString("messageNIckName");
        mAge= bundle.getString("messageAge");
        mMessagePhoto = bundle.getString("messagePhoto"); //업로드하는사진
        mImage=  bundle.getString("messageImage"); //내 프로필사진
        mContent = bundle.getString("messageContent"); //내용

        //뷰 set
        mTitleTextView.setText(mTitle);
        mNickNameTextView.setText(mNickName);
        mAgeTextView.setText(mAge);
        mMessageTextView.setText(mContent);
        mDatesTextView.setText(mDate);
        if (mImage.equals("basic")) { //프로필사진이 없는경우
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(mPhotoImageView);
        } else {
            Glide.with(this).load(mImage).into(mPhotoImageView);
        }
        if (mMessagePhoto != "basic") {
            Glide.with(this).load(mMessagePhoto).into(mMessageImageView);
        } else {
            //사진첨부안했으니 안올림
        }
    }
}
