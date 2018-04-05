package com.example.administrator.familyrecord.myConfig;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.Base64Utils;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.GetPath;
import com.example.administrator.familyrecord.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountConfig extends AppCompatActivity {

    private String srcPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_config);

        final EditText newNick = (EditText) findViewById(R.id.new_nickname);
        final EditText newBirth = (EditText) findViewById(R.id.new_birthday);
        final EditText newPsw = (EditText) findViewById(R.id.new_psw);
        final EditText newCfPsw = (EditText) findViewById(R.id.new_cf_psw);
        Button myConfigSubmit = (Button) findViewById(R.id.btn_myconfig_submit);
        Button selectNewHead = (Button) findViewById(R.id.btn_select_newhead);
        Button newHeadSubmit = (Button) findViewById(R.id.btn_newhead_submit);

        newHeadSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (srcPath!=null){
                    String base64 = Base64Utils.imageToBase64(srcPath);
                    final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                    final SharedPreferences.Editor editor =sp.edit();
                    String account = sp.getString("username","");
                    final JSONObject user = new JSONObject();
                    try {
                        user.put("account",account);
                        user.put("img",("data:image/jpeg;base64," + base64).replaceAll("\\+","%2B"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                boolean issuccess = Base64Utils.base64ToFile(base64,"/storage/sdcard/Download/77094b36acaf2edda0d6156d861001e9390193haha.jpg");

                    new Thread(){
                        public void run(){
                            super.run();
                            Looper.prepare();
                            HttpUtils hu = new HttpUtils();
                            String headImageUploadUrl = ConfigUtils.getProperties(getApplicationContext(), "headImageUploadUrl");
                            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + headImageUploadUrl;
                            JSONObject msg = hu.uploadHeadImage(url,user);
                            String result = "上传失败";
                            try {
                                result = msg.getString("headImageUrl");
                                if (!result.equals("null")){
                                    editor.putString("headImageUrl",result);
                                    editor.commit();
                                    Toast.makeText(AccountConfig.this, "上传成功", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }.start();
                }else {
                    Toast.makeText(AccountConfig.this, "请选择一张图片", Toast.LENGTH_LONG).show();
                }



            }
        });

        selectNewHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 1);
            }
        });

        myConfigSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nick = newNick.getText().toString();
                final String birth = newBirth.getText().toString();
                final String psw = newPsw.getText().toString();
                String cfPsw = newCfPsw.getText().toString();

                if (!nick.equals("")||!birth.equals("")||!psw.equals("")||!cfPsw.equals("")){
                    if (psw.equals(cfPsw)){

                        final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                        final SharedPreferences.Editor editor =sp.edit();
                        final JSONObject user = new JSONObject();
                        try {
                            user.put("account",sp.getString("username",""));
                            user.put("birthday",birth);
                            user.put("nickName",nick);
                            user.put("password",psw);
                            user.put("repassword",cfPsw);

                            new Thread(){
                                public void run(){
                                    super.run();
                                    Looper.prepare();
                                    HttpUtils hu = new HttpUtils();
                                    String updateUserUrl = ConfigUtils.getProperties(getApplicationContext(), "updateUserUrl");
                                    String url = ConfigUtils.getProperties(getApplicationContext(),"host") + updateUserUrl;
                                    JSONObject msg = hu.update(url,user);
                                    boolean result = false;
                                    try {
                                        result = msg.getBoolean("success");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (result){
                                        if (!nick.equals("")) editor.putString("nickName",nick);
                                        if (!psw.equals("")) editor.putString("password",psw);
                                        editor.commit();
                                        Toast.makeText(AccountConfig.this, "修改成功", Toast.LENGTH_LONG).show();
                                    }
                                    Looper.loop();
                                }
                            }.start();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else {
                        Toast.makeText(AccountConfig.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            Uri uri = data.getData();
            GetPath path = new GetPath();
            srcPath = path.getPath(getApplicationContext(),uri);

        }
    }
}
