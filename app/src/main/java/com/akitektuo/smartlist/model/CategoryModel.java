package com.akitektuo.smartlist.model;

import java.util.List;

/**
 * Created by Akitektuo on 15.12.2017.
 */

public class CategoryModel {

    private int id;
    private String name;
    private List<ProductModel> products;
    private double value;

    public CategoryModel(int id, String name, List<ProductModel> products) {
        setId(id);
        setName(name);
        setProducts(products);
        setValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductModel> products) {
        this.products = products;
    }

    public double getValue() {
        return value;
    }

    public void setValue() {
        this.value = 0;
        for (ProductModel product : getProducts()) {
            this.value += product.getValue();
        }
    }
}
