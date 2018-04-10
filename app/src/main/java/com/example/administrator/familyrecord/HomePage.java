package com.example.administrator.familyrecord;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.familyrecord.subHomePage.ArticleFragment;
import com.example.administrator.familyrecord.subHomePage.MineFragment;
import com.example.administrator.familyrecord.subHomePage.PhotoFragment;
import com.example.administrator.familyrecord.subHomePage.VideoFragment;

public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private ArticleFragment articleFragment;
    private PhotoFragment photoFragment;
    private VideoFragment videoFragment;
    private MineFragment mineFragment;

    private View articleLayout;
    private View photoLayout;
    private View videoLayout;
    private View mineLayout;

    private ImageView articleIcon;
    private ImageView photoIcon;
    private ImageView videoIcon;
    private ImageView mineIcon;

    private TextView articleText;
    private TextView photoText;
    private TextView videoText;
    private TextView mineText;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        initViews();
        fragmentManager = getFragmentManager();
        setTabSelection(0);
    }

    private void initViews(){
        articleLayout = findViewById(R.id.article_layout);
        photoLayout = findViewById(R.id.photo_layout);
        videoLayout = findViewById(R.id.video_layout);
        mineLayout = findViewById(R.id.mine_layout);

        articleIcon = (ImageView) findViewById(R.id.article_icon);
        photoIcon = (ImageView) findViewById(R.id.photo_icon);
        videoIcon = (ImageView) findViewById(R.id.video_icon);
        mineIcon = (ImageView) findViewById(R.id.mine_icon);

        articleText = (TextView) findViewById(R.id.article_text);
        photoText = (TextView) findViewById(R.id.photo_text);
        videoText = (TextView) findViewById(R.id.video_text);
        mineText = (TextView) findViewById(R.id.mine_text);

        articleLayout.setOnClickListener(this);
        photoLayout.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.article_layout:
                // 当点击了帖子tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.photo_layout:
                // 当点击了相册tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.video_layout:
                // 当点击了视频tab时，选中第3个tab
                setTabSelection(2);
                break;
            case R.id.mine_layout:
                // 当点击了我的tab时，选中第4个tab
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    private void setTabSelection(int index) {
        // 每次选中之前先清掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了帖子tab时，改变控件的图片和文字颜色
                articleIcon.setImageResource(R.drawable.article_selected);
                articleText.setTextColor(Color.parseColor("#ff99cc"));
                if (articleFragment == null) {
                    // 如果articleFragment为空，则创建一个并添加到界面上
                    articleFragment = new ArticleFragment();
                    transaction.add(R.id.content, articleFragment);
                } else {
                    // 如果articleFragment不为空，则直接将它显示出来
                    transaction.show(articleFragment);
                }
                break;
            case 1:
                // 当点击了相册tab时，改变控件的图片和文字颜色
                photoIcon.setImageResource(R.drawable.photo_selected);
                photoText.setTextColor(Color.parseColor("#ff99cc"));
                if (photoFragment == null) {
                    // 如果photoFragment为空，则创建一个并添加到界面上
                    photoFragment = new PhotoFragment();
                    transaction.add(R.id.content, photoFragment);
                } else {
                    // 如果photoFragment不为空，则直接将它显示出来
                    transaction.show(photoFragment);
                }
                break;
            case 2:
                // 当点击了视频tab时，改变控件的图片和文字颜色
                videoIcon.setImageResource(R.drawable.video_selected);
                videoText.setTextColor(Color.parseColor("#ff99cc"));
                if (videoFragment == null) {
                    // 如果videoFragment为空，则创建一个并添加到界面上
                    videoFragment = new VideoFragment();
                    transaction.add(R.id.content, videoFragment);
                } else {
                    // 如果videoFragment不为空，则直接将它显示出来
                    transaction.show(videoFragment);
                }
                break;
            case 3:
                // 当点击了我的tab时，改变控件的图片和文字颜色
                mineIcon.setImageResource(R.drawable.mine_selected);
                mineText.setTextColor(Color.parseColor("#ff99cc"));
                if (mineFragment == null) {
                    // 如果mineFragment为空，则创建一个并添加到界面上
                    mineFragment = new MineFragment();
                    transaction.add(R.id.content, mineFragment);
                } else {
                    // 如果mineFragment不为空，则直接将它显示出来
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void clearSelection(){
        articleIcon.setImageResource(R.drawable.article);
        articleText.setTextColor(Color.parseColor("#8a8a8a"));
        photoIcon.setImageResource(R.drawable.photo);
        photoText.setTextColor(Color.parseColor("#8a8a8a"));
        videoIcon.setImageResource(R.drawable.video);
        videoText.setTextColor(Color.parseColor("#8a8a8a"));
        mineIcon.setImageResource(R.drawable.mine);
        mineText.setTextColor(Color.parseColor("#8a8a8a"));
    }

    private void hideFragments(FragmentTransaction transaction){

        if (articleFragment !=  null){
            transaction.hide(articleFragment);
        }
        if (photoFragment !=  null){
            transaction.hide(photoFragment);
        }
        if (videoFragment !=  null){
            transaction.hide(videoFragment);
        }
        if (mineFragment != null){
            transaction.hide(mineFragment);
        }
    }

}
