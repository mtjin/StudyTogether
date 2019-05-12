package com.mtjin.studdytogether.cities_view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.view.WriteActivity;
import com.mtjin.studdytogether.adapter.MessageAdapter;
import com.mtjin.studdytogether.rtdb_model.StudyMessage;

import java.util.ArrayList;


public class SeoulActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    static final String TAG = "SeoulActivityTAG";
    static final int WRITE = 100;
    //자신 프로필
    private String mNickName;
    private String mSex; //성별 남자 or 여자
    private String mEmail; //이메일
    private String mAge; //나이대
    private String mImage; //프로필사진

    private StudyMessage mStudyMessage; //글 아이템

    private MessageAdapter mMessageAdapter;

    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 위치한곳
    DatabaseReference mSeoulDatabaseReference = mRootDatabaseReference.child("seoulStudy"); //profile이란 이름의 하위 데이터베이스

    private EditText mSearchEditText; //검색텍스트
    private RecyclerView mMessageRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<StudyMessage> mMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seoul);
        setTitle("서울지역");

        mSearchEditText = findViewById(R.id.seoul_et_search); //검색
        mMessageRecyclerView = findViewById(R.id.seoul_rev_message); //채팅메세지들 리사이클러뷰

        findViewById(R.id.seoul_tv_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeoulActivity.this, WriteActivity.class);
                startActivityForResult(intent, WRITE);
            }
        });

        mSearchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE); // 키보드 확인 버튼 클릭시
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) { //처리할 일
                    if(mSearchEditText.getText().toString().trim().length() >=2) {
                        searchPost(mSearchEditText.getText().toString().trim());
                        return true;
                    }
                }
                Toast.makeText(getApplicationContext(), "두글자 이상입력해야합니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //리사이클러뷰 끝까지 끌어당기면 새로고침하게 해주는 뷰 onRefresh()에 해당코드 구현
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.seoul_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mMessageList = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mMessageList, getApplicationContext());

        //아래구분선 세팅
        mMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        // 리사이클러뷰에 레이아웃 매니저와 어댑터를 설정한다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true); //레이아웃매니저 생성
        layoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(layoutManager); ////만든 레이아웃매니저 객체를(설정을) 리사이클러 뷰에 설정해줌
        mMessageRecyclerView.setAdapter(mMessageAdapter); //어댑터 셋 ( 파이어베이스 어댑터는 액티비티 생명주기에 따라서 상태를 모니터링하게하고 멈추게하고 그런 코드를 작성하도록 되있다.==> 밑에 onStart()와 onStop에 구현해놨다)

        mSeoulDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                    StudyMessage studyMessage = dataSnapshot2.getValue(StudyMessage.class);
                    String id = dataSnapshot2.getKey();
                    studyMessage.setId(id);
                    studyMessage.setCity("seoulStudy");

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mMessageList.add(studyMessage);
                    mMessageAdapter.notifyItemInserted(mMessageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* // 키보드 올라올 때 RecyclerView의 위치를 마지막 포지션으로 이동
        mMessageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMessageRecyclerView.smoothScrollToPosition(mMessageAdapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });*/


        /*// 새로운 글이 추가되면 제일 하단으로 포지션 이동
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                LinearLayoutManager layoutManager = (LinearLayoutManager) mMessageRecyclerView.getLayoutManager();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });*/
    }

    //검색기능
    public void searchPost(final String searchText) {
        mMessageAdapter.clear();
        mSeoulDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                    StudyMessage studyMessage = dataSnapshot2.getValue(StudyMessage.class);
                    String id = dataSnapshot2.getKey();
                    studyMessage.setId(id);
                    studyMessage.setCity("seoulStudy");
                    //해당 텍스트의 메세지가 있으면 어댑터에 추가해줌(제목하고 내용검색)
                    if(studyMessage.getTitle().contains(searchText) || studyMessage.getContent().contains(searchText)) {
                        mMessageList.add(studyMessage);
                    }
                }
                mMessageAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //작성하기 결과
        if (requestCode == WRITE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String title = bundle.getString("title");
                String contents = bundle.getString("contents");
                String imageUri = bundle.getString("donwnloadImageUri"); //글에올린사진
                String dates = bundle.getString("dates");
                mStudyMessage = new StudyMessage(title, mNickName, contents, mImage, imageUri, mAge, dates);

                //리사이클러뷰에 쓴 글 추가
                mSeoulDatabaseReference.push() //DB에 (MESSAGES_CHILD)messages라는 이름의 하위디렉토리(?)라는걸 만들고 여기다 데이터를 넣겠다고 생각하면된다.
                        .setValue(mStudyMessage); //DB에 데이터넣음
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //프로필 정보받아옴
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        mEmail = pref.getString("email", "");
        mNickName = pref.getString("nickName", "");
        mSex = pref.getString("sex", "");
        mAge = pref.getString("age", "");
        mImage = pref.getString("image", "");
        Log.d(TAG, mImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mFirebaseAdapter.startListening();  // FirebaseRecyclerAdapter 실시간 쿼리 시작
    }

    @Override
    protected void onStop() {
        super.onStop();
        // mFirebaseAdapter.stopListening(); // FirebaseRecyclerAdapter 실시간 쿼리 중지
    }


    @Override //리사이클러뷰 위로당겼을때 호출
    public void onRefresh() {
        mMessageAdapter.clear();
        mSeoulDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) { //하위노드가 없을 떄까지 반복

                    StudyMessage studyMessage = dataSnapshot2.getValue(StudyMessage.class);
                    String id = dataSnapshot2.getKey();
                    studyMessage.setId(id);
                    studyMessage.setCity("seoulStudy");
                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mMessageList.add(studyMessage);
                }
                mMessageAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
