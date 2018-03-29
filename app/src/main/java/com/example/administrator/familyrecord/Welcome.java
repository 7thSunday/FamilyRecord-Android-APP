package com.example.administrator.familyrecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import com.example.administrator.familyrecord.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
        final String usern = sp.getString("username","");
        final String psw = sp.getString("password","");
        final boolean isinFG = sp.getBoolean("inFG",true);

        //登录判断
        new Thread(){
            public void run(){
                super.run();
                Looper.prepare();

                Toast.makeText(Welcome.this, "初始化中...", Toast.LENGTH_LONG).show();
                JSONObject user = new JSONObject();
                try {
                    user.put("account",usern);
                    user.put("password",psw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpUtils hu = new HttpUtils();
                String url = "http://10.77.115.148:8080/FamilyRecord/login/loginin.do";
                if (hu.sign(user,url)) {

                    if (isinFG){
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
