package com.example.administrator.familyrecord.album;

import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAlbum extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_album);

        final EditText newAlbumName = (EditText)findViewById(R.id.new_album_name);
        Button submit = (Button) findViewById(R.id.btn_create_album_submit);


        final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
//        final SharedPreferences.Editor editor =sp.edit();
        final String creator = sp.getString("username","null");
        final String groupId = sp.getString("groupId","null");


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject newAlbum = new JSONObject();
                final String name = newAlbumName.getText().toString();
                if (name.length()>0){
                    try {
                        newAlbum.put("albumName",name);
                        newAlbum.put("creator",creator);
                        newAlbum.put("groupId",groupId);
                        newAlbum.put("type","1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread(){
                        public void run(){
                            super.run();
                            Looper.prepare();
                            HttpUtils hu = new HttpUtils();
                            String creatAlbumUrl = ConfigUtils.getProperties(getApplicationContext(), "creatAlbumUrl");
                            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + creatAlbumUrl;
                            JSONObject result =  hu.createAlbum(url,newAlbum);
                            boolean isSuccess = false;
                            try {
                                isSuccess = result.getBoolean("success");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (isSuccess){
                                Toast.makeText(CreateAlbum.this, "创建成功", Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(CreateAlbum.this, "创建失败", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    }.start();
                }else {
                    Toast.makeText(CreateAlbum.this, "请输入相册名称", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
