package com.yuxiansen.chart_industrydemo;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuxiansen.chart_industrydemo.base.BaseActivity;
import com.yuxiansen.chart_industrydemo.db.ChartModel;
import com.yuxiansen.chart_industrydemo.widget.ChartContentView;
import com.yuxiansen.chart_industrydemo.widget.ChartViewGroup;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * author  : Mr.Yu
 * Time    : 2019/1/7
 * Email   : yudehai0204@163.com
 * desc    : 画图
 */
public class DrawChartActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks {
    private ChartViewGroup chartViewGroup;
    private ImageView img_left,img_face;
    private TextView tv_title,tv_share_title,tv_author,tv_create_polygon,tv_save_img;
    private RelativeLayout rl_share_top;
    private LinearLayout ll_share_right;
    private ChartModel data;
    private String name, author;
    private long sql_id;
    private final String permissions[] = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void initParams(Bundle param) {
        ChartConstant.init(this);
        try {
            author = param.getString("author", "于先森");
            name = param.getString("name", "") + "产业地图";
            data = (ChartModel) param.getSerializable("data");
        } catch (NullPointerException e) {

        }
    }

    @Override
    protected int getContentView() {
        return R.layout.aty_draw_chart;
    }

    @Override
    protected void initView() {
        chartViewGroup = findViewById(R.id.chartGroup);
        img_left = findViewById(R.id.img_left);
        img_face = findViewById(R.id.img_face);
        tv_title = findViewById(R.id.tv_title);
        tv_share_title = findViewById(R.id.tv_share_title);
        tv_author = findViewById(R.id.tv_author);
        rl_share_top = findViewById(R.id.rl_share_top);
        ll_share_right = findViewById(R.id.ll_share_right);
        tv_create_polygon = findViewById(R.id.tv_create_polygon);
        tv_save_img = findViewById(R.id.tv_save_img);

    }

    @Override
    protected void initData() {
        if (data != null) {
            name = data.getChart_name();
            author = data.getAuthor();
            chartViewGroup.updateData(data);
            sql_id = data.getId();
        }
        tv_title.setText(name);
        tv_share_title.setText(name);
        tv_author.setText("作者:" + author);
        chartViewGroup.setInfo(name, author, sql_id);
    }

    @Override
    protected void initListener() {
        chartViewGroup.setListener(new ChartViewGroup.ChartViewLongClick() {
            @Override
            public void onLongClick(ChartContentView view) {
                if ((int) view.getTag(R.id.chart_content_type) != ChartConstant.TYPE_Z) {
                    ChartEditDialog dialog = new ChartEditDialog();
                    dialog.setChart_View(view);
                    dialog.show(DrawChartActivity.this.getSupportFragmentManager(), "chart");
                }

            }
        });
        img_left.setOnClickListener(this);
        tv_create_polygon.setOnClickListener(this);
        tv_save_img.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        chartViewGroup.saveDataToDatabase();
        super.onDestroy();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_polygon:
                if (ChartConstant.isAllowCheck) {
                    if (chartViewGroup.isCanBulidPolygon()) {
                        ChartConstant.isAllowCheck = false;
                        tv_create_polygon.setText("搭建产业结构");
                        tv_create_polygon.setTextColor(getResources().getColor(R.color.tv_most_black));
                        tv_create_polygon.setBackgroundResource(R.drawable.shape_bg_black_round_stroke);
                        chartViewGroup.resetAllChildFocus();
                    } else {
                        ToastManager.shotToast("至少选择一个Z轴节点");
                    }
                } else {

                    ChartConstant.isAllowCheck = true;
                    tv_create_polygon.setText("完成");
                    tv_create_polygon.setTextColor(getResources().getColor(R.color.white));
                    tv_create_polygon.setBackgroundResource(R.drawable.shape_bg_red_solid);
                    chartViewGroup.removeAllChildFocus();
                }
                break;
            case R.id.tv_save_img:
                if(EasyPermissions.hasPermissions(this,permissions))
                    ChartUtils.SaveChartImage(chartViewGroup, ll_share_right, rl_share_top, callBack);
                else
                    EasyPermissions.requestPermissions(this,"请求存储权限",0,permissions);

                break;
        }
    }

    private ChartUtils.CallBack callBack = new ChartUtils.CallBack() {
        @Override
        public void buildImageSuccess(Bitmap bitmap) {
            String path = ChartUtils.saveBitmapToSdCard(DrawChartActivity.this, bitmap, name);

        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {//同意
        ChartUtils.SaveChartImage(chartViewGroup, ll_share_right, rl_share_top, callBack);

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
