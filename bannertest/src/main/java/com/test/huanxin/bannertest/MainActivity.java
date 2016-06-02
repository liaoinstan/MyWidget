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
        images.add(new Images(1,"夏季衬衫，清凉一夏","http://img2.imgtn.bdimg.com/it/u=2401368128,869327646&fm=21&gp=0.jpg"));
        images.add(new Images(2,"男子怒打妻儿，竟然只为了买一件衣服","http://img1.imgtn.bdimg.com/it/u=839795904,770645941&fm=21&gp=0.jpg"));
        images.add(new Images(3,"冠希复出，陈妍希表示呵呵","http://pic44.nipic.com/20140726/6205649_111852997000_2.jpg"));
        images.add(new Images(4,"iphon7预览版发售，你还在等什么","http://img4.imgtn.bdimg.com/it/u=3831361042,2579496760&fm=21&gp=0.jpg"));
        images.add(new Images(5,"马云：成功不只是嘴上说说","http://img0.imgtn.bdimg.com/it/u=1415714570,832901974&fm=21&gp=0.jpg"));
    }

    private void initView() {
        banner = (Banner) findViewById(R.id.banner);
//        banner.showTitle(false);
        banner.setDatas(images);
    }
}
