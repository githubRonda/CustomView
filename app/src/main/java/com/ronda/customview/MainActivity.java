package com.ronda.customview;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;

import com.ronda.customview.banner.BannerActivity;
import com.ronda.customview.dropedit.DropEditActivity;
import com.ronda.customview.listview.RefreshListViewActivity;
import com.ronda.customview.sliding.SlidingActivity;
import com.ronda.customview.switchbutton.SwitchButtonActivity;
import com.ronda.customview.youku.YoukuMenuActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_youku_menu).setOnClickListener(this);
        findViewById(R.id.btn_banner).setOnClickListener(this);
        findViewById(R.id.btn_drop_edit).setOnClickListener(this);
        findViewById(R.id.btn_switch_button).setOnClickListener(this);
        findViewById(R.id.btn_refresh_list_view).setOnClickListener(this);
        findViewById(R.id.btn_sliding).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_youku_menu:
                startActivity(new Intent(this, YoukuMenuActivity.class));
                break;
            case R.id.btn_banner:
                startActivity(new Intent(this, BannerActivity.class));
                break;
            case R.id.btn_drop_edit:
                startActivity(new Intent(this, DropEditActivity.class));
                break;
            case R.id.btn_switch_button:
                startActivity(new Intent(this, SwitchButtonActivity.class));
                break;
            case R.id.btn_refresh_list_view:
                startActivity(new Intent(this, RefreshListViewActivity.class));
                break;
            case R.id.btn_sliding:
                startActivity(new Intent(this, SlidingActivity.class));
                break;
        }
    }
}
