package com.zixuexiangmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by 007 on 2015/11/3.
 */
public class SurfaceViewTep extends SurfaceView implements SurfaceHolder.Callback ,Runnable{

    private SurfaceHolder mHolder;
    private Canvas mCancas;

    //用于绘制的线程
    private Thread t;

    //线程的控制开关
    private boolean isRuning = true;
    //盘块的奖项
    private String[] mStrs = new String[]{"恭喜发财","IPAD","恭喜发财","服装一套","恭喜发财","IPONE"};
    //奖项的图片
    private int[] mImages = new int[]{R.drawable.a,R.drawable.b,R.drawable.cc,R.drawable.d,R.drawable.e,R.drawable.aa};
    private Bitmap[] mImgsBitmap;
    //盘块的颜色
    private int[] mColor = new int[]{0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01};
    //盘块的数量
    private int itemCount = 6;
    // 整个盘块的范围
    private RectF mRange = new RectF();
    //整个盘块的直径
    private int mRidius;
    //绘制图形的画笔和绘制文字的画笔
    private Paint arcPaint;
    private Paint textPaint;
    //盘块滚动的速度
    private double speed = 0;
    //开始角度
    private   volatile int startAngle = 0;
    //判断是否点击了定制按钮
    private boolean isShouldEnd;
    //转盘的中心位置
    private int mCenter;
    private int mPanding;
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.m);
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());
    public SurfaceViewTep(Context context) {
       this(context, null);
    }

    public SurfaceViewTep(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPanding = getPaddingLeft();
        //半径
        mRidius = width - mPanding*2;
        //中心点
        mCenter = width/2;

        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化绘制盘块的画笔
        arcPaint = new Paint();
        arcPaint.setDither(true);
        arcPaint.setAntiAlias(true);
     //初始化绘制文本的画笔
        textPaint = new Paint();
        textPaint.setColor(0xffffffff);
        textPaint.setTextSize(mTextSize);
        //初始化盘块绘制的范围
        mRange = new RectF(mPanding,mPanding,mPanding+mRidius,mPanding+mRidius);
        mImgsBitmap = new Bitmap[itemCount];
        for (int i =0;i < itemCount;i++){
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),mImages[i]);
        }

        isRuning = true;
        t = new Thread( this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRuning = false;
    }

    @Override
    public void run() {

        while (isRuning){
            long statr = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - statr<50){
                try {
                    Thread.sleep(50 - (end - statr));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void draw(){
        try {
        mCancas = mHolder.lockCanvas();

        if (mCancas != null){
            //绘制背景
            drawBg();
            //绘制盘块
            float tmpAngle = startAngle;
            float sweepAngle = 360/itemCount;
            for (int i =0;i<itemCount;i++){
                arcPaint.setColor(mColor[i]);
                //绘制盘块
                mCancas.drawArc(mRange, tmpAngle, sweepAngle, true, arcPaint);
                //绘制文本
                drawText(tmpAngle,sweepAngle,mStrs[i]);
                //绘制图片
                drawicon(tmpAngle,mImgsBitmap[i]);
                
                tmpAngle+=sweepAngle;
            }
            startAngle += speed;
            //如果点击了停止按钮
            if (isShouldEnd){
                speed-=1;
            }
            if (speed<=0){
                speed = 0;
                isShouldEnd = false;
            }


        }

        }catch (Exception e){

        }
        finally {
            if (mCancas != null){
                mHolder.unlockCanvasAndPost(mCancas);
            }
        }
    }
    //点击启动
    public void luckStart(){
        speed = 50;
        isShouldEnd = false;
    }
    public void luckEnd(){
        isShouldEnd = true;
    }
    //转盘还在旋转
    public boolean isStart(){
        return speed !=0;
    }
    public boolean isEnd(){
        return isShouldEnd;
    }

//绘制图像
    private void drawicon(float tmpAngle, Bitmap bitmap) {
        //设置图片的宽度为直径的八分之一
        int imgWidth = mRidius/8;

        //
        float angle = (float) ((tmpAngle+360/itemCount/2)*Math.PI/180);

        int x  = (int) (mCenter + mRidius/2/2*Math.cos(angle));
        int y  = (int) (mCenter + mRidius/2/2*Math.sin(angle));
        //确定图片的位置
        Rect rect = new Rect(x - imgWidth,y-imgWidth,x+imgWidth/2,y+imgWidth/2);

        mCancas.drawBitmap(bitmap,null,rect,null);

    }

    //绘制每个盘块的文本
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        //利用高水平偏移量，让蚊子居中
        float textWidh =  textPaint.measureText(mStr);
        int hOffset = (int) (mRidius*Math.PI/itemCount/2 - textWidh/2);
        Path path = new Path();
        int vOffset = mRidius/2/6;
        path.addArc(mRange,tmpAngle,sweepAngle);
        mCancas.drawTextOnPath(mStr,path,hOffset,vOffset,textPaint);
    }

    //绘制背景
    private void drawBg() {
        mCancas.drawColor(0xFFFFFFF);
        mCancas.drawBitmap(mBgBitmap,null,new Rect(mPanding/2,mPanding/2,getMeasuredWidth()-mPanding/2,getMeasuredHeight() - mPanding/2),null);
    }
}
