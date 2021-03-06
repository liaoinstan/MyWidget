package com.test.huanxin.bannertest.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.huanxin.bannertest.R;
import com.test.huanxin.bannertest.common.CustomBitmapLoadCallBack;
import com.test.huanxin.bannertest.entity.Images;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30 0030.
 */
public class BannerView extends FrameLayout implements Runnable{

    private static final String TAG = "Banner";

    private Context context;
    private LayoutInflater inflater;
    private TextView text_banner;
    private ViewPager mViewPager;
    private DotView dotView;
    private BannerAdapter mBannerAdapter;
    private List<Images> images;

    private int mBannerPosition = 0;
    private final int FAKE_BANNER_SIZE = 100;
    private final int DEFAULT_BANNER_SIZE = 5;
    private boolean mIsUserTouched = false;

    private boolean isAutoScroll = true;
    private final static int AUTO_SCROLL_WHAT = 0;
    private long    mDelayTimeInMills = 3000;

    private GradientDrawable mUnSelectedGradientDrawable;
    private GradientDrawable mSelectedGradientDrawable;
    private int dot_size = 13;

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);

        mSelectedGradientDrawable = new GradientDrawable();
        mUnSelectedGradientDrawable = new GradientDrawable();
        mSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
        mUnSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
        mSelectedGradientDrawable.setSize(dot_size, dot_size);
        mUnSelectedGradientDrawable.setSize(dot_size, dot_size);
        mSelectedGradientDrawable.setColor(Color.rgb(255, 255, 255));
        mUnSelectedGradientDrawable.setColor(Color.argb(33, 255, 255, 255));
    }

    @Override
    protected void onFinishInflate() {
        inflater.inflate(R.layout.banner, this, true);
        super.onFinishInflate();
        initCtrl();
    }

    private void sendScrollMessage() {
        if (mHandler != null) {
            mHandler.removeMessages(AUTO_SCROLL_WHAT);
            if (isAutoScroll && mViewPager.getAdapter().getCount() > 1) {
                mHandler.sendEmptyMessageDelayed(AUTO_SCROLL_WHAT, mDelayTimeInMills);
            }
        }
    }

    private void initCtrl() {
        text_banner = (TextView) findViewById(R.id.text_banner);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        dotView = (DotView) findViewById(R.id.dotView);
    }

    private void initView() {
        mBannerAdapter = new BannerAdapter(context);
        mViewPager.setAdapter(mBannerAdapter);
        mViewPager.addOnPageChangeListener(mBannerAdapter);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    sendScrollMessage();
                    mIsUserTouched = true;
                } else if (action == MotionEvent.ACTION_UP) {
                    sendScrollMessage();
                    mIsUserTouched = false;
                }
                return false;
            }
        });

        dotView.setViewPager(mViewPager, DEFAULT_BANNER_SIZE);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == AUTO_SCROLL_WHAT) {
                int size = mViewPager.getAdapter().getCount();
                int position = (mViewPager.getCurrentItem() + 1) % size;
                mViewPager.setCurrentItem(position, true);
                BannerView.this.sendScrollMessage();
            }
        }
    };

    @Override
    public void run() {
        if (mBannerPosition == FAKE_BANNER_SIZE - 1) {
            mViewPager.setCurrentItem(DEFAULT_BANNER_SIZE - 1, false);
        } else {
            mViewPager.setCurrentItem(mBannerPosition);
        }
    }

    private class BannerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LayoutInflater mInflater;

        public BannerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //计算实际位置
            position %= DEFAULT_BANNER_SIZE;

            //初始化imageView
            ImageView imageView = new ImageView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //绑定网络图片
            x.image().bind(imageView, images.get(position).getImg(), new CustomBitmapLoadCallBack(imageView));

            //点击事件
            final int pos = position;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "click banner item :" + pos, Toast.LENGTH_SHORT).show();
                }
            });

            //添加到容器
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = mViewPager.getCurrentItem();
            if (position == 0) {
                position = DEFAULT_BANNER_SIZE;
                mViewPager.setCurrentItem(position, false);
            } else if (position == FAKE_BANNER_SIZE - 1) {
                position = DEFAULT_BANNER_SIZE - 1;
                mViewPager.setCurrentItem(position, false);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mBannerPosition = position;
//            setIndicator(position);
            position %= DEFAULT_BANNER_SIZE;
            text_banner.setText(images.get(position).getTitle());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    //#######################对外方法
    public void setDatas(List<Images> images){
        this.images = images;
        initView();
        sendScrollMessage();
    }
    public void showTitle(boolean needShow){
        if (needShow){
            text_banner.setVisibility(VISIBLE);
        }else {
            text_banner.setVisibility(GONE);
        }
        requestLayout();
    }
}
