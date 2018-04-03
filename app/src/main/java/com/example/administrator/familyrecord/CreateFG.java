package com.example.administrator.familyrecord;

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

public class CreateFG extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_fg);


        final EditText fgname = (EditText) findViewById(R.id.fg_name); //获取家庭组名称
        final Button creatFg = (Button) findViewById(R.id.btn_createfg);

        final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
//        final SharedPreferences.Editor editor =sp.edit();
        final String creator = sp.getString("username","");

        creatFg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fgname.getText().toString().trim();
                final JSONObject fg = new JSONObject();

                if (name.length()>0){
                    try {
                        fg.put("name",name);
                        fg.put("creator",creator);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread(){
                        public void run(){
                            super.run();
                            Looper.prepare();
                            HttpUtils hu = new HttpUtils();
                            String creatFgUrl = ConfigUtils.getProperties(getApplicationContext(), "createFgUrl");
                            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + creatFgUrl;
                            JSONObject result =  hu.creatFG(url,fg);
                            boolean isSuccess = false;
                            try {
                                isSuccess = result.getBoolean("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (isSuccess){
                                Toast.makeText(CreateFG.this, "创建成功", Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(CreateFG.this, "创建失败", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    }.start();
                }else {
                    Toast.makeText(CreateFG.this, "请输入家庭组名称", Toast.LENGTH_SHORT).show();
                }




            }
        });
    }
}
