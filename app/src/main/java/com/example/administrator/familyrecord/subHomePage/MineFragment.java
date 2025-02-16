package com.example.administrator.familyrecord.subHomePage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.familyrecord.myConfig.AccountConfig;
import com.example.administrator.familyrecord.myConfig.FgManage;
import com.example.administrator.familyrecord.Login;
import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.loopj.android.image.SmartImageView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View view;
    private Button signout;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
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
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        Button reload = (Button) getView().findViewById(R.id.btn_reload_mine);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private void init(){
        Button signout = (Button) view.findViewById(R.id.btn_signout);
        Button manageFg = (Button) view.findViewById(R.id.btn_fgmanage);
        Button myConfig = (Button) view.findViewById(R.id.btn_accountconfig);
        SmartImageView myhead = (SmartImageView) view.findViewById(R.id.myhead);
        TextView myId = (TextView) view.findViewById(R.id.show_accountid);
        TextView myNick = (TextView) view.findViewById(R.id.show_nickname);
        TextView myFgName = (TextView) view.findViewById(R.id.my_group_name);


        SharedPreferences sp=getActivity().getSharedPreferences("login",MODE_PRIVATE);
        myFgName.setText("我的家庭组：" + sp.getString("groupName",""));
        String path = sp.getString("headImageUrl","null");
        String creator = sp.getString("creator","");
        String user = sp.getString("username","null");

        if (!creator.equals(user)){
            manageFg.setEnabled(false);
            manageFg.setBackgroundColor(Color.parseColor("#8a8a8a"));
        }

        String projectName = ConfigUtils.getProperties(getActivity().getApplicationContext(), "project");
        String url = ConfigUtils.getProperties(getActivity().getApplicationContext(), "host") + projectName;
        url += path;

        myhead.setImageUrl(url);
        myId.setText("账户："+ sp.getString("username",""));
        myNick.setText("昵称：" + sp.getString("nickName",""));

        myConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountConfig.class);
                startActivity(intent);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp=getActivity().getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor =sp.edit();
                editor.putString("username","");
                editor.putString("password","");
                editor.putString("groupId","null");
                editor.putString("groupName","null");
                editor.putString("creator","null");
                editor.putString("nickName","null");
                editor.putString("headImageUrl","url");
                editor.commit();
                Toast.makeText(getActivity(), "已注销，请重新登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        manageFg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FgManage.class);
                startActivity(intent);
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
