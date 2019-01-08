package com.yuxiansen.chart_industrydemo.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * author  : Mr.Yu
 * Time    : 2018.1.2
 * Email   : yudehai0204@163.com
 * desc    : Base类
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(getIntent().getExtras());
        setContentView(getContentView());
        initView();
        initData();
        initListener();
    }

    protected abstract void initParams(Bundle bundle);

    protected abstract int getContentView();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                );

            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /***
     *
     * @param v
     * @param event
     * @return 是否需要隐藏软键盘
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
