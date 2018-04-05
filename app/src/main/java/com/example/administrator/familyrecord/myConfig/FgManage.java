package com.example.administrator.familyrecord.myConfig;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.example.administrator.familyrecord.utils.RecyclerViewUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FgManage extends AppCompatActivity {

    public  JSONObject object;
    private RecyclerView recyclerView;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;
    AddMember addWindow;

    String addMemberId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fg_manage);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.btn_add_member) ;

        init();

        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.member_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                mRefreshLayout.setRefreshing(false);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWindow = new AddMember(FgManage.this);
                addWindow.showPopupWindow(new View(FgManage.this));

            }
        });

        RecyclerViewUtil util = new RecyclerViewUtil(this,recyclerView);
        util.setOnItemLongClickListener(new RecyclerViewUtil.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                final String familyId = sp.getString("groupId","");


                final JSONObject delete = new JSONObject();
                try {
                    delete.put("familyId",familyId);
                    delete.put("userId",list.get(position).get("userId").toString());
                    delete.put("nickName",list.get(position).get("nickName").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread(){
                    public void run(){
                        super.run();
                        Looper.prepare();

                        HttpUtils hu = new HttpUtils();
                        String deleteMemberUrl = ConfigUtils.getProperties(getApplicationContext(), "deleteMemberUrl");
                        String url = ConfigUtils.getProperties(getApplicationContext(),"host") + deleteMemberUrl;

                        JSONObject msg = hu.deleteMember(url,delete);

                        String alert = "";
                        try {
                            alert = msg.getString("msg").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(FgManage.this, alert, Toast.LENGTH_LONG).show();

                        Looper.loop();
                    }
                }.start();
            }

            @Override
            public void OnItemLongClickListener(int position, View view) {

            }
        });
    }


    private void init() {
        list.clear();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_member);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置布局管理器，实现横向或数值滚动的列表布局

        Thread getmember = new Thread(new Runnable() {
            @Override
            public void run() {

                final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                final String familyId = sp.getString("groupId","");

                JSONObject family = new JSONObject();
                try {
                    family.put("familyId", familyId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String select = ConfigUtils.getProperties(getApplicationContext(), "getMemberUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(), "host") + select;
                HttpUtils hu = new HttpUtils();
                jsonJX(hu.selectMember(url, family));


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
                                String memberAccount = object.getString("userId");
                                String memberNickname = object.getString("nickName");
                                //int type = object.getInt("type");
                                //String albumName = object.getString("albumName");
                                //存入map
                                map.put("userId", memberAccount);
                                map.put("nickName", memberNickname);
                                //map.put("id",id);
                                //map.put("type",type);

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
        getmember.start();
        try {
            getmember.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAdapter = new FgManage.RecyclerViewAdapter(list);
        recyclerView.setAdapter(mAdapter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<FgManage.RecyclerViewAdapter.myViewHolder> {

        private ArrayList data;

        public RecyclerViewAdapter(ArrayList<Map<String, Object>> list) {
            this.data = list;
        }



        @Override
        public FgManage.RecyclerViewAdapter.myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FgManage.RecyclerViewAdapter.myViewHolder holder= new FgManage.RecyclerViewAdapter.myViewHolder(View.inflate(viewGroup.getContext(), R.layout.member_item, null));
            return holder;
        }
        //绑定数据


        @Override
        public void  onBindViewHolder(FgManage.RecyclerViewAdapter.myViewHolder holder, int i) {

            holder.memberId.setText(list.get(i).get("userId").toString()+")");
            holder.memberNick.setText(list.get(i).get("nickName").toString());

        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView memberId;
            TextView memberNick;
            public myViewHolder(View itemView)
            {
                super(itemView);
                memberId = (TextView)itemView.findViewById(R.id.member_account);
                memberNick = (TextView) itemView.findViewById(R.id.member_nickname);
            }
        }
    }

    private class AddMember extends PopupWindow {

        private Button btn_cancel;
        private Button btn_submit;
        private EditText newMember;


        public AddMember(Activity context) {
            // 通过layout的id找到布局View
            View contentView = LayoutInflater.from(context).inflate(R.layout.add_member_window, null);

            btn_cancel = (Button) contentView.findViewById(R.id.btn_addmember_cancel);
            btn_submit = (Button) contentView.findViewById(R.id.btn_addmember_submit);
            newMember = (EditText) contentView.findViewById(R.id.add_member_id) ;

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMemberId = newMember.getText().toString();

                    final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                    final String familyId = sp.getString("groupId","");
                    final JSONObject aNewMember = new JSONObject();
                    try {
                        aNewMember.put("userId",addMemberId);
                        aNewMember.put("familyId",familyId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread(){
                        public void run(){
                            super.run();
                            Looper.prepare();

                            HttpUtils hu = new HttpUtils();
                            String addMemberUrl = ConfigUtils.getProperties(getApplicationContext(), "addMemberUrl");
                            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + addMemberUrl;

                            JSONObject msg = hu.addMember(url,aNewMember);

                            String alert = "";
                            try {
                                alert = msg.getString("msg").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(FgManage.this, alert, Toast.LENGTH_LONG).show();

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
