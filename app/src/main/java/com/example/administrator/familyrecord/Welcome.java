package com.example.administrator.familyrecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
        final SharedPreferences.Editor editor =sp.edit();
        final String usern = sp.getString("username","");
        final String psw = sp.getString("password","");

        //登录判断
        new Thread(){
            public void run(){
                super.run();
                Looper.prepare();
                String myuseraccount = null;
                JSONObject user = new JSONObject();
                String groupId = null;
                String groupName = null;
                String creator = null;
                String nickname = null;

                Toast.makeText(Welcome.this, "初始化中...", Toast.LENGTH_LONG).show();


                try {
                    user.put("account",usern);
                    user.put("password",psw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtils hu = new HttpUtils();
                String loginUrl = ConfigUtils.getProperties(getApplicationContext(), "loginUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(),"host") + loginUrl;
                try {
                    JSONObject myuser = hu.sign(user,url,0);
                    System.out.println(myuser);
                    myuseraccount = myuser.getString("account");
                    groupId = myuser.getString("groupId");
                    nickname = myuser.getString("nickName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (myuseraccount!=null) {

                    if (!groupId.equals("null")){
                        editor.putString("groupId",groupId);
                        editor.putString("groupName",groupName);
                        editor.putString("creator",creator);
                        editor.putString("nickName",nickname);
                        editor.commit();
                        handlerInFG.sendEmptyMessageDelayed(0,3000);
                    }else {
                        handlerNotInFG.sendEmptyMessageDelayed(0,3000);
                    }


                }else {
                    handlerFailed.sendEmptyMessageDelayed(0,3000);
                }

                Looper.loop();
            }
        }.start();

    }

    private Handler handlerInFG = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(Welcome.this,HomePage.class);
            startActivity(intent);
            finish();
            super.handleMessage(msg);
        }
    };

    private Handler handlerNotInFG = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(Welcome.this,Idle.class);
            startActivity(intent);
            finish();
            super.handleMessage(msg);
        }
    };

    private Handler handlerFailed = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(Welcome.this,Login.class);
            startActivity(intent);
            finish();
            super.handleMessage(msg);
        }
    };

}
