package com.github.sasakicks.draggableflipview;

import android.view.MotionEvent;
import java.util.HashMap;

/**
 * a class detecting drag of user
 *
 * Created by sasakicks on 2015/09/09.
 */
public class DragGestureDetector {

    public float deltaX;
    public float deltaY;
    public float prevDeltaX;
    public float prevDeltaY;
    public int originalIndex;
    public float velocityX;
    public float velocityY;

    private HashMap<Integer, TouchPoint> pointMap = new HashMap<>();

    private DragGestureListener dragGestureListener;

    public DragGestureDetector(DragGestureListener dragGestureListener) {
        this.dragGestureListener = dragGestureListener;
        pointMap.put(0, createPoint(0.f, 0.f));
    }

    public void setPointMap(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        TouchPoint downPoint = pointMap.get(0);
        if (downPoint != null) {
            downPoint.setXY(eventX, eventY);
            return;
        }
        downPoint = createPoint(eventX, eventY);
        pointMap.put(0, downPoint);
    }

    public TouchPoint getTouchPoint() {
        return pointMap.get(originalIndex);
    }

    synchronized public boolean onTouchEvent(MotionEvent event) {

        float eventX = event.getX(originalIndex);
        float eventY = event.getY(originalIndex);

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                TouchPoint originalPoint = pointMap.get(originalIndex);
                if (originalPoint != null) {
                    deltaX = eventX - originalPoint.x;
                    deltaY = eventY - originalPoint.y;

                    if (dragGestureListener != null) {
                        dragGestureListener.onDragGestureListener(this, action);
                    }

                    velocityX = deltaX - prevDeltaX;
                    velocityY = deltaY - prevDeltaY;
                    prevDeltaX = deltaX;
                    prevDeltaY = deltaY;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                TouchPoint originalPoint = pointMap.get(originalIndex);
                if (originalPoint != null && dragGestureListener != null) {
                    dragGestureListener.onDragGestureListener(this, action);
                }
                velocityX = velocityY = 0;
                prevDeltaX = prevDeltaY = 0;
                deltaX = deltaY = 0;
                break;
            }
            default:
        }
        return false;
    }

    private TouchPoint createPoint(float x, float y) {
        return new TouchPoint(x, y);
    }

    public interface DragGestureListener {
        void onDragGestureListener(DragGestureDetector dragGestureDetector, int action);
    }

    public class TouchPoint {

        private float x;
        private float y;

        public TouchPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public TouchPoint setXY(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }
    }
}
