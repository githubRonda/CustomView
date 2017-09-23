package com.ronda.customview.sliding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.socks.library.KLog;

import java.util.Currency;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/22
 * Version: v1.0
 * <p>
 * 侧滑菜单
 */

public class SlideMenu extends ViewGroup {

    private static final int MAIN_STATE = 0;
    private static final int MENU_STATE = 1;
    private int curState = MAIN_STATE;
    private float lastX;
    private Scroller scroller;
    private float downY; // 按下的y坐标

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化滚动器, 其实就是一个滚动效果的数值模拟器
        scroller = new Scroller(getContext());

    }

    /**
     * 测量并设置 所有子View的宽高
     * widthMeasureSpec: 当前控件的宽度测量规则
     * heightMeasureSpec: 当前控件的高度测量规则
     * 不是布局里写什么就是什么，有时候发现在布局中写的东西会不生效。包括layout_weight, wrap_content, match_parent 最终都会计算出一个精确的值
     * 对于测量规则的理解：有时候，因为控件设置的宽高不一定是显示的效果，因为每个布局都是有自己的规则的，例如线性布局和相对布局规则就不一样。可能会被挤出去，或者被父控件的大小所限制
     * 若想真正按照设置的宽高来显示，则可以把设置的值传给measure()方法即可，因为measure()才是真正的决策者。传递进来的值为多少，测量和绘制的宽高就是这个值
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        KLog.d("widthMeasureSpec: " + widthMeasureSpec + ", heightMeasureSpec: " + heightMeasureSpec); //widthMeasureSpec: 1073742592, heightMeasureSpec: 1073742958 --> 这两个值不知道为什么会这么大

        View leftMenu = getChildAt(0);

        leftMenu.measure(leftMenu.getLayoutParams().width, heightMeasureSpec);//直接指定一个宽度 leftMenu.getLayoutParams().width = 480 或者 第一个参数为 MeasureSpec.UNSPECIFIED(按原有规则测量) 也是可以的

        // leftMenu 的根标签ScrollView和次根标签LinearLayout都必须都是设置为240dp时，这里的 leftMenu.getMeasuredWidth() 才等于 onMeasure中测量时传递的 leftMenu.getLayoutParams().width值（480）
        // 如果ScrollView 和 LinearLayout只有一个设置了240dp, 则leftMenu.getMeasuredWidth(): 827.  很奇怪：这个值竟然比屏幕还要宽
        KLog.d("leftMenu.getMeasuredWidth() --> " + leftMenu.getMeasuredWidth()); //leftMenu.getMeasuredWidth() --> 480


        View mainContent = getChildAt(1);
        mainContent.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * changed: 当前控件的尺寸大小, 位置 是否发生了变化
     * left:当前控件 左边界 距离屏幕左边的距离
     * top:当前控件 顶边距
     * right:当前控件 右边界 距离屏幕左边的距离
     * bottom:当前控件 下边界 距离屏幕上边的距离
     * 以上四个参数就可以确定控件的大小和位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //有了当前ViewGroup相对屏幕左上角的边界位置，接下来就是摆放子View的位置

        KLog.e(l + ", " + t + ", " + r + ", " + b); //0, 0, 768, 1134 --> 表示当前这个ViewGroup的边界 (屏幕尺寸：768*1280 包括NavigationBar)
        View leftMenu = getChildAt(0);

        leftMenu.layout(-leftMenu.getMeasuredWidth(), 0, 0, b);

        getChildAt(1).layout(l, t, r, b);
    }

    /**
     * scrollTo(int x, int y): 指窗口(滚动条)滚动到指定位置. 正数表示窗口向右向下移动，而内容向左向上移动。 和浏览器的滚动条效果是一样，但是一定要注意：若窗口向下滚动，滚动条也向下滚动，而手机屏幕上手指确是向上滑动，刚好是相反的。
     * scrollBy(int x, int y): 指窗口(滚动条)滚动相对当前位置偏移
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();

                // 将要发生的偏移量/变化量
                int offsetX = (int) (lastX - moveX); // 注意相减的顺序不要弄反了. 向左滑：窗口向右移动,滚动条向右移动（正数）; 向右滑：窗口向左移动,滚动条向左移动(负数)。 滚动条的移动刚好和滑动方向是相反的

                int newScrollX = getScrollX() + offsetX; // getScrollX() 当前滚动到的位置,可正可负

                //左右滑动边界限制
                if (newScrollX < -getChildAt(0).getMeasuredWidth()) { // 若超出左边界，则直接定到左边界的位置
                    scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
                } else if (newScrollX > 0) { //若超出右边界，则直接定到右边界的位置
                    scrollTo(0, 0);
                } else {
                    scrollBy(offsetX, 0);
                }

                lastX = moveX;//要更新lastX

                break;
            case MotionEvent.ACTION_UP:
                // 根据当前滚动到的位置, 和左面板的一半进行比较, 是否展开或关闭菜单
                int leftCenterX = -(int) (getChildAt(0).getMeasuredWidth() / 2.0);
                if (getScrollX() < leftCenterX) {
                    // 打开, 切换成菜单面板
                    curState = MENU_STATE;
                    updateContent();
                } else {
                    // 关闭, 切换成主面板
                    curState = MAIN_STATE;
                    updateContent();
                }
                break;
        }
        return true;//消费事件。 一般对于ViewGroup和View，则可以直接返回true/false, 对于其他控件(eg:ListView) 则必须要返回super.onTouchEvent()[因为它有自己的特定实现]
    }

    /**
     * 当触摸抬起的时候，根据当前的状态, 关闭/开启 菜单 (带有缓冲运动效果)
     */
    private void updateContent() {
        int startX = getScrollX();
        int dx = 0;
        if (curState == MENU_STATE) {// 打开菜单
            dx = -getChildAt(0).getMeasuredWidth() - startX; //目标点位置坐标 - 当前位置坐标
            //scrollTo(-getChildAt(0).getMeasuredWidth(), 0); // 这种方式一步到位的用户体验不好
        } else {// 恢复主界面
            dx = 0 - startX;
            //scrollTo(0, 0);
        }

        int duration = Math.abs(dx * 2);// duration 应该与dx是正相关的关系。距离越大， duration 应该越长
        /**
         * startX: 开始的x值
         * startY: 开始的y值
         * dx: 将要发生的水平变化量. 移动的x距离 --> 目标点位置坐标 - 当前位置坐标
         * dy: 将要发生的竖直变化量. 移动的y距离
         * duration : 数据模拟持续的时长
         */
        scroller.startScroll(startX, 0, dx, 0, duration);

        invalidate();// 重绘界面. View.invalidate() 会导致 ViewGroup.drawChild()被调用,内部调用的就是View.draw() ,而View.draw()内部又会调用 computeScroll();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (scroller.computeScrollOffset()) {// 若duration没有结束，则返回true

            int currX = scroller.getCurrX();
            KLog.d("currX: " + currX);
            scrollTo(currX, 0);
            invalidate();//scrollTo自带invalidate(), 所以这里可以不用写也可以。但是在之前比较老的版本中必须要加上
        }
    }

    /**
     * 当在ScrollView中左右滑动时，必须要拦截事件，否则事件会被ScrollView消费，造成左右滑动无效
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX(); // 必须要使用 onTouchEvent 中的lastX, 因为 MotionEvent事件是同一个
                downY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:

                int offsetX = (int) Math.abs(ev.getX() - lastX);
                int offsetY = (int) Math.abs(ev.getY() - downY);

                if(offsetX > offsetY && offsetX > 5){ // 水平方向超出一定距离时,才拦截
                    return true; // 直接在ViewGroup中拦截此次触摸事件, 避免事件传递给ScrollView，使横向触摸ScrollView时也可以展开/收缩菜单
                }


                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void open() {
        curState = MENU_STATE;
        updateContent();
    }

    public void close() {
        curState = MAIN_STATE;
        updateContent();
    }

    public int getCurState() {
        return curState;
    }

    public void toggleState() {

        if (curState == MENU_STATE) {
            close();
        } else {
            open();
        }
    }
}
