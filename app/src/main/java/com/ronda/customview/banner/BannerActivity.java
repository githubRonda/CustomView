package com.ronda.customview.banner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ronda.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/10
 * Version: v1.0
 */

public class BannerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private TextView mTvDesc;
    private LinearLayout mLlPointContainer;

    private int[] mImgResIds;
    private String[] mDescs;
    private List<ImageView> mImageViewList;
    private int lastSelectedPoint = 0;
    private boolean isRunning = true; //是否进行自动播放

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        initViews();

        initData();

        initAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mViewPager.removeOnPageChangeListener(this);
    }

    private void initViews() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTvDesc = (TextView) findViewById(R.id.tv_desc);
        mLlPointContainer = (LinearLayout) findViewById(R.id.ll_point_container);

        mViewPager.addOnPageChangeListener(this); //setOnPageChangeListener方法 已过时 。 先添加监听器后设置setAdapter,则可以在onPageSelected中初始化第一个页面
    }

    private void initData() {

        mImgResIds = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
        // 文本描述
        mDescs = new String[]{
                "巩俐不低俗，我就不能低俗",
                "扑树又回来啦！再唱经典老歌引万人大合唱",
                "揭秘北京电影如何升级",
                "乐视网TV版大派送",
                "热血屌丝的反杀"
        };

        // 初始化要展示的5个ImageView
        mImageViewList = new ArrayList<ImageView>();

        ImageView imageView; // 把 imageView 放到for循环外，遮阳栈内存就只需要创建一个引用，节省资源
        View pointView;
        LinearLayout.LayoutParams params;
        for (int i = 0; i < mImgResIds.length; i++) {
            // 初始化要显示的图片对象
            imageView = new ImageView(this);
            imageView.setBackgroundResource(mImgResIds[i]);
            mImageViewList.add(imageView);

            // 加小白点, 指示器
            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.selector_bg_point);
            params = new LinearLayout.LayoutParams(10, 10);
            if (i != 0) {
                params.leftMargin = 20;
                pointView.setEnabled(false);
            } else {//第一项为可用状态
                pointView.setEnabled(true);
            }

            mLlPointContainer.addView(pointView, params);
        }

    }

    private void initAdapter() {

        //初始化第一个页面相关内容（若是先添加监听器，后setAdapter的话，则下面两行不用写）
        mLlPointContainer.getChildAt(0).setEnabled(true);
        mTvDesc.setText(mDescs[0]);

        mViewPager.setAdapter(new MyAdapter());

        // 选择 Integer.MAX_VALUE 中间某个模上 mImageViewList.size() 为0 的位置的值。 这样就实现了左右都可以无限滑动的效果了(伪实现)
        //int pos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mImageViewList.size());
        //mViewPager.setCurrentItem(pos);//对于低版本的环境，会有一个小bug（点击Item也会滑动）  Integer.MAX_VALUE 造成的
        mViewPager.setCurrentItem(5000000); //选择一个稍微小点的值，比如5000000就不会有上面这个bug了

        // 实现自动播放
        new Thread() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning) {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);//滑动到下一个。要在主线程中执行
                        }
                    });
                }
            }
        }.start();
    }

    //=================OnPageChangeListener=======================
    // 滚动时调用
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    // 新的条目被选中时调用
    @Override
    public void onPageSelected(int position) {

        int pos = position % mImageViewList.size(); // 取余

        mTvDesc.setText(mDescs[pos]);
        mLlPointContainer.getChildAt(lastSelectedPoint).setEnabled(false);
        mLlPointContainer.getChildAt(pos).setEnabled(true);

        //记录位置
        lastSelectedPoint = pos;
    }

    // 滚动状态变化时调用
    @Override
    public void onPageScrollStateChanged(int state) {
        /*
        ViewPager#SCROLL_STATE_IDLE
        ViewPager#SCROLL_STATE_DRAGGING
        ViewPager#SCROLL_STATE_SETTLING
        */
        //当滑动停止时把 isRunning 置为 true，可以减少在手动滑动过程中，自动滑动对其造成影响
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            isRunning = true;
        } else {
            isRunning = false;
        }

    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //return mImageViewList.size();
            return Integer.MAX_VALUE; //无限循环。本质上就是伪无限循环 //1s中滑动一次，则可以滑69年（2147483647）
        }

        // 是否复用View的判断逻辑
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object; // 固定写法
        }

        /**
         * 实例化一个Item。该方法必须要复写，因为父类默认是抛出一个异常。
         * 该方法内部有两个逻辑：
         * 1. 把 itemView 添加至 container
         * 2. 返回 itemView 至适配器
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d("TAG", "instantiateItem position: " + position);

            ImageView imageView = mImageViewList.get(position % mImageViewList.size()); //求模，取余数。因为getCount返回的是Integer的最大值
            container.addView(imageView);
            return imageView;
        }

        // 销毁Item。该方法必须要复写，因为父类默认是抛出一个异常
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d("TAG", "destroyItem position: " + position);

            container.removeView((View) object);
        }
    }
}
