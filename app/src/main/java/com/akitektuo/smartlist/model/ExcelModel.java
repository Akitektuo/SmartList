package com.akitektuo.smartlist.model;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by AoD Akitektuo on 31-Aug-17 at 01:59.
 */

public class ExcelModel {

    private String name;
    private String size;
    private Date date;

    public ExcelModel(String name, Long size, Date date) {
        setName(name);
        setSize(processSize(size));
        setDate(date);
    }

    private String processSize(Long size) {
        return new DecimalFormat("0.00#").format((double) size / 1024) + " kb";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
