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

    //���ڻ��Ƶ��߳�
    private Thread t;

    //�̵߳Ŀ��ƿ���
    private boolean isRuning = true;
    //�̿�Ľ���
    private String[] mStrs = new String[]{"��ϲ����","IPAD","��ϲ����","��װһ��","��ϲ����","IPONE"};
    //�����ͼƬ
    private int[] mImages = new int[]{R.drawable.a,R.drawable.b,R.drawable.cc,R.drawable.d,R.drawable.e,R.drawable.aa};
    private Bitmap[] mImgsBitmap;
    //�̿����ɫ
    private int[] mColor = new int[]{0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01,0xFFFc300,0xFFF17E01};
    //�̿������
    private int itemCount = 6;
    // �����̿�ķ�Χ
    private RectF mRange = new RectF();
    //�����̿��ֱ��
    private int mRidius;
    //����ͼ�εĻ��ʺͻ������ֵĻ���
    private Paint arcPaint;
    private Paint textPaint;
    //�̿�������ٶ�
    private double speed = 0;
    //��ʼ�Ƕ�
    private   volatile int startAngle = 0;
    //�ж��Ƿ����˶��ư�ť
    private boolean isShouldEnd;
    //ת�̵�����λ��
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

        //�ɻ�ý���
        setFocusable(true);
        setFocusableInTouchMode(true);
        //���ó���
        setKeepScreenOn(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPanding = getPaddingLeft();
        //�뾶
        mRidius = width - mPanding*2;
        //���ĵ�
        mCenter = width/2;

        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //��ʼ�������̿�Ļ���
        arcPaint = new Paint();
        arcPaint.setDither(true);
        arcPaint.setAntiAlias(true);
     //��ʼ�������ı��Ļ���
        textPaint = new Paint();
        textPaint.setColor(0xffffffff);
        textPaint.setTextSize(mTextSize);
        //��ʼ���̿���Ƶķ�Χ
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
            //���Ʊ���
            drawBg();
            //�����̿�
            float tmpAngle = startAngle;
            float sweepAngle = 360/itemCount;
            for (int i =0;i<itemCount;i++){
                arcPaint.setColor(mColor[i]);
                //�����̿�
                mCancas.drawArc(mRange, tmpAngle, sweepAngle, true, arcPaint);
                //�����ı�
                drawText(tmpAngle,sweepAngle,mStrs[i]);
                //����ͼƬ
                drawicon(tmpAngle,mImgsBitmap[i]);
                
                tmpAngle+=sweepAngle;
            }
            startAngle += speed;
            //��������ֹͣ��ť
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
    //�������
    public void luckStart(){
        speed = 50;
        isShouldEnd = false;
    }
    public void luckEnd(){
        isShouldEnd = true;
    }
    //ת�̻�����ת
    public boolean isStart(){
        return speed !=0;
    }
    public boolean isEnd(){
        return isShouldEnd;
    }

//����ͼ��
    private void drawicon(float tmpAngle, Bitmap bitmap) {
        //����ͼƬ�Ŀ��Ϊֱ���İ˷�֮һ
        int imgWidth = mRidius/8;

        //
        float angle = (float) ((tmpAngle+360/itemCount/2)*Math.PI/180);

        int x  = (int) (mCenter + mRidius/2/2*Math.cos(angle));
        int y  = (int) (mCenter + mRidius/2/2*Math.sin(angle));
        //ȷ��ͼƬ��λ��
        Rect rect = new Rect(x - imgWidth,y-imgWidth,x+imgWidth/2,y+imgWidth/2);

        mCancas.drawBitmap(bitmap,null,rect,null);

    }

    //����ÿ���̿���ı�
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        //���ø�ˮƽƫ�����������Ӿ���
        float textWidh =  textPaint.measureText(mStr);
        int hOffset = (int) (mRidius*Math.PI/itemCount/2 - textWidh/2);
        Path path = new Path();
        int vOffset = mRidius/2/6;
        path.addArc(mRange,tmpAngle,sweepAngle);
        mCancas.drawTextOnPath(mStr,path,hOffset,vOffset,textPaint);
    }

    //���Ʊ���
    private void drawBg() {
        mCancas.drawColor(0xFFFFFFF);
        mCancas.drawBitmap(mBgBitmap,null,new Rect(mPanding/2,mPanding/2,getMeasuredWidth()-mPanding/2,getMeasuredHeight() - mPanding/2),null);
    }
}
