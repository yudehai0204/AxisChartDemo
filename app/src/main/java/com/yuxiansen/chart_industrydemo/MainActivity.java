package com.yuxiansen.chart_industrydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.yuxiansen.chart_industrydemo.base.BaseActivity;
import com.yuxiansen.chart_industrydemo.db.ChartModel;
import com.yuxiansen.chart_industrydemo.db.ChartModel_Table;

/**
 * author  : Mr.Yu
 * Time    : 2018.1.2
 * Email   : yudehai0204@163.com
 * desc    :
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_edit,tv_create;
    private EditText edit_author,edit_kind;

    @Override
    protected void initParams(Bundle bundle) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        tv_edit = findViewById(R.id.tv_edit);
        tv_create = findViewById(R.id.tv_create);
        edit_author = findViewById(R.id.edit_author);
        edit_kind = findViewById(R.id.edit_kind);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        tv_create.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_create:
                goCreateUml();
                break;
            case R.id.tv_edit:
                ChartModel chartModel = new Select().from(ChartModel.class).orderBy(ChartModel_Table.id,false).limit(1).querySingle();
                if(chartModel==null)
                    goCreateUml();
                else {
                    startActivity(new Intent(this,DrawChartActivity.class)
                            .putExtra("name",edit_kind.getText().toString())
                            .putExtra("author",edit_author.getText().toString())
                            .putExtra("data",chartModel)
                    );
                }
                break;
        }
    }

    private void goCreateUml(){
        if(TextUtils.isEmpty(edit_kind.getText().toString())||TextUtils.isEmpty(edit_author.getText().toString())){
            ToastManager.shotToast("请输入信息");
            return;
        }
        startActivity(new Intent(this,DrawChartActivity.class)
                .putExtra("name",edit_kind.getText().toString())
                .putExtra("author",edit_author.getText().toString())

        );
    }
}
