package com.meng.interest.recyclerviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * @author wangchengmeng
 *         自定义ProgressBar
 */

public class ColorFullProgress extends ProgressBar {
    //属性默认值
    private static final int ATTR_UNREACH_COLOR  = 0xFFD3D6DA;
    private static final int ATTR_UNREACH_HEIGHT = 2; //dp
    private static final int ATTR_REACH_COLOR    = 0xFFFC00D1;
    private static final int ATTR_REACH_HEIGHT   = 2;//dp
    private static final int ATTR_TEXT_COLOR     = 0xFFFC00D1;
    private static final int ATTR_TEXT_SIZE      = 10; //sp
    private static final int ATTR_TEXT_OFFSET    = 10; //dp

    //属性的成员变量
    private int mUnreachColor  = ATTR_UNREACH_COLOR;
    private int mUnreachHeight = dp2px(ATTR_UNREACH_HEIGHT);
    private int mReachColor    = ATTR_REACH_COLOR;
    private int mReachHeight   = dp2px(ATTR_REACH_HEIGHT);
    private int mTextColor     = ATTR_TEXT_COLOR;
    private int mTextSize      = sp2px(ATTR_TEXT_SIZE);
    private int mTextOffset    = dp2px(ATTR_TEXT_OFFSET);

    private Paint mPaint;

    //控件的宽度
    private int mProgressBarWidth;

    public ColorFullProgress(Context context) {
        this(context, null);
    }

    public ColorFullProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        getStyledAttrs(context, attrs);
        init();
    }

    public ColorFullProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
    }

    private void getStyledAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ColorFullProgress);
        mUnreachColor = array.getColor(R.styleable.ColorFullProgress_progress_unreach_color, ATTR_UNREACH_COLOR);
        mUnreachHeight = (int) array.getDimension(R.styleable.ColorFullProgress_progress_unreach_height, ATTR_UNREACH_HEIGHT);
        mReachColor = array.getColor(R.styleable.ColorFullProgress_progress_reach_color, ATTR_REACH_COLOR);
        mReachHeight = (int) array.getDimension(R.styleable.ColorFullProgress_progress_reach_height, ATTR_REACH_HEIGHT);
        mTextColor = array.getColor(R.styleable.ColorFullProgress_progress_text_color, ATTR_TEXT_COLOR);
        mTextSize = (int) array.getDimension(R.styleable.ColorFullProgress_progress_text_size, ATTR_TEXT_SIZE);
        mTextOffset = (int) array.getDimension(R.styleable.ColorFullProgress_progress_text_offset, ATTR_TEXT_OFFSET);
        array.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int viewHeight;

        //进度条 宽度一般给定值 所以这里就不分析三种情况默认是 EXACTLY

        //测量高度
        if (heightMode == MeasureSpec.EXACTLY) {
            //给定了值
            viewHeight = height;
        } else {
            //剩余两种情况 根据情况分析(不要忘记padding)  在这里progress高度取 reach、unreach、text的高度三者最大值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());//要绘制文字的高度
            viewHeight = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mUnreachHeight, mReachHeight), textHeight);
            if (heightMode == MeasureSpec.AT_MOST) {
                //AT_MOST 是至多的意思 意思是不能超过用户本身给的值
                viewHeight = Math.min(viewHeight, height);
            }
        }
        setMeasuredDimension(width, viewHeight);
        //测量完毕就可以 获取到view的宽度了
        mProgressBarWidth = getMeasuredWidth();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        //绘制是从左边的paddingLeft 高度的一半作为坐标开始绘制
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        //定义个标志位 是否需要绘制unreach部分
        boolean noUnreach = false;
        //1、draw  reach部分
        float radio = getProgress() * 1.0f / getMax();//进度
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);

        float progress = radio * mProgressBarWidth;
        if (progress + textWidth >= mProgressBarWidth) {
            progress = mProgressBarWidth - textWidth - getPaddingRight();//reach部分不可以超过这个值不然文本就看不到了
            noUnreach = true;
        }
        float endX = progress - mTextOffset / 2;
        if (endX > 0) {
            //绘制已经reach的进度
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        //2、draw 文本
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progress, y, mPaint);
        //3、draw unreach

        if (!noUnreach) {
            int start = (int) (progress + mTextOffset / 2 + textWidth);
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(start, 0, mProgressBarWidth, 0, mPaint);
        }
        canvas.restore();
    }

    //单位转换
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
