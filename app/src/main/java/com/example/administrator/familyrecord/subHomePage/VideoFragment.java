package com.example.administrator.familyrecord.subHomePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.example.administrator.familyrecord.utils.GetPath;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.example.administrator.familyrecord.utils.RecyclerViewUtil;
import com.example.administrator.familyrecord.video.PlayVideo;
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
 * {@link VideoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public JSONObject object;
    private RecyclerView recyclerView;
    private Context context;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;
    private  String srcPath;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
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
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.video_swipe_refresh);
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
                Intent intent = new Intent(getActivity(), PlayVideo.class);
                intent.putExtra("path",list.get(position).get("filePath").toString());
                startActivity(intent);
            }
        });

        FloatingActionButton uploadVideo = (FloatingActionButton) getView().findViewById(R.id.btn_video_upload);
        FloatingActionButton selectVideo = (FloatingActionButton) getView().findViewById(R.id.btn_select_video);
        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sp=getActivity().getSharedPreferences("login",MODE_PRIVATE);
                final String creator = sp.getString("username","");
                final String id = sp.getString("groupId","");


                final JSONObject user = new JSONObject();
                try {
                    user.put("type","2");
                    user.put("creator",creator);
                    user.put("rId",id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(srcPath==null){
                    Toast.makeText(getActivity(), "请选择一段视频", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(){
                        public void run(){

                            HttpUtils hu = new HttpUtils();
                            String uploadUrl = ConfigUtils.getProperties(getActivity().getApplicationContext(), "uploadUrl");
                            String url = ConfigUtils.getProperties(getActivity().getApplicationContext(),"host") + uploadUrl;

                            hu.upload(url,srcPath,user);

                        }

                    }.start();
                }
            }
        });
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent local = new Intent();
                local.setType("video/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 3);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            Uri uri = data.getData();
            GetPath path = new GetPath();
            srcPath = path.getPath(getActivity().getApplicationContext(),uri);


        }
    }

    private void init() {
        list.clear();

        context = getActivity().getApplicationContext();
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_video);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);

        //设置布局管理器，实现横向或数值滚动的列表布局

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //ArrayList集合

        Thread getVideo = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                String groupId = sp.getString("groupId", null);
                JSONObject user = new JSONObject();
                try {
                    user.put("rId", groupId);
                    user.put("type","2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String selectVideoUrl = ConfigUtils.getProperties(getActivity().getApplicationContext(), "selectVideoUrl");
                String url = ConfigUtils.getProperties(getActivity().getApplicationContext(), "host") + selectVideoUrl;
                HttpUtils hu = new HttpUtils();
                jsonJX(hu.selectVideo(url, user));


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
                                String videoPath = object.getString("filePath");
//                                String id = object.getString("id");
//                                int type = object.getInt("type");
                                String videoName = object.getString("fileName");
                                //存入map
                                map.put("filePath", videoPath);
                                map.put("fileName", videoName);
//                                map.put("id",id);
//                                map.put("type",type);

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
        getVideo.start();
        try {
            getVideo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(mAdapter);


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

//            list.get(i).get("albumCover").toString();
            holder.videoName.setText(list.get(i).get("fileName").toString());
            holder.albumCover.setImageResource(R.drawable.video);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView videoName;
            SmartImageView albumCover;
            public myViewHolder(View itemView)
            {
                super(itemView);
                videoName = (TextView)itemView.findViewById(R.id.album_name);
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
