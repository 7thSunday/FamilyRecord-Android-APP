package com.example.administrator.familyrecord;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        //获取控件
        Button login = (Button) findViewById(R.id.btn_login);
        Button signup = (Button) findViewById(R.id.btn_signup);
        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);

        //绑定点击事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usern = username.getText().toString();
                String psw = password.getText().toString();
                try {
                    loginBtnClick(usern,psw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupBtnClick();
            }
        });


    }

    //注册按钮的点击事件
    private void signupBtnClick() {
        Intent intent = new Intent(Login.this,SignUp.class);
        startActivity(intent);
    }

    //登录按钮的点击事件
    private void loginBtnClick(final String username, final String password) throws JSONException {
        final JSONObject user = new JSONObject();
        user.put("account",username);
        user.put("password",password);
        //
        //查询并返回结果
        new Thread(){
            public void run(){
                super.run();
                Looper.prepare();
                String myuseraccount = null;
                String groupID = null;
                String loginUrl = ConfigUtils.getProperties(getApplicationContext(), "loginUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(),"host") + loginUrl;
                HttpUtils hu = new HttpUtils();
                try {
                    JSONObject myuser = hu.sign(user,url,0);
                    myuseraccount = myuser.getString("account");
                    groupID = myuser.getString("groupId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (myuseraccount!=null) {
                    //保存账户密码
                    SharedPreferences.Editor editor =sp.edit();
                    editor.putString("username",username);
                    editor.putString("password",password);
                    editor.putString("groupId",groupID);
                    editor.commit();
                    if (!groupID.equals("null")){
                        Intent intent = new Intent(Login.this,HomePage.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(Login.this,Idle.class);
                        startActivity(intent);
                    }
                    finish();

                }else {
                    Toast.makeText(Login.this, "登录失败", Toast.LENGTH_SHORT).show();
                }

                Looper.loop();
            }
        }.start();

        /*Intent intent = new Intent(Login.this,Idle.class);
        startActivity(intent);*/
    }


}
