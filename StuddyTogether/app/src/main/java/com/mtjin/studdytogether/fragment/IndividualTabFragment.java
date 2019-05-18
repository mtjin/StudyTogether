package com.mtjin.studdytogether.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.function.DataValidation;
import com.mtjin.studdytogether.interfaces.MyMessageInterface;
import com.mtjin.studdytogether.rtdb_model.Comment;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IndividualTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IndividualTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndividualTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static final String TAG = "IndividualTabFragTAG";

    private MyMessageInterface mMyMessageInterface;
    ViewGroup rootView;
    private EditText mReceiverEditText;
    private Button mCheckSameButton;
    private Button mMyMessageButton;
    private EditText mContentEditText;
    private Button mSendButton;
    private String mReceiverEdit;
    private String mReceiverId;
    private String mReceiverEmail; //수신인의 이메일
    private String mTmpReceiverEdit; //중복유무한 아이디와 같은 아이디인지
    private String mContent; //내용물
    Boolean canSend; //보낼수있는지 (닉네임 확인 유무)
    //내 프로필
    String mNickName;
   String mAge;
   String mImage;
    //날짜포맷
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd mm:ss");
    //유저토큰
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser;
    private String mUid;

    //DB
    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mIndivMessageDatabaseReference;

    private OnFragmentInteractionListener mListener;

    public IndividualTabFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static IndividualTabFragment newInstance(String param1, String param2) {
        IndividualTabFragment fragment = new IndividualTabFragment();
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
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_individual_tab, container, false);
        mReceiverEditText = rootView.findViewById(R.id.indiv_pt_receiver);
        mCheckSameButton = rootView.findViewById(R.id.indiv_btn_checkNick);
        mMyMessageButton = rootView.findViewById(R.id.indiv_btn_myreceive);
        mContentEditText = rootView.findViewById(R.id.indiv_pt_content);
        mSendButton = rootView.findViewById(R.id.indiv_btn_send);
        loadShared(); //프로필정보 쉐어드 가져옴
        mCheckSameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiverEdit = mReceiverEditText.getText().toString().trim();
                mTmpReceiverEdit = mReceiverEditText.getText().toString().trim();
                if (mReceiverEdit.equals("")) {
                    Toast.makeText(getActivity(), "아이디를 입력해야합니다", Toast.LENGTH_SHORT).show();
                } else {
                    mRootDatabaseReference.child("nickNameList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(mReceiverEdit)) {
                                canSend = true;
                                mReceiverEmail = (String) dataSnapshot.child(mReceiverEdit).getValue();
                                Log.d(TAG, "수신인 이메일: " + mReceiverEmail);
                                Toast.makeText(getActivity(), "해당 닉네임으로 메세지를 보낼 수 있습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "해당 닉네임은 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        //전송버튼
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContent = mContentEditText.getText().toString().trim(); //보내는메세지
                if(canSend && mTmpReceiverEdit.equals(mReceiverEdit) && !mContent.equals("") ) { //보내기가능
                    mIndivMessageDatabaseReference = mRootDatabaseReference.child("indiviMessage");
                    //작성시간 put
                    Calendar time = Calendar.getInstance();
                    String dates = format1.format(time.getTime());
                    Comment comment = new Comment(mNickName, mAge, mImage, dates, mReceiverEdit, mUid );
                    mIndivMessageDatabaseReference.child(DataValidation.parsingEmail(mReceiverEmail)).push().setValue(comment); //해당아이디의 이메일을 노드로 주소보냄 그래야 아이디바꿔도 예외없으므로
                    Toast.makeText(getContext(), "메세지 송신 완료", Toast.LENGTH_SHORT);
                    mContentEditText.setText("");//텍스트초기화
                }else{ //
                    Toast.makeText(getActivity(), "닉네임 유무확인과 메세지를 적어주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //내메세지 확인버튼
        mMyMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyMessageInterface.showMyMessage();
            }
        });


        return rootView;
    }

    // 쉐어드값을 불러오는 메소드
    private void loadShared() {
        SharedPreferences pref = getActivity().getSharedPreferences("profile", getActivity().MODE_PRIVATE);
        mNickName = pref.getString("nickName", "");
        mAge = pref.getString("age", "");
        mImage = pref.getString("image", "");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        canSend =false;
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseAuth.getUid();   //사용자 고유 토큰 받아옴
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
        if(context instanceof MyMessageInterface){
            mMyMessageInterface = (MyMessageInterface) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
