package com.mtjin.studdytogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtjin.studdytogether.realtime_database.Profile;

import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    static final String TAG = "ProfileActivityTAG";
    private EditText mNickNameEditText;
    private Spinner mSexSpinner;
    private Spinner mAgeSpinner;
    private Button mOkButton;
    private CircleImageView mPhotoCircleImageView;
    private String mUid; //사용자 토큰 고유 아이디
    private String mNickName; //닉네임
    private String mSex; //성별 남자 or 여자
    private String mEmail; //이메일
    private String mAge; //나이대
    private String mImage; //프로필사진
    private FirebaseAuth mFirebaseAuth; //인증객체
    private FirebaseUser mFirebaseUser; //인증이 되면 이객체를 얻을 수 있다. (인증된 유저받아올 수 있음)

    //RequestCode
    final static int PICK_IMAGE = 1;

    Profile profile; //데이터베이스에 저장 할 객체
    ArrayList<String> sexArrayList; //스피너 리스트
    ArrayList<String> ageArrayList; //스피너리스트
    ArrayAdapter<String> sexAdapter; //스피너 어댑터
    ArrayAdapter<String> ageAdapter;
    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 위치한곳
    DatabaseReference mProfieDatabaseReference = mRootDatabaseReference.child("profile"); //profile이란 이름의 하위 데이터베이스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNickNameEditText = findViewById(R.id.profile_pt_nickname);
        mSexSpinner = findViewById(R.id.profile_sp_sex);
        mAgeSpinner = findViewById(R.id.profile_sp_age);
        mOkButton = findViewById(R.id.profile_btn_ok);
        mPhotoCircleImageView = findViewById(R.id.profile_iv_photo);
        mPhotoCircleImageView.setClickable(true);

        //스피너 설정
        spinnerDo();

        //프로필이미지 클릭 시
        mPhotoCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override //이미지 불러오기기
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        DatabaseReference databaseReference = mProfieDatabaseReference;
        Log.d("DDDDDD", databaseReference.toString() + "");


    }

    @Override //갤러리에서 이미지 불러온 후 행동
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_IMAGE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    mPhotoCircleImageView.setImageBitmap(img);
                    mImage = data.getData() + "";//사용할려면 uri.parse함수 사용해야함
                    Log.d("ProfileActivityTAG", data.getData() + "");
                    Log.d("ProfileActivityTAG", in + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //파이어베이스 인증
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseAuth.getUid();   //사용자 고유 토큰 받아옴
        mEmail = mFirebaseUser.getEmail(); //가입한 이메일 받아옴
        Log.d("PROFILE22", mEmail);



    /*    ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Boolean a = dataSnapshot.exists();
                Log.d("AAAAA", a +"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mProfieDatabaseReference.child("SSS").addValueEventListener(postListener);*/


        //ok버튼 클릭시
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSex는 스피너리스너에서 저장해놈놈
                mNickName = mNickNameEditText.getText().toString().trim();
                if (mNickName.length() > 5 || mNickName.length() <= 0 || (DataValidation.checkOnlyCharacters(mNickName) == false)) { //닉네임 1자 이하 5자 이상으로 한 경우, 또는 특수문자
                    Toast.makeText(ProfileActivity.this, "닉네임은 1~5글자 이하이고 특수문자를 쓰면 안됩니다.", Toast.LENGTH_SHORT).show();
                } else if (mNickName != null && mSex != null) { //제대로 작성한 경우
                    //값 데이터베이스에서 넣어줌
                    profile = new Profile(mEmail, mNickName, mSex, mAge, mImage);
                    //닉네임을 루트로 사용자 정보 저장
                    mProfieDatabaseReference.child(mUid).setValue(profile);
                    //SharedPReference에도 저장해줌 (쉽게 갖다쓰기위해)
                    saveProfileSharedPreferences(profile);
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                } else { //공백을 입력한 경우
                    Toast.makeText(ProfileActivity.this, "공백이 있으면 안됩니다", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //로컬에 프로필정보 저장 (확인 버튼 클릭시 호출)
    public void saveProfileSharedPreferences(Profile profile) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", profile.getEmail());
        editor.putString("nickName", profile.getNickName());
        editor.putString("sex", profile.getSex());
        editor.putString("age", profile.getAge());
        editor.putString("image", profile.getImage());

        Log.d(TAG, profile.getEmail());
        Log.d(TAG, profile.getNickName());
        Log.d(TAG, profile.getSex());
        Log.d(TAG, profile.getAge());
        Log.d(TAG, profile.getImage());
        editor.commit();
    }


    /*
     *   스피너 관련 메소드
     * */
    public void spinnerDo() {
        //스피너에 넣을 arrayList 데이터 (성별, 나이)
        sexArrayList = new ArrayList<String>();
        sexArrayList.add("남자");
        sexArrayList.add("여자");
        ageArrayList = new ArrayList<String>();
        ageArrayList.add("10대");
        ageArrayList.add("20대");
        ageArrayList.add("30대");
        ageArrayList.add("40대");
        ageArrayList.add("50대");
        ageArrayList.add("60대");
        ageArrayList.add("70대");
        ageArrayList.add("80대");
        ageArrayList.add("90대");

        //스피너 선택시 리스너
        mSexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSex = sexArrayList.get(position);
                Toast.makeText(getApplicationContext(), "(" + sexArrayList.get(position) + ") 가 선택되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAge = ageArrayList.get(position);
                Toast.makeText(getApplicationContext(), "(" + ageArrayList.get(position) + ") 가 선택되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //스피너어댑터 설정
        sexAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, sexArrayList);
        mSexSpinner.setAdapter(sexAdapter);
        ageAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, ageArrayList);
        mAgeSpinner.setAdapter(ageAdapter);
    }

 /*   public Boolean isExistEmail(String email){
        Query query = mProfieDatabaseReference; //쿼리문의 수행위치 저장 (파이어베이스 리얼타임데이터베이스의 하위에있는 MESSAGES_CHILD에서 데이터를 가져오겠다는 뜻이다. ==> 메세지를 여기다 저장했으므로)

    }

    public  Boolean isExistNickName(String nickName){

    }*/
}
