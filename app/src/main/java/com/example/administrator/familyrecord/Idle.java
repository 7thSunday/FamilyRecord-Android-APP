package com.example.administrator.familyrecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Idle extends AppCompatActivity {

    //private boolean createFGisClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idle_page);

        final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
        String groupID = sp.getString("groupId","");


        //获取控件
        final Button createFG = (Button) findViewById(R.id.create_fg);
        Button reload = (Button) findViewById(R.id.reload);
        final Button signout = (Button) findViewById(R.id.signout_in_idle);

        if (!groupID.equals("null")){
            createFG.setEnabled(false);
            createFG.setBackgroundColor(Color.parseColor("#8a8a8a"));
        }

        //绑定点击事件
        createFG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFamilyGroup();
                createFG.setEnabled(false);
                createFG.setBackgroundColor(Color.parseColor("#8a8a8a"));
            }
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadLogin();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username","");
                editor.putString("password","");
                editor.putString("groupId","null");
                editor.putString("groupName","null");
                editor.putString("creator","null");
                editor.putString("nickName","null");
                editor.commit();
                Toast.makeText(Idle.this, "已注销，请重新登录", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Idle.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

    }


    //刷新
    private void reloadLogin() {
        Intent intent = new Intent(Idle.this,Welcome.class);
        startActivity(intent);
        finish();
    }

    //创建家庭组
    private void createFamilyGroup() {
        Intent intent = new Intent(Idle.this,CreateFG.class);
        startActivity(intent);
    }
}
