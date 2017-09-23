package com.ronda.customview.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ronda.customview.R;
import com.socks.library.KLog;

import java.text.SimpleDateFormat;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/09/14
 * Version: v1.0
 * <p>
 * 包含下拉刷新 和 上拉加载更多 功能的ListView
 */

public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static final int PULL_REFRESH = 0; //下拉刷新状态 默认状态
    private static final int RELEASE_REFRESH = 1; //释放刷新
    private static final int REFRESHING = 2; //正在刷新

    private int curState = PULL_REFRESH;

    private View mHeaderView;
    private int mHeaderViewHeight;//头布局测量的高度
    private float downY; //按下时的纵坐标
    private RotateAnimation rotateUpAnim; //向上旋转的动画
    private RotateAnimation rotateDownAnim;//向下旋转的动画
    private ImageView mIvArrow;
    private TextView mTvLastRefreshTime;
    private TextView mTvTitle;
    private View mFooterView;
    private int mFooterViewHeight;
    private boolean isLoadingMore = false; // 是否正在加载更多

    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化头布局，尾布局，滚动监听
     */
    private void init() {
        initHeaderView();
        initFooterView();
        initAnim();

        setOnScrollListener(this);
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        // 向上转, 围绕着自己的中心, 逆时针旋转0 -> -180.
        rotateUpAnim = new RotateAnimation(0, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnim.setDuration(300);
        rotateUpAnim.setFillAfter(true);//动画停留在最后一帧

        // 向下转, 围绕着自己的中心, 逆时针旋转 -180 -> -360
        rotateDownAnim = new RotateAnimation(-180f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateDownAnim.setDuration(300);
        rotateDownAnim.setFillAfter(true);//动画停留在最后一帧
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {

        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.item_header_layout, null);
        mIvArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        mTvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        mTvLastRefreshTime = (TextView) mHeaderView.findViewById(R.id.tv_last_refresh_time);


        //提前手动测量宽高,按照原有规则测量（即xml或者java中设置的宽高padding等相关属性）。否则getMeasuredHeight()获取为0,
        mHeaderView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);// 或 mesure(0,0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);


        addHeaderView(mHeaderView);
    }

    /**
     * 初始脚布局
     */
    private void initFooterView() {

        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.item_footer_layout, null);
        mFooterView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        mFooterViewHeight = mFooterView.getMeasuredHeight();

        //隐藏尾布局
        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

        addFooterView(mFooterView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断滑动距离，实时更新头布局的paddingTop
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                // 如果是正在刷新中, 就直接返回父类的处理。 其实这个感觉可以不要
                if (curState == REFRESHING) {
                    return super.onTouchEvent(ev);
                }

                float moveY = ev.getY();
                float offset = moveY - downY; //计算下拉的偏移量
                //只有当偏移量>0(表示是下拉,或者下拉之后再往回拉) 且 第一个可见的条目的索引是0时，才慢慢显示头布局
                if (offset > 0 && getFirstVisiblePosition() == 0) {

                    int paddingTop = (int) (-mHeaderViewHeight + offset);
                    mHeaderView.setPadding(0, paddingTop, 0, 0);

                    if (paddingTop >= 0 && curState != RELEASE_REFRESH) {//头布局完全显示时,切换为释放刷新状态. 第二个判断可以保证只刷新一次，避免频繁刷新
                        curState = RELEASE_REFRESH;
                        updateHeader();

                        KLog.d("释放刷新");
                    } else if (paddingTop < 0 && curState != PULL_REFRESH) {//头布局不完全显示时,切换为下拉刷新状态
                        curState = PULL_REFRESH;
                        updateHeader();

                        KLog.d("下拉刷新");
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 判断头布局是佛完全显示，决定是否执行刷新。可以根据 curState 判断，也可以根据 ACTION_MOVE中的 paddingTop 判断，都是一样的
                if (curState == PULL_REFRESH) {
                    mHeaderView.setPadding(0, mHeaderViewHeight, 0, 0);
                } else if (curState == RELEASE_REFRESH) {
                    mHeaderView.setPadding(0, 0, 0, 0);

                    curState = REFRESHING;
                    updateHeader();
                }

                break;
        }

        return super.onTouchEvent(ev);// 必须要调用父类的实现方法
    }

    private void updateHeader() {
        switch (curState) {
            case PULL_REFRESH: // 切换回下拉刷新
                mIvArrow.startAnimation(rotateDownAnim);
                mTvTitle.setText("下拉刷新");
                break;
            case RELEASE_REFRESH: // 切换成释放刷新
                mIvArrow.startAnimation(rotateUpAnim);
                mTvTitle.setText("释放刷新");
                break;
            case REFRESHING:// 刷新中...
                mIvArrow.clearAnimation();
                mIvArrow.setVisibility(View.INVISIBLE);
                mTvTitle.setText("正在刷新...");

                if (mListener != null) {
                    mListener.onRefresh();// 通知调用者, 让其到网络加载更多数据.
                }
                break;
        }
    }

    /**
     * 刷新结束, 头布局恢复初始效果
     */
    public void onRefreshComplete() {
        if (isLoadingMore) {
            //加载更多
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0); // 隐藏尾布局。 现在其他的下拉刷新控件中，加载更多的尾布局很多都没有隐藏
            isLoadingMore = false;

        } else {
            // 下拉刷新
            curState = PULL_REFRESH;
            mTvTitle.setText("下拉刷新"); // 切换文本
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏头布局
            mIvArrow.setVisibility(View.VISIBLE);
            String time = getTime();
            mTvLastRefreshTime.setText("最后刷新时间: " + time);
        }
    }


    public String getTime() {
        long time = System.currentTimeMillis();
        String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        return formatTime;
    }


    //=============================OnScrollListener================================

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        /**
         * SCROLL_STATE_IDLE = 0; // 空闲
         * SCROLL_STATE_TOUCH_SCROLL = 1; // 触摸滑动
         * SCROLL_STATE_FLING = 2; // （由于惯性）滑翔
         */

        KLog.d("scrollState: " + scrollState + ", getLastVisiblePosition(): " + getLastVisiblePosition() + ", getCount(): " + getCount());

        //若是正在加载更多，则直接返回
        if (isLoadingMore) {
            return;
        }

        // 最新状态是空闲状态, 并且当前界面显示了所有数据的最后一条. 加载更多
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && getLastVisiblePosition() == getCount() - 1) {

            isLoadingMore = true;
            KLog.d("开始加载更多");
            mFooterView.setPadding(0, 0, 0, 0);

            setSelection(getCount() - 1); // 滑动到最底部

            if (mListener != null) {
                mListener.onLoadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    private OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();

        void onLoadMore();
    }
}
