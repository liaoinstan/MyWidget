package com.test.huanxin.bannertest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.test.huanxin.bannertest.entity.Images;
import com.test.huanxin.bannertest.view.Banner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Banner banner;
    private List<Images> images = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
        initView();
    }

    private void initData() {
//        images.add(new Images(1,"title1",R.mipmap.img1+""));
//        images.add(new Images(2,"title2",R.mipmap.img1+""));
//        images.add(new Images(3,"title3",R.mipmap.img1+""));
//        images.add(new Images(4,"title4",R.mipmap.img1+""));
//        images.add(new Images(5,"title5",R.mipmap.img1+""));
        images.add(new Images(1,"title1","http://pic.58pic.com/58pic/17/32/69/25e58PICyAR_1024.jpg"));
        images.add(new Images(2,"title2","http://pic.58pic.com/58pic/17/32/69/25e58PICyAR_1024.jpg"));
        images.add(new Images(3,"title3","http://pic.58pic.com/58pic/17/32/69/25e58PICyAR_1024.jpg"));
        images.add(new Images(4,"title4","http://pic.58pic.com/58pic/17/32/69/25e58PICyAR_1024.jpg"));
        images.add(new Images(5,"title5","http://pic.58pic.com/58pic/17/32/69/25e58PICyAR_1024.jpg"));
    }

    private void initView() {
        banner = (Banner) findViewById(R.id.banner);
//        banner.showTitle(false);
        banner.setDatas(images);
    }
}
