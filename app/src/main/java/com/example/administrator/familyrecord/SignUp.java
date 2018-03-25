package com.example.administrator.familyrecord;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        //获取控件
        final EditText suUsername = (EditText) findViewById(R.id.signup_username);
        final EditText suPassword = (EditText) findViewById(R.id.signup_password);
        final EditText cfPassword = (EditText) findViewById(R.id.signup_cfpassword);
        final EditText suNickname = (EditText) findViewById(R.id.signup_nickname);
        final EditText suBirthday = (EditText) findViewById(R.id.signup_birthday);
        Button suSubmit = (Button) findViewById(R.id.signup_submit);

        //绑定点击
        suSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usern = suUsername.toString();
                String psw = suPassword.toString();
                String cfpsw = cfPassword.toString();
                String nick = suNickname.toString();
                String birth = suBirthday.toString();
                submit();
            }
        });
    }



    //点击事件
    private void submit() {

    }
}
