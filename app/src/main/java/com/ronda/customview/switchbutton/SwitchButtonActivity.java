package com.ronda.customview.switchbutton;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ronda.customview.R;
import com.ronda.customview.switchbutton.view.SwitchButton;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/12
 * Version: v1.0
 */

public class SwitchButtonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_button);

        SwitchButton switchButton = (SwitchButton) findViewById(R.id.switch_button);

//        switchButton.setSwitchBackground(R.drawable.switch_background);
//        switchButton.setSlideResource(R.drawable.slide_button);
//        switchButton.setSwitchState(false);
        switchButton.setOnStateChangedListener(new SwitchButton.OnStateChangedListener() {
            @Override
            public void onChanged(boolean state) {

                Toast.makeText(SwitchButtonActivity.this, "state: " + state, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
