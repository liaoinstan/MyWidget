package com.liaoinstan.mylinearlayout.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.TextView;

import com.liaoinstan.mylinearlayout.R;

/**
 * Created by liaoinstan on 2016/3/16.
 */
public class MyWidgetView extends ViewGroup implements View.OnClickListener{

    private Context context;
    private OverScroller mScroller;
    private String[] mData = new String[]{"XXS","XS","S","M","L","XL","XXL","XXXL"};    //数据，注意不要小于每页显示最大个数，没有处理这种情况

    private int parentWidth;             //父View最大宽度
    private int childSpaceWidth;        //单步位移长度
    private int childWidth = 80;        //子元素的宽固定为80.高也一样
    private int MAX_NUM = 5;            //每页显示数量，注意为偶数则中间位置有2个
    private int firstPosition = 3;      //初始化时候的中心位置，3表示第4个元素
    private int textColor = Color.parseColor("#555555");        //文字颜色
    private int textColorHot = Color.parseColor("#FFFFFF");     //文字选中的颜色
    private int circleColor = Color.parseColor("#fd648f");      //圆圈的颜色
    private Paint paint = new Paint();                            //初始化一支画笔

    private boolean moveNable = true;         //打开或关闭滑动，默认开启，设为false则不处理滑动
    private int mLastX;                         //上次X坐标
    private int mFirstX;                        //第一次X坐标
    private int dx;                             //移动距离
    private boolean needMyMove = false;     //是否需要移动
    private TextView lastChild;                 //上一次经过的子view
    private int now;

    private int mTouchSlop;					//帮我区别用户是点击还是拖拽

    public MyWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mScroller = new OverScroller(context);                              //初始化滚动器
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();   //获取点击宽度默认值

        paint.setColor(circleColor);    //画笔颜色
        paint.setAntiAlias(true);        //画笔设置抗锯齿
    }

    @Override
    protected void onFinishInflate() {
        Log.e("liao", "onFinishInflate");
        addView();
        super.onFinishInflate();
    }

    private void addView(){
        for (int i=0;i<mData.length;i++){
            LinearLayout.LayoutParams para=new LinearLayout.LayoutParams(childWidth,childWidth);
            TextView textView = new TextView(context);
            textView.setLayoutParams(para);
            textView.setText(mData[i]);
            textView.setTextColor(textColor);
            textView.setGravity(Gravity.CENTER);
            textView.setTag(i);
            textView.setOnClickListener(this);
            addView(textView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("liao", "onMeasure");
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        childSpaceWidth = parentWidth/MAX_NUM;
        if (getChildCount()>0){
            for (int i=0;i<getChildCount();i++){
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("liao", "onLayout");
        if (getChildCount()>0){
            for (int i=0;i<getChildCount();i++){
                View child = getChildAt(i);
                int left = childSpaceWidth*i + (childSpaceWidth-childWidth)/2;
                int top = (getHeight()-child.getMeasuredHeight())/2 ;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
            }
        }
        moveTo(firstPosition, false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        View child = getChildAt(MAX_NUM/2);
        int offset = getScrollX();
        if (child != null) {
            //绘制圆形背景
            canvas.drawCircle(child.getLeft() + child.getWidth() / 2 + offset, child.getTop() + child.getHeight() / 2, child.getWidth() / 2, paint);
        }
        super.dispatchDraw(canvas);
    }

    /**
     * 事件分发：判断是点击事件还是移动事件，分发给不同的对象处理
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        dealMulTouchEvent(event);
        int action = event.getAction();
        int x = (int) event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFirstX = x;
                needMyMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //移动距离大于系统预设值，则代表是move事件，不是点击
                if (Math.abs(x-mFirstX)>=mTouchSlop){
                    needMyMove = true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 事件拦截：点击事件交给内部的子view处理，如果是移动事件则亲自处理
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return needMyMove;
    }

    /**
     * 事件处理：在ACTION_MOVE中进行位移，在ACTION_UP中设置回弹
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!moveNable) return true;
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //是否超出左右边界
                if (!isOverLeft()&&dx>0 || !isOverRight()&&dx<0){
                    this.now = getCurrentPosition();
                    scrollBy(-dx,0);
                    //获取当前中心位置的view并切换字体颜色
                    int position = getCurrentPosition();
                    TextView child = (TextView) getChildAt(position);
                    TextView lastChild = null;
                    if (dx>0) lastChild = (TextView)getChildAt(position+1);
                    if (dx<0) lastChild = (TextView)getChildAt(position-1);
                    if (child!=null){
                        child.setTextColor(textColorHot);
                        this.lastChild = child;
                    }
                    if (lastChild!=null){
                        lastChild.setTextColor(textColor);
                    }
                }else {
                    //如果位移超出了左右边界，依然允许移动一段距离，并减小加速度，根据位置改变字体颜色
                    scrollBy(-dx/2,0);
                    if (getScrollX()< -childSpaceWidth*(MAX_NUM/2+0.5)) {
                        TextView childfirst = (TextView) getChildAt(0);
                        if (childfirst!=null) {
                            childfirst.setTextColor(textColor);
                            lastChild = childfirst;
                        }
                    }
                    if (getScrollX()>(mData.length-MAX_NUM/2-1+0.5)*childSpaceWidth) {
                        TextView childlast = (TextView) getChildAt(getChildCount() - 1);
                        if (childlast != null){
                            childlast.setTextColor(textColor);
                            lastChild = childlast;
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //如果滑动超过左右边界则，回弹到边界的位置
                if (isOverRight()||isOverLeft()){
                    if (isOverLeft()){
                        mScroller.startScroll(getScrollX(), 0, -getScrollX() - childSpaceWidth * (MAX_NUM/2), 0);
                        invalidate();
                        if (lastChild!=null)lastChild.setTextColor(textColorHot);
                    }
                    if (isOverRight()){
                        //计算回滚距离 ： -(显示宽度-(总宽度-滑动距离)-子元素占的宽度*(最大显示数量/2))
                        mScroller.startScroll(getScrollX(), 0, -(childSpaceWidth*MAX_NUM-(childSpaceWidth*mData.length-getScrollX())-childSpaceWidth * (MAX_NUM/2)), 0);
                        invalidate();
                        if (lastChild!=null)lastChild.setTextColor(textColorHot);
                    }
                }else {
                    //如果超过2分之一个单位距离则回弹到下一个view，没超过则弹动回当前view
                    int offsetX = getScrollX() % childSpaceWidth;
                    if (Math.abs(offsetX) > childSpaceWidth / 2) {
                        if (offsetX < 0) {
                            offsetX = childSpaceWidth - Math.abs(offsetX);
                        } else {
                            offsetX = Math.abs(offsetX) - childSpaceWidth;
                        }
                    }
                    mScroller.startScroll(getScrollX(), 0, -offsetX, 0);
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * 处理多点触控的情况，准确地计算Y坐标和移动距离dy
     * 同时兼容单点触控的情况
     */
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    public void dealMulTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                mLastX = (int) x;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                dx = (int) (x - mLastX);
                mLastX = (int) x;
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId != mActivePointerId) {
                    mLastX = (int) MotionEventCompat.getX(ev, pointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastX = (int) MotionEventCompat.getX(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
    }

    /**
     * 根据坐标计算中心位置的是哪一个view
     */
    private int getCurrentPosition(){
        int outoff = (getScrollX()+childSpaceWidth/2)/childSpaceWidth;
        if (getScrollX()+childSpaceWidth/2 < 0){
            outoff -= 1;
        }
        outoff += MAX_NUM/2;
        return outoff;
    }

    /**
     * 是否超出左边界
     */
    private boolean isOverLeft(){
        int pernow = getScrollX()/childSpaceWidth;
        return !(pernow >  - MAX_NUM/2);
    }

    /**
     * 是否超出右边界
     */
    private boolean isOverRight(){
        int pernow = getScrollX()/childSpaceWidth;
        return !(pernow < mData.length - MAX_NUM/2-1);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();

            //滚动的时候切换字体颜色
            int position = getCurrentPosition();
            TextView child = (TextView) getChildAt(position);
            if (child!=null){
                child.setTextColor(textColorHot);
                TextView childper = (TextView) getChildAt(position-1);
                TextView childnext = (TextView) getChildAt(position+1);
                if (childper!=null) childper.setTextColor(textColor);
                if (childnext!=null) childnext.setTextColor(textColor);
            }
        }
    }

    private void next(int step,boolean isAnim){
        int pernow = getScrollX()/childSpaceWidth +step;
        if (pernow < mData.length - MAX_NUM/2){
            if (mScroller.isFinished()) {
                now = pernow + MAX_NUM/2;
                if (isAnim) {
                    mScroller.startScroll(getScrollX(), 0, childSpaceWidth * step, 0);
                    invalidate();
                }else {
                    scrollBy(childSpaceWidth * step,0);
                }

                if (!isAnim) {
                    TextView child = (TextView) getChildAt(now);
                    if (child != null) {
                        if (lastChild != null) lastChild.setTextColor(textColor);
                        child.setTextColor(textColorHot);
                        lastChild = child;
                    }
                }
            }
        }
        Log.e("liao","scrollx:"+getScrollX());
    }
    private void last(int step,boolean isAnim){
        int pernow = getScrollX()/childSpaceWidth -step;
        if (pernow > - MAX_NUM/2-1) {
            if (mScroller.isFinished()) {
                now = pernow + MAX_NUM/2;
                if (isAnim) {
                    mScroller.startScroll(getScrollX(), 0, -childSpaceWidth * step, 0);
                    invalidate();
                }else {
                    scrollBy(-childSpaceWidth * step,0);
                }

                if (!isAnim) {
                    TextView child = (TextView) getChildAt(now);
                    if (child != null) {
                        if (lastChild != null) lastChild.setTextColor(textColor);
                        child.setTextColor(textColorHot);
                        lastChild = child;
                    }
                }
            }
        }
    }

    private void moveTo(int index,boolean isAnim){
        int step = index - now;
        if (step>0){
            next(step,isAnim);
        }else {
            last(-step,isAnim);
        }
    }
    @Override
    public void onClick(View v) {
        moveTo((int)v.getTag(),true);
    }

    //#####################################################
    //######      对外接口
    //#####################################################

    public void next(int step){
        next(step, true);
    }
    public void last(int step) {
        last(step, true);
    }
    public void moveTo(int index){
        moveTo(index, true);
    }
    public String getContent(){
        return mData[getCurrentPosition()];
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    public void setTextColorHot(int textColorHot) {
        this.textColorHot = textColorHot;
    }
    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }
    public void setMoveNable(boolean moveNable) {
        this.moveNable = moveNable;
    }
    public void setmData(String[] mData) {
        this.mData = mData;
        removeAllViews();
        addView();
        requestLayout();
    }
    /**
     * 设置数据
     * @param mData 数据数组
     * @param firstPosition  初始化中间坐标
     * @param MAX_NUM 每页最大显示数量
     */
    public void setmData(String[] mData,int firstPosition,int MAX_NUM) {
        if(mData==null||mData.length==0) return;
        //如果初始位置<0或者大于数租长度，则设置为数组中间位置
        if (firstPosition<0 || firstPosition>=mData.length)
            this.firstPosition = mData.length/2;
        else
            this.firstPosition = firstPosition;
        this.MAX_NUM = MAX_NUM;
        setmData(mData);
    }
}
