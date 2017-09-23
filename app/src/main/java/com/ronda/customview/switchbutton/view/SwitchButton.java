package com.ronda.customview.switchbutton.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/12
 * Version: v1.0
 */

/**
 * 自定义开关
 * <p>
 * <p>
 * <p>
 * Android 的界面绘制流程
 * 测量			 摆放		绘制
 * measure	->	layout	->	draw
 * | 		  |			 |
 * onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 * <p>
 * onResume()之后执行
 * <p>
 * View
 * onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 * <p>
 * ViewGroup
 * onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */

/**
 * 1. onDraw() 中通过 switchSate 值, 绘制开关状态
 * 2. 通过 onTouchEvent 改变 switchSate 的值 以及绘制滑块移动效果
 * 3. ACTION_UP 时，适时调用回调接口方法
 */
public class SwitchButton extends View {

    private Bitmap switchBackgroupBitmap;// 背景图片
    private Bitmap slideButtonBitmap;// 滑块图片

    private boolean switchSate = false; // 开关状态, 默认false
    private Paint paint;
    private float currentX;
    private boolean isTouchMode; //是否是触摸状态



    /**
     * java直接 new 控件时, 走此方法
     */
    public SwitchButton(Context context) {
        super(context);
        init();
    }

    /**
     * 解析xml中声明的控件时, 走此方法。 可指定自定义属性， attrs 就是指控件的各个属性集
     */
    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        String namespace = "http://schemas.android.com/apk/res-auto";
        //获取自定义的属性
        int switch_background = attrs.getAttributeResourceValue(namespace, "switch_background", 0);
        int slide_src = attrs.getAttributeResourceValue(namespace, "slide_src", 0);
        boolean switch_state = attrs.getAttributeBooleanValue(namespace, "switch_state", false);

        setSwitchBackground(switch_background);
        setSlideResource(slide_src);
        setSwitchState(switch_state);
    }

    /**
     * 解析xml控件时 且 xml属性中有style属性时，走此方法
     */
    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(switchBackgroupBitmap.getWidth(), switchBackgroupBitmap.getHeight());
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
        canvas.drawBitmap(switchBackgroupBitmap, 0, 0, paint);
        //绘制滑块

        if (isTouchMode) {
            // 精确的来说，newLeft 应该是当前鼠标相对于switchBackgroupBitmap的横坐标 - 鼠标相对于slideButtonBitmap的横坐标
            // 但是由于 鼠标相对于slideButtonBitmap的横坐标 不方便计算出来，所以就近似用 slideButtonBitmap 宽度的一半表示
            float newLeft = currentX - slideButtonBitmap.getWidth() / 2.0f;


            // 限定滑块范围
            if (newLeft < 0) {
                newLeft = 0;
            }
            float maxLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
            if (newLeft > maxLeft) {
                newLeft = maxLeft;
            }

            canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
        } else {
            // 根据开关状态boolean, 直接设置图片位置
            if (switchSate) { // 开
                int left = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
                canvas.drawBitmap(slideButtonBitmap, left, 0, paint);
            } else { //关
                canvas.drawBitmap(slideButtonBitmap, 0, 0, paint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchMode = true;
                currentX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                isTouchMode = false;
                currentX = event.getX();

                //判断滑块是否滑过一半，决定开关状态
                float center = switchBackgroupBitmap.getWidth() / 2.0f;
                boolean newState = currentX > center;
                if (newState != switchSate && onStateChangedListener!=null){ //说明状态已改变,则需要调用监听器
                    onStateChangedListener.onChanged(newState);
                }

                switchSate = currentX > center; //重绘时会更新这个状态
                break;
        }
        // 重绘界面
        invalidate(); //会导致 onDraw() 方法被调用

        return true;//消费事件
    }

    /**
     * 设置背景图
     */
    public void setSwitchBackground(int switchBgRes) {
        this.switchBackgroupBitmap = BitmapFactory.decodeResource(getResources(), switchBgRes);
    }

    /**
     * 设置滑块图片资源
     */
    public void setSlideResource(int slideButtonRes) {
        this.slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButtonRes);
    }

    /**
     * 设置开关状态
     */
    public void setSwitchState(boolean state) {
        this.switchSate = state;
    }


    //接口监听状态改变
    private OnStateChangedListener onStateChangedListener;

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public interface OnStateChangedListener {
        void onChanged(boolean state);
    }
}
