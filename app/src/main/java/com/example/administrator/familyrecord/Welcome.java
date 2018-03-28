package com.example.administrator.familyrecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


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

        //登录判断
        new Thread(){
            public void run(){
                super.run();
                Looper.prepare();

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

                    Intent intent = new Intent(Welcome.this,Idle.class);
                    startActivity(intent);

                }else {
                    Intent intent = new Intent(Welcome.this,Login.class);
                    startActivity(intent);
                }



                Looper.loop();
            }
        }.start();

        //根据结果进行跳转

        //登录页面或主页面
    }
}
