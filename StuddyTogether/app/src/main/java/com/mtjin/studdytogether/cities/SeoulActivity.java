package com.mtjin.studdytogether.cities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.WriteActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class SeoulActivity extends AppCompatActivity {
    static  final String TAG = "SeoulActivityTAG";
    static  final int WRITE = 100;
    //자신 프로필
    private String mNickName;
    private String mSex; //성별 남자 or 여자
    private String mEmail; //이메일
    private String mAge; //나이대
    private String mImage; //프로필사진

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView nickNameTextView;
        TextView ageTextView;
        ImageView messageImageView;
        TextView messageTextView;
        CircleImageView photoImageView;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            nickNameTextView = itemView.findViewById(R.id.message_tv_name);
            ageTextView = itemView.findViewById(R.id.message_tv_age);
            messageImageView = itemView.findViewById(R.id.message_iv_photo); //업로드한사진
            messageTextView = itemView.findViewById(R.id.message_tv_message);
            photoImageView = itemView.findViewById(R.id.message_iv_profile); //내 프로필사진
        }
    }

    private RecyclerView mMessageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seoul);

        mMessageRecyclerView = findViewById(R.id.seoul_rev_message); //채팅메세지들 리사이클러뷰

        findViewById(R.id.seoul_tv_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeoulActivity.this, WriteActivity.class);
                startActivityForResult(intent, WRITE);
            }
        });

       /* ImageView imageView = findViewById(R.id.imageView);
        // 선택한 이미지에서 비트맵 생성
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(Uri.parse("content://com.android.providers.downloads.documents/document/59"));
            Bitmap img = BitmapFactory.decodeStream(in);
            Log.d("ProfileTAG", in+"");
            in.close();

            // 이미지 표시
            imageView.setImageBitmap(img);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //작성하기 결과
        if (requestCode == WRITE) {
            if(resultCode == RESULT_OK) {

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //프로필 정보받아옴
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        mEmail = pref.getString("email","");
        mNickName = pref.getString("nickName", "");
        mSex = pref.getString("sex","");
        mAge = pref.getString("age","");
        mImage = pref.getString("image","");
    }
}
