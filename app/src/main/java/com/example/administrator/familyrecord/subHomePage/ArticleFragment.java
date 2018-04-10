package com.example.administrator.familyrecord.subHomePage;

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

import com.example.administrator.familyrecord.article.ArticleEditor;
import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.article.ShowMessage;
import com.example.administrator.familyrecord.article.ShowArticle;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.example.administrator.familyrecord.utils.Marquee;
import com.example.administrator.familyrecord.utils.RecyclerViewUtil;

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
 * {@link ArticleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public JSONObject object;
    private RecyclerView recyclerView;
    private Context context;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;
    int pageNumber;
    JSONArray arr;
//    private  String srcPath;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArticleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleFragment newInstance(String param1, String param2) {
        ArticleFragment fragment = new ArticleFragment();
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
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pageNumber = 1;
        init();
        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.article_swipe_refresh);
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
                JSONObject article = new JSONObject();
                try {
                    article.put("title",list.get(position).get("title").toString());
                    article.put("id",list.get(position).get("id").toString());
                    article.put("rId",list.get(position).get("rId").toString());
                    article.put("content",list.get(position).get("content").toString());
                    article.put("creator",list.get( position).get("creator").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), ShowArticle.class);
                intent.putExtra("article",article.toString());
                startActivity(intent);
            }
        });

        FloatingActionButton write = (FloatingActionButton) getView().findViewById(R.id.btn_new_article);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ArticleEditor.class);
                startActivity(intent);
            }
        });
        FloatingActionButton message = (FloatingActionButton) getView().findViewById(R.id.btn_message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowMessage.class);
                startActivity(intent);
            }
        });

        getNotification();

    }

    private void getNotification(){

        final SharedPreferences sp=getActivity().getSharedPreferences("login",MODE_PRIVATE);
//        final SharedPreferences.Editor editor =sp.edit();
        final String groupId = sp.getString("groupId","");
        final JSONObject news = new JSONObject();
        try {
            news.put("topNum",0);
            news.put("groupId",groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
         Thread getNews = new Thread(new Runnable() {
             @Override
             public void run() {
                 HttpUtils hu = new HttpUtils();
                 String getHomePageInfoUrl = ConfigUtils.getProperties(getActivity().getApplicationContext(), "getHomePageInfoUrl");
                 String url = ConfigUtils.getProperties(getActivity().getApplicationContext(),"host") + getHomePageInfoUrl;
                 arr = hu.getNews(url,news);
             }
        });
        getNews.start();
        try {
            getNews.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject result = (JSONObject) arr.get(i);
                TextView show = (TextView) getView().findViewById(R.id.birthday_notification);
                String content = show.getText().toString();
                content += result.getString("nickName").toString() + "的生日" + result.getString("birthday").toString() + "快到了哦~";
                if (i+1<arr.length()) content += "\n";
                show.setText(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        list.clear();

        context = getActivity().getApplicationContext();
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view_article);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);

        //设置布局管理器，实现横向或数值滚动的列表布局

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //ArrayList集合

        Thread getArticle = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                String groupId = sp.getString("groupId", null);
                JSONObject select = new JSONObject();
                try {
                    select.put("rId", groupId);
                    select.put("pageSize",10);
                    select.put("pageNum",pageNumber);
//                    user.put("type","2");pageNumber,pageSize
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String selectArticleUrl = ConfigUtils.getProperties(getActivity().getApplicationContext(), "selectArticleUrl");
                String url = ConfigUtils.getProperties(getActivity().getApplicationContext(), "host") + selectArticleUrl;
                HttpUtils hu = new HttpUtils();
                jsonJX(hu.selectArticle(url, select));


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
                                String title = object.getString("title");
                                String id = object.getString("id");
                                String rId = object.getString("rId");
                                String creator = object.getString("creator");
                                String content = object.getString("content");
                                String createTime = object.getString("createTime");
                                //存入map
                                map.put("title", title);
                                map.put("content", content);
                                map.put("id",id);
                                map.put("rId",rId);
                                map.put("creator",creator);
                                map.put("createTime",createTime);

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
        getArticle.start();
        try {
            getArticle.join();
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
            myViewHolder holder=new myViewHolder(View.inflate(viewGroup.getContext(), R.layout.article_item, null));
            return holder;
        }
        //绑定数据

        @Override
        public void  onBindViewHolder(myViewHolder holder, int i) {

//            list.get(i).get("albumCover").toString();
            holder.title.setText(list.get(i).get("title").toString());
            holder.info .setText("作者："+list.get(i).get("creator")+" "+"发表时间："+list.get(i).get("createTime"));
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView title;
            TextView info;
            public myViewHolder(View itemView)
            {
                super(itemView);
                title = (TextView)itemView.findViewById(R.id.article_title);
                info = (TextView) itemView.findViewById(R.id.article_info);
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
