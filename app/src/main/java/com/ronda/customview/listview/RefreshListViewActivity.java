package com.ronda.customview.listview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ronda.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/14
 * Version: v1.0
 */

public class RefreshListViewActivity extends Activity {
    private RefreshListView refreshListView;
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 对于AppCompatActivity来说，这样设置取消标题栏是没有效果的
        setContentView(R.layout.activity_refresh_list_view);

        refreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);


        for (int i = 0; i < 30; i++) {
            mData.add("这是一条ListView数据: " + i);
        }

        mAdapter = new MyAdapter();
        refreshListView.setAdapter(mAdapter);

        refreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mData.add(0,"我是下拉刷新出来的数据");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                refreshListView.onRefreshComplete();
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onLoadMore() {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mData.add("我是加载更多出来的数据1");
                        mData.add("我是加载更多出来的数据2");
                        mData.add("我是加载更多出来的数据3");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                refreshListView.onRefreshComplete();
                            }
                        });
                    }
                }.start();
            }
        });
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setText(getItem(position));
            textView.setTextSize(22f);
            return textView;
        }
    }
}
