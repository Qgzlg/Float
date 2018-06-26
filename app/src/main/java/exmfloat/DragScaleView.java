package exmfloat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.administrator.myfloat.R;

import Application.Data;

public class DragScaleView extends View implements View.OnTouchListener {
    protected int screenWidth;
    protected int screenHeight;
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

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenW_H() {
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public DragScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = getDirection(v, (int) event.getX(),
                    (int) event.getY());
        }
        // 处理拖动事件
        delDrag(app.getMfloatView(), event, action);
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
                        center(v, dx, dy);
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
                    WindowManager.LayoutParams mLayoutParams = app.getMlayoutParams();
                    WindowManager mWindowManager = app.getMwindowManager();
                    updateFloatView (mLayoutParams.x, mLayoutParams.y, oriRight - oriLeft, oriBottom - oriTop);
//                    //v.layout(1, 1, oriRight - oriLeft - 1, oriBottom - oriTop - 1);
//                    DragFieldView dragFieldView = (DragFieldView) app.getMfloatView().findViewById(R.id.ds1);
//                    dragFieldView.layout(oriLeft + (oriRight - oriLeft) / 3 , oriTop + (oriBottom - oriTop) / 3,
//                            oriLeft + 2 * (oriRight - oriLeft) / 3 , oriTop + 2 * (oriBottom - oriTop) / 3);
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
        if(0 == oriLeft && 0 == oriTop) {
            oriLeft = app.getMfloatView().getLeft();
            oriTop = app.getMfloatView().getTop();
            oriBottom = oriTop + app.getMfloatView().getHeight();
            oriRight = oriLeft + app.getMfloatView().getWidth();
        }
        oriLeft += dx;
        oriTop += dy;
        oriRight += dx;
        oriBottom += dy;
        if (oriLeft < 0) {
            oriLeft = 0;
            oriRight = v.getWidth();
        }
        if (oriRight > screenWidth) {
            oriRight = screenWidth;
            oriLeft = oriRight - v.getWidth();
        }
        if (oriTop < 0) {
            oriTop = 0;
            oriBottom = v.getHeight();
        }
        if (oriBottom > screenHeight ) {
            oriBottom = screenHeight;
            oriTop = oriBottom - v.getHeight();
        }
        updateFloatView (oriLeft, oriTop, oriRight - oriLeft, oriBottom - oriTop);
        //v.layout(1, 1, oriRight - oriLeft - 1, oriBottom - oriTop - 1);
        //DragFieldView dragFieldView = (DragFieldView) app.getMfloatView().findViewById(R.id.ds1);
        //dragFieldView.layout((oriRight - oriLeft) / 2 -30, (oriBottom - oriTop) / 2 -30,
         //       (oriRight - oriLeft) / 2 + 30 , (oriBottom - oriTop) / 2 + 30);
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
        if (oriBottom - oriTop <= 200) {
            oriTop = oriBottom - 200;
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
        if (oriBottom > screenHeight) {
            oriBottom = screenHeight;
        }
        if (oriBottom - oriTop <= 200) {
            oriBottom = 200 + oriTop;
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
        if (oriRight > screenWidth) {
            oriRight = screenWidth;
        }
        if (oriRight - oriLeft <= 200) {
            oriRight = oriLeft + 200;
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
        if (oriRight - oriLeft  <= 200) {
            oriLeft = oriRight - 200;
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
        if(0 == oriLeft && 0 == oriTop) {
            oriLeft = app.getMfloatView().getLeft();
            oriTop = app.getMfloatView().getTop();
            oriBottom = oriTop + app.getMfloatView().getHeight();
            oriRight = oriLeft + app.getMfloatView().getWidth();
        }
        int xlength = app.getMfloatView().getWidth() / 3;
        int ylength = app.getMfloatView().getHeight() / 3;
        if (x < xlength && y < ylength) {
            return CENTER;
        }
        if (x > 2 * xlength && y < ylength) {
            return CENTER;
        }
        if (x < xlength  && y > 2 * ylength) {
            return CENTER;
        }
        if (x > 2 * xlength && y > 2 * ylength) {
            return RIGHT_BOTTOM;
        }
        if (x < xlength) {
            return CENTER;
        }
        if (y < ylength) {
            return CENTER;
        }
        if (x > 2 * xlength) {
            return RIGHT;
        }
        if (y > 2 * ylength) {
            return BOTTOM;
        }
        return CENTER;
    }

    private void updateFloatView(int x, int y, int width, int height) {
        WindowManager.LayoutParams mLayoutParams = app.getMlayoutParams();
        WindowManager mWindowManager = app.getMwindowManager();
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        mLayoutParams.width = width;
        mLayoutParams.height = height;
        mWindowManager.updateViewLayout(app.getMfloatView(), mLayoutParams);
    }
}
