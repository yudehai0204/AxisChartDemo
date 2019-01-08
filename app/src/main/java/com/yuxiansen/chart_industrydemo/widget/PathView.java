package com.yuxiansen.chart_industrydemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yuxiansen.chart_industrydemo.ChartConstant;
import com.yuxiansen.chart_industrydemo.ChartContentBean;
import com.yuxiansen.chart_industrydemo.DisplayUtils;
import com.yuxiansen.chart_industrydemo.db.ChartModel;
import com.yuxiansen.chart_industrydemo.db.ChartModel_Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class PathView extends View {
    private TextPaint axis_textPaint, axis_textpaint_y;
    private Paint mLinePaint;//轴画笔
    private Paint mBgPaint;//背景画笔
    private final int AXIS_COLOR = Color.parseColor("#dd5050");
    private final int AXIS_NAME_COLOR = Color.BLACK;
    private int MIN_HEIGHT;//最短距离z轴最短  y轴间隔是*2
    private static int AXIS_TEXT_HEIGHT;//字体高度
    private static int CHART_VIEW_WIDTH;
    private List<ChartContentView> zList, y0List, y1List, y2List, y3List, y4List;
    private List<Point> points;
    private int y1_length, y2_length, y3_length, y4_length;//Y长
    private static int X_LENGTH, Z_LENGTH, Y_LENGTH;//轴长度


    public PathView(Context context) {
        super(context);
        init(context);

    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PathView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setClickable(true);
        MIN_HEIGHT = ChartConstant.ChartAxis_MinHeight;
        CHART_VIEW_WIDTH = ChartConstant.ChartContentWidth;
        Z_LENGTH = MIN_HEIGHT * 10;
        y1_length = y2_length = y3_length = y4_length = Z_LENGTH;
        Y_LENGTH = (int) Math.sqrt(Math.pow(Z_LENGTH, 2) * 2);
        X_LENGTH = CHART_VIEW_WIDTH * 3;
        AXIS_TEXT_HEIGHT = MIN_HEIGHT * 2;

        //初始化画笔
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(AXIS_COLOR);
        mLinePaint.setStrokeWidth(1.5f);
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.parseColor("#f5caca"));
        mBgPaint.setAlpha(122);
        axis_textPaint = new TextPaint();
        axis_textPaint.setAntiAlias(true);
        axis_textPaint.setColor(AXIS_NAME_COLOR);
        axis_textPaint.setFakeBoldText(true);
        axis_textPaint.setTextSize(DisplayUtils.sp2px(context, 15));
        axis_textpaint_y = new TextPaint();
        axis_textpaint_y.setAntiAlias(true);
        axis_textpaint_y.setColor(Color.parseColor("#4b4b4b"));
        axis_textpaint_y.setTextSize(DisplayUtils.sp2px(context, 11));


        //横向数组
        y0List = new ArrayList<>();
        y1List = new ArrayList<>();
        y2List = new ArrayList<>();
        y3List = new ArrayList<>();
        y4List = new ArrayList<>();

        points = new ArrayList<>();
    }

    public void setZList(List<ChartContentView> list) {
        if (list == null)
            return;
        zList = list;
    }

    public void setYList(List<ChartContentView> list, int type) {
        if (list == null)
            return;
        int y_length = (int) (ChartConstant.ChartContentWidth * (list.size() * 1.5 + 1));
        if (y_length < ChartConstant.ChartContentWidth * 3) {
            y_length = ChartConstant.ChartContentWidth * 3;
        }
        switch (type) {
            case 0:
                y0List = list;
                X_LENGTH = y_length + ChartConstant.ChartAxis_MinHeight;
                break;
            case 1:
                y1List = list;
                y1_length = y_length;
                break;
            case 2:
                y2List = list;
                y2_length = y_length;
                break;
            case 3:
                y3List = list;
                y3_length = y_length;
                break;
            case 4:
                y4List = list;
                y4_length = y_length;
                break;

        }

        int width = getRealWidth(type);
        if (width > getMeasuredWidth()) {
            requestLayout();
        }
        postInvalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = Z_LENGTH + AXIS_TEXT_HEIGHT + MIN_HEIGHT;//总高度
        int width = X_LENGTH;
        width = initViewWidth(width);
        setMeasuredDimension(width, height);


    }

    private int initViewWidth(int width) {
        for (int i = 0; i < 5; i++) {
            if (width < getRealWidth(i)) {
                width = getRealWidth(i);
            }
        }
        return width;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawpolygon(canvas);
        drawAxis(canvas);
        drawAxisText(canvas);
    }

    private void drawpolygon(Canvas canvas) {
        points.clear();
        for (int i = 0; i < zList.size(); i++) {
            if (zList.get(i).isChartChecked()) {
                points.add(zList.get(i).getChartPoint());
            }
        }
        if(points.size()<=0){
            return;
        }
        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return (o1.x != o2.x) ? (o1.x - o2.x) : (o1.y - o2.y);
            }
        });
        List<Point> y0_point = new ArrayList<>();
        List<Point> y1_point = new ArrayList<>();
        List<Point> y2_point = new ArrayList<>();
        List<Point> y3_point = new ArrayList<>();
        List<Point> y4_point = new ArrayList<>();

        setYpoint(y0List,y0_point);
        setYpoint(y1List,y1_point);
        setYpoint(y2List,y2_point);
        setYpoint(y3List,y3_point);
        setYpoint(y4List,y4_point);

        decidePoint(y0_point, y1_point, y2_point, y3_point, y4_point);
        if (points.size() <= 0) {
            return;
        }


//        LogUtils.e(points.toString());
        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (i == 0) {
                path.moveTo(point.x, point.y);
            } else {
                path.lineTo(point.x, point.y);
            }
        }
        path.close();
        canvas.drawPath(path, mBgPaint);
    }

    private void setYpoint(List<ChartContentView> ylist,List<Point> y_point){
        for (int i = 0; i < ylist.size(); i++) {
            if (ylist.get(i).isChartChecked()) {
                y_point.add(ylist.get(i).getChartPoint());
            }
        }
    }
    private void decidePoint(List<Point> y0_point, List<Point> y1_point, List<Point> y2_point, List<Point> y3_point, List<Point> y4_point) {

        //正加点
        if (y0_point.size() > 0) {
            points.addAll(y0_point);
        }
        if (y1_point.size() > 0) {
            if (y0_point.size() > 0)
                points.add(y1_point.get(y1_point.size() - 1));
            else
                points.addAll(y1_point);
        }
        if (y2_point.size() > 0) {
            if (y0_point.size() > 0 || y1_point.size() > 0)
                points.add(y2_point.get(y2_point.size() - 1));
            else
                points.addAll(y2_point);
        }
        if (y3_point.size() > 0) {
            if (y1_point.size() > 0 || y2_point.size() > 0 || y0_point.size() > 0)
                points.add(y3_point.get(y3_point.size() - 1));
            else
                points.addAll(y3_point);
        }
        if (y4_point.size() > 0) {
            if (y4_point.size() > 0 || y2_point.size() > 0 || y0_point.size() > 0 || y1_point.size() > 0)
                points.add(y4_point.get(y4_point.size() - 1));
            else
                points.addAll(y4_point);
        }
        //倒推
        if (y4_point.size() > 1 && (y0_point.size() > 0 || y1_point.size() > 0 || y2_point.size() > 0 || y3_point.size() > 0))
            points.add(y4_point.get(0));
        else if (y3_point.size() > 1 && (y0_point.size() > 0 || y1_point.size() > 0 || y2_point.size() > 0))
            points.add(y3_point.get(0));
        else if (y2_point.size() > 1 && (y0_point.size() > 0 || y1_point.size() > 0))
            points.add(y2_point.get(0));
        else if (y1_point.size() > 1 && y0_point.size() > 0)
            points.add(y1_point.get(0));


    }

    private String y_strs[] = new String[]{"Y1支持", "Y2相关", "Y3配套", "Y4配套服务"};

    private void drawAxisText(Canvas canvas) {
        //写轴名称
        canvas.drawText("Z轴-商业逻辑", 0, AXIS_TEXT_HEIGHT - 20, axis_textPaint);
        int str_width = (int) axis_textPaint.measureText("Y轴-相关产业链");
        canvas.drawText("Y轴-相关产业链", Z_LENGTH - str_width - MIN_HEIGHT, AXIS_TEXT_HEIGHT + MIN_HEIGHT, axis_textPaint);
        canvas.drawText("X轴-主产业链", X_LENGTH + MIN_HEIGHT, getMeasuredHeight() - MIN_HEIGHT * 0.75f, axis_textPaint);
        Rect rect = new Rect();
        axis_textpaint_y.getTextBounds(y_strs[0], 0, 3, rect);

        canvas.drawText(y_strs[0], ChartConstant.ChartAxis_MinHeight, getMeasuredHeight() - ChartConstant.ChartAxis_MinHeight * 3 + rect.height() / 2, axis_textpaint_y);
        str_width = (int) axis_textpaint_y.measureText(y_strs[1]) + ChartConstant.ChartAxis_MinHeight / 2;
        canvas.drawText(y_strs[1], ChartConstant.ChartAxis_MinHeight * 5 - str_width, getMeasuredHeight() - ChartConstant.ChartAxis_MinHeight * 5 + rect.height() / 2, axis_textpaint_y);
        str_width = (int) axis_textpaint_y.measureText(y_strs[2]) + ChartConstant.ChartAxis_MinHeight / 2;
        canvas.drawText(y_strs[2], ChartConstant.ChartAxis_MinHeight * 7 - str_width, getMeasuredHeight() - ChartConstant.ChartAxis_MinHeight * 7 + rect.height() / 2, axis_textpaint_y);
        str_width = (int) axis_textpaint_y.measureText(y_strs[3]) + ChartConstant.ChartAxis_MinHeight / 2;
        canvas.drawText(y_strs[3], ChartConstant.ChartAxis_MinHeight * 9 - str_width, getMeasuredHeight() - ChartConstant.ChartAxis_MinHeight * 9 + rect.height() / 2, axis_textpaint_y);

    }

    /***
     * 画轴线
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {
        canvas.drawLine(0, getMeasuredHeight() - MIN_HEIGHT, X_LENGTH, getMeasuredHeight() - MIN_HEIGHT, mLinePaint);//X轴
        canvas.drawLine(MIN_HEIGHT, getMeasuredHeight() - MIN_HEIGHT, MIN_HEIGHT, AXIS_TEXT_HEIGHT, mLinePaint);//Z周
        canvas.drawLine(MIN_HEIGHT, getMeasuredHeight() - MIN_HEIGHT, Z_LENGTH, AXIS_TEXT_HEIGHT + MIN_HEIGHT, mLinePaint);//Y轴
        for (int i = 1; i <= 9; i++) {//画Z轴小短线
            int Y = getMeasuredHeight() - MIN_HEIGHT * (i + 1);
            canvas.drawLine(0, Y, MIN_HEIGHT, Y, mLinePaint);
        }
        for (int i = 1; i < 5; i++) {//画Y平行线
            int startx = MIN_HEIGHT * (i * 2 + 1);
            int y = getMeasuredHeight() - startx;
            int endx = startx + getYlength(i);
            canvas.drawLine(startx, y, endx, y, mLinePaint);
        }
        //画箭头
        canvas.drawLine(Z_LENGTH, AXIS_TEXT_HEIGHT + MIN_HEIGHT, Z_LENGTH, AXIS_TEXT_HEIGHT + MIN_HEIGHT * 1.5f, mLinePaint);
        canvas.drawLine(Z_LENGTH - MIN_HEIGHT * 0.5f, AXIS_TEXT_HEIGHT + MIN_HEIGHT, Z_LENGTH, AXIS_TEXT_HEIGHT + MIN_HEIGHT, mLinePaint);//Y箭头
        canvas.save();//Z箭头
        canvas.rotate(45, MIN_HEIGHT, AXIS_TEXT_HEIGHT);
        canvas.drawLine(MIN_HEIGHT, AXIS_TEXT_HEIGHT, MIN_HEIGHT * 1.5f, AXIS_TEXT_HEIGHT, mLinePaint);
        canvas.drawLine(MIN_HEIGHT, AXIS_TEXT_HEIGHT, MIN_HEIGHT, AXIS_TEXT_HEIGHT + MIN_HEIGHT * 0.5f, mLinePaint);
        canvas.restore();
        canvas.save();//X箭头
        canvas.rotate(45, X_LENGTH, getMeasuredHeight() - MIN_HEIGHT);
        canvas.drawLine(X_LENGTH - MIN_HEIGHT * 0.5f, getMeasuredHeight() - MIN_HEIGHT, X_LENGTH, getMeasuredHeight() - MIN_HEIGHT, mLinePaint);
        canvas.drawLine(X_LENGTH, getMeasuredHeight() - MIN_HEIGHT, X_LENGTH, getMeasuredHeight() - MIN_HEIGHT * 0.5f, mLinePaint);
        canvas.restore();


    }


    private int getYlength(int i) {
        switch (i) {
            case 1:
                return y1_length;
            case 2:
                return y2_length;
            case 3:
                return y3_length;
            case 4:
                return y4_length;
            default:
                return Z_LENGTH;
        }
    }

    private int getRealWidth(int i) {
        int width = getMeasuredWidth();
        switch (i) {
            case 0:
                width = X_LENGTH + ChartConstant.ChartContentWidth * 3;
                break;
            case 1:
                width = y1_length + ChartConstant.ChartAxis_MinHeight * 3 + ChartConstant.ChartContentWidth * 2;
                break;
            case 2:
                width = y2_length + ChartConstant.ChartAxis_MinHeight * 5 + ChartConstant.ChartContentWidth * 2;
                break;
            case 3:
                width = y3_length + ChartConstant.ChartAxis_MinHeight * 7 + ChartConstant.ChartContentWidth * 2;
                break;
            case 4:
                width = y4_length + ChartConstant.ChartAxis_MinHeight * 9 + ChartConstant.ChartContentWidth * 2;
                break;
        }
        return width;
    }

    public void saveDataToDataBase(String name, String author, long id) {
        String z_data = translateforGson(zList);
        String y0_data = translateforGson(y0List);
        String y1_data = translateforGson(y1List);
        String y2_data = translateforGson(y2List);
        String y3_data = translateforGson(y3List);
        String y4_data = translateforGson(y4List);
        ChartModel chartModel = null;
        if (id > 0) {//更新
            chartModel = SQLite.select().from(ChartModel.class)
                    .where(ChartModel_Table.id.is(id)).querySingle();
        } else {
            chartModel = new ChartModel();
            chartModel.setUid("test");
        }
        if (chartModel == null)
            return;
        chartModel.setChart_name(name);
        chartModel.setAuthor(author);
        chartModel.setY0_data(y0_data);
        chartModel.setY1_data(y1_data);
        chartModel.setY2_data(y2_data);
        chartModel.setY3_data(y3_data);
        chartModel.setY4_data(y4_data);
        chartModel.setZ_data(z_data);
        if (id > 0)
            chartModel.update();
        else
            chartModel.save();


    }

    private String translateforGson(List<ChartContentView> list) {
        if (list == null || list.size() == 0)
            return "";
        String result = "";
        List<ChartContentBean> list_chart = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ChartContentBean bean = new ChartContentBean();
            bean.setCheck(list.get(i).isChartChecked());
            bean.setText(list.get(i).getText().toString());
            list_chart.add(bean);
        }


        return new Gson().toJson(list_chart);
    }



    public void removeAllChildFocus() {
        changeFocus(y0List,false);
        changeFocus(y1List,false);
        changeFocus(y2List,false);
        changeFocus(y3List,false);
        changeFocus(y4List,false);
    }

    public void resetAllChildFocus(){
        changeFocus(y0List,true);
        changeFocus(y1List,true);
        changeFocus(y2List,true);
        changeFocus(y3List,true);
        changeFocus(y4List,true);
    }

    private void changeFocus(List<ChartContentView> list,boolean state){
        for (int i = 0; i < list.size(); i++){
            list.get(i).setFocusable(state);
            if(state){
                list.get(i).setFocusableInTouchMode(true);
                list.get(i).requestFocus();
                list.get(i).requestFocusFromTouch();
            }
        }
    }
}
