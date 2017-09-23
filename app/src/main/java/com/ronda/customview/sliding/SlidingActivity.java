package com.ronda.customview.sliding;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.ronda.customview.R;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/20
 * Version: v1.0
 */

public class SlidingActivity extends Activity {

    private SlideMenu slideMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sliding);

        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);

        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenu.toggleState();
            }
        });

    }

    public void onTabClick(View view) {

    }
}
