package com.example.administrator.familyrecord.article;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.Base64Utils;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.GetPath;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.example.richeditor.RichEditor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleEditor extends AppCompatActivity {

    private RichEditor mEditor;
    private TextView mPreview;
    private String srcPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_editor);

        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");
        //mEditor.setInputEnabled(false);

        mPreview = (TextView) findViewById(R.id.preview);
        Button submit = (Button) findViewById(R.id.btn_new_article_subimt);
        final EditText title = (EditText) findViewById(R.id.new_title);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mPreview.getText().toString();
                String regex = "/storage/.*?\\.jpg";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(content);
                StringBuffer sb=new StringBuffer();
                while(m.find()){
                    m.appendReplacement(sb, "data:image/jpeg;base64," + Base64Utils.imageToBase64(m.group()).replaceAll("\\+","%2B"));
                }
                m.appendTail(sb);
                content = sb.toString();
                /*String base64 = Base64Utils.imageToBase64(srcPath);
            String img = "data:image/jpeg;base64," + base64.replaceAll("\\+","%2B");*/
                final String mytitle = title.getText().toString();

                if (mytitle.length()>0&&content.length()>0){
                    final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
//                final SharedPreferences.Editor editor =sp.edit();
                    final String usern = sp.getString("username","");
                    final String rId = sp.getString("groupId","");

                    final JSONObject article = new JSONObject();
                    try {
                        article.put("content",content);
                        article.put("creator",usern);
                        article.put("rId",rId);
                        article.put("id","");
                        article.put("title",mytitle);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread(){
                        public void run(){
                            super.run();
                            Looper.prepare();

                            HttpUtils hu = new HttpUtils();
                            String createArticleUrl = ConfigUtils.getProperties(getApplicationContext(), "createArticleUrl");
                            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + createArticleUrl;

                            JSONObject msg = hu.createArticle(url,article);

                            String alert = "";
                            try {
                                alert = msg.getString("msg").toString();
                                boolean isSuccess = msg.getBoolean("success");
                                Toast.makeText(ArticleEditor.this, alert, Toast.LENGTH_LONG).show();
                                if (isSuccess){
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }.start();

                }else {
                    Toast.makeText(ArticleEditor.this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });


        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });



        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });


        findViewById(R.id.action_txt_color_black).setOnClickListener(new View.OnClickListener() {
            //private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(Color.parseColor("#000000"));
                //isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_txt_color_red).setOnClickListener(new View.OnClickListener() {
            //private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(Color.parseColor("#ff0000"));
                //isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_txt_color_yellow).setOnClickListener(new View.OnClickListener() {
            //private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(Color.parseColor("#ffff00"));
                //isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_txt_color_green).setOnClickListener(new View.OnClickListener() {
            //private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(Color.parseColor("#00ff00"));
                //isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_txt_color_blue).setOnClickListener(new View.OnClickListener() {
            //private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(Color.parseColor("#0000ff"));
                //isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_txt_color_cyan).setOnClickListener(new View.OnClickListener() {
            //private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(Color.parseColor("#00ffff"));
                //isChanged = !isChanged;
            }
        });


        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local,4);
                /*mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                        "dachshund");*/
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            Uri uri = data.getData();
            GetPath path = new GetPath();
            srcPath = path.getPath(getApplicationContext(),uri);


            mEditor.insertImage(srcPath,"image");

        }
    }
}
