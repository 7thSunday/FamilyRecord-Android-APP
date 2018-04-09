package com.example.administrator.familyrecord.article;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowMessage extends AppCompatActivity {

    public  JSONObject object;
    private RecyclerView recyclerView;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_message);

        init();
    }

    private void init() {
        list.clear();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_message);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置布局管理器，实现横向或数值滚动的列表布局

        Thread getMessage = new Thread(new Runnable() {
            @Override
            public void run() {


                final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
//        final SharedPreferences.Editor editor =sp.edit();
                final String usern = sp.getString("username","");
                final JSONObject user = new JSONObject();
                try {
                    user.put("account",usern);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpUtils hu = new HttpUtils();
                String getMessageListUrl = ConfigUtils.getProperties(getApplicationContext(), "getMessageListUrl");
                final String url = ConfigUtils.getProperties(getApplicationContext(),"host") + getMessageListUrl;
                jsonJX(hu.getMessage(url, user));


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
                                String articleTitle = object.getString("articleTitle");
                                String replyNickName = object.getString("replyNickName");
                                String comment = object.getString("comment");
                                //存入map
                                map.put("articleTitle", articleTitle);
                                map.put("replyNickName", replyNickName);
                                map.put("comment",comment);

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
        getMessage.start();
        try {
            getMessage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(mAdapter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder> {

        private ArrayList data;

        public RecyclerViewAdapter(ArrayList<Map<String, Object>> list) {
            this.data = list;
        }



        @Override
        public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            RecyclerViewAdapter.myViewHolder holder= new RecyclerViewAdapter.myViewHolder(View.inflate(viewGroup.getContext(), R.layout.comment_item, null));
            return holder;
        }
        //绑定数据


        @Override
        public void  onBindViewHolder(myViewHolder holder, int i) {

            holder.info.setText(list.get(i).get("replyNickName").toString() + "在《" + list.get(i).get("articleTitle").toString() + "》中回复了：");
            holder.info.setTextColor(Color.parseColor("#ff99cc"));
            holder.info.setTextSize(14);
            holder.content.setText(list.get(i).get("comment").toString());
            holder.content.setTextSize(17);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView info;
            TextView content;
            public myViewHolder(View itemView)
            {
                super(itemView);
                info = (TextView)itemView.findViewById(R.id.comment_info);
                content = (TextView) itemView.findViewById(R.id.comment_content);
            }
        }
    }
}
