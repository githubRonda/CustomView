package com.ronda.customview.youku;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ronda.customview.R;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/08
 * Version: v1.0
 * <p>
 * 优酷菜单
 */

public class YoukuMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_level1;
    private RelativeLayout rl_level2;
    private RelativeLayout rl_level3;

    private boolean isLevel1Display = true;
    private boolean isLevel2Display = true;
    private boolean isLevel3Display = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youku_menu);

        initView();
    }

    private void initView() {

        rl_level1 = (RelativeLayout) findViewById(R.id.rl_level1);
        rl_level2 = (RelativeLayout) findViewById(R.id.rl_level2);
        rl_level3 = (RelativeLayout) findViewById(R.id.rl_level3);

        findViewById(R.id.ib_home).setOnClickListener(this);
        findViewById(R.id.ib_menu).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {

        //当还有动画正在执行时，不再响应任何点击事件
        if (AnimationUtil.runningAnimationCount > 0) {
            return;
        }

        switch (v.getId()) {
            case R.id.ib_home:
                if (isLevel2Display) { //隐藏动画
                    int delay = 0;
                    if (isLevel3Display) {//level3显示时
                        AnimationUtil.rotateOutAnim(rl_level3, 0);
                        delay += 200;
                        isLevel3Display = false;
                    }
                    AnimationUtil.rotateOutAnim(rl_level2, delay);
                    isLevel2Display = false;
                } else { //显示动画
                    AnimationUtil.rotateInAnim(rl_level2, 0);
                    isLevel2Display = true;
                }

                break;
            case R.id.ib_menu:
                if (isLevel3Display) {
                    AnimationUtil.rotateOutAnim(rl_level3, 0);
                } else {
                    AnimationUtil.rotateInAnim(rl_level3, 0);
                }
                isLevel3Display = !isLevel3Display;
                break;
        }
    }

    // 菜单按钮事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // keyCode 事件码
        System.out.println("onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            if (AnimationUtil.runningAnimationCount > 0) {
                // 当前有动画正在执行, 取消当前事件
                return true;
            }

            //如果按下的是菜单按钮
            if (isLevel1Display) {
                long delay = 0;
                // 隐藏三级菜单
                if (isLevel3Display) {
                    AnimationUtil.rotateOutAnim(rl_level3, 0);
                    isLevel3Display = false;
                    delay += 200;
                }

                // 隐藏二级菜单
                if (isLevel2Display) {
                    AnimationUtil.rotateOutAnim(rl_level2, delay);
                    isLevel2Display = false;
                    delay += 200;
                }

                // 隐藏一级菜单
                AnimationUtil.rotateOutAnim(rl_level1, delay);

            } else {
                // 顺次转进来
                AnimationUtil.rotateInAnim(rl_level1, 0);
                AnimationUtil.rotateInAnim(rl_level2, 200);
                AnimationUtil.rotateInAnim(rl_level3, 400);

                isLevel3Display = true;
                isLevel2Display = true;
            }
            isLevel1Display = !isLevel1Display;

            return true;// 消费了当前事件
        }

        return super.onKeyDown(keyCode, event);
    }
}
