package com.handmark.pulltorefresh.samples.custom;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.samples.R;

/**
 *
 * 关于"PullToRefreshBase"原理的几点认识：
 *
 * 1. 布局架构？
 *    PullToRefreshBase<T> =
 *      (头)RotateLoadingLayout + FrameLayout + (尾)RotateLoadingLayout
 *
 *    在"FrameLayout"作为容器，存放"mRefreshableView"， 即各种泛型view，包括listview、gridview等
 *
 * 2. PullToRefreshBase 如何拦截手势操作？
 *    onInterceptTouchEvent和onTouchEvent配合
 *
 *    onInterceptTouchEvent：
 *    返回true，决定哪些event需要由"onTouchEvent"来处理；记录初始位置等
 *    此处为：需要将ACTION_MOVE给onTouchEvent处理，因此返回true。
 *
 *    onTouchEvent：
 *    执行具体的ui更新操作，比如在ACTION_MOVE中执行"pullEvent"
 *
 *  3. 为什么需要判断"isReadyForPull" ？
 *    组件不位于头部，下滑只会滑动组件内容，不会也不需要执行刷新操作。
 *    比如listview
 *
 *  4. pullEvent 如何更新下拉状态？
 *     scrollTo等
 *
 *  TODO:
 *  1. 为何无法在PTR中的"onInterceptTouchEvent"中收到"ACTION_MOVE"？
 *     子view中的前一个事件"ACTION_DOWN"返回了false。导致父容器无法接收后续事件
 *     参考：
 *     onInterceptTouchEvent 方法收不到ACTION_MOVE事件：
 *     https://blog.csdn.net/Zheng548/article/details/84028561
 *
 *  2. scrollTo、scrollBy基本用法
 *
 *  参考：
 *  1. PullToRefreshListView总结：
 *  https://www.it610.com/article/4992560.htm
 *
 *  2. PullToRefreshListview下拉刷新的原理分析
 *  https://www.jianshu.com/p/bddf93a3e54e
 *
 */
public class PTRMyLInearLayoutActivity extends Activity {

    private PullToRefreshMyLinearLayout mPullToRefreshMyLinearLayout;
    private MyLinearLayout mMyLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptrmy_linear_layout);
        mPullToRefreshMyLinearLayout = findViewById(R.id.pull_refresh_my_linear);
        mPullToRefreshMyLinearLayout.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshMyLinearLayout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<MyLinearLayout>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MyLinearLayout> refreshView) {
                Log.d("test", "onPullDownToRefresh() called with: refreshView = [" + refreshView + "]");
                TextView textView = new TextView(PTRMyLInearLayoutActivity.this);
                textView.setText("haha");
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                mMyLinearLayout.addView(textView);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshMyLinearLayout.onRefreshComplete();
                    }
                }, 2000);

                // 在子线程中"onRefreshComplete"会失败，想想为什么？
                /*
                new Thread(){
                    public void run() {
                        try {
                            mPullToRefreshMyLinearLayout.onRefreshComplete();
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                */
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MyLinearLayout> refreshView) {
                Log.d("test", "onPullUpToRefresh() called with: refreshView = [" + refreshView + "]");
                TextView textView = new TextView(PTRMyLInearLayoutActivity.this);
                textView.setText("heihei");
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                mMyLinearLayout.addView(textView);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshMyLinearLayout.onRefreshComplete();
                    }
                }, 2000);
            }
        });
        mMyLinearLayout = mPullToRefreshMyLinearLayout.getRefreshableView();

        TextView textView = new TextView(this);
        textView.setText("这是空的View");
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        mMyLinearLayout.addView(textView);
    }
}
