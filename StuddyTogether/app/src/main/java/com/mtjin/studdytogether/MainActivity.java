package com.mtjin.studdytogether;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.im);
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
        }

    }
}
