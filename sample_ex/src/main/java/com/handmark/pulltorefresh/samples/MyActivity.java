package com.handmark.pulltorefresh.samples;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.LinkedList;
import java.util.List;

public class MyActivity extends Activity implements PullToRefreshBase.OnRefreshListener2 {
    // 区别于 "PullToRefreshBase.OnRefreshListener", "OnRefreshListener2" 可以判断上拉、下拉方向

    private PullToRefreshListView mPullToRefreshListView;

    private ArrayAdapter mArrayAdapter;

    private List<String> mDataFakeRemoteList;
    private List<String> mInnerDataList = new LinkedList<>();
    private static final int PAGE_COUNT = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mPullToRefreshListView = findViewById(R.id.pull_refresh_list_my);
        mPullToRefreshListView.setOnRefreshListener(this);
        /*
        * BOTH: 对应 OnRefreshListener2
        * PULL_FROM_START、PULL_FROM_END: 对应 OnRefreshListener
        * */
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        TextView textView = new TextView(this);
        textView.setText("没有数据");
        textView.setTextSize(20);
        mPullToRefreshListView.setEmptyView(textView);

        ListView actualListView = mPullToRefreshListView.getRefreshableView();

        // 设置自定义下拉刷新动画文字
        ILoadingLayout headerLayout = mPullToRefreshListView.getLoadingLayoutProxy(true, false);
        headerLayout.setPullLabel("向下拖动完成刷新...");
        headerLayout.setRefreshingLabel("正在加载新数据...");
        headerLayout.setReleaseLabel("释放完成刷新...");
        headerLayout.setLastUpdatedLabel("上次update");
        //  会覆盖xml中配置: ptr:ptrDrawable="@drawable/icon_selected"
        headerLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.icon_selected));

        //设置底部刷新文字
        ILoadingLayout footLayout = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
        footLayout.setPullLabel("向上拽动完成刷新...");
        footLayout.setRefreshingLabel("正在疯刷新数据...");
        footLayout.setReleaseLabel("松开完成刷新...");
        footLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.android));

        initDataList();
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mInnerDataList);
        actualListView.setAdapter(mArrayAdapter);

        // 自动刷新。Mode选择BOTH的时候，默认是下拉刷新
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListView.setRefreshing();
            }
        }, 2000);
    }

    private void initDataList() {
        if (mDataFakeRemoteList == null) {
            mDataFakeRemoteList = new LinkedList<>();
            for (int i = 0; i < PAGE_COUNT * 10; i++) {
                mDataFakeRemoteList.add(Integer.toString(i));
            }
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        // 设置上次更新时间
        String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        Log.d("test", "onPullDownToRefresh() called with: refreshView = [" + refreshView + "]");
        Toast.makeText(this, "来自下拉刷新!" ,Toast.LENGTH_SHORT).show();
        new GetDataTask().execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        Log.d("test", "onPullUpToRefresh() called with: refreshView = [" + refreshView + "]");
        // 设置上次更新时间
        String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        Toast.makeText(this, "来自上拉刷新!" ,Toast.LENGTH_SHORT).show();
        new GetDataTask().execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
            * 模拟分页加载：
            * 每次从mDataFakeRemoteList取出10个数据
            * */
            List<String> dataGet = mDataFakeRemoteList.subList
                    (pageId * PAGE_COUNT, pageId * PAGE_COUNT + PAGE_COUNT);
            return dataGet;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            // 注意:如果数据有重复，这里要判断去重！
            mInnerDataList.addAll(pageId * PAGE_COUNT, strings);
            pageId++;
            mArrayAdapter.notifyDataSetChanged();
            //  1. refresh 完了之后要把 pull view hide 起来
            //  2. 必须异步调用！同步调用头部动画不会消失！
            mPullToRefreshListView.onRefreshComplete();
            super.onPostExecute(strings);
        }
    }

    private int pageId = 0;


//    @Override
//    public void onRefresh(PullToRefreshBase refreshView) {
//        Log.d("test", "onRefresh() called with: refreshView = [" + refreshView + "]");
//        new GetDataTask().execute();
//    }
}
