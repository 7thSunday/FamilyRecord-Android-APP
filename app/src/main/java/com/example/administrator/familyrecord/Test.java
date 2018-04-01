package com.example.administrator.familyrecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test extends AppCompatActivity {

    public  JSONObject object;
    private RecyclerView recyclerView;
    private Context context;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        init();

    }

    void init() {
        list.clear();


        context = getApplicationContext();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_album2);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);

        //设置布局管理器，实现横向或数值滚动的列表布局

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Thread getImage = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                String groupId = sp.getString("groupId", null);
                JSONObject user = new JSONObject();
                try {
                    user.put("groupId", groupId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String selectAlbumUrl = ConfigUtils.getProperties(getApplicationContext(), "selectAlbumUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(), "host") + selectAlbumUrl;
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
                                //获取到json数据中的的内容filePath
                                String coverPath = object.getString("filePath");
                                //获取到json数据中的内容albumName
                                String albumName = object.getString("albumName");
                                //存入map
                                map.put("coverPath", coverPath);
                                map.put("albumName", albumName);
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
        getImage.start();
        try {
            getImage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //ArrayList集合
        mAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(mAdapter);

    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mAdapter = new RecyclerViewAdapter(list);
                    recyclerView.setAdapter(mAdapter);
//                    mAdapter.onBindViewHolder(mAdapter.onCreateViewHolder(recyclerView,0),0);
                    break;
            }

        }

    };

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder> {

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
        //将数据模型的title和publishtime赋给两个TextView显示

        @Override
        public void  onBindViewHolder(myViewHolder holder, int i) {

            //list.get(i);
            holder.albumName.setText(list.get(i).get("albumName").toString());
            holder.albumCover.setImageUrl("http://10.77.115.148:8080//FamilyRecord///images/photo/a90708a84ed44aeba986b334f2281ea1");

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public  class myViewHolder extends RecyclerView.ViewHolder{
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
}
