package com.yuxiansen.chart_industrydemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yuxiansen.chart_industrydemo.base.BaseActivity;
import com.yuxiansen.chart_industrydemo.widget.ChartContentView;
import com.yuxiansen.chart_industrydemo.widget.ChartViewGroup;

import java.io.File;
import java.io.FileOutputStream;

import static com.yuxiansen.chart_industrydemo.ChartConstant.ChartAxis_MinHeight;
import static com.yuxiansen.chart_industrydemo.ChartConstant.ChartContentWidth;
import static com.yuxiansen.chart_industrydemo.ChartConstant.TYPE_Y1;
import static com.yuxiansen.chart_industrydemo.ChartConstant.TYPE_Y2;
import static com.yuxiansen.chart_industrydemo.ChartConstant.TYPE_Y3;
import static com.yuxiansen.chart_industrydemo.ChartConstant.TYPE_Y4;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class ChartUtils {
    public static void refreshPathView() {
        refreshPathView(false);
    }

    public static void refreshPathView(boolean refreshLayout){
        if(ChartConstant.Global_PathView == null)
            return;
        if(refreshLayout)
            ChartConstant.Global_PathView.requestLayout();

        ChartConstant.Global_PathView.postInvalidate();

    }

    /***
     * 设置Z轴上控件中心坐标
     * @param view
     */
    public static void SetZPoint(ChartContentView view) {
        Point point = new Point();
        point.x = 0;
        int index = (int) view.getTag(R.id.chart_content_id);
        point.y = ChartAxis_MinHeight * (13 - index - 1);
        view.setChartPoint(point);
    }

    /***
     * 设置Ypoint
     * @param view
     */
    public static void setYPoint(ChartContentView view) {
        int type = (int) view.getTag(R.id.chart_content_type);
        int index = (int) view.getTag(R.id.chart_content_id);
        Point point = new Point();
        int startx;
        switch (type) {
            case TYPE_Y1:
                point.y = ChartAxis_MinHeight * (13 - 3);
                startx = ChartAxis_MinHeight * 3;
                break;
            case TYPE_Y2:
                point.y = ChartAxis_MinHeight * (13 - 5);
                startx = ChartAxis_MinHeight * 5;
                break;
            case TYPE_Y3:
                point.y = ChartAxis_MinHeight * (13 - 7);
                startx = ChartAxis_MinHeight * 7;
                break;
            case TYPE_Y4:
                point.y = ChartAxis_MinHeight * (13 - 9);
                startx = ChartAxis_MinHeight * 9;
                break;
            default:
                point.y = ChartAxis_MinHeight * (13 - 1);
                startx = ChartAxis_MinHeight;
                break;

        }

        point.x = (int) (startx + ChartContentWidth * ((index + 1) * 1.5f - 0.5f));
        view.setChartPoint(point);
        Log.e("ChartView", "Type:" + type + "Index:" + index + "Point:" + point.toString());
    }

    public interface CallBack{
        void buildImageSuccess(Bitmap bitmap);
    }

    /***
     *
     * @param left
     * @param right
     * @param top
     * @return 生成分享图
     */
    public static void SaveChartImage(final ChartViewGroup left, final LinearLayout right, final RelativeLayout top, final ChartUtils.CallBack callBack) {
        final int left_width = left.getMeasuredWidth();
        int left_height = left.getMeasuredHeight();
        int right_width = right.getWidth();
        final int top_height = top.getHeight();
        final int img_height = left_height + top_height;
        final int img_width = left_width + right_width;

        top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                top.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Bitmap bitmap;
                Bitmap bitmap_left = getCacheBitmapFromView(left);
                Bitmap bitmap_right = getCacheBitmapFromView(right);
                Bitmap bitmap_top = getCacheBitmapFromView(top);
                bitmap = Bitmap.createBitmap(img_width, img_height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap_top, 0, 0, null);
                canvas.drawBitmap(bitmap_left, 0, top_height, null);
                canvas.drawBitmap(bitmap_right, left_width, top_height, null);
                callBack.buildImageSuccess(bitmap);
            }
        });
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) top.getLayoutParams();
        params.width = img_width;
        top.setLayoutParams(params);
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) right.getLayoutParams();
        params1.height = left_height;
        right.setLayoutParams(params1);

    }


    /**
     * 将 Bitmap 保存到SD卡
     *
     * @param activity
     * @param bitmap
     * @param name
     * @return
     */
    public static String saveBitmapToSdCard(BaseActivity activity, Bitmap bitmap, String name) {
        boolean result = false;
        //创建位图保存目录
        String path = ChartConstant.FILE_DIR + "Image/Uml/";
        File sd = new File(path);
        if (!sd.exists()) {
            sd.mkdirs();
        }
        File file = new File(path + name + ".jpg");
        FileOutputStream fileOutputStream = null;
        if (file.exists()) {
            file.delete();
        }
        try {

            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            activity.sendBroadcast(intent);
            ToastManager.shotToast("已保存至" + file.getAbsolutePath());
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            ToastManager.shotToast(e.getMessage());
        }

        return "";
    }

    /**
     * 获取一个 View 的缓存视图
     *  (前提是这个View已经渲染完成显示在页面上)
     * @param view
     * @return
     */
    public static Bitmap getCacheBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }
}
