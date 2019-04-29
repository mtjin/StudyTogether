package com.mtjin.studdytogether;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

public class WriteActivity extends AppCompatActivity {
    Button saveButton;
    Button cancelButton;
    EditText titleEditText;
    EditText contentsEditText;
    TextView addPhotoButton;
    TextView deletePhotoButton;
    ImageView photoImageView;

    //RequestCode
    final static int PICK_IMAGE = 1;

    //값들
    private String mImage;

    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        saveButton = findViewById(R.id.write_btn_save);
        cancelButton = findViewById(R.id.write_btn_cancel);
        titleEditText = findViewById(R.id.write_et_title);
        contentsEditText = findViewById(R.id.write_et_contents);
        addPhotoButton = findViewById(R.id.write_tv_addphoto);
        deletePhotoButton = findViewById(R.id.write_tv_deletephoto);
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
                finish();
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

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
                    photoImageView.setImageBitmap(img);
                   mImage = data.getData() + "";//사용할려면 uri.parse함수 사용해야함
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveButtonClick(){
        String title = titleEditText.getText().toString();
        String contents = contentsEditText.getText().toString();
    }





}
