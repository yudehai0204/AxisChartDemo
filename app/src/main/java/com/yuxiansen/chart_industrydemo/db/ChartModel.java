package com.yuxiansen.chart_industrydemo.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

@Table(database = AppDataBase.class)
public class ChartModel extends BaseModel implements Serializable {

    @Column
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String chart_name;

    @Column
    private long time;
    @Column
    private String author;
    @Column
    private String uid;
    @Column
    private String z_data;
    @Column
    private String y0_data;
    @Column
    private String y1_data;
    @Column
    private String y2_data;
    @Column
    private String y3_data;
    @Column
    private String y4_data;


    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }


    public String getChart_name() {
        return chart_name;
    }

    public void setChart_name(String chart_name) {
        this.chart_name = chart_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getZ_data() {
        return z_data;
    }

    public void setZ_data(String z_data) {
        this.z_data = z_data;
    }

    public String getY0_data() {
        return y0_data;
    }

    public void setY0_data(String y0_data) {
        this.y0_data = y0_data;
    }

    public String getY1_data() {
        return y1_data;
    }

    public void setY1_data(String y1_data) {
        this.y1_data = y1_data;
    }

    public String getY2_data() {
        return y2_data;
    }

    public void setY2_data(String y2_data) {
        this.y2_data = y2_data;
    }

    public String getY3_data() {
        return y3_data;
    }

    public void setY3_data(String y3_data) {
        this.y3_data = y3_data;
    }

    public String getY4_data() {
        return y4_data;
    }

    public void setY4_data(String y4_data) {
        this.y4_data = y4_data;
    }
}
