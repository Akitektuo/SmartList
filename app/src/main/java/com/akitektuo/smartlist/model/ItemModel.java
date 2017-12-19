package com.akitektuo.smartlist.model;

import java.util.Calendar;

/**
 * Created by Akitektuo on 15.12.2017.
 */

public class ItemModel {

    private double value;
    private Calendar calendar;

    public ItemModel(double value, Calendar calendar) {
        setValue(value);
        setCalendar(calendar);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
