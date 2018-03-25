package com.example.administrator.familyrecord;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获取控件
        Button login = (Button) findViewById(R.id.btn_login);
        Button signup = (Button) findViewById(R.id.btn_signup);
        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);

        //绑定点击事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usern = username.toString();
                String psw = password.toString();
                loginBtnClick();
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
        Intent intent = new Intent(LoginActivity.this,SignUp.class);
        startActivity(intent);
    }

    //登录按钮的点击事件
    private void loginBtnClick() {
        System.out.println("loging");
        Intent intent = new Intent(LoginActivity.this,Idle.class);
        startActivity(intent);
    }


}
