package com.rangiworks.android.widget.radiallibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sajjad on 8/22/14.
 */
public class RadialLayout extends ViewGroup {
    private static final String TAG = RadialLayout.class.getSimpleName();
    private static final int FIRST_CHILD_ANGLE_OFFSET = 270;

    private int mRadius;

    private boolean mHasCenterChild;

    private int mFirstChildAngleOffset; //the degree offset of the first radial child

    public RadialLayout(Context context) {
        this(context, null);
    }

    public RadialLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadialLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RadialLayout);
        mRadius = array.getLayoutDimension(R.styleable.RadialLayout_radius, 0);
        mFirstChildAngleOffset = array.getInt(R.styleable.RadialLayout_firstChildOffsetAngle, FIRST_CHILD_ANGLE_OFFSET);
//        mHasCenterChild = array.getBoolean(R.styleable.RadialLayout_hasCenterChild, false);

        array.recycle();
    }



    /**
     * The measurement of this view will be dependent on the raidus, the maximum width among the
     * child views, and the maximum height among the child views. The measurement is calculated by
     * adding
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        int maxCenterChildHeight = 0;
        int maxCenterChildWidth = 0;

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams)child.getLayoutParams();
            if (child.getVisibility() != GONE) {
                // Measure the child
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                //only update the max height if this view isn't the center view, but watch out if radius is zero, the r=0 is handled further down
                if(!lp.mIsCentered) {
                    maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                    maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                }else{
                    maxCenterChildHeight = Math.max(child.getMeasuredHeight(), maxCenterChildHeight);
                    maxCenterChildWidth = Math.max(child.getMeasuredWidth(), maxCenterChildWidth);
                }
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        if(mRadius == LayoutParams.FIT_SHORTEST_WIDTH){ //take the parents size and adjust the radius accordingly
            if(specHeight < specWidth){
                mRadius = (specHeight - maxHeight) / 2;
            }else{
                mRadius = (specWidth - maxWidth ) / 2;
            }
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight + mRadius * 2 +  paddingTop + paddingBottom , getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth + mRadius * 2 + paddingLeft + paddingRight, getSuggestedMinimumWidth());

        //let's make sure that the max height and width is at least as big as our center child
        maxHeight = Math.max(maxHeight, maxCenterChildHeight);
        maxWidth = Math.max(maxWidth, maxCenterChildWidth);

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth , widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int childCount = getChildCount();

        int currentAngle = mFirstChildAngleOffset; //angle offset to place first child
        int angleAmongChildren = 0; //the angle between two children

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        //find the number of children that will be centered
        int numCenteredChildren = getNumCenteredChildren();
        int childCountExcludingGone = getNumVisibleChildren();

        if((childCountExcludingGone - numCenteredChildren) > 0){
            angleAmongChildren = 360 / (childCountExcludingGone - numCenteredChildren);
        }

        int viewCenterX = (r - l) / 2;
        int viewCenterY = (b - t) / 2;


        for(int i = 0; i < childCount; i++){ //iterate through each of the children and lay them out
            final View child = getChildAt(i);

            int centerX = 0;
            int centerY = 0;

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int cl, ct, cr, cb; //left, top, right, bottoms

            if(child.getVisibility() != View.GONE) {


                if (lp.mIsCentered) { //place the center child in the center
                    centerX = viewCenterX;
                    centerY = viewCenterY;

                }else{ //a child to be placed around the center
                    centerX = viewCenterX + (int)(mRadius * Math.cos(Math.toRadians(currentAngle)));
                    centerY = viewCenterY + (int)(mRadius * Math.sin(Math.toRadians(currentAngle)));

                    //update the angle
                    currentAngle += angleAmongChildren;
                }

                //now compute the actual child left, right, top..
                cl = (centerX - width / 2) + lp.leftMargin - lp.rightMargin;
                ct = (centerY - height / 2)  + lp.topMargin - lp.bottomMargin;
                cr = (centerX + width / 2) - lp.rightMargin + lp.leftMargin;
                cb = (centerY + height / 2) - lp.bottomMargin + lp.topMargin;

                //do the layout
                child.layout(cl, ct, cr, cb);
            }


        }

    }

    /**
     * Get the number of children not GONE
     * @return
     */
    private int getNumVisibleChildren() {
        int childCount = getChildCount();
        int num = 0;
        for(int i =0; i < childCount; i++){
            if(getChildAt(i).getVisibility() != View.GONE)
                ++num;
        }
        return num;
    }

    /**
     * Get the number of centered children excluding the ones that are gone
     * @return Number of centered children excluding GONE
     */
    protected int getNumCenteredChildren(){
        int c = getChildCount();
        int numCenteredChildren = 0;
        LayoutParams lp;
        View child;
        for(int i = 0; i < c; i++){
             child = getChildAt(i);
            if(child.getVisibility() != View.GONE) {
                lp = (LayoutParams) child.getLayoutParams();
                if (lp.mIsCentered)
                    ++numCenteredChildren;
            }
        }

        return numCenteredChildren;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }

    public int getFirstChildAngleOffset() {
        return mFirstChildAngleOffset;
    }

    public void setFirstChildAngleOffset(int mFirstChildAngleOffset) {
        this.mFirstChildAngleOffset = mFirstChildAngleOffset;
        invalidate();
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams{

        public static final int FIT_SHORTEST_WIDTH = -1;

        private boolean mIsCentered;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RadialLayout);
            mIsCentered = a.getBoolean(R.styleable.RadialLayout_isCentered, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public boolean isCentered() {
            return mIsCentered;
        }

        public void setCentered(boolean isCentered) {
            this.mIsCentered = isCentered;
        }
    }
}
