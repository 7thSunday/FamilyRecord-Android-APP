package com.example.administrator.familyrecord;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Idle extends AppCompatActivity {

    //private boolean createFGisClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idle_page);


        //获取控件
        final Button createFG = (Button) findViewById(R.id.create_fg);
        ImageButton reload = (ImageButton) findViewById(R.id.reload);

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
    }

    //刷新
    private void reloadLogin() {
        Intent intent = new Intent(Idle.this,ArticleEditor.class);
        startActivity(intent);
    }

    //创建家庭组
    private void createFamilyGroup() {
        Intent intent = new Intent(Idle.this,CreateFG.class);
        startActivity(intent);
    }
}
