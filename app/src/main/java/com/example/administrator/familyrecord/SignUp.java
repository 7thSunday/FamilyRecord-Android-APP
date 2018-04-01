package com.example.administrator.familyrecord;

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
                String usern = suUsername.getText().toString().trim();
                String psw = suPassword.getText().toString().trim();
                String cfpsw = cfPassword.getText().toString().trim();
                String nick = suNickname.getText().toString().trim();
                String birth = suBirthday.getText().toString().trim();

                if (usern.length()>5){
                    if (psw.equals(cfpsw)){
                        JSONObject user = new JSONObject();
                        try {
                            user.put("account",usern);
                            user.put("birthday",birth);
                            user.put("nickName",nick);
                            user.put("password",psw);
                            user.put("rePassword",cfpsw);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        submit(user);
                    }else {
                        Toast.makeText(SignUp.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(SignUp.this, "账号至少为6个字符", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    //点击事件
    private void submit(final JSONObject user) {
        new Thread(){
            public void run(){
                super.run();
                Looper.prepare();
                boolean isSuccess = false;
                String signupUrl = ConfigUtils.getProperties(getApplicationContext(), "signupUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(),"host") + signupUrl;
                HttpUtils hu = new HttpUtils();
                try {
                    isSuccess =  hu.sign(user,url,1).getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess) {

                    Toast.makeText(SignUp.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    Toast.makeText(SignUp.this, "注册失败", Toast.LENGTH_SHORT).show();
                }



                Looper.loop();
            }
        }.start();
    }
}
