package com.mtjin.studdytogether.activity;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.adapter.CommentAdapter;
import com.mtjin.studdytogether.function.DataValidation;
import com.mtjin.studdytogether.rtdb_model.Comment;

import java.util.ArrayList;

public class MyMessageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mCommentsRecyclerView;
    //어댑터 , 아이템리스트
    private CommentAdapter mCommentAdapter;
    private ArrayList<Comment> mCommentList;
    //위로당기면 새로고침
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //아이템객체
    private Comment mComment;
    //디비위치
    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 위치한곳
    DatabaseReference mMyMessageDatabaseReference; //여기에 값 추가

    private FirebaseAuth mFirebaseAuth; //인증객체(uid토큰 받기가능)
    private FirebaseUser mFirebaseUser; //uid토큰을 발급받은 즉, 인증이된 유저의 이메일같은 정보를 가져올 수 있다.
    private String mMyEmail; //내이메일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        //파이어베이스 인증
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mMyEmail = mFirebaseUser.getEmail();
        mMyMessageDatabaseReference = mRootDatabaseReference.child("indiviMessage");

        mCommentsRecyclerView = findViewById(R.id.mymessage_rev_comments);
        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(mCommentList, getApplicationContext());
        //아래구분선 세팅
        mCommentsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        // 리사이클러뷰에 레이아웃 매니저와 어댑터를 설정한다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true); //레이아웃매니저 생성
        mCommentsRecyclerView.setLayoutManager(layoutManager); ////만든 레이아웃매니저 객체를(설정을) 리사이클러 뷰에 설정해줌
        mCommentsRecyclerView.setAdapter(mCommentAdapter); //어댑터 셋

        //리사이클러뷰 끝까지 끌어당기면 새로고침하게 해주는 뷰 onRefresh()에 해당코드 구현
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mymessage_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        Log.d("MMMMM", "내이메일 : " + DataValidation.parsingEmail(mMyEmail));

        mMyMessageDatabaseReference.child(DataValidation.parsingEmail(mMyEmail)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                    Comment comment = dataSnapshot2.getValue(Comment.class);
                    mCommentList.add(comment);
                    mCommentAdapter.notifyItemInserted(mCommentList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRefresh() {
        mCommentAdapter.clear();
        mMyMessageDatabaseReference.child(DataValidation.parsingEmail(mMyEmail)).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //파이어베이스 인증
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mMyEmail = mFirebaseUser.getEmail();
    }
}
