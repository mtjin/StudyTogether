package com.mtjin.studdytogether.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtjin.studdytogether.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailCityActivity extends AppCompatActivity {
    static final String TAG = "DetailCityActivityTAG";
    private TextView mTitleTextView;//제목
    private TextView mNickNameTextView; //닉네임
    private TextView mAgeTextView; //나이
    private ImageView mMessageImageView; //게시물업로드사진
    private TextView mMessageTextView; //게시물 내용
    private CircleImageView mPhotoImageView; //내프로필사진
    private TextView mDatesTextView; //날짜
    private TextView mCommentTextView; //댓글
    private ImageButton mTrashImageButton; //삭제버튼

    private FirebaseAuth mFirebaseAuth; //인증객체(uid토큰 받기가능)

    //게시물id, 도시 , 현재접속유저id,
    private String mId;
    private String mCity;
    private String currentUserUid;
    private String messageUid;
    //게시물
    private String mNickName;
    private String mAge; //나이대
    private String mImage; //프로필사진
    private String mTitle; //제목
    private String mContent; //내용
    private String mMessagePhoto; //게시물에 업로드한 사진
    private String mDate; //날짜
    private String mCommentNum; // 댓글개수

    //드로어,메뉴
    private DrawerLayout mDrawerLayout;
    private View drawerView;
    private Button mProfileMenuButton;
    private Button mQuestionMenuButton;
    private Button mLogoutButton;
    private CircleImageView mDrawerProfileCircleImageView;
    private TextView mDrawerNickNameTextView;
    private String mSharedNickName;
    private String mSharedImage;



    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 root
    DatabaseReference mMessageDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_city);
        loadShared(); //프로필정보받아옴
        setDrawer(); //드로어 세팅

        mTitleTextView = findViewById(R.id.detail_tv_title);
        mNickNameTextView = findViewById(R.id.detail_tv_name);
        mAgeTextView = findViewById(R.id.detail_tv_age);
        mMessageImageView = findViewById(R.id.detail_iv_photo);
        mPhotoImageView = findViewById(R.id.detail_iv_profile);
        mDatesTextView = findViewById(R.id.detail_tv_date);
        mMessageTextView = findViewById(R.id.detail_tv_message);
        mCommentTextView = findViewById(R.id.detail_tv_comment);
        mTrashImageButton = findViewById(R.id.detail_btn_trash);

        //인텐트처리
        processIntent();
        //지역과 게시물에 맞게 디비참조
        mMessageDatabaseReference = mRootDatabaseReference.child(mCity).child(mId);
        //프로필사진클릭시 줌인줌아웃가능
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCityActivity.this , PhotoZoomActivity.class);
                intent.putExtra("zoomProfilePhoto", mImage);
                startActivity(intent);
            }
        });
        //게시물사진 클릭시 줌인줌아웃가능
        mMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCityActivity.this , PhotoZoomActivity.class);
                intent.putExtra("zoomMessagePhoto", mMessagePhoto);
                startActivity(intent);
            }
        });

        //댓글눌렀을떄 댓글쓰기화면으로
        mCommentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCityActivity.this, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",mId);
                bundle.putString("city", mCity);
                Log.d(TAG, "mId: "+mId);
                Log.d(TAG, "City: " +mCity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //게시글 삭제
        mTrashImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUserUid.equals(messageUid)) { //작성자가 맞으면 삭제가능
                    showMessge();
                }else{
                    Toast.makeText(getApplicationContext(), "본인 게시물 외에는 삭제가 불가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showMessge() {

        //다이얼로그 객체 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //속성 지정
        builder.setTitle("안내");
        builder.setMessage("해당 글을 삭제하면 복구할 수 없습니다 " +
                "삭제 하시겠습니까?");
        //아이콘
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        //예 버튼 눌렀을 때
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMessageDatabaseReference.setValue(null);
                Toast.makeText(getApplicationContext(), "게시물이 삭제되었습니디.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        //예 버튼 눌렀을 때
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //만들어주기
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void processIntent(){

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle.getString("id") != null) {
            mId = bundle.getString("id");
        }
        if(bundle.getString("city") != null){
            mCity = bundle.getString("city");
        }
        //값 get
        mTitle= bundle.getString("messageTitle");
        mDate = bundle.getString("messageDate");
        mNickName =  bundle.getString("messageNIckName");
        mAge= bundle.getString("messageAge");
        mMessagePhoto = bundle.getString("messagePhoto"); //업로드하는사진
        mImage=  bundle.getString("messageImage"); //내 프로필사진
        mContent = bundle.getString("messageContent"); //내용
        messageUid = bundle.getString("uid"); //작성자 uid
        mCommentNum = bundle.getString("commentNum");

        //뷰 set
        mTitleTextView.setText(mTitle);
        mNickNameTextView.setText(mNickName);
        mAgeTextView.setText(mAge);
        mMessageTextView.setText(mContent);
        mDatesTextView.setText(mDate);
        mCommentTextView.setText(mCommentNum);
        if (mImage.equals("basic")) { //프로필사진이 없는경우
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(mPhotoImageView);
        } else {
            Glide.with(this).load(mImage).into(mPhotoImageView);
        }
        if (mMessagePhoto != "basic") {
            Glide.with(this).load(mMessagePhoto).into(mMessageImageView);
        } else {
            //사진첨부안했으니 안올림
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //현재 유저 uid
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = mFirebaseAuth.getUid();   //사용자 고유 토큰 받아옴
        loadShared(); //프로필정보받아옴
    }

    // 쉐어드값을 불러오는 메소드
    private void loadShared() {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        mSharedNickName = pref.getString("nickName", "");
        mSharedImage = pref.getString("image", "");
    }

    //옵션 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.openDrawer(drawerView);
        return true;
    }



    public void setDrawer(){
        //드로어,메뉴
        mDrawerLayout = findViewById(R.id.detail_drawer_layout);
        drawerView = findViewById(R.id.drawer);
        mProfileMenuButton = findViewById(R.id.drawer_btn_profileSetting);
        mQuestionMenuButton = findViewById(R.id.drawer_btn_question);
        mLogoutButton = findViewById(R.id.drawer_btn_logout);
        mDrawerProfileCircleImageView = findViewById(R.id.drawer_civ_profileimage);
        mDrawerNickNameTextView = findViewById(R.id.drawer_tv_nickName);
        mDrawerNickNameTextView.setText(mSharedNickName);
        if (mSharedImage.equals("basic")) { //프로필사진이 없는경우
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(mDrawerProfileCircleImageView);
        } else {
            Glide.with(this).load(mSharedImage).into(mDrawerProfileCircleImageView);
        }
        //드로어관련 클릭리스너
        mProfileMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                //이 플래그는 API 11 (허니콤)부터 사용이가능한데 그 이하버전은 0.2%수준이다.
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                }else{
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
            }
        });
        mQuestionMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                try {
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"seungeon.jin2@gmail.com"});

                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivity(intent);

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"seungeon.jin2@gmail.com"});
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }
            }
        });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //이 플래그는 API 11 (허니콤)부터 사용이가능한데 그 이하버전은 0.2%수준이다.
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
            }
        });
        mDrawerProfileCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoomActivity.class);
                intent.putExtra("zoomProfilePhoto", mSharedImage);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        mDrawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }
    //드로어 리스너
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {

        }

        @Override
        public void onDrawerClosed(@NonNull View view) {

        }

        @Override
        public void onDrawerStateChanged(int i) {

        }
    };
}
