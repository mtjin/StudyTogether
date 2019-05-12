package com.mtjin.studdytogether.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.cities_view.SeoulActivity;

public class MainActivity extends AppCompatActivity {
    //지역버튼
    Button seoulButton;
    Button gyeonggiButton;
    Button incheonButton;
    Button gangwonButton;
    Button chungnamButton;
    Button daegeonButton;
    Button chungbukButton;
    Button sejongButton;
    Button busanButton;
    Button ulsanButton;
    Button daeguButton;
    Button kyungbukButton;
    Button kyungnamButton;
    Button jeonnamButton;
    Button gwangjuButton;
    Button jeonbukButton;
    Button jejuButton;
    Button allcityButton;

    //프로필정보
    private String mEmail;
    private String nickName;
    private String sex;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seoulButton = findViewById(R.id.main_btn_seoul);
        gyeonggiButton = findViewById(R.id.main_btn_gyeonggi);
        incheonButton = findViewById(R.id.main_btn_incheon);
        gangwonButton = findViewById(R.id.main_btn_gangwon);
        chungnamButton = findViewById(R.id.main_btn_chungnam);
        daegeonButton = findViewById(R.id.main_btn_daegeon);
        chungbukButton = findViewById(R.id.main_btn_chungbuk);
        sejongButton = findViewById(R.id.main_btn_sejong);
        busanButton = findViewById(R.id.main_btn_busan);
        ulsanButton = findViewById(R.id.main_btn_ulsan);
        daeguButton = findViewById(R.id.main_btn_daegu);
        kyungbukButton = findViewById(R.id.main_btn_kyungbuk);
        kyungnamButton = findViewById(R.id.main_btn_kyungnam);
        jeonnamButton = findViewById(R.id.main_btn_jeonnam);
        gwangjuButton = findViewById(R.id.main_btn_gwangju);
        jeonbukButton = findViewById(R.id.main_btn_jeonbuk);
        jejuButton = findViewById(R.id.main_btn_jeju);
        allcityButton = findViewById(R.id.main_btn_allcity);

        //버튼클릭
        onClickButton();

/*
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

    public void onClickButton() {
        seoulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SeoulActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        restoreState();
    }

    public void restoreState() {
        /*SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        if ((pref != null) && (pref.contains("email")) && (pref.contains("nickName")) && (pref.contains("sex"))) {
            mEmail = pref.getString("email", "");
            nickName = pref.getString("nickName", "");
            sex = pref.getString("sex", "");
        }*/

      /*  SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", profile.getEmail());
        editor.putString("nickName", profile.getNickName());
        editor.putString("sex", profile.getSex());
        editor.putString("image", image);
        editor.commit();*/
    }
}
