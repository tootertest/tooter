package com.jyb.tooter.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PointGroup extends View {

    private Paint mPaint;
    private int mSize;
    private int mRadius;
    private int mMargin;
    private int mRadiusDiv;
    private int mMarginMut;
    private int mX;
    private int mY;
    private int mYdiv;
    private int mYMutBottom;
    private int mCenter;
    private int mNormalColor;
    private int mSelectlColor;
    private int mSelect;
    private Canvas mCanvas;

    public PointGroup(Context context,int size) {
        super(context);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        mPaint.setStrokeWidth(8);//设置画笔粗细

        mSize = size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        mRadiusDiv = 40;
        mMarginMut = 2;
//        mSize = 3;
        mYMutBottom = 3;

        mRadius = getWidth() / 2 / mRadiusDiv;
        mMargin = mRadius * mMarginMut;
        mSelectlColor = Color.WHITE;
        mNormalColor = Color.LTGRAY;
        mX = getWidth() / 2;
        mY = mRadius;
        mYMutBottom = getHeight() - mRadius * mYMutBottom;
        double n = mSize;
        double r = n / 2;
        double center = Math.ceil(r);
        mCenter = (int) center;

        for (int i = 0; i < mSize; i++) {
            int x = mX - (mCenter - i - 1) * (mMargin + mRadius);
            if (i == mSelect) {
                mPaint.setColor(mSelectlColor);
            } else {
                mPaint.setColor(mNormalColor);
            }
            mCanvas.drawCircle(x, mYMutBottom, mRadius, mPaint);
        }
    }

    public void setSelect(int postion) {
        mSelect = postion;
    }

}
