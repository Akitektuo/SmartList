package com.akitektuo.smartlist.database;

import android.provider.BaseColumns;

/**
 * Created by Akitektuo on 15.03.2017.
 */

public class DatabaseContract {

    abstract class ListContractEntry implements BaseColumns {
        static final String TABLE_NAME = "list";
        static final String COLUMN_NAME_NUMBER = "number";
        static final String COLUMN_NAME_VALUE = "value";
        static final String COLUMN_NAME_PRODUCT = "product";
        static final String COLUMN_NAME_DATE = "date";
    }

    public abstract class UsageContractEntry implements BaseColumns {
        public static final String COLUMN_NAME_PRODUCTS = "products";
        static final String TABLE_NAME = "usage";
        static final String COLUMN_NAME_PRICES = "prices";
        static final String COLUMN_NAME_CATEGORY_ID = "category_id";
    }

    abstract class CategoryContractEntry implements BaseColumns {
        static final String TABLE_NAME = "category";
        static final String COLUMN_NAME_ID = "id";
        static final String COLUMN_NAME_NAME = "name";
    }

}
