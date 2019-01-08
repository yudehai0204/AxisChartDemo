package com.yuxiansen.chart_industrydemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.yuxiansen.chart_industrydemo.ChartConstant;
import com.yuxiansen.chart_industrydemo.ChartUtils;
import com.yuxiansen.chart_industrydemo.R;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class ChartContentView extends AppCompatEditText {
    private boolean isChecked ;//是否处于合并状态
    private Point point;//获取中心的坐标点
    public ChartContentView(Context context) {
        super(context);
        init(context);
    }

    public ChartContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setBackgroundResource(R.drawable.shape_bg_grey_solid);
        setGravity(Gravity.CENTER);
        setTextColor(Color.BLACK);
        setSingleLine();
        setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        setPadding(0,0,0,0);
        setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(6)
        });
        setImeOptions(EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            setCustomInsertionActionModeCallback(new ActionModeCallbackInterceptor());
            setCustomSelectionActionModeCallback(new ActionModeCallbackInterceptor());
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ChartConstant.isAllowCheck)
                    setChartChecked(!isChecked);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(ChartConstant.ChartContentWidth,ChartConstant.ChartContentHeight);
    }

    /***
     * 解锁限制
     */
    public void unlockMaxLength(){
        setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(60)
        });
    }

    public Point getChartPoint() {
        return point;
    }

    public void setChartPoint(Point point) {
        this.point = point;
    }

    public boolean isChartChecked() {
        return isChecked;
    }

    public void setChartChecked(boolean checked) {
        isChecked = checked;
        if(isChecked){
            setBackgroundResource(R.drawable.shape_bg_red_solid);
            setTextColor(getResources().getColor(R.color.white));
        }else {
            setBackgroundResource(R.drawable.shape_bg_grey_solid);
            setTextColor(Color.BLACK);
        }
        ChartUtils.refreshPathView();
    }

    private class ActionModeCallbackInterceptor implements ActionMode.Callback {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
