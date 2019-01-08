package com.yuxiansen.chart_industrydemo.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuxiansen.chart_industrydemo.ChartConstant;
import com.yuxiansen.chart_industrydemo.ChartContentBean;
import com.yuxiansen.chart_industrydemo.ChartUtils;
import com.yuxiansen.chart_industrydemo.DisplayUtils;
import com.yuxiansen.chart_industrydemo.R;
import com.yuxiansen.chart_industrydemo.db.ChartModel;

import java.util.ArrayList;
import java.util.List;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class ChartViewGroup extends ViewGroup {
    private static int mTouchSlop;
    private Scroller mScroller;
    private ChartViewLongClick mListener;
    private final String str_z[]= new String []{"资源(Z0)","中转库(Z1)","物联网(Z2)","技术(Z3)","金融(Z4)","互联网(Z5)","大数据(Z6)","算法(Z7)","去中心化(Z8)","征信(Z9)"};
    private static int AXIS_START;
    private int left_distance;
    private int z_distance;
    private List<ChartContentView> zlist;
    private List<Y_ChartGroup> ygrouplist;
    private String name,author;
    private long id;
    private int screen_width,screen_height;


    public interface ChartViewLongClick{
        void onLongClick(ChartContentView view);
    }
    public ChartViewGroup(Context context) {
        super(context);
        init(context);
    }

    public ChartViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        zlist = new ArrayList<>();
        ygrouplist = new ArrayList<>();
        screen_width = DisplayUtils.getScreenWidth(context);
        screen_height = DisplayUtils.getScreenHeight(context)-DisplayUtils.dip2px(context,65);
        left_distance = DisplayUtils.dip2px(context,20);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller = new Scroller(context);
        AXIS_START = DisplayUtils.dip2px(context,80);//
        z_distance=DisplayUtils.dip2px(context,33);
        PathView pathView = new PathView(context);
        ChartConstant.Global_PathView = pathView;
        addView(pathView);
        for (int i=0;i<str_z.length;i++){//Z轴上的组件
            ChartContentView view = new ChartContentView(context);
            view.setOnLongClickListener(onLongClickListener);
            view.unlockMaxLength();
            view.setText(str_z[i]);
            view.setTag(R.id.chart_content_id,i);
            view.setTag(R.id.chart_content_type,ChartConstant.TYPE_Z);
            view.setFocusable(false);
            addView(view);
            ChartUtils.SetZPoint(view);
            zlist.add(view);
        }
        pathView.setZList(zlist);
        for (int i=4;i>=0;i--){//y43210
            Y_ChartGroup y = new Y_ChartGroup(context);
            y.setTag(i);
            y.setY_Type(getY_type(i));
            addView(y);
            ygrouplist.add(y);
        }
    }

    public void setInfo(String name, String author, long sql_id) {
        this.name = name;
        this.author = author;
        this.id = sql_id;
    }

    public boolean isCanBulidPolygon() {
        for(int i=0;i<zlist.size();i++){
            if(zlist.get(i).isChartChecked())
                return true;
        }
        return false;
    }
    private int getY_type(int i){
        int type = ChartConstant.TYPE_Y0;
        switch (i){
            case 4:
                type = ChartConstant.TYPE_Y4;
                break;
            case 3:
                type = ChartConstant.TYPE_Y3;
                break;
            case 2:
                type = ChartConstant.TYPE_Y2;
                break;
            case 1:
                type = ChartConstant.TYPE_Y1;
                break;
        }
        return type;
    }

    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {
        l=l+left_distance;//对左留20dp
        t=0;
        PathView view = (PathView) getChildAt(0);
        view.layout(AXIS_START+l,t,l+view.getMeasuredWidth()+AXIS_START,t+view.getMeasuredHeight());
        int top,left;
        for (int i=0;i<str_z.length;i++){//Z
            View chartView_Z =  getChildAt(i+1);
            top = view.getMeasuredHeight()-(z_distance+i*ChartConstant.ChartAxis_MinHeight)+t;
            chartView_Z.layout(l,top,l+chartView_Z.getMeasuredWidth(),top+chartView_Z.getMeasuredHeight());
        }
        for (int i=0;i<5;i++){
            Y_ChartGroup y_view = (Y_ChartGroup) getChildAt(str_z.length+1+i);
            top= ChartConstant.ChartAxis_MinHeight*4-ChartConstant.ChartContentHeight/2+ChartConstant.ChartAxis_MinHeight*2*i+t;
            left = ChartConstant.ChartAxis_MinHeight*((4-i)*2+1)+l+AXIS_START;
            y_view.layout(left,top,left+y_view.getNeedWidth(),top+y_view.getMeasuredHeight());
        }
    }

    public void setListener(ChartViewLongClick mListener) {
        this.mListener = mListener;
        for (int i=0;i<ygrouplist.size();i++){
            ygrouplist.get(i).setListener(mListener);
        }
    }

    public void  removeAllChildFocus(){
        if(ChartConstant.Global_PathView!=null)
            ChartConstant.Global_PathView.removeAllChildFocus();
    }

    public void  resetAllChildFocus(){
        if(ChartConstant.Global_PathView!=null)
            ChartConstant.Global_PathView.resetAllChildFocus();
    }

    public void saveDataToDatabase(){
        boolean isChange=false;
        if(ygrouplist==null)
            return;
        for (int i=0;i<ygrouplist.size();i++){
            if (ygrouplist.get(i).getChildCount()>2){
                isChange=true;
            }
        }
        if(isChange)
            ChartConstant.Global_PathView.saveDataToDataBase(name,author,id);
    }

    /***
     * 刷新数据
     * @param chartModel
     */
    public void updateData(ChartModel chartModel){

        List<ChartContentBean> list;
        if(!TextUtils.isEmpty(chartModel.getZ_data())){
            list =  new Gson().fromJson(chartModel.getZ_data(),new TypeToken<List<ChartContentBean>>(){}.getType());
            for(int i=0;i<list.size();i++){
                zlist.get(i).setChartChecked(list.get(i).isCheck());
            }
        }

        if(!TextUtils.isEmpty(chartModel.getY0_data())){
            ygrouplist.get(4).updateData(chartModel.getY0_data());
        }
        if(!TextUtils.isEmpty(chartModel.getY1_data())){
            ygrouplist.get(3).updateData(chartModel.getY1_data());
        }
        if(!TextUtils.isEmpty(chartModel.getY2_data())){
            ygrouplist.get(2).updateData(chartModel.getY2_data());
        }
        if(!TextUtils.isEmpty(chartModel.getY3_data())){
            ygrouplist.get(1).updateData(chartModel.getY3_data());
        }
        if(!TextUtils.isEmpty(chartModel.getY4_data())){
            ygrouplist.get(0).updateData(chartModel.getY4_data());
        }

        ChartUtils.refreshPathView(true);

    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener(){


        @Override
        public boolean onLongClick(View v) {
            if(v instanceof ChartContentView && mListener!=null){
                mListener.onLongClick((ChartContentView) v);
            }

            return true;
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i=0;i<childCount;i++){
            View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
        }
        PathView pathView = (PathView) getChildAt(0);
        int height = pathView.getMeasuredHeight();
        int width = pathView.getMeasuredWidth();
        setMeasuredDimension(width,height);

    }

    private float mXDown,mYDown,mXLastMove,mYLastMove;
    private float mXMove,mYMove;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mScroller!=null&&!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mXDown = ev.getRawX();
                mYDown = ev.getRawY();
                mXLastMove = mXDown;
                mYLastMove = mYDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                mYMove = ev.getRawY();
                mXLastMove = mXMove;
                mYLastMove = mYMove;
                float diffX = Math.abs(mXMove - mXDown);
                float diffY = Math.abs(mYMove - mYDown);
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diffX > mTouchSlop||diffY>mTouchSlop) {
                    return true;
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                mYMove = event.getRawY();
                int scrollY= (int) (mYLastMove-mYMove);
                int scrollX = (int) (mXLastMove-mXMove);
                Log.e("Test","scrollX:"+getScrollX()+"====scrollY:"+getScrollY());
                scrollBy(scrollX,scrollY);
                mXLastMove =mXMove;
                mYLastMove = mYMove;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                boolean needScroll= false;
                int distance_y=0;
                int distance_x=0;
                if(getScrollY()<0||getMeasuredHeight()<=screen_height){
                    distance_y = -getScrollY();
                    needScroll = true;
                }else if(getScrollY()+screen_height>getMeasuredHeight()){
                    distance_y = -(getScrollY()+screen_height - getMeasuredHeight());
                }

                if(getScrollX()<0||getMeasuredWidth()<=screen_width){
                    distance_x = -getScrollX();
                    needScroll = true;
                }else if(getScrollX()+screen_width>getMeasuredWidth()){
                    distance_x = -(getScrollX()+screen_width-getMeasuredWidth());
                    needScroll = true;
                }
                if(needScroll){
                    mScroller.startScroll(getScrollX(),getScrollY(),distance_x,distance_y,500);
                    invalidate();
                }

                break;

        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());

            postInvalidate();
        }
        super.computeScroll();

    }
}
