package com.example.administrator.familyrecord.video;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.utils.ConfigUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class PlayVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        String projectName = ConfigUtils.getProperties(getApplicationContext(), "project");
        String url = ConfigUtils.getProperties(getApplicationContext(), "host") + projectName;
        url += path;

        FloatingActionButton downloadVideo = (FloatingActionButton) findViewById(R.id.btn_video_download);
        final String finalUrl = url;
        downloadVideo.setOnClickListener(new View.OnClickListener() {
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
        /*if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }*/
        final VideoView vv = (VideoView) findViewById(R.id.vv);
        vv.setVideoPath(url); //设置播放路径
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vv.start();
            }
        });
// 设置video的控制器
        vv.setMediaController(new MediaController(this));
    }
}
