package com.handmark.pulltorefresh.samples.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.samples.R;

public class PullToRefreshMyLinearLayout extends PullToRefreshBase<MyLinearLayout> {

    public PullToRefreshMyLinearLayout(Context context) {
        super(context);
    }

    public PullToRefreshMyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected MyLinearLayout createRefreshableView(Context context, AttributeSet attrs) {
        InternalMyLinearLayout myLinearLayout = new InternalMyLinearLayout(context, attrs);
        myLinearLayout.setId(R.id.mylinearlayout);
        return myLinearLayout;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        Log.d("test", "isReadyForPullEnd() called");
        return true;
    }

    @Override
    protected boolean isReadyForPullStart() {
        Log.d("test", "isReadyForPullStart() called");
        return true;
    }

    final class InternalMyLinearLayout extends MyLinearLayout {

        public InternalMyLinearLayout(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
//            boolean ret = super.onTouchEvent(event);
//            Log.d("test", "InternalMyLinearLayout onTouchEvent() called with: event = [" + event + "] " + ret);
//            return ret;
            return true;
        }

    }
}
