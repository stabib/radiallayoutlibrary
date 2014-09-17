package com.rangiworks.android.widget.radiallibrary;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by sajjad on 8/25/14.
 */
public class FancyRadialLayout extends RadialLayout {

    private static final int DEFAULT_PAINT_WIDTH = 2;

    private static final int DEFAULT_SHADOW_WIDTH = 1;

    private boolean mDrawCirclePerimter;

    private Paint mCirclePaint;
    private int mCirclePaintColor;
    private int mCirclePaintWidth;
    private int mCirclePaintShadowColor;

    private int mCirclePaintShadowDx;
    private int mCirclePaintShadowDy;
    private int mCirclePaintShadowRadius;

    private ImageView mCircleImageView;

    public FancyRadialLayout(Context context) {
        this(context, null);
    }

    public FancyRadialLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FancyRadialLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FancyRadialLayout);

        mDrawCirclePerimter = a.getBoolean(R.styleable.FancyRadialLayout_drawCirclePerimeter, false);

        mCirclePaintColor = a.getColor(R.styleable.FancyRadialLayout_circlePaintColor, Color.WHITE);

        mCirclePaintWidth = a.getDimensionPixelSize(R.styleable.FancyRadialLayout_circlePaintWidth, DEFAULT_PAINT_WIDTH);

        mCirclePaintShadowColor = a.getColor(R.styleable.FancyRadialLayout_circlePaintShadowColor, Color.DKGRAY);

        mCirclePaintShadowDx = a.getDimensionPixelSize(R.styleable.FancyRadialLayout_circlePaintShadowDx, DEFAULT_SHADOW_WIDTH);

        mCirclePaintShadowDy = a.getDimensionPixelSize(R.styleable.FancyRadialLayout_circlePaintShadowDy, DEFAULT_SHADOW_WIDTH);

        mCirclePaintShadowRadius = a.getDimensionPixelSize(R.styleable.FancyRadialLayout_circlePaintShadowRadius, DEFAULT_SHADOW_WIDTH);

        a.recycle();

        init();

        setWillNotDraw(false);
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStrokeWidth(mCirclePaintWidth);
        mCirclePaint.setColor(mCirclePaintColor);
//        mCirclePaint.set
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setShadowLayer(mCirclePaintShadowRadius, mCirclePaintShadowDx, mCirclePaintShadowDy, mCirclePaintShadowColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mDrawCirclePerimter){
            drawCirclePerimeter(canvas);
        }

        super.onDraw(canvas);
    }

    private void drawCirclePerimeter(Canvas c){
        float x = getWidth() / 2;
        float y = getHeight() / 2;

        c.drawCircle(x, y, getRadius(), mCirclePaint);
    }

    public boolean isDrawCirclePerimter() {
        return mDrawCirclePerimter;
    }

    public void setDrawCirclePerimter(boolean drawCirclePerimter) {
        mDrawCirclePerimter = drawCirclePerimter;
    }

    public static class CustomLayoutTransition extends LayoutTransition{


    }
}
