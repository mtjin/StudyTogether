package com.mtjin.studdytogether.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.function.BackPressedFunction;
import com.mtjin.studdytogether.rtdb_model.StudyMessage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WriteActivity extends AppCompatActivity {
    Button saveButton;
    Button cancelButton;
    EditText titleEditText;
    EditText contentsEditText;
    ImageView photoImageView;

    private StudyMessage studyMessage;
    private Bitmap img; //비트맵 업로드사진
    private Uri mDownloadImageUri; //업로드사진 스토리지 URI
    private String mUid; //사용자 토큰 고유 아이디
    private FirebaseAuth mFirebaseAuth; //인증객체(uid발급가능)
    private StorageReference mStorageRef; //파이어베이스 스토리지
    private StorageReference mMessageImageRef; //게시물이미지 담을 파베 스토리지

    //프로그래스 로딩 다이얼로그
    ProgressDialog progressDialog;
    //2번뒤로가기 클릭시 종료
    private BackPressedFunction mBackPressedFunction;
    //RequestCode
    final static int PICK_IMAGE = 1;
    //값들
    private String mImage;
    //날짜포맷
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        setTitle("서울지역");

        //파이어베이스 스토리지 얻어옴
        mStorageRef = FirebaseStorage.getInstance().getReference();

        saveButton = findViewById(R.id.write_btn_save);
        cancelButton = findViewById(R.id.write_btn_cancel);
        titleEditText = findViewById(R.id.write_et_title);
        contentsEditText = findViewById(R.id.write_et_contents);
        photoImageView = findViewById(R.id.write_iv_photo);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessge();
            }
        });
        photoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoDialogRadio();
            }
        });

        mBackPressedFunction = new BackPressedFunction(this); //뒤로가기 2번시 종료 핸들러
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
                    img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    photoImageView.setImageBitmap(img);
                    mImage = data.getData() + "";//사용할려면 uri.parse함수 사용해야함
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveButtonClick() {
        final String title = titleEditText.getText().toString();
        final String contents = contentsEditText.getText().toString();
        if (!title.equals("") && !contents.equals("")) {
            if (img != null) {
                loading(); //로딩 다이얼로그
                //파이어베이스 스토리지에 업로드
                Toast.makeText(WriteActivity.this, "업로드중입니다. 잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                byte[] datas = baos.toByteArray();
                UploadTask uploadTask = mMessageImageRef.putBytes(datas);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return mMessageImageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            mDownloadImageUri = task.getResult();
                            //값 데이터베이스에서 넣어줌
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", title);
                            bundle.putString("contents", contents);
                            bundle.putString("donwnloadImageUri", mDownloadImageUri + "");
                            //작성시간 put
                            Calendar time = Calendar.getInstance();
                            String dates = format1.format(time.getTime());
                            bundle.putString("dates", dates);
                            bundle.putString("uid", mUid);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            progressDialog.dismiss();
                            finish();

                        } else {
                            // Handle failures
                            progressDialog.dismiss();
                            Toast.makeText(WriteActivity.this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else { //저장한 이미지 없는 경우
                //값 데이터베이스에서 넣어줌
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("contents", contents);
                bundle.putString("donwnloadImageUri", "basic");
                //작성시간 put
                Calendar time = Calendar.getInstance();
                String dates = format1.format(time.getTime());
                bundle.putString("dates", dates);
                bundle.putString("uid", mUid);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Toast.makeText(this, "공백을 채워주세요", Toast.LENGTH_SHORT).show();
        }

    }

    public void showMessge() {

        //다이얼로그 객체 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //속성 지정
        builder.setTitle("안내");
        builder.setMessage("작성하시던 글이 지워집니다 " +
                "종료 하시겠습니까?");
        //아이콘
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        //예 버튼 눌렀을 때
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(WriteActivity.this, "예버튼이 눌렸습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        //예 버튼 눌렀을 때
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(WriteActivity.this, "아니오 버튼이 눌렸습니다", Toast.LENGTH_SHORT).show();
            }
        });

        //만들어주기
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUid = mFirebaseAuth.getUid();   //사용자 고유 토큰 받아옴
        mMessageImageRef = mStorageRef.child(mUid + "messageImage"); //프로필 스토리지 저장이름은 사용자 고유토큰과 스트링섞어서 만든다.
        // Log.d("PROFILE22", mEmail);
    }

    public void loading() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(WriteActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, 0);
    }

    @Override
    public void onBackPressed() {
        mBackPressedFunction.onBackPressed();
    }

    //사진찍기 or 앨범에서 가져오기 선택 다이얼로그
    public void photoDialogRadio() {
        final CharSequence[] PhotoModels = {"갤러리에서 가져오기", "지도에서 장소캡쳐", "사진 삭제"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("사진 업로드");
        alt_bld.setSingleChoiceItems(PhotoModels, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(WriteActivity.this, PhotoModels[item] + "가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                if (item == 0) { //갤러리
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE);
                } else if (item == 1) { //네이버지도
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.map.naver.com/"));
                    startActivity(intent);
                } else { //사진 삭제
                    photoImageView.setImageBitmap(null);
                    img = null;
                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }
}
