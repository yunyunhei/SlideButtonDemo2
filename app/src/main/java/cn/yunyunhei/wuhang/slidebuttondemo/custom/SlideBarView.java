package cn.yunyunhei.wuhang.slidebuttondemo.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cn.yunyunhei.wuhang.slidebuttondemo.R;

/**
 * Created by wuhang on 17/2/28.
 */

public class SlideBarView extends View {

    //初始时的颜色
    private int colorDefault = ContextCompat.getColor(getContext(), R.color.colorAccent);

    //开始变化时的颜色
    private int colorChange = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);

    private Paint mPaint;


    //绘制的高度
    private int drawLineHeight = 600;

    //绘制的宽度  线的宽度
    private int drawLineWidth = 6;

    //绘制的线和图片之间的空隙
    private int drawLineSpace = 20;

    //第一条线的绘制区域
    private int mFirstLineStartX = 0;
    private int mFirstLineStartY = 0;
    private int mFirstLineEndX = 0;
    private int mFirstLineEndY = 0;

    //第二条线的绘制区域
    private int mSecondLineStartX = 0;
    private int mSecondLineStartY = 0;
    private int mSecondLineEndX = 0;
    private int mSecondLineEndY = 0;


    //中间的图片
    private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_20_effect_bright);

    private int mBitmapLeft = 0;
    private int mBitmapTop = 0;


    //控件的宽
    private int mWidth;
    //控件的高
    private int mHeight;

    //点击滑动区域的左边范围
    private float move_left_slide;
    private int mBitmapWidth;
    private int mBitmapHeight;


    //图片可滑动到的最上方的Y的最小值
    private int mBitmap_Top_MIN;
    //图片可滑动到的最下方的Y的最大值
    private int mBitmap_Bottom_MAX;


    private int paddingRight;
    private int paddingLeft;
    private int paddingTop;
    private int paddingBottom;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        int centerY = mHeight / 2;

        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        mBitmapLeft = mWidth - mBitmapWidth - paddingRight;
        mBitmapTop = centerY - mBitmapHeight / 2;

        int drawCenterX = mWidth - mBitmapWidth / 2;


        mFirstLineStartX = drawCenterX - drawLineWidth / 2 - paddingRight;
        mFirstLineEndX = mFirstLineStartX + drawLineWidth;

        mSecondLineStartX = mFirstLineStartX;
        mSecondLineEndX = mFirstLineEndX;

        mFirstLineEndY = mBitmapTop - drawLineSpace;
        mFirstLineStartY = centerY - drawLineHeight / 2;

        mSecondLineEndY = mFirstLineStartY + drawLineHeight;
        mSecondLineStartY = mBitmapTop + mBitmapHeight + drawLineSpace;

        move_left_slide = mWidth - mBitmapWidth - paddingRight;

        mBitmap_Top_MIN = mFirstLineStartY - mBitmapHeight / 2;
        mBitmap_Bottom_MAX = mSecondLineEndY - mBitmapHeight / 2;


        log(String.format("mWidth : %s , mHeight : %s , mBitmapLeft : %s , mBitmapTop : %s , mBitmap_Top_MIN : %s , mBitmap_Bottom_MAX : %s ",
                mWidth, mHeight, mBitmapLeft, mBitmapTop,mBitmap_Top_MIN,mBitmap_Bottom_MAX));

        log(String.format("mFirstLineStartX : %s , mFirstLineEndX : %s , mFirstLineStartY : %s , mFirstLineEndY : %s , "
                , mFirstLineStartX, mFirstLineEndX, mFirstLineStartY, mFirstLineEndY));

        log(String.format("mSecondLineStartX : %s , mSecondLineEndX : %s , mSecondLineStartY : %s , mSecondLineEndY : %s , "
                , mSecondLineStartX, mSecondLineEndX, mSecondLineStartY, mSecondLineEndY));
    }

    public SlideBarView(Context context) {
        this(context, null);
    }

    public SlideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(colorDefault);
        paddingRight = getPaddingRight();
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
        log(String.format("getPadding paddingLeft : %s , paddingRight : %s , paddingTop : %s , paddingBottom : %s ",paddingLeft,paddingRight,paddingTop, paddingBottom));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWidth == 0 || mHeight == 0) {
            return;
        }

        //绘制矩形区域
        drawRect(canvas);

        //绘制图片Bitmap
        drawBitmap(canvas);

    }

    private void drawRect(Canvas canvas) {
        canvas.drawRect(mFirstLineStartX, mFirstLineStartY, mFirstLineEndX, mFirstLineEndY, mPaint);
        canvas.drawRect(mSecondLineStartX, mSecondLineStartY, mSecondLineEndX, mSecondLineEndY, mPaint);
    }

    private void drawBitmap(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mBitmapLeft, mBitmapTop, mPaint);
    }


//    private float down_x = 0;
    private float down_y = 0;
    private int down_bitmap_top;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (eventX > move_left_slide) {
//                    down_x = event.getX();
                    down_y = event.getY();
                    down_bitmap_top = mBitmapTop;
                    log(String.format("ACTION_DOWN eventX : %s , eventY : %s , move_left_slide : %s , ", eventX, eventY, move_left_slide));
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                log(String.format("ACTION_CANCEL eventX : %s , eventY : %s", eventX, eventY));
                restoreDefaultColor();
                break;
            case MotionEvent.ACTION_MOVE:
                float changed_y = eventY - down_y;
                log(String.format("ACTION_MOVE eventX : %s , eventY : %s , changed_y : %s ", eventX, eventY,changed_y));
                reCalculate(changed_y);
                break;
            case MotionEvent.ACTION_UP:
                log(String.format("ACTION_UP eventX : %s , eventY : %s", eventX, eventY));
                restoreDefaultColor();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void restoreDefaultColor(){
        mPaint.setColor(colorDefault);
        postInvalidate();
    }

    private void reCalculate(float changed) {
        mBitmapTop = (int) (down_bitmap_top + changed);

        if (mBitmapTop <= mBitmap_Top_MIN){
            mBitmapTop = mBitmap_Top_MIN;
        }

        if (mBitmapTop >= mBitmap_Bottom_MAX){
            mBitmapTop = mBitmap_Bottom_MAX;
        }

        mFirstLineEndY = mBitmapTop - drawLineSpace;

        mSecondLineStartY = mBitmapTop + mBitmapHeight + drawLineSpace;

        log(String.format("mWidth : %s , mHeight : %s , mBitmapLeft : %s , mBitmapTop : %s ",
                mWidth, mHeight, mBitmapLeft, mBitmapTop));

        log(String.format("mFirstLineStartX : %s , mFirstLineEndX : %s , mFirstLineStartY : %s , mFirstLineEndY : %s , "
                , mFirstLineStartX, mFirstLineEndX, mFirstLineStartY, mFirstLineEndY));

        log(String.format("mSecondLineStartX : %s , mSecondLineEndX : %s , mSecondLineStartY : %s , mSecondLineEndY : %s , "
                , mSecondLineStartX, mSecondLineEndX, mSecondLineStartY, mSecondLineEndY));
        mPaint.setColor(colorChange);
        postInvalidate();
    }

    private static void log(String content) {
        Log.d("SlideBarView", content);
    }
}
