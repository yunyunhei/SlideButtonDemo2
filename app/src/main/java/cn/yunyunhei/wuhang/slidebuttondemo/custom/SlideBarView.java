package cn.yunyunhei.wuhang.slidebuttondemo.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cn.yunyunhei.wuhang.slidebuttondemo.R;

/**
 * 用于例如相机页面调节曝光度的滑块，或者可以用于调节屏幕亮度的滑块
 * <p>
 * 控件测量方面并没有重写，所以使用一定要设置宽高,不能使用wrap_content
 * <p>
 * Created by wuhang on 17/2/28.
 */

public class SlideBarView extends View {

    private static final boolean DEBUG = true;

    //初始时的颜色
    private int colorDefault = ContextCompat.getColor(getContext(), R.color.colorAccent);

    //开始变化时的颜色
    private int colorChange = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);

    //绘制的文字的默认文本
    private static final String Default_Draw_Text = "0.0";

    //绘制的文字的默认大小
    private static final int Default_Text_Font_Size = 30;

    //绘制的文字的默认的中心距离右侧的距离
    private static final int Default_Text_Right_Margin = 40;

    //字体大小
    private int mTextFontSize = Default_Text_Font_Size;

    //绘制线条以及图片的画笔
    private Paint mPaint;
    //绘制文字的画笔
    private TextPaint mTextPaint;

    //绘制文字的x,y值，目前是以以Align.Center计算。
    private float mTextX;
    private float mTextY;

    //文字中心距离右侧的距离
    private int mTextCenterRightMargin = Default_Text_Right_Margin;
    //需要绘制的文本内容
    private String mDrawText = Default_Draw_Text;


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

    //图片绘制时的左边和顶部的坐标
    private int mBitmapLeft = 0;
    private int mBitmapTop = 0;

    //初始时绘制图片的顶部的坐标
    private int mBitmapDefaultTop;


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


    //在xml文件中设置的布局的padding值
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
        //布局宽高确定后计算初始值
        mWidth = w;
        mHeight = h;

        int centerY = mHeight / 2;

        //获取绘制图片的宽高
        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();

        //计算图片开始绘制的左边与上方的坐标
        mBitmapLeft = mWidth - mBitmapWidth - paddingRight;
        mBitmapTop = centerY - mBitmapHeight / 2;

        //存储初始时绘制图片的顶部位置
        mBitmapDefaultTop = mBitmapTop;

        int drawCenterX = mWidth - mBitmapWidth / 2;

        //计算上方线的开始,结束x值
        mFirstLineStartX = drawCenterX - drawLineWidth / 2 - paddingRight;
        mFirstLineEndX = mFirstLineStartX + drawLineWidth;

        //计算下方线的开始,结束x值
        mSecondLineStartX = mFirstLineStartX;
        mSecondLineEndX = mFirstLineEndX;

        //计算上方线的开始,结束y值
        mFirstLineEndY = mBitmapTop - drawLineSpace;
        mFirstLineStartY = centerY - drawLineHeight / 2;

        //计算下方线的开始,结束y值
        mSecondLineEndY = mFirstLineStartY + drawLineHeight;
        mSecondLineStartY = mBitmapTop + mBitmapHeight + drawLineSpace;

        //计算接受事件的区域 x范围
        move_left_slide = mWidth - mBitmapWidth - paddingRight;

        //计算图片的上方与下方的极限值
        mBitmap_Top_MIN = mFirstLineStartY - mBitmapHeight / 2;
        mBitmap_Bottom_MAX = mSecondLineEndY - mBitmapHeight / 2;


        //计算文字的绘制x,y值
        mTextX = mBitmapLeft - mTextCenterRightMargin;
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        float bottom = fontMetrics.bottom;
        float top = fontMetrics.top;
        float descent = fontMetrics.descent;
        float ascent = fontMetrics.ascent;
        float leading = fontMetrics.leading;

        log(String.format("FontMetrics bottom : %s , top : %s , descent : %s , ascent : %s , leading : %s ",
                bottom, top, descent, ascent, leading));

        mTextY = (bottom - top) / 2 + mBitmapTop + mBitmapHeight / 2 - bottom;


        log(String.format("mWidth : %s , mHeight : %s , mBitmapLeft : %s , mBitmapTop : %s , mBitmap_Top_MIN : %s , mBitmap_Bottom_MAX : %s ",
                mWidth, mHeight, mBitmapLeft, mBitmapTop, mBitmap_Top_MIN, mBitmap_Bottom_MAX));

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
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs,int defStyleAttr) {
        if (attrs != null){
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideBarView, defStyleAttr, 0);
            int n = typedArray.getIndexCount();
            for (int i = 0 ; i < n ; i ++){
                int attr = typedArray.getIndex(i);
                switch (attr){
                    case R.styleable.SlideBarView_slideBarTextSize:
                        mTextFontSize = typedArray.getDimensionPixelSize(attr,Default_Text_Font_Size);
                        break;
                    case R.styleable.SlideBarView_slideBarTextRightSpace:
                        mTextCenterRightMargin = typedArray.getDimensionPixelSize(attr,Default_Text_Right_Margin);
                        break;
                }
            }
            typedArray.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(colorDefault);
        paddingRight = getPaddingRight();
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextFontSize);
        mTextPaint.setColor(colorDefault);

        log(String.format("getPadding paddingLeft : %s , paddingRight : %s , paddingTop : %s , paddingBottom : %s ", paddingLeft, paddingRight, paddingTop, paddingBottom));
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

        //绘制左侧的文字
        drawText(canvas);

    }

    private void drawRect(Canvas canvas) {
        canvas.drawRect(mFirstLineStartX, mFirstLineStartY, mFirstLineEndX, mFirstLineEndY, mPaint);
        canvas.drawRect(mSecondLineStartX, mSecondLineStartY, mSecondLineEndX, mSecondLineEndY, mPaint);
    }

    private void drawBitmap(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mBitmapLeft, mBitmapTop, mPaint);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(mDrawText, mTextX, mTextY, mTextPaint);
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
                log(String.format("ACTION_MOVE eventX : %s , eventY : %s , changed_y : %s ", eventX, eventY, changed_y));
                reCalculate(changed_y);
                break;
            case MotionEvent.ACTION_UP:
                log(String.format("ACTION_UP eventX : %s , eventY : %s", eventX, eventY));
                restoreDefaultColor();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void restoreDefaultColor() {
//        mPaint.setColor(colorDefault);
        postInvalidate();
    }

    private void reCalculate(float changed) {
        mBitmapTop = (int) (down_bitmap_top + changed);

        if (mBitmapTop <= mBitmap_Top_MIN) {
            mBitmapTop = mBitmap_Top_MIN;
        }

        if (mBitmapTop >= mBitmap_Bottom_MAX) {
            mBitmapTop = mBitmap_Bottom_MAX;
        }

        //获取到的值的范围是[0~1.0]
        float percent = (mBitmapTop - mBitmap_Top_MIN) * 1f / (mBitmap_Bottom_MAX - mBitmap_Top_MIN);

        //便于计算展示，转换为[-2.1~2.1]
        percent = (percent - 0.5f) * 4.2f;

        log(String.format("percent : %s", percent));

        mFirstLineEndY = mBitmapTop - drawLineSpace;

        mSecondLineStartY = mBitmapTop + mBitmapHeight + drawLineSpace;

        log(String.format("mWidth : %s , mHeight : %s , mBitmapLeft : %s , mBitmapTop : %s ",
                mWidth, mHeight, mBitmapLeft, mBitmapTop));

        log(String.format("mFirstLineStartX : %s , mFirstLineEndX : %s , mFirstLineStartY : %s , mFirstLineEndY : %s , "
                , mFirstLineStartX, mFirstLineEndX, mFirstLineStartY, mFirstLineEndY));

        log(String.format("mSecondLineStartX : %s , mSecondLineEndX : %s , mSecondLineStartY : %s , mSecondLineEndY : %s , "
                , mSecondLineStartX, mSecondLineEndX, mSecondLineStartY, mSecondLineEndY));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        float bottom = fontMetrics.bottom;
        float top = fontMetrics.top;

        log(String.format("FontMetrics bottom : %s , top : %s ", bottom, top));

        mTextY = (bottom - top) / 2 + mBitmapTop + mBitmapHeight / 2 - bottom;


        //获取需要绘制的文字
        mDrawText = getDrawText(percent);

        postInvalidate();
    }

    private String getDrawText(float percent) {
        if (Math.abs(percent) <= 0.2f) {
            mPaint.setColor(colorDefault);
            mTextPaint.setColor(colorDefault);

            //当滑动到这部分区域是，使图片顶部的位置强制在这个地方，形成一种磁力的效果
            mBitmapTop = mBitmapDefaultTop;
            mFirstLineEndY = mBitmapTop - drawLineSpace;
            mSecondLineStartY = mBitmapTop + mBitmapHeight + drawLineSpace;

            //使文字的位置也固定
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float bottom = fontMetrics.bottom;
            float top = fontMetrics.top;
            log(String.format("FontMetrics bottom : %s , top : %s ", bottom, top));
            mTextY = (bottom - top) / 2 + mBitmapTop + mBitmapHeight / 2 - bottom;

            return Default_Draw_Text;
        }

        mPaint.setColor(colorChange);
        mTextPaint.setColor(colorChange);

        //记录数据的正负号
        int sign = percent > 0 ? 1 : -1;

        //转化为2~21
        int value = Math.abs((int) (percent * 10));

        //转化为1~20
        value -= 1;

        //将结果转换成保留一位小数，直接用于展示
        float result = ((float) value) / 10;

        return String.format("%s%s", (sign == 1 ? "-" : "+"), result);
    }

    private static void log(String content) {
        if (DEBUG) {
            Log.d("SlideBarView", content);
        }
    }
}
