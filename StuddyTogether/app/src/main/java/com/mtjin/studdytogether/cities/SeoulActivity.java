package com.mtjin.studdytogether.cities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mtjin.studdytogether.MainActivity;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.WriteActivity;
import com.mtjin.studdytogether.realtime_database.StudyMessage;

import de.hdodenhof.circleimageview.CircleImageView;


public class SeoulActivity extends AppCompatActivity {
    static final String TAG = "SeoulActivityTAG";
    static final int WRITE = 100;
    //자신 프로필
    private String mNickName;
    private String mSex; //성별 남자 or 여자
    private String mEmail; //이메일
    private String mAge; //나이대
    private String mImage; //프로필사진

    private StudyMessage mStudyMessage; //글 아이템

    private FirebaseRecyclerAdapter<StudyMessage, MessageViewHolder> mFirebaseAdapter;

    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference(); //데이터베이스 위치한곳
    DatabaseReference mSeoulDatabaseReference = mRootDatabaseReference.child("seoulStudy"); //profile이란 이름의 하위 데이터베이스

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView nickNameTextView;
        TextView ageTextView;
        ImageView messageImageView;
        TextView messageTextView;
        CircleImageView photoImageView;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.message_tv_title);
            nickNameTextView = itemView.findViewById(R.id.message_tv_name);
            ageTextView = itemView.findViewById(R.id.message_tv_age);
            messageImageView = itemView.findViewById(R.id.message_iv_photo); //업로드한사진
            messageTextView = itemView.findViewById(R.id.message_tv_message);
            photoImageView = itemView.findViewById(R.id.message_iv_profile); //내 프로필사진
        }
    }

    private RecyclerView mMessageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seoul);
        setTitle("서울지역");

        mMessageRecyclerView = findViewById(R.id.seoul_rev_message); //채팅메세지들 리사이클러뷰

        findViewById(R.id.seoul_tv_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeoulActivity.this, WriteActivity.class);
                startActivityForResult(intent, WRITE);
            }
        });

        Query query = mSeoulDatabaseReference; //쿼리문의 수행위치 저장 (파이어베이스 리얼타임데이터베이스의 하위에있는 MESSAGES_CHILD에서 데이터를 가져오겠다는 뜻이다. ==> 메세지를 여기다 저장했으므로)
        FirebaseRecyclerOptions<StudyMessage> options = new FirebaseRecyclerOptions.Builder<StudyMessage>() //어떤데이터를 어디서갖고올거며 어떠한 형태의 데이터클래스 결과를 반환할거냐 옵션을 정의한다.
                .setQuery(query, StudyMessage.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<StudyMessage, MessageViewHolder>(options) { //위에서정의한 옵션을 넣어줌줌
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull StudyMessage model) { //홀더에 세팅만해주면됨 (modeld에 데이터가 다들어와있으니 그냥넣어주면됨)
                holder.titleTextView.setText(model.getTitle());
                holder.nickNameTextView.setText(model.getNickName());
                holder.ageTextView.setText(model.getAge());
                holder.messageTextView.setText(model.getContent());
                if(model.getImage().equals("basic")) { //프로필사진이 없는경우
                    Glide.with(SeoulActivity.this).load("https://firebasestorage.googleapis.com/v0/b/studdytogether.appspot.com/o/Basisc%2FbasicProfile.png?alt=media&token=dd0e0e17-a057-40a4-ae7f-364fa529e2a3").into(holder.photoImageView);
                }else{
                    Glide.with(SeoulActivity.this).load(model.getImage()).into(holder.photoImageView);
                }
               if(model.getPhoto() != "basic") {
                   Glide.with(SeoulActivity.this).load(model.getPhoto()).into(holder.messageImageView);
               }else{
                   //사진첨부안했으니 안올림
               }
            }

            @NonNull
            @Override //뷰홀더가 만들어지는 시점에 호출된다
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_message, viewGroup, false); //우리가쓸려는 chatMessage아이템의 뷰객체 생성
                return  new MessageViewHolder(view); //각각의 chatMessage아이템을 위한 뷰를 담고있는 뷰홀더객체를 반환한다.
            }
        };

        // 리사이클러뷰에 레이아웃 매니저와 어댑터를 설정한다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        mMessageRecyclerView.setLayoutManager(layoutManager); ////만든 레이아웃매니저 객체를(설정을) 리사이클러 뷰에 설정해줌
        mMessageRecyclerView.setAdapter(mFirebaseAdapter); //어댑터 셋 ( 파이어베이스 어댑터는 액티비티 생명주기에 따라서 상태를 모니터링하게하고 멈추게하고 그런 코드를 작성하도록 되있다.==> 밑에 onStart()와 onStop에 구현해놨다)

        // 키보드 올라올 때 RecyclerView의 위치를 마지막 포지션으로 이동
        mMessageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMessageRecyclerView.smoothScrollToPosition(mFirebaseAdapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });

        // 새로운 글이 추가되면 제일 하단으로 포지션 이동
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
                mStudyMessage = new StudyMessage(title, mNickName, contents, mImage, imageUri,mAge);

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
        mFirebaseAdapter.startListening();  // FirebaseRecyclerAdapter 실시간 쿼리 시작
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening(); // FirebaseRecyclerAdapter 실시간 쿼리 중지
    }


}
