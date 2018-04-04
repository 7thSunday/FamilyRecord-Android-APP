package com.example.administrator.familyrecord.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        String projectName = ConfigUtils.getProperties(getApplicationContext(), "project");
        String url = ConfigUtils.getProperties(getApplicationContext(), "host") + projectName;
        url += path;
        final ImageView iv = (ImageView) findViewById(R.id.iv_show_photo);
        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);
        Picasso.with(this).load(url).into(iv);
        FloatingActionButton download = (FloatingActionButton) findViewById(R.id.btn_download_photo);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String fileName = formatter.format(curDate);

                Drawable drawable = iv.getDrawable();
                if (drawable == null) {
                    return;
                }
                FileOutputStream outStream = null;
                File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName);
                if (file.isDirectory()) {//如果是目录不允许保存
                    return;
                }
                try {
                    outStream = new FileOutputStream(file);
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    bitmap.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outStream != null) {
                            outStream.close();
                            Toast.makeText(ShowPhoto.this, "保存成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        });

    }
}
