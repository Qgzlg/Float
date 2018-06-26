package exmfloat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import Application.Data;

public class DragFieldView extends View implements View.OnTouchListener {
    protected int viewWidth;
    protected int viewHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    protected Paint paint = new Paint();
    private Data app = (Data)Data.getMyApplication();
    private int centerX = 0;
    private int centerY = 0;

    /**
     * 初始化获取屏幕宽高
     */
    protected void initViewW_H() {
        View mfloatView = app.getMfloatView();
        viewWidth = mfloatView.getWidth();
        viewHeight = mfloatView.getHeight();
    }

    public DragFieldView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    public DragFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public DragFieldView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);
        initViewW_H();
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        //canvas.drawRect(viewWidth / 2 -60, viewHeight / 2 - 60, viewWidth / 2 + 60, viewHeight / 2 + 60, paint);
        canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, paint);
        canvas.drawLine(getWidth()/2, 0, getWidth() / 2, getHeight(), paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        initViewW_H();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            oriLeft = v.getLeft();
            oriRight = v.getRight();
            oriTop = v.getTop();
            oriBottom = v.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            centerX = app.getMfloatView().getWidth() / 2;
            centerY = app.getMfloatView().getHeight() / 2;
            dragDirection = getDirection(v, (int) event.getX(),
                    (int) event.getY());
        }
        // 处理拖动事件
        delDrag(v, event, action);
        invalidate();
        return false;
    }

    /**
     * 处理拖动事件
     *
     * @param v
     * @param event
     * @param action
     */
    protected void delDrag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        //center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                }
                if (dragDirection != CENTER) {
                    v.layout(oriLeft, oriTop, oriRight, oriBottom);
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dragDirection = 0;
                break;
        }
    }

    /**
     * 触摸点为中心->>移动
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < 0) {
            left = 0;
            right = left + v.getWidth();
        }
        if (right > viewWidth ) {
            right = viewWidth ;
            left = right - v.getWidth();
        }
        if (top < 0) {
            top = 0;
            bottom = top + v.getHeight();
        }
        if (bottom > viewHeight ) {
            bottom = viewHeight;
            top = bottom - v.getHeight();
        }
        v.layout(left, top, right, bottom);
    }

    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < 0) {
            oriTop = 0;
        }
        else {
            oriBottom -= dy;
        }

        if (oriBottom - oriTop <= 200) {
            oriTop = centerY - 100;
            oriBottom = centerY + 100;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > viewHeight) {
            oriBottom = viewHeight;
        }
        else {
            oriTop -= dy;
        }
        if (oriBottom - oriTop <= 200) {
            oriTop = centerY - 100;
            oriBottom = centerY + 100;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > viewWidth) {
            oriRight = viewWidth;
        }
        else {
            oriLeft -= dx;
        }

        if (oriRight - oriLeft <= 200) {
            oriLeft = centerX - 100;
            oriRight = centerX + 100;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < 0) {
            oriLeft = 0;
        }
        else {
            oriRight -=dx;
        }
        if (oriRight - oriLeft <= 200) {
            oriLeft = centerX - 100;
            oriRight = centerX + 100;
        }
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        int w = v.getWidth();
        int h = v.getHeight();
        if (x < w / 3 && y < h / 3) {
            return LEFT_TOP;
        }
        if (x > w * 2 / 3 && y < h / 3) {
            return RIGHT_TOP;
        }
        if (x < w / 3 && y > h * 2 / 3) {
            return LEFT_BOTTOM;
        }
        if (x > w * 2 / 3 && y > h * 2 / 3) {
            return RIGHT_BOTTOM;
        }
        if (x < w / 3) {
            return LEFT;
        }
        if (y < h / 3) {
            return TOP;
        }
        if (x > w * 2 / 3) {
            return RIGHT;
        }
        if (y > h * 2 / 3) {
            return BOTTOM;
        }
        return CENTER;
    }

    /**
     * 获取截取宽度
     *
     * @return
     */
    public int getViewWidth() {
        View mfloatView = app.getMfloatView();
        return mfloatView.getWidth();
    }

    /**
     * 获取截取高度
     *
     * @return
     */
    public int getViewHeight() {
        View mfloatView = app.getMfloatView();
        return mfloatView.getHeight();
    }

}

