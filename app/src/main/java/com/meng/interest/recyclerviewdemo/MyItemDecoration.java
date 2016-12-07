package com.meng.interest.recyclerviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author wangchengmeng
 */

public class MyItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = {android.R.attr.listDivider};
    private Drawable mDivider;

    public MyItemDecoration(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        mDivider = typedArray.getDrawable(0);//先试用系统知道的分割线
        typedArray.recycle();//不需要用了就释放
    }

    @Override

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //先获取 divider的上下左右
        int left = parent.getPaddingLeft();

        int right = parent.getWidth() - parent.getPaddingRight();

        //获取子元素
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //获取当前的item
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            //获取到divider左上角的坐标
            int top = child.getBottom() + layoutParams.bottomMargin;
            //右下角的坐标
            int bottom = top + mDivider.getIntrinsicHeight();
            //设置divider的范围
            mDivider.setBounds(left, top, right, bottom);
            //绘制divider到item的下方
            mDivider.draw(c);
        }
    }

}
