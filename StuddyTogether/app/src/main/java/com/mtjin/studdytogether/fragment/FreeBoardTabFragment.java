package com.mtjin.studdytogether.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FreeBoardTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FreeBoardTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeBoardTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ViewGroup rootView;
    private RecyclerView mCommentsRecyclerView;
    private EditText mWriteEditText;
    private Button mSendButton;


    private String mId; //해당 게시물의 id
    private String mFreeBoard = "FreeBoard"; //게시판 노드
    private FirebaseAuth mFirebaseAuth; //인증객체(uid토큰 받기가능)
    //프로필
    private String mNickName;
    private String mImage;
    private String mAge;
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
    DatabaseReference mFreeBoardDatabaseReference; //여기에 값 추가

    public FreeBoardTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FreeBoardTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeBoardTabFragment newInstance(String param1, String param2) {
        FreeBoardTabFragment fragment = new FreeBoardTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_free_board_tab, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드올라올떄 UI 위로 떙겨줌
        mFreeBoardDatabaseReference = mRootDatabaseReference.child(mFreeBoard);
        loadShared();


        mCommentsRecyclerView = rootView.findViewById(R.id.free_rev_comments);
        mWriteEditText = rootView.findViewById(R.id.free_pt_write);
        mSendButton = rootView.findViewById(R.id.free_btn_send);

        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(mCommentList, getActivity());
        //아래구분선 세팅
        mCommentsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        // 리사이클러뷰에 레이아웃 매니저와 어댑터를 설정한다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true); //레이아웃매니저 생성
        mCommentsRecyclerView.setLayoutManager(layoutManager); ////만든 레이아웃매니저 객체를(설정을) 리사이클러 뷰에 설정해줌
        mCommentsRecyclerView.setAdapter(mCommentAdapter); //어댑터 셋

        //리사이클러뷰 끝까지 끌어당기면 새로고침하게 해주는 뷰 onRefresh()에 해당코드 구현
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.free_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);


        mFreeBoardDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

        return rootView;

    }
    // 자신의 프로필 정보 불러오기
    public void loadShared() {
        SharedPreferences pref = this.getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
        mNickName = pref.getString("nickName", "");
        mAge = pref.getString("age", "");
        mImage = pref.getString("image", "");
    }

    public void writeComment() {
        final String message = mWriteEditText.getText().toString();
        if (message.equals("")) { //빈칸인경우
            Toast.makeText(getActivity(), "한글자 이상 작성해야합니다.", Toast.LENGTH_SHORT).show();
        } else {
            mFreeBoardDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() { //삭제된 게시물인지 확인
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Calendar time = Calendar.getInstance();
                        String dates = format1.format(time.getTime()); //작성시간
                        mComment = new Comment(mNickName, mAge, mImage, dates, message, "freeBoard");

                        //리사이클러뷰에 쓴 글 추가
                    mFreeBoardDatabaseReference.push() //DB에 (MESSAGES_CHILD)messages라는 이름의 하위디렉토리(?)라는걸 만들고 여기다 데이터를 넣겠다고 생각하면된다.
                                .setValue(mComment); //DB에 데이터넣음

                        mCommentAdapter.clear();
                    mFreeBoardDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mWriteEditText.setText(""); //작성후 빈칸초기화
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        mCommentAdapter.clear();
        mFreeBoardDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
