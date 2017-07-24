package com.boger.mayiprizeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by liubo on 2017/7/21.
 */

public class SugerView extends View {
    private static final String TAG = "SugerView";
    static final String suan = "酸";
    static final String tian = "甜";
    public static final float LINE_WIDTH = 10f;
    private Paint mSuanPaint;
    private Paint mTianPaint;
    private Paint mLinepaint;
    private Paint mTextPaint;
    private Rect mLineLocation = new Rect();
    private Rect mIndicator = new Rect();
    private int mTotalValidWidth;

    private int value = -44;
    private boolean hasIndicator = false;

    public SugerView(Context context) {
        super(context);
    }

    public SugerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SugerView);
        hasIndicator = ta.getBoolean(R.styleable.SugerView_hasIndicator, true);
        ta.recycle();

        mSuanPaint = new Paint();
        mSuanPaint.setStyle(Paint.Style.FILL);
        mSuanPaint.setAntiAlias(true);
        mSuanPaint.setDither(true);
        mTianPaint = new Paint(mSuanPaint);
        mLinepaint = new Paint(mSuanPaint);
        mTextPaint = new Paint(mSuanPaint);

        mSuanPaint.setColor(ContextCompat.getColor(context, R.color.reef));
        mTianPaint.setColor(ContextCompat.getColor(context, R.color.pizazz));

        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(20f);

    }

    public SugerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int width;
    int height;

    private int lastX;
    private boolean touch;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch = true;
                lastX = x;
                int padding = (width - mTotalValidWidth) / 2;
                int click = x - padding > 0 ? x - padding : 0;
                click = click > mTotalValidWidth ? mTotalValidWidth : click;
                value = (int) ((click - mTotalValidWidth / 2) * 100f / (mTotalValidWidth / 2));
                setValue(value);
                return true;
            case MotionEvent.ACTION_MOVE:
                int offset = (int) ((x - lastX) * 100f / (mTotalValidWidth / 2));
                Log.d("event", x + "  " + event.getX() + " " + lastX + " " + value + " " + offset);
                value = value + offset;
                lastX = x;
                setValue(value);
                break;
            case MotionEvent.ACTION_UP:
                touch = false;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);
        int topMiddle = height / 2;
        int left = getPaddingLeft() - width / 2;
        int right = width / 2 - getPaddingRight();
        int top;
        int bottom;
        if (hasIndicator) {
            top = 0;
            bottom = topMiddle - getPaddingBottom();
        } else {
            top = getPaddingTop() - topMiddle;
            bottom = topMiddle - getPaddingBottom();
        }
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        int textWidth = (int) mTextPaint.measureText(suan);
        Log.d(TAG, " " + textWidth);
        int baseLine = (int) (bottom + top - fm.bottom - fm.top) / 2;
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.christi));
        canvas.drawText(suan, left + textWidth / 2, baseLine, mTextPaint);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.safety_orange));
        canvas.drawText(tian, right - textWidth / 2, baseLine, mTextPaint);

        int rtTop = top + 10;
        int rtBottom = bottom - 10;
        canvas.drawRect(left + textWidth / 2 * 4, rtTop, 0, rtBottom, mSuanPaint);
        canvas.drawRect(0, rtTop, right - textWidth * 2, rtBottom, mTianPaint);

        int progressWidth = (right - textWidth * 4 - left) / 2;
        mTotalValidWidth = progressWidth * 2;
        float percentage = value / 100f;
        if (percentage > 0) {
            mLinepaint.setColor(ContextCompat.getColor(getContext(), R.color.safety_orange));
            mLineLocation.left = (int) (progressWidth * percentage);
            mLineLocation.right = (int) (mLineLocation.left + LINE_WIDTH);
            mLineLocation.top = top;
            mLineLocation.bottom = bottom;
            canvas.drawRect(mLineLocation, mLinepaint);
        } else {
            mLinepaint.setColor(ContextCompat.getColor(getContext(), R.color.christi));
            mLineLocation.left = (int) (progressWidth * percentage);
            mLineLocation.right = (int) (mLineLocation.left + LINE_WIDTH);
            mLineLocation.top = top;
            mLineLocation.bottom = bottom;
            canvas.drawRect(mLineLocation, mLinepaint);
        }

        if (touch && hasIndicator) {
            //draw  indicator

            mIndicator.left = (mLineLocation.left - 25);
            mIndicator.right = mLineLocation.right + 25;
            mIndicator.top = 10 - topMiddle;
            mIndicator.bottom = -10;
            Path path = new Path();
            path.moveTo((mIndicator.left + mIndicator.right) / 2, -10);
            path.lineTo(mIndicator.left, (mIndicator.bottom + mIndicator.top) / 2);
            path.quadTo((mIndicator.left + mIndicator.right) / 2, mIndicator.top, mIndicator.right, (mIndicator.bottom + mIndicator.top) / 2);
            path.lineTo((mIndicator.left + mIndicator.right) / 2, -10);
            canvas.drawPath(path, mLinepaint);
            mTextPaint.setColor(Color.WHITE);
            canvas.drawText(value + "", (mIndicator.right + mIndicator.left) / 2, (mIndicator.bottom + mIndicator.top - fm.bottom - fm.top) / 2, mTextPaint);
        }

    }

    /**
     * 0-100;
     * <p>
     * +  tian
     * - suan
     *
     * @param value
     */
    public void setValue(int value) {
        if (value > 0) {
            if (value > 100) {
                value = 100;
            }
        } else {
            if (value < -100) {
                value = -100;
            }
        }
        this.value = value;
        invalidate();
    }
}
