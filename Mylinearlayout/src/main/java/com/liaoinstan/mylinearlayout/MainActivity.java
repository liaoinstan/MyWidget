package com.liaoinstan.mylinearlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.liaoinstan.mylinearlayout.view.MyWidgetView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MyWidgetView my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        my = (MyWidgetView) findViewById(R.id.my);
        my.setmData(new String[]{"XXS","XS","S","M","L","XL","XXL","XXXL"});
        findViewById(R.id.last).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.moveto).setOnClickListener(this);
        findViewById(R.id.getcontent).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.last:
                my.last(1);
                break;
            case R.id.next:
                my.next(1);
                break;
            case R.id.moveto:
                my.moveTo(3);
                break;
            case R.id.getcontent:
                my.getContent();
                Toast.makeText(this,my.getContent(),Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
