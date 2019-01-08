package com.yuxiansen.chart_industrydemo;

import android.content.Context;
import android.os.Environment;

import com.yuxiansen.chart_industrydemo.widget.PathView;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    : emmmm
 */
public class ChartConstant {
    public static final String FILE_DIR =Environment.getExternalStorageDirectory().getPath()+"/Chart/";
    public static PathView Global_PathView;
    public static final int TYPE_Z = 11, TYPE_Y0 = 12, TYPE_Y1 = 13, TYPE_Y2 = 14, TYPE_Y3 = 15, TYPE_Y4 = 16;
    public static int ChartContentWidth, ChartContentHeight,ChartAxis_MinHeight;
    public static boolean isAllowCheck;

    public static void init(Context context){
        ChartContentWidth = DisplayUtils.dip2px(context, 90);
        ChartContentHeight = DisplayUtils.dip2px(context, 18);
        ChartAxis_MinHeight = DisplayUtils.dip2px(context, 24);
        isAllowCheck = false;
    }

    public static void destroy(){
        Global_PathView = null;
    }

}
