package com.boger.mayiprizeview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by liubo on 2017/7/19.
 */

public class MayiView extends View {
    private static final String TAG = "MayiView";
    private Paint mBgPaint;
    private Paint mTextPaint;
    private int width;
    private int height;
    private String time = "08";

    private Handler workHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public MayiView(Context context) {
        this(context, null);
    }

    public MayiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MayiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.BLACK);
        mBgPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setTextSize(dp2px(14));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return super.getSuggestedMinimumHeight() == 0 ? dp2px(20) : super.getSuggestedMinimumHeight();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return super.getSuggestedMinimumWidth() == 0 ? dp2px(20) : super.getSuggestedMinimumHeight();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        valueAnimator.setFloatValues(0, width);
        valueAnimator.setDuration(4000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    private float value;

    private int lastX, lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int o1 = x - lastX;
                int o2 = y - lastY;
                offsetTopAndBottom(o2);
            case MotionEvent.ACTION_UP:
                int ox = x - lastX;
                int oy = y - lastY;
                offsetTopAndBottom(oy);
        }
        return true;
    }

    ValueAnimator valueAnimator = new ValueAnimator();


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(width / 2, height / 2);
        canvas.drawLine(-width / 2, 0, width / 2, 0, mBgPaint);
        canvas.drawLine(0, -height / 2, 0, height / 2, mBgPaint);
        /*Camera camera = new Camera();
        camera.rotateX(value);
        Matrix matrix = new Matrix();
        camera.getMatrix(matrix);
        canvas.setMatrix(matrix);*/
        mBgPaint.setColor(Color.BLACK);
        RectF rf = new RectF(getPaddingLeft() - width / 2, getPaddingTop() - height / 2, width / 2 - getPaddingRight(), height / 2 - getPaddingBottom());
        canvas.drawRoundRect(rf, 5f, 5f, mBgPaint);
        mBgPaint.setColor(Color.WHITE);
        canvas.drawLine(getPaddingLeft() - width / 2, 0, width / 2 - getPaddingRight(), 0, mBgPaint);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        canvas.drawText(time, 0, 0, mTextPaint);

    }

    private int dp2px(int value) {
        return (int) (getResources().getDisplayMetrics().density * value + 0.5f);
    }
}
