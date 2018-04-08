package com.example.administrator.familyrecord.article;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.myConfig.FgManage;
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

public class ShowArticle extends AppCompatActivity {

    public  JSONObject object;
    private RecyclerView recyclerView;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;
    private String previousFloor;
    private String currentFloor;
    private String comment;
    private String myParentId;
    private String myParentNick;
    private String creator;
    JSONObject article = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_article);
        Intent intent = getIntent();
       article = new JSONObject();
        try {
            article = new JSONObject(intent.getStringExtra("article"));
            creator = article.getString("creator");
//            System.out.println(article);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView showTitle = (TextView) findViewById(R.id.show_title);
        WebView showContent = (WebView) findViewById(R.id.show_content);
        FloatingActionButton reply = (FloatingActionButton) findViewById(R.id.btn_reply);

        try {
            showTitle.setText(article.getString("title"));
            showContent.loadData(article.getString("content"),"text/html" , "utf-8");
            selectComment(article.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerViewUtil util = new RecyclerViewUtil(this,recyclerView);
        util.setOnItemClickListener(new RecyclerViewUtil.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                myParentId = list.get(position).get("parentId").toString();
                myParentNick = list.get(position).get("parentNickName").toString();
                currentFloor = list.get(position).get("floor").toString();
                Reply replywindow = new Reply(ShowArticle.this,1);
                replywindow.showPopupWindow(new View(ShowArticle.this));
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reply replywindow = new Reply(ShowArticle.this,2);
                replywindow.showPopupWindow(new View(ShowArticle.this));
            }
        });

    }

    private void selectComment(String id) {
        list.clear();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_comment);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置布局管理器，实现横向或数值滚动的列表布局

        final String articleId = id;
        Thread getcomment = new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject article = new JSONObject();
                try {
                    article.put("articleId",articleId);
                    article.put("pageNum",1);
                    article.put("pageSize",30);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HttpUtils hu = new HttpUtils();
                String selectCommentUrl = ConfigUtils.getProperties(getApplicationContext(), "selectCommentUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(),"host") + selectCommentUrl;
                jsonJX(hu.selectComment(url, article));

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
                                String id = object.getString("id");
                                String articleId = object.getString("articleId");
                                String parentId = object.getString("parentId");
                                String replyId = object.getString("replyId");
                                String comment = object.getString("comment");
                                int floor = object.getInt("floor");
                                String parentNickName = object.getString("parentNickName");
                                String replyNickName = object.getString("replyNickName");
                                //存入map
                                map.put("id", id);
                                map.put("articleId",articleId);
                                map.put("parentId",parentId);
                                map.put("replyId",replyId);
                                map.put("comment",comment);
                                map.put("floor",floor);
                                map.put("parentNickName",parentNickName);
                                map.put("replyNickName",replyNickName);

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
        getcomment.start();
        try {
            getcomment.join();
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
           currentFloor = list.get(i).get("floor").toString();

//            list.get(i).get("photo").toString();
            if (currentFloor == previousFloor){
                holder.info.setText("    " + list.get(i).get("replyNickName").toString() + " 回复 " + list.get(i).get("parentNickName").toString() + "：");
                holder.content.setText(list.get(i).get("comment").toString());
                holder.info.setTextSize(12);
                holder.content.setTextSize(12);
            }else {
                holder.info.setText(list.get(i).get("replyNickName").toString() + "：");
                holder.content.setText(list.get(i).get("comment").toString());
            }


            previousFloor = currentFloor;

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

    private class Reply extends PopupWindow {

        private Button btn_cancel;
        private Button btn_submit;
        private EditText content;
        private TextView tips;


        public Reply(Activity context, final int type) {
            // 通过layout的id找到布局View
            View contentView = LayoutInflater.from(context).inflate(R.layout.add_member_window, null);

            btn_cancel = (Button) contentView.findViewById(R.id.btn_addmember_cancel);
            btn_submit = (Button) contentView.findViewById(R.id.btn_addmember_submit);
            content = (EditText) contentView.findViewById(R.id.add_member_id) ;
            tips = (TextView) contentView.findViewById(R.id.window_tips);

            if (type==1){
                tips.setText("回复 " + myParentNick);
            }else {
                tips.setText("回复 " + creator);
            }



            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comment = content.getText().toString();

                    final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                    final String myId = sp.getString("username","");
                    final JSONObject reply = new JSONObject();
                    try {
                        reply.put("comment",comment);
                        reply.put("replyId",myId);
                        reply.put("articleId",article.getString("id"));
                        if (type==1){
                            reply.put("floor",currentFloor);
                            reply.put("parentId",myParentId);
                        }else {
                            reply.put("floor","");
                            reply.put("parentId",creator);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread(){
                        public void run(){
                            super.run();
                            Looper.prepare();

                            HttpUtils hu = new HttpUtils();
                            String replyArticleUrl = ConfigUtils.getProperties(getApplicationContext(), "replyArticleUrl");
                            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + replyArticleUrl;

                            JSONObject msg = hu.replyArticle(url,reply);

                            String alert = "";
                            try {
                                alert = msg.getString("msg").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(ShowArticle.this, alert, Toast.LENGTH_LONG).show();

                            Looper.loop();
                        }
                    }.start();

                    dismiss();
                }
            });

            // 获取PopupWindow的宽高
            int h = context.getWindowManager().getDefaultDisplay().getHeight();
            int w = context.getWindowManager().getDefaultDisplay().getWidth();
            // 设置PopupWindow的View
            this.setContentView(contentView);
            // 设置PopupWindow弹出窗体的宽高
            this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置PopupWindow弹出窗体可点击（下面两行代码必须同时出现）
            this.setFocusable(true);
            this.setOutsideTouchable(true); // 当点击外围的时候隐藏PopupWindow
            // 刷新状态
            this.update();
            // 设置PopupWindow的背景颜色为半透明的黑色
            ColorDrawable dw = new ColorDrawable(Color.parseColor("#ff99cc"));
            this.setBackgroundDrawable(dw);


            // 这里也可以从contentView中获取到控件，并为它们绑定控件
        }

        // 显示PopupWindow，有两种方法：showAsDropDown、showAtLocation
        public void showPopupWindow(View parent) {
            if (!this.isShowing()) {
                // showAsDropDown方法，在parent下方的(x,y)位置显示，x、y是第二和第三个参数
                // this.showAsDropDown(parent, parent.getWidth() / 2 - 400, 18);

                // showAtLocation方法，在parent的某个位置参数，具体哪个位置由后三个参数决定
                this.showAtLocation(parent, Gravity.CENTER, 0, 0);
            } else {
                this.dismiss();
            }
        }
    }
}
