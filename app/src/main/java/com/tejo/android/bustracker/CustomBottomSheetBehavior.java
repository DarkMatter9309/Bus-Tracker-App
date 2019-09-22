package com.tejo.android.bustracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    Context mContext;
    private boolean isExpandedOrCollapsed;
    public CustomBottomSheetBehavior() {
        super();
        listenForSlideEvents();
    }

    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        listenForSlideEvents();
    }


    void listenForSlideEvents() {
        setBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                isExpandedOrCollapsed = slideOffset < 0.1f || slideOffset > 0.9f;
            }
        });
    }


    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {

        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return true;
        }

        return super.onTouchEvent(parent, child, event);
    }
}
