package com.mtjin.studdytogether.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.adapter.CommentAdapter;
import com.mtjin.studdytogether.rtdb_model.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mCommentsRecyclerView;
    private EditText mWriteEditText;
    private Button mSendButton;


    private String mId; //해당 게시물의 id
    private String mCity; //도시
    private FirebaseAuth mFirebaseAuth; //인증객체(uid토큰 받기가능)
    //프로필
    private String mNickName;
    private String mTmpDownloadImageUri;
    private String mAge;
    private String mUid;
    private String mImage; //프로필사진
    //날짜포맷
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd mm:ss");
    //어댑터 , 아이템리스트
    private CommentAdapter mCommentAdapter;
    private ArrayList<Comment> mCommentList;
    //위로당기면 새로고침
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //아이템객체
    private Comment mComment;
    //디비위치
    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 위치한곳
    DatabaseReference mCityDatabaseReference; //여기에 값 추가
    DatabaseReference mMessageDatabaseReference; //값 추가하기전에 해당 게시물이 삭제되었는지 확인
    Boolean isHasMessage; //현재있는 게시물이 존재하는지 확인 (삭제되있으면 댓글작성불가)

    //드로어,메뉴
    private DrawerLayout mDrawerLayout;
    private View drawerView;
    private Button mProfileMenuButton;
    private Button mQuestionMenuButton;
    private Button mLogoutButton;
    private CircleImageView mDrawerProfileCircleImageView;
    private TextView mDrawerNickNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        loadShared(); //쉐어드 데이터가져오기
        setDrawer(); //드로어 세팅
        processIntent(); //인텐트 처리
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드올라올떄 UI 위로 떙겨줌
        mCityDatabaseReference = mRootDatabaseReference.child(mCity).child(mId).child(mId + "Comment"); //해당지역의 게시글의 데이터베이스 참조


        mCommentsRecyclerView = findViewById(R.id.comments_rev_comments);
        mWriteEditText = findViewById(R.id.comments_pt_write);
        mSendButton = findViewById(R.id.comments_btn_send);

        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(mCommentList, getApplicationContext());
        //아래구분선 세팅
        mCommentsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        // 리사이클러뷰에 레이아웃 매니저와 어댑터를 설정한다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        mCommentsRecyclerView.setLayoutManager(layoutManager); ////만든 레이아웃매니저 객체를(설정을) 리사이클러 뷰에 설정해줌
        mCommentsRecyclerView.setAdapter(mCommentAdapter); //어댑터 셋

        //리사이클러뷰 끝까지 끌어당기면 새로고침하게 해주는 뷰 onRefresh()에 해당코드 구현
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.comment_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mCityDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if (dataSnapshot.hasChild(mId+"Comment")) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                    Comment comment = dataSnapshot2.getValue(Comment.class);
                    mCommentList.add(comment);
                    mCommentAdapter.notifyItemInserted(mCommentList.size() - 1);
                }
                //   }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeComment();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUid = mFirebaseAuth.getUid();
    }

    public void writeComment() {
        final String message = mWriteEditText.getText().toString();
        if (message.equals("")) { //빈칸인경우
            Toast.makeText(getApplicationContext(), "한글자 이상 작성해야합니다.", Toast.LENGTH_SHORT).show();
        } else {
            mMessageDatabaseReference = mRootDatabaseReference.child(mCity);
            mMessageDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() { //삭제된 게시물인지 확인
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isHasMessage = dataSnapshot.hasChild(mId);
                    if (isHasMessage) {
                        Calendar time = Calendar.getInstance();
                        String dates = format1.format(time.getTime()); //작성시간
                        mComment = new Comment(mNickName, mAge, mTmpDownloadImageUri, dates, message, mUid);

                        //리사이클러뷰에 쓴 글 추가
                        mCityDatabaseReference.push() //DB에 (MESSAGES_CHILD)messages라는 이름의 하위디렉토리(?)라는걸 만들고 여기다 데이터를 넣겠다고 생각하면된다.
                                .setValue(mComment); //DB에 데이터넣음

                        mCommentAdapter.clear();
                        mCityDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //  if (dataSnapshot.hasChild(mId+"Comment")) {
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                                    Comment comment = dataSnapshot2.getValue(Comment.class);
                                    mCommentList.add(comment);
                                }
                                mCommentAdapter.notifyDataSetChanged();
                                //    }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "삭제된 게시물입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mWriteEditText.setText(""); //작성후 빈칸초기화
    }

    //인텐트처리
    public void processIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d("TEST22", bundle.getString("id", ""));
        Log.d("TEST22", bundle.getString("city", ""));
        if (bundle.getString("id") != null) {
            mId = bundle.getString("id");
        }
        if (bundle.getString("city") != null) {
            mCity = bundle.getString("city");
        }
    }

    // 자신의 프로필 정보 불러오기
    public void loadShared() {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        mNickName = pref.getString("nickName", "");
        if (pref.getString("image", "").equals("basic") || pref.getString("image", "").equals("")) {
            mTmpDownloadImageUri = "basic";
        } else {
            mTmpDownloadImageUri = pref.getString("image", "basic");
        }
        mAge = pref.getString("age", "");
        mImage = pref.getString("image", "");
    }

    @Override
    public void onRefresh() {
        mCommentAdapter.clear();
        mCityDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if (dataSnapshot.hasChild(mId+"Comment")) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                    Comment comment = dataSnapshot2.getValue(Comment.class);
                    mCommentList.add(comment);
                }
                mCommentAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
            //  }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeComment();
            }
        });
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
        mDrawerLayout = findViewById(R.id.comment_drawer_layout);
        drawerView = findViewById(R.id.drawer);
        mProfileMenuButton = findViewById(R.id.drawer_btn_profileSetting);
        mQuestionMenuButton = findViewById(R.id.drawer_btn_question);
        mLogoutButton = findViewById(R.id.drawer_btn_logout);
        mDrawerProfileCircleImageView = findViewById(R.id.drawer_civ_profileimage);
        mDrawerNickNameTextView = findViewById(R.id.drawer_tv_nickName);
        mDrawerNickNameTextView.setText(mNickName);
        if (mImage.equals("basic")) { //프로필사진이 없는경우
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(mDrawerProfileCircleImageView);
        } else {
            Glide.with(this).load(mImage).into(mDrawerProfileCircleImageView);
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
                intent.putExtra("zoomProfilePhoto", mImage);
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
