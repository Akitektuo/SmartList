package com.akitektuo.smartlist.model;

import java.util.Date;

/**
 * Created by Akitektuo on 15.12.2017.
 */

public class ItemModel {

    private int value;
    private Date date;

    public ItemModel(int value, Date date) {
        setValue(value);
        setDate(date);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
