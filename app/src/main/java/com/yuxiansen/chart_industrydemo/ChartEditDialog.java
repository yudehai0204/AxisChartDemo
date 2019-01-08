package com.yuxiansen.chart_industrydemo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yuxiansen.chart_industrydemo.widget.ChartContentView;
import com.yuxiansen.chart_industrydemo.widget.Y_ChartGroup;


public class ChartEditDialog extends DialogFragment {
    TextView tvClear;
    TextView tvDelete;

    private ChartContentView chart_View;

    public void setChart_View(ChartContentView chart_View) {
        this.chart_View = chart_View;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 设置宽度为屏宽、靠近屏幕底部。
        Dialog dialog = getDialog();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chart_edit, container, false);

        tvClear = view.findViewById(R.id.tv_clear);
        tvDelete = view.findViewById(R.id.tv_delete);
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chart_View!=null){
                    if((int)chart_View.getTag(R.id.chart_content_type)!=ChartConstant.TYPE_Z)
                     chart_View.setText("");
                    else
                        ToastManager.shotToast("Z轴控件不可清除");
                }
                dismiss();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(chart_View!=null){
                    if((int)chart_View.getTag(R.id.chart_content_type)!=ChartConstant.TYPE_Z){
                        Y_ChartGroup y_group = (Y_ChartGroup) chart_View.getParent();
                        y_group.deleteView((Integer) chart_View.getTag(R.id.chart_content_id));
                    }else
                        ToastManager.shotToast("Z轴控件不可删除");
                }
                dismiss();
            }
        });
        return view;
    }


}
