package com.meng.interest.recyclerviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * @author wangchengmeng
 *         圆形的progressbar
 */

public class CircleProgress extends ColorFullProgress {

    private Paint mPaint;
    private int mRadiu = dp2px(30);//半径
    private int mPaintMaxStrok;//画笔中宽度大的一个值

    //progress的限制范围

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mRadiu = (int) array.getDimension(R.styleable.CircleProgress_circle_radio, mRadiu);
        array.recycle();
        mReachHeight = (int) (mUnreachHeight * 2.5f);

        mPaintMaxStrok = Math.max(mReachHeight, mUnreachHeight);

        //初始化画笔
        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);
        //防抖动
        mPaint.setDither(true);

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expectWidth = mRadiu * 2 + mPaintMaxStrok + getPaddingRight() + getPaddingLeft();

        //resolveSize内部实现跟通常区分三种情况写法一样
        int width = resolveSize(expectWidth, widthMeasureSpec);
        int height = resolveSize(expectWidth, heightMeasureSpec);//画圆形控件 大小默认为正方形

        //计算半径
        int readWidth = Math.min(width, height);
        mRadiu = (readWidth - mPaintMaxStrok - getPaddingLeft() - getPaddingRight()) / 2;

        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft() + mPaintMaxStrok / 2, getPaddingTop() + mPaintMaxStrok / 2);
        //话unreach部分  画个圆
        mPaint.setStrokeWidth(mUnreachHeight);
        mPaint.setColor(mUnreachColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mRadiu, mRadiu, mRadiu, mPaint);
        //draw reach部分
        mPaint.setStrokeWidth(mReachHeight);
        mPaint.setColor(mReachColor);
        mPaint.setStyle(Paint.Style.STROKE);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadiu * 2, mRadiu * 2), 0, sweepAngle, false, mPaint);

        //draw  text
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        int textHeight = (int) (mPaint.descent() + mPaint.ascent());
        canvas.drawText(text, mRadiu - textWidth / 2, mRadiu - textHeight / 2, mPaint);
        canvas.restore();
    }
}
