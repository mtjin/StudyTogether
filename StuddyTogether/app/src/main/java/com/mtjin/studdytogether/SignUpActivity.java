package com.mtjin.studdytogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    final static String TAG = "SIGNIN";

    private EditText mIdEditText;
    private EditText mPasswordEditText;
    private EditText mPassConfirmEditText;
    private String mEmail;
    private String mPassword;
    private String mPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mIdEditText = findViewById(R.id.signup_pt_id);
        mPasswordEditText = findViewById(R.id.signup_pt_password);
        mPassConfirmEditText = findViewById(R.id.signup_pt_passconfirm);

        mAuth = FirebaseAuth.getInstance();

        //회원가입 완료 버튼 클릭
        findViewById(R.id.signup_btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mIdEditText.getText().toString().trim();
                mPassword = mPasswordEditText.getText().toString().trim();
                mPassConfirm = mPassConfirmEditText.getText().toString().trim();

                if(mPassword.equals(mPassConfirm)) {

                    mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(SignUpActivity.this, "패스워드가 서로 다릅니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.signup_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}