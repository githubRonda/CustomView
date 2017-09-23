package com.ronda.customview.youku;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/09
 * Version: v1.0
 */

public class AnimationUtil {

    public static int runningAnimationCount = 0;

    /**
     * 旋转出去的动画
     *
     * @param viewGroup
     * @param delay
     */
    public static void rotateOutAnim(ViewGroup viewGroup, long delay) {

        //当动画旋转出去时，禁用子View的点击事件 [补间动画的不足]
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            viewGroup.getChildAt(i).setEnabled(false);
        }

        Animation animation = new RotateAnimation(
                0, -180f, // 旋转角度0,-180. 逆时针旋转
                Animation.RELATIVE_TO_SELF, 0.5f,//旋转中心：横坐标，相对于自身0.5的长度
                Animation.RELATIVE_TO_SELF, 1f);//旋转中心：纵坐标，相对于自身1.0的长度

        animation.setDuration(500);
        animation.setFillAfter(true);// 设置动画停留在结束位置
        animation.setStartOffset(delay);// 设置动画开始延时
        animation.setAnimationListener(new MyAnimationListener());
        viewGroup.startAnimation(animation);
    }

    /**
     * 旋转进来的动画
     *
     * @param viewGroup
     * @param delay
     */
    public static void rotateInAnim(ViewGroup viewGroup, long delay) {
        //当动画旋转进来时，启用子View的点击事件 
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            viewGroup.getChildAt(i).setEnabled(true);
        }

        Animation animation = new RotateAnimation(
                -180f, 0,
                Animation.RELATIVE_TO_SELF, 0.5f,//旋转中心：横坐标，相对于自身0.5的长度
                Animation.RELATIVE_TO_SELF, 1f);//旋转中心：纵坐标，相对于自身1.0的长度

        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setStartOffset(delay);
        animation.setAnimationListener(new MyAnimationListener());
        viewGroup.startAnimation(animation);
    }

    /**
     * 动画的监听器，共享 count 变量
     * 当 count 不为0时， 就表示还有动画正在执行
     */
    static class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            runningAnimationCount++;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            runningAnimationCount--;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    ;
}
