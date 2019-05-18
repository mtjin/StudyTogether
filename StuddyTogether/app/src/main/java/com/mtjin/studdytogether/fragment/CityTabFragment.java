package com.mtjin.studdytogether.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mtjin.studdytogether.R;
import com.mtjin.studdytogether.interfaces.CallCityInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CityTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CityTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ViewGroup rootView;
    private CallCityInterface mCallCityInterface;
    //지역버튼
    Button seoulButton;
    Button gyeonggiButton;
    Button incheonButton;
    Button gangwonButton;
    Button chungnamButton;
    Button daegeonButton;
    Button chungbukButton;
    Button sejongButton;
    Button busanButton;
    Button ulsanButton;
    Button daeguButton;
    Button kyungbukButton;
    Button kyungnamButton;
    Button jeonnamButton;
    Button gwangjuButton;
    Button jeonbukButton;
    Button jejuButton;
    Button allcityButton;

    public CityTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CityTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CityTabFragment newInstance(String param1, String param2) {
        CityTabFragment fragment = new CityTabFragment();
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
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_city_tab, container, false);
        seoulButton = rootView.findViewById(R.id.citytab_btn_seoul);
        gyeonggiButton =rootView.findViewById(R.id.citytab_btn_gyeonggi);
        incheonButton = rootView.findViewById(R.id.citytab_btn_incheon);
        gangwonButton = rootView.findViewById(R.id.citytab_btn_gangwon);
        chungnamButton = rootView.findViewById(R.id.citytab_btn_chungnam);
        daegeonButton = rootView.findViewById(R.id.citytab_btn_daegeon);
        chungbukButton = rootView.findViewById(R.id.citytab_btn_chungbuk);
        sejongButton = rootView.findViewById(R.id.citytab_btn_sejong);
        busanButton = rootView.findViewById(R.id.citytab_btn_busan);
        ulsanButton = rootView.findViewById(R.id.citytab_btn_ulsan);
        daeguButton = rootView.findViewById(R.id.citytab_btn_daegu);
        kyungbukButton = rootView.findViewById(R.id.citytab_btn_kyungbuk);
        kyungnamButton = rootView.findViewById(R.id.citytab_btn_kyungnam);
        jeonnamButton = rootView.findViewById(R.id.citytab_btn_jeonnam);
        gwangjuButton = rootView.findViewById(R.id.citytab_btn_gwangju);
        jeonbukButton = rootView.findViewById(R.id.citytab_btn_jeonbuk);
        jejuButton = rootView.findViewById(R.id.citytab_btn_jeju);
        allcityButton = rootView.findViewById(R.id.citytab_btn_allcity);

        //버튼클릭
        onClickButton();

        return rootView;
    }

    public void onClickButton(){
        seoulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("seoul");
            }
        });
        gyeonggiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("gyenggi");
            }
        });
        incheonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("incheon");
            }
        });
        gangwonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("gangwon");
            }
        });
        chungnamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("chungnam");
            }
        });
        daegeonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("daegeon");
            }
        });
        chungbukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("chungbuk");
            }
        });
        sejongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("sejong");
            }
        });
        busanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("busani");
            }
        });
        ulsanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("ulsan");
            }
        });
        daeguButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("daegu");
            }
        });
        kyungbukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("kyungbuk");
            }
        });;
        kyungnamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("kyungnam");
            }
        });
        jeonnamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("jeonnam");
            }
        });
        gwangjuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("gwangju");
            }
        });
        jeonbukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("jeonbuk");
            }
        });
        jejuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("jeju");
            }
        });
        allcityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallCityInterface.callCity("allcity");
            }
        });
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
        if(context instanceof CallCityInterface){
            mCallCityInterface = (CallCityInterface) context;
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
