package com.mtjin.studdytogether.view;

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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.function.DataValidation;
import com.mtjin.studdytogether.rtdb_model.Profile;

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
    private Button mCheckidButton; //닉네임중복확인버튼
    private CircleImageView mPhotoCircleImageView;
    private String mUid; //사용자 토큰 고유 아이디
    private String mNickName; //닉네임
    private String mSex; //성별 남자 or 여자
    private String mEmail; //이메일
    private String mAge; //나이대
    private Uri mDownloadImageUri; //프로필사진 스토리지 URI
    private String mTmpDownloadImageUri; //Shared에서 받아올떄 String형이라 임시로 받아오는데 사용
    private int mTmpAge; //스피너 기존값 초기세팅해주기위해 사용
    private int mTmpSex; // ''
    private Bitmap img; //비트맵 프로필사진 (이걸
    private FirebaseAuth mFirebaseAuth; //인증객체
    private FirebaseUser mFirebaseUser; //인증이 되면 이객체를 얻을 수 있다. (인증된 유저받아올 수 있음)
    private StorageReference mStorageRef; //파이어베이스 스토리지
    private StorageReference mProfileRef; //프로필이미지 담을 파베 스토리

    //RequestCode
    final static int PICK_IMAGE = 1; //갤러리에서 사진선택
    final static int CAPTURE_IMAGE = 2;  //카메라로찍은 사진선택

    ProgressDialog progressDialog;    //로딩 프로그래스 다이얼로그
    private String mCurrentPhotoPath; //카메라로 찍은 사진 저장할 루트경로

    Profile profile; //데이터베이스에 저장 할 객체
    ArrayList<String> sexArrayList; //스피너 리스트
    ArrayList<String> ageArrayList; //스피너리스트
    ArrayAdapter<String> sexAdapter; //스피너 어댑터
    ArrayAdapter<String> ageAdapter;
    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 위치한곳
    DatabaseReference mProfieDatabaseReference = mRootDatabaseReference.child("profile"); //profile이란 이름의 하위 데이터베이스
    DatabaseReference mNickNameDatabaseReference = mRootDatabaseReference.child("nickNameList"); //닉네임 담아놀 하위 데이터베이스
    FirebaseFirestore db; //파이어스토어 디비
    String tmpNickName; //중복확인용 임시닉네임
    String removeNickName; //등록시 기존 자기 아이디 닉네임리스트에서 지우기 위한 임시저장용도
    Boolean isHasRemovedNickName; //지울 닉네임이 존재하는지
    Boolean isNickExisted1;
    Boolean isCheckid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("프로필");

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        mStorageRef = FirebaseStorage.getInstance().getReference(); //스토리지
        db = FirebaseFirestore.getInstance(); //파이어스토어

        mNickNameEditText = findViewById(R.id.profile_pt_nickname);
        mSexSpinner = findViewById(R.id.profile_sp_sex);
        mAgeSpinner = findViewById(R.id.profile_sp_age);
        mOkButton = findViewById(R.id.profile_btn_ok);
        mCheckidButton = findViewById(R.id.profile_btn_checkid);
        mPhotoCircleImageView = findViewById(R.id.profile_iv_photo);
        mPhotoCircleImageView.setClickable(true);

        //스피너 설정
        spinnerDo();
        //Shared에서 이전에 저장했던것들 변수에저장
       loadShared();
        //Shared에서 꺼내온것들을 각각 알맞게 뷰에 세팅 (과거했던걸 세팅해놔줌)
     loadInitalSetting();

        //프로필이미지 클릭 시
        mPhotoCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override //이미지 불러오기기
            public void onClick(View v) {
                photoDialogRadio(); //갤러리에서 불러오기 or 사진찍어서 불러오기
            }
        });


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
                Log.d(TAG, data.getData() + "");
                Log.d(TAG, img + "");

                mTmpDownloadImageUri = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (resultCode == RESULT_OK) {
                try {
                    File file = new File(mCurrentPhotoPath);
                    InputStream in = getContentResolver().openInputStream(Uri.fromFile(file));
                    img = BitmapFactory.decodeStream(in);
                    mPhotoCircleImageView.setImageBitmap(img);
                    in.close();

                    mTmpDownloadImageUri = null;
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
        mProfileRef = mStorageRef.child("profileImage").child(mUid); //프로필 스토리지 저장이름은 사용자 고유토큰과 스트링섞어서 만든다.

        //초기화
        isNickExisted1 = false;
        isHasRemovedNickName = false;
        isCheckid = true;
        isHasRemovedNickName = null;

        mCheckidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpNickName = mNickNameEditText.getText().toString().trim();
                if (tmpNickName.length() > 7 || tmpNickName.length() <= 0 || (!DataValidation.checkOnlyCharacters(tmpNickName))) {
                    Toast.makeText(ProfileActivity.this, "닉네임은 1~7글자 이하이고 특수문자를 쓰면 안됩니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("DDDD", "IN mCheckidButton");
                    //초기화
                    isNickExisted1 = false;
                    isHasRemovedNickName = false;
                    isCheckid = true;
                    isHasRemovedNickName = null;
                    mRootDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("nickNameList") || dataSnapshot.child("nickNameList").hasChild(tmpNickName)) {
                                if (!dataSnapshot.child("nickNameList").hasChild(tmpNickName) || dataSnapshot.child("nickNameList").child(tmpNickName).getValue().equals(mEmail)) { //사용가능닉네임
                                    Toast.makeText(getApplicationContext(), "사용가능한 닉네임입니다.", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "1");
                                    if ( dataSnapshot.child("nickNameList").hasChild(tmpNickName) && !dataSnapshot.child("nickNameList").child(tmpNickName).getValue().equals(mEmail)) { //원래랑 같은 닉네임 (지울필요 X)
                                        isHasRemovedNickName = false;
                                        Log.d(TAG, "2");
                                    } else if(dataSnapshot.child("nickNameList").hasChild(tmpNickName)){ //원래랑 다른닉네임 원래닉네임은 지워줘야함
                                        //removeNickName = (String) dataSnapshot.child(tmpNickName).getValue(); //지워야할닉네임

                                        removeNickName = (String) dataSnapshot.child("profile").child(mUid).child("nickName").getValue(); //기존에 갖고있던 닉네임( nickNameList에서 지워줘야함)
                                        isHasRemovedNickName = true; //지울닉네임 존재 (기존닉네임
                                        Log.d(TAG, "3");
                                    }else { //처음 닉네임 짓는경우
                                        if ( dataSnapshot.hasChild("profile") && dataSnapshot.child("profile").hasChild(mUid)){
                                            isHasRemovedNickName = true;
                                            removeNickName = (String) dataSnapshot.child("profile").child(mUid).child("nickName").getValue(); //기존에 갖고있던 닉네임( nickNameList에서 지워줘야함)
                                            Log.d(TAG, "4");
                                        }else {
                                            isHasRemovedNickName = false;
                                            Log.d(TAG, "5");
                                        }
                                    }
                                    isNickExisted1 = false;
                                } else { //중복된 닉네임 사용불가
                                    Toast.makeText(getApplicationContext(), "중복된 닉네임이 존재합니다.", Toast.LENGTH_LONG).show();
                                    isNickExisted1 = true;
                                    isHasRemovedNickName = false;
                                    Log.d(TAG, "6");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "사용가능한 닉네임입니다.", Toast.LENGTH_LONG).show();
                                isNickExisted1 = false;
                                isHasRemovedNickName = false;
                                Log.d(TAG, "7");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "통신오류", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });


        //ok버튼 클릭시
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("profilelogin", isCheckid+"");
                Log.d("profilelogin1", isNickExisted1+"");
                Log.d("profilelogin3", mUid);
                //mSex는 스피너리스너에서 저장해놈놈
                mNickName = mNickNameEditText.getText().toString().trim();

                if (isCheckid && mNickName.equals(tmpNickName) && (!isNickExisted1)) { //닉네임 생성할 수 있는 조건
                    if (mNickName.length() > 7 || mNickName.length() <= 0 || (!DataValidation.checkOnlyCharacters(mNickName))) { //닉네임 1자 이하 5자 이상으로 한 경우, 또는 특수문자
                        Toast.makeText(ProfileActivity.this, "닉네임은 1~7글자 이하이고 특수문자를 쓰면 안됩니다.", Toast.LENGTH_SHORT).show();
                    } else if (mNickName != null && mSex != null) { //제대로 작성한 경우
                        loading();
                        if (img != null && mTmpDownloadImageUri==null) { //프로필사진을 지정했을 경우
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
                                        //디비에넣기전 이전 아이디는 디비에서삭제
                                        if (isHasRemovedNickName) {
                                            mNickNameDatabaseReference.child(removeNickName).setValue(null); //child는 하위값이 없으면 자동으로 삭제되는점 이용
                                        }
                                        //값 데이터베이스에서 넣어줌
                                        profile = new Profile(mEmail, mNickName, mSex, mAge, mDownloadImageUri + "");
                                        //사용자토큰을 루트로 사용자 정보 저장
                                        mProfieDatabaseReference.child(mUid).setValue(profile);
                                        //닉네임리스트에 닉네임저장
                                        mNickNameDatabaseReference.child(profile.getNickName()).setValue(mEmail);
                                        //SharedPReference에도 저장해줌 (쉽게 갖다쓰기위해)
                                        saveProfileSharedPreferences(profile);
                                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        loadingEnd();//로딩종료
                                    } else {
                                        // Handle failures
                                        Toast.makeText(ProfileActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                        loadingEnd();//로딩종료
                                    }
                                }
                            });
                        } else if (mTmpDownloadImageUri != null) { //과거세팅값으로 한 경우
                            if (isHasRemovedNickName) {
                                mNickNameDatabaseReference.child(removeNickName).setValue(null); //child는 하위값이 없으면 자동으로 삭제되는점 이용
                            }
                            //값 데이터베이스에서 넣어줌
                            profile = new Profile(mEmail, mNickName, mSex, mAge, mTmpDownloadImageUri + "");
                            //사용자토큰을 루트로 사용자 정보 저장
                            mProfieDatabaseReference.child(mUid).setValue(profile);
                            //닉네임리스트에 닉네임저장
                            mNickNameDatabaseReference.child(profile.getNickName()).setValue(mEmail);
                            //SharedPReference에도 저장해줌 (쉽게 갖다쓰기위해)
                            saveProfileSharedPreferences(profile);
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            loadingEnd();//로딩종료

                        } else { //프로필이미지 기본으로할경우
                            Toast.makeText(ProfileActivity.this, "업로드중입니다. 잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                            //디비에넣기전 이전 아이디는 디비에서삭제
                            if (isHasRemovedNickName) {
                                mNickNameDatabaseReference.child(removeNickName).setValue(null); //child는 하위값이 없으면 자동으로 삭제되는점 이용
                            }
                            //값 데이터베이스에서 넣어줌
                            profile = new Profile(mEmail, mNickName, mSex, mAge, "basic");
                            //사용자토큰을 루트로 사용자 정보 저장
                            mProfieDatabaseReference.child(mUid).setValue(profile);
                            //닉네임리스트에 닉네임저장
                            mNickNameDatabaseReference.child(profile.getNickName()).setValue(mEmail);
                            //SharedPReference에도 저장해줌 (쉽게 갖다쓰기위해)
                            saveProfileSharedPreferences(profile);
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            loadingEnd();
                            startActivity(intent);
                        }
                    } else { //공백을 입력한 경우
                        Toast.makeText(ProfileActivity.this, "공백이 있으면 안됩니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "아이디 중복확인을 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                mTmpSex = position;
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
                mTmpAge = position;
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
        final CharSequence[] PhotoModels = {"갤러리에서 가져오기", "카메라로 촬영 후 가져오기", "기본사진으로 하기"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("프로필사진 설정");
        alt_bld.setSingleChoiceItems(PhotoModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(ProfileActivity.this, PhotoModels[item] + "가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                if (item == 0) { //갤러리
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE);
                } else if (item == 1) { //카메라찍은 사진가져오기
                    takePictureFromCameraIntent();
                } else { //기본화면으로하기
                    mPhotoCircleImageView.setImageResource(R.drawable.profile);
                    img = null;
                    mTmpDownloadImageUri = null;
                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    public void loading() {
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(ProfileActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, 0);
    }

    public void loadingEnd() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }

    //카메라로 촬영한 이미지를파일로 저장해주는 함수
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //카메라 인텐트실행 함수
    private void takePictureFromCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mtjin.studdytogether.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
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

        //스피너 초기값 세팅해줄때 사용
        editor.putInt("tmpAge", mTmpAge);
        editor.putInt("tmpSex", mTmpSex);

        Log.d(TAG, profile.getEmail());
        Log.d(TAG, profile.getNickName());
        Log.d(TAG, profile.getSex());
        Log.d(TAG, profile.getAge());
        Log.d(TAG, profile.getImage());
        editor.commit();
    }

    // 설정값을 불러오는 함수
    private void loadShared() {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        //mEmail = pref.getString("email", "");
        mNickName = pref.getString("nickName", "");
        if (pref.getString("image", "").equals("basic") || pref.getString("image", "").equals("")) {
            mTmpDownloadImageUri = null;
        } else {
            mTmpDownloadImageUri = pref.getString("image", "");
        }
        mTmpAge = pref.getInt("tmpAge", 0);
        mTmpSex = pref.getInt("tmpSex", 0);
    }

    //이전에 했던 값들 미리 띄움
    public void loadInitalSetting() {
        mNickNameEditText.setText(mNickName);
        mAgeSpinner.setSelection(mTmpAge);
        mSexSpinner.setSelection(mTmpSex);
        if (mTmpDownloadImageUri != null) {
            Glide.with(ProfileActivity.this).load(mTmpDownloadImageUri).into(mPhotoCircleImageView);
            Log.d("GLIDE", mTmpDownloadImageUri);
        }
    }

    /*//이전 프로필과 똑같은지
    public Boolean isSameBefore() {

    }*/


}
