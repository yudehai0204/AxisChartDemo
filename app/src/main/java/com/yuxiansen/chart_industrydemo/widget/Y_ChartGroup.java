package com.yuxiansen.chart_industrydemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuxiansen.chart_industrydemo.ChartConstant;
import com.yuxiansen.chart_industrydemo.ChartContentBean;
import com.yuxiansen.chart_industrydemo.ChartUtils;
import com.yuxiansen.chart_industrydemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class Y_ChartGroup extends LinearLayout {
    private List<ChartContentView> y_list;

    private ChartViewGroup.ChartViewLongClick mListener;
    private  int Y_type;
    public Y_ChartGroup(Context context) {
        super(context);
        init(context);
    }

    public Y_ChartGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Y_ChartGroup(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setOrientation(HORIZONTAL);
    }
    public void setY_Type(int type){
        if(ChartConstant.Global_PathView ==null){
            throw  new RuntimeException("You must first init  ChartConstant.Global_PathView");
        }
        this.Y_type = type;
        y_list = new ArrayList<>();
        addChartValue(0);
        ImageView img = new ImageView(getContext());
        img.setOnClickListener(listener);
        img.setImageResource(R.mipmap.img_chart_add);
        addView(img);
        LinearLayout.LayoutParams params = (LayoutParams) img.getLayoutParams();
        params.width = ChartConstant.ChartContentHeight;
        params.leftMargin = ChartConstant.ChartContentWidth / 2;
        params.height = ChartConstant.ChartContentHeight;
        img.setLayoutParams(params);
        ChartConstant.Global_PathView.setYList(y_list, (Integer) getTag());
    }

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v) {
            if(v instanceof ChartContentView && mListener!=null){
                mListener.onLongClick((ChartContentView) v);
            }
            return true;
        }
    };
    private View.OnClickListener listener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            addChartValue(getChildCount()-1);
            if(ChartConstant.Global_PathView!=null){
                ChartConstant.Global_PathView.setYList(y_list, (Integer) getTag());
            }
        }
    };

    public void deleteView(int index){
        removeViewAt(index);
        y_list.remove(index);
        refreshAllCount();

    }

    public void updateData(String data){
        if(y_list==null)
            return;
        removeViewAt(0);
        y_list.clear();
        List<ChartContentBean> list =  new Gson().fromJson(data,new TypeToken<List<ChartContentBean>>(){}.getType());
        for (int i=0;i<list.size();i++){
            addChartValue(i);
            y_list.get(i).setChartChecked(list.get(i).isCheck());
            y_list.get(i).setText(list.get(i).getText());
        }
        ChartConstant.Global_PathView.setYList(y_list, (Integer) getTag());
    }

    private void refreshAllCount() {
        int count  = getChildCount();
        for (int i=0;i<count-1;i++){
            ChartContentView view = (ChartContentView) getChildAt(i);
            view.setTag(R.id.chart_content_type,Y_type);
            view.setTag(R.id.chart_content_id,i);
            ChartUtils.setYPoint(view);
        }
        if(ChartConstant.Global_PathView!=null)
            ChartConstant.Global_PathView.setYList(y_list, (Integer) getTag());
        ChartUtils.refreshPathView(true);
    }

    public void setListener(ChartViewGroup.ChartViewLongClick mListener) {
        this.mListener = mListener;
    }

    public void addChartValue(int index) {
        ChartContentView view = new ChartContentView(getContext());
        view.setTag(R.id.chart_content_type,Y_type);

        view.setTag(R.id.chart_content_id,index);
        if(ChartConstant.isAllowCheck){
            view.setFocusable(false);
        }
        view.setOnLongClickListener(longClickListener);
        addView(view,index);
        setViewMargin(view);
        ChartUtils.setYPoint(view);
        y_list.add(view);

    }


    private void setViewMargin(View view){
        LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.leftMargin = ChartConstant.ChartContentWidth / 2;
        view.setLayoutParams(params);
    }



    public int  getNeedWidth() {
        return (int) (ChartConstant.ChartContentWidth*(y_list.size()*1.5+2));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),ChartConstant.ChartContentHeight);
    }
}
