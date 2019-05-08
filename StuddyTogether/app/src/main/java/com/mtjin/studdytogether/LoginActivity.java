package com.mtjin.studdytogether;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener { //GoogleApiClient.OnConnectionFailedListener, View.OnClickListener 구현
    private FirebaseAuth mAuth;
    final static String TAG = "LOGIN";

    private EditText mIdEditText;
    private EditText mPasswordEditText;
    private CheckBox mCheckBox;
    private String mEmail;
    private String mPassword;
    private boolean isSavedLogData;
    private SharedPreferences mAppData;
    // 구글로그인 result 상수
    private static final int CODE_SIGN_IN = 1000;

    // 구글api클라이언트
    private GoogleApiClient mGoogleApiClient; //구글인증에필요

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 구글  로그인 버튼
    private SignInButton googleButton;

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
        googleButton = findViewById(R.id.login_btn_google);

        mAuth = FirebaseAuth.getInstance();


        // 이전에 로그인 정보를 저장시킨 기록이 있다면
        if (isSavedLogData) {
            mIdEditText.setText(mEmail);
            mPasswordEditText.setText(mPassword);
            mCheckBox.setChecked(isSavedLogData);
        }

        //구글로그인관련 소스
        mAuth = FirebaseAuth.getInstance(); //인증은 구글로 할 것이다. (페이스북이나 다른걸로도 가능)
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this) //기본으로 세팅해줌
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.login_btn_google).setOnClickListener(this);

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

    //구글인증연결 실패시
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //구글로그인버튼 눌렀을 때 처리
    @Override
    public void onClick(View v) {
        Intent signInintent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInintent, CODE_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_SIGN_IN) { //구글로그인버튼 누르고 응답결과
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) { //로그인 성공시
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "구글 로그인을 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { //task에서 다양한 정보를 담고있기 때문에 잘 사용하면된다.
                        if (!task.isSuccessful()) { //실패했다면
                            Toast.makeText(LoginActivity.this, "인증 실패하였습니다", Toast.LENGTH_LONG).show();
                        } else { //성공했으면 다시 로그인액티비티에서 프로필액티비티로 가게해주면된다.
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            finish();
                        }
                    }
                });
    }



}
