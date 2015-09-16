package com.github.sasakicks.draggableflipview;

import android.content.Context;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * a class performing animation of view depending on the movement of user
 *
 * Created by sasakicks on 2015/09/09.
 */
public class DraggableFlipView extends FrameLayout implements DragGestureDetector.DragGestureListener {

    private static final float DRAG_THRESHOLD_PARAM = 50.0f;
    private static final int DEFAULT_VALUE = 0;
    private static final int DEFAULT_DRAGGABLE_VALUE = 50;
    private static final int DEFAULT_DRAG_DETECT_VALUE = 3;

    private DragGestureDetector mDragGestureDetector;
    private boolean isAnimation;
    private int mAngle;
    private int mDraggableAngle;
    private int mDragDetectAngle;
    private boolean mIsReverse;
    private FlipListener mFlipListener;

    private RelativeLayout mFrontLayout;
    private RelativeLayout mBackLayout;

    private enum RotateDirection {
        RIGHT(1), LEFT(-1);

        private int mValue;

        RotateDirection(int value) {
            this.mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    public DraggableFlipView(Context context) {
        this(context, null);
    }

    public DraggableFlipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableFlipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mFrontLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mFrontLayout.setLayoutParams(params1);

        mBackLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mBackLayout.setLayoutParams(params2);

        this.addView(mFrontLayout);
        this.addView(mBackLayout);
        mBackLayout.setVisibility(View.INVISIBLE);

        mFlipListener = new FlipListener(mFrontLayout, mBackLayout, this);
        mDragGestureDetector = new DragGestureDetector(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DraggableFlipView);
        LayoutInflater.from(context).inflate(a.getResourceId(R.styleable.DraggableFlipView_frontView, DEFAULT_VALUE), mFrontLayout);
        LayoutInflater.from(context).inflate(a.getResourceId(R.styleable.DraggableFlipView_backView, DEFAULT_VALUE), mBackLayout);

        mDraggableAngle = a.getInteger(R.styleable.DraggableFlipView_draggableAngle, DEFAULT_DRAGGABLE_VALUE);
        mDragDetectAngle = a.getInteger(R.styleable.DraggableFlipView_dragDetectAngle, DEFAULT_DRAG_DETECT_VALUE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragGestureDetector == null) return false;
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - mDragGestureDetector.getTouchPoint().getX())
                        > DRAG_THRESHOLD_PARAM
                        || Math.abs(ev.getY() - mDragGestureDetector.getTouchPoint().getY())
                        > DRAG_THRESHOLD_PARAM) {
                    mDragGestureDetector.setPointMap(ev);
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragGestureDetector != null) {
            mDragGestureDetector.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public void onDragGestureListener(DragGestureDetector dragGestureDetector, int action) {
        if (isAnimation) return;
        if (action == MotionEvent.ACTION_UP) {
            if (mAngle >= mDragDetectAngle) {
                setAutoRotateAnimation(RotateDirection.RIGHT);
            } else if (mAngle <= -mDragDetectAngle) {
                setAutoRotateAnimation(RotateDirection.LEFT);
            }
            return;
        }

        this.setRotationY((dragGestureDetector.deltaX - dragGestureDetector.prevDeltaX) > 0 ? ++mAngle : --mAngle);

        if (mAngle >= mDraggableAngle) {
            setAutoRotateAnimation(RotateDirection.RIGHT);
        } else if (mAngle < -mDraggableAngle) {
            setAutoRotateAnimation(RotateDirection.LEFT);
        }
    }

    private void setAutoRotateAnimation(RotateDirection rotateDirection) {
        isAnimation = true;
        if (mIsReverse) {
            mFlipListener.reverse();
        } else {
            mIsReverse = true;
        }

        mFlipListener.setRotateDirection(rotateDirection.getValue());
        ValueAnimator mFlipAnimator = ValueAnimator.ofFloat(0f, 1f);
        mFlipAnimator.addUpdateListener(mFlipListener);
        mFlipAnimator.start();
        mFlipAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAngle = 0;
                isAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}

