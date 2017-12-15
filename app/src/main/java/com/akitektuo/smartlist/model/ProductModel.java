package com.akitektuo.smartlist.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Akitektuo on 15.12.2017.
 */

public class ProductModel {

    private String name;
    private double value;
    private List<ItemModel> items;

    public ProductModel(String name, List<ItemModel> items) {
        setName(name);
        setItems(items);
        setValue();
    }

    public double getValue() {
        return value;
    }

    public void setValue() {
        this.value = 0;
        for (ItemModel item : items) {
            this.value += item.getValue();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }
}
