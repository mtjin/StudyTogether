package com.mtjin.studdytogether;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mtjin.studdytogether.realtime_database.Profile;

import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private Uri mDownloadImageUri; //프로필사진 스토리지 URI
    private Bitmap img; //비트맵 프로필사진
    private FirebaseAuth mFirebaseAuth; //인증객체
    private FirebaseUser mFirebaseUser; //인증이 되면 이객체를 얻을 수 있다. (인증된 유저받아올 수 있음)
    private StorageReference mStorageRef; //파이어베이스 스토리지
    private StorageReference mProfileRef; //프로필이미지 담을 파베 스토리지

    private String mCurrentPhotoPath; //카메라로 찍은 사진 저장할 루트경로

    //RequestCode
    final static int PICK_IMAGE = 1;

    //로딩 프로그래스 다이얼로그
     ProgressDialog progressDialog;

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

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();

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
                photoDialogRadio(); //갤러리에서 불러오기 or 사진찍어서 불러오기
            }
        });

        DatabaseReference databaseReference = mProfieDatabaseReference;
        Log.d(TAG, databaseReference.toString() + "");

    }

    @Override //갤러리에서 이미지 불러온 후 행동
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                img = BitmapFactory.decodeStream(in);
                in.close();
                // 이미지 표시
                mPhotoCircleImageView.setImageBitmap(img);
                mImage = data.getData() + "";//사용할려면 uri.parse함수 사용해야함
                Log.d(TAG, data.getData() + "");
                Log.d(TAG, img+ "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void basicQueryValueListener() {
       /* String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");

        // [START basic_query_value_listener]
        // My top posts by number of stars
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        // [END basic_query_value_listener]*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //파이어베이스 인증
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseAuth.getUid();   //사용자 고유 토큰 받아옴
        mEmail = mFirebaseUser.getEmail(); //가입한 이메일 받아옴
        mProfileRef = mStorageRef.child(mUid + "profileImage"); //프로필 스토리지 저장이름은 사용자 고유토큰과 스트링섞어서 만든다.
        Log.d("PROFILE22", mEmail);

        //ok버튼 클릭시
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSex는 스피너리스너에서 저장해놈놈
                mNickName = mNickNameEditText.getText().toString().trim();
                if (mNickName.length() > 7 || mNickName.length() <= 0 || (DataValidation.checkOnlyCharacters(mNickName) == false)) { //닉네임 1자 이하 5자 이상으로 한 경우, 또는 특수문자
                    Toast.makeText(ProfileActivity.this, "닉네임은 1~7글자 이하이고 특수문자를 쓰면 안됩니다.", Toast.LENGTH_SHORT).show();
                } else if (mNickName != null && mSex != null) { //제대로 작성한 경우
                    loading();
                    if (img != null) {
                        //파이어베이스 스토리지에 업로드
                        Toast.makeText(ProfileActivity.this, "업로드중입니다. 잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] datas = baos.toByteArray();
                        UploadTask uploadTask = mProfileRef.putBytes(datas);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return mProfileRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    mDownloadImageUri = task.getResult();
                                    Log.d(TAG + "DOWN", mDownloadImageUri + "");
                                    //값 데이터베이스에서 넣어줌
                                    profile = new Profile(mEmail, mNickName, mSex, mAge, mDownloadImageUri + "");
                                    //닉네임을 루트로 사용자 정보 저장
                                    mProfieDatabaseReference.child(mUid).setValue(profile);
                                    //SharedPReference에도 저장해줌 (쉽게 갖다쓰기위해)
                                    saveProfileSharedPreferences(profile);
                                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    progressDialog.dismiss(); //로딩종료
                                } else {
                                    // Handle failures
                                    Toast.makeText(ProfileActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else { //프로필이미지 기본으로할경우
                        Toast.makeText(ProfileActivity.this, "업로드중입니다. 잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                        //값 데이터베이스에서 넣어줌
                        profile = new Profile(mEmail, mNickName, mSex, mAge, "basic");
                        //닉네임을 루트로 사용자 정보 저장
                        mProfieDatabaseReference.child(mUid).setValue(profile);
                        //SharedPReference에도 저장해줌 (쉽게 갖다쓰기위해)
                        saveProfileSharedPreferences(profile);
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss(); //로딩종료
                    }
                } else { //공백을 입력한 경우
                    Toast.makeText(ProfileActivity.this, "공백이 있으면 안됩니다", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //로컬에 프로필정보 저장 (확인 버튼 클릭시 호출)
    public void saveProfileSharedPreferences(Profile profile) {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
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


    //스피너 세팅작업
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

    //사진찍기 or 앨범에서 가져오기 선택 다이얼로그
    private void photoDialogRadio() {
        final CharSequence[] PhotoModels = {"갤러리에서 가져오기","기본사진으로 하기"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("사진 가져오기");
        alt_bld.setSingleChoiceItems(PhotoModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(ProfileActivity.this, PhotoModels[item] + "가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                if (item == 0) { //갤러리
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE);
                } else { //기본화면으로하기
                    mPhotoCircleImageView.setImageResource(R.drawable.profile);
                    mImage = "";
                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
    public void loading(){
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(ProfileActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, 100);
    }
}
