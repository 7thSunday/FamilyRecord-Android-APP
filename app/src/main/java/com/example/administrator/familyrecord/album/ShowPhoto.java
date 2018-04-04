package com.example.administrator.familyrecord.album;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
        final FloatingActionButton download = (FloatingActionButton) findViewById(R.id.btn_download_photo);

        final String finalUrl = url;
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String fileName = formatter.format(curDate);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(finalUrl));

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setTitle("FamilyRecord");
                request.setDescription("对于该请求文件的描述");

// 设置下载文件的保存位置
                File saveFile = new File(Environment.getExternalStorageDirectory(), fileName);
                request.setDestinationUri(Uri.fromFile(saveFile));

                DownloadManager manager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

// 将下载请求加入下载队列
                manager.enqueue(request);

            }
        });

    }
}
