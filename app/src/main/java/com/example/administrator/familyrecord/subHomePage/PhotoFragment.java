package com.example.administrator.familyrecord.subHomePage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.album.ShowAlbum;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.example.administrator.familyrecord.utils.RecyclerViewUtil;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    public  JSONObject object;
    private RecyclerView recyclerView;
    private Context context;
    public  ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public  RecyclerViewAdapter mAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
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
//        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        //init();

        // Inflate the layout for this fragment
        return view;
    }

    private void init() {
        list.clear();

        context = getActivity().getApplicationContext();
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_album);
    //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);

    //设置布局管理器，实现横向或数值滚动的列表布局

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //ArrayList集合

        Thread getAlbum = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                String groupId = sp.getString("groupId", null);
                JSONObject user = new JSONObject();
                try {
                    user.put("groupId", groupId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String selectAlbumUrl = ConfigUtils.getProperties(getActivity().getApplicationContext(), "selectAlbumUrl");
                String url = ConfigUtils.getProperties(getActivity().getApplicationContext(), "host") + selectAlbumUrl;
                HttpUtils hu = new HttpUtils();
                jsonJX(hu.selectAlbum(url, user));


            }

            private void jsonJX(JSONArray data) {
                //判断数据是空
                if (data != null) {
                    try {
                        //遍历
                        for (int i = 0; i < data.length(); i++) {
                            object = data.getJSONObject(i);

                            Map<String, Object> map = new HashMap<String, Object>();

                            try {
                                //获取到json数据中的内容
                                String coverPath = object.getString("filePath");
                                String id = object.getString("id");
                                int type = object.getInt("type");
                                String albumName = object.getString("albumName");
                                //存入map
                                map.put("albumCover", coverPath);
                                map.put("albumName", albumName);
                                map.put("id",id);
                                map.put("type",type);

                                //ArrayList集合
                                list.add(map);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        getAlbum.start();
        try {
            getAlbum.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.album_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                mRefreshLayout.setRefreshing(false);
            }
        });
        RecyclerViewUtil util = new RecyclerViewUtil(getActivity(),recyclerView);
        util.setOnItemClickListener(new RecyclerViewUtil.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                //System.out.println(list.get(position));
                //Toast.makeText(getActivity().getApplicationContext(),position+" 单击",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ShowAlbum.class);
                intent.putExtra("id",list.get(position).get("id").toString());
                intent.putExtra("type",list.get(position).get("type").toString());
                startActivity(intent);
            }
        });
    }



    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder>{

        private ArrayList data;

        public RecyclerViewAdapter(ArrayList<Map<String, Object>> list) {
            this.data = list;
        }



        @Override
        public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            myViewHolder holder=new myViewHolder(View.inflate(viewGroup.getContext(), R.layout.album_item, null));
            return holder;
        }
        //绑定数据


        @Override
        public void  onBindViewHolder(myViewHolder holder, int i) {

            String projectName = ConfigUtils.getProperties(getActivity().getApplicationContext(), "project");
            String url = ConfigUtils.getProperties(getActivity().getApplicationContext(),"host") + projectName;
            url += list.get(i).get("albumCover")==null? "":list.get(i).get("albumCover").toString();
            holder.albumName.setText(list.get(i).get("albumName").toString());
            holder.albumCover.setImageUrl(url);


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView albumName;
            SmartImageView albumCover;
            public myViewHolder(View itemView)
            {
                super(itemView);
                albumName = (TextView)itemView.findViewById(R.id.album_name);
                albumCover = (SmartImageView) itemView.findViewById(R.id.album_cover);
            }
        }
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
