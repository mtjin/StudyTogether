package com.mtjin.studdytogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    final static String TAG = "LOGIN";

    private EditText mIdEditText;
    private EditText mPasswordEditText;
    private CheckBox mCheckBox;
    private String mEmail;
    private String mPassword;
    private  boolean isSavedLogData;
    private SharedPreferences mAppData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // FirebaseApp.initializeApp(this);

        // 설정값 불러오기
        mAppData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        mIdEditText = findViewById(R.id.login_pt_id);
        mPasswordEditText = findViewById(R.id.login_pt_password);
        mCheckBox = findViewById(R.id.checkBox);

        mAuth = FirebaseAuth.getInstance();



        // 이전에 로그인 정보를 저장시킨 기록이 있다면
        if (isSavedLogData) {
            mIdEditText.setText(mEmail);
            mPasswordEditText.setText(mPassword);
            mCheckBox.setChecked(isSavedLogData);
        }

       /* if (isSavedLogData) { //로그인정보저장했을 경우
            getSharedPrefLog();
            mCheckBox.setChecked(isSavedLogData);
        }
*/


        //로그인 버튼 클릭
        findViewById(R.id.login_btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mIdEditText.getText().toString().trim();
                mPassword = mPasswordEditText.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                    save(); //로그인 정보저장
                                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                                    mIdEditText.startAnimation(shake);
                                    mPasswordEditText.startAnimation(shake);
                                }
                            }
                        });
            }
        });

        //회원가입 버튼 클릭
        findViewById(R.id.login_btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override //When initializing your Activity, check to see if the user is currently signed in.
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    // 설정값을 저장하는 함수
    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = mAppData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", mCheckBox.isChecked());
        editor.putString("ID", mIdEditText.getText().toString().trim());
        editor.putString("PWD", mPasswordEditText.getText().toString().trim());
        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        isSavedLogData = mAppData.getBoolean("SAVE_LOGIN_DATA", false);
        mEmail = mAppData.getString("ID", "");
        mPassword = mAppData.getString("PWD", "");
    }


}
