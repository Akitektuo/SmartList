package com.akitektuo.smartlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akitektuo.smartlist.util.Preference;

import java.text.DecimalFormat;

import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.PRICE_LIMIT;
import static com.akitektuo.smartlist.util.Constant.handler;

/**
 * Created by Akitektuo on 15.03.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_LIST = "CREATE TABLE IF NOT EXISTS " + DatabaseContract.ListContractEntry.TABLE_NAME + " (" +
            DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " NUMBER," +
            DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE + " TEXT," +
            DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT + " TEXT," +
            DatabaseContract.ListContractEntry.COLUMN_NAME_DATE + " TEXT" + ");";

    private static final String DATABASE_CREATE_USAGE = "CREATE TABLE IF NOT EXISTS " + DatabaseContract.UsageContractEntry.TABLE_NAME + " (" +
            DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " TEXT," +
            DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES + " TEXT" + ");";

    private static final String DATABASE_ALTER_USAGE_CATEGORY = "ALTER TABLE " + DatabaseContract.UsageContractEntry.TABLE_NAME +
            " ADD COLUMN " + DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID + " NUMBER;";

    private static final String DATABASE_CREATE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + DatabaseContract.CategoryContractEntry.TABLE_NAME + " (" +
            DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID + " NUMBER," +
            DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME + " TEXT);";

    private Preference preference;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        preference = new Preference(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_LIST);
        sqLiteDatabase.execSQL(DATABASE_CREATE_USAGE);
        updateToVersion2(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        System.out.println("------------------- UPDATE");
        switch (oldVersion) {
            case 1:
                updateToVersion2(sqLiteDatabase);
        }
    }

    private void updateToVersion2(SQLiteDatabase db) {
        db.execSQL(DATABASE_ALTER_USAGE_CATEGORY);
        db.execSQL(DATABASE_CREATE_CATEGORY);
    }

    public void addList(int number, String value, String product, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER, number);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE, value);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT, product);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_DATE, date);
        getWritableDatabase().insert(DatabaseContract.ListContractEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getList() {
        String[] list = {DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER,
                DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE,
                DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT,
                DatabaseContract.ListContractEntry.COLUMN_NAME_DATE};
        return getReadableDatabase().query(DatabaseContract.ListContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    public int getListNumberNew() {
        int number = 0;
        Cursor cursor = getList();
        if (cursor.moveToLast()) {
            number = cursor.getInt(0);
        }
        cursor.close();
        return ++number;
    }

    public Cursor getListForNumber(int number) {
        String[] results = {DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER,
                DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE,
                DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT,
                DatabaseContract.ListContractEntry.COLUMN_NAME_DATE};
        String selection = DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(number)};
        return getReadableDatabase().query(DatabaseContract.ListContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
    }

    public void deleteList(int number) {
        String selection = DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(number)};
        getWritableDatabase().delete(DatabaseContract.ListContractEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateList(int oldNumber, int number, String value, String product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER, number);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_VALUE, value);
        contentValues.put(DatabaseContract.ListContractEntry.COLUMN_NAME_PRODUCT, product);
        String selection = DatabaseContract.ListContractEntry.COLUMN_NAME_NUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(oldNumber)};
        getWritableDatabase().update(DatabaseContract.ListContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    private void addUsage(String products, String prices) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS, products);
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES, prices);
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID, 0);
        getWritableDatabase().insert(DatabaseContract.UsageContractEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getUsage() {
        String[] list = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS,
                DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES,
                DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID};
        return getReadableDatabase().query(DatabaseContract.UsageContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    private String getPricesForProducts(String products) {
        String[] results = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES};
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        Cursor cursor = getReadableDatabase().query(DatabaseContract.UsageContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }

    private boolean isProduct(String products) {
        String[] results = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS};
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        Cursor cursor = getReadableDatabase().query(DatabaseContract.UsageContractEntry.TABLE_NAME, results, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        cursor.close();
        return false;
    }

    public void updateUsage(String products, int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID, id);
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        getWritableDatabase().update(DatabaseContract.UsageContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public void updateUsage(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID, 0);
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        getWritableDatabase().update(DatabaseContract.UsageContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    private void updateUsage(String products, String prices) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES, prices);
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {products};
        getWritableDatabase().update(DatabaseContract.UsageContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public int getCommonPriceForProduct(String product) {
        String[] pricesRaw = getPricesForProducts(product).split("_");
        int[] prices = new int[preference.getPreferenceInt(KEY_SMART_PRICE)];
        int priceMax = Integer.MIN_VALUE, res = 0;
        for (int i = 0; i < prices.length; i++) {
            prices[i] = Integer.parseInt(pricesRaw[i]);
            priceMax = Math.max(priceMax, prices[i]);
            if (priceMax == prices[i]) {
                res = i;
                if (priceMax == 0) {
                    res = 0;
                }
            }
        }
        return res;
    }

    private void addProduct(String product, String price) {
        int roundedPrice = (int) Math.ceil(Double.parseDouble(price));
        if (roundedPrice > 0) {
            int[] pricesGenerate = new int[PRICE_LIMIT];
            String stringBuilder = "";
            for (int i = 0; i < pricesGenerate.length; i++) {
                pricesGenerate[i] = 0;
                if (i == roundedPrice) {
                    pricesGenerate[i]++;
                }
                stringBuilder = stringBuilder + new DecimalFormat("0.#").format(pricesGenerate[i]) + "_";
            }
            addUsage(product, stringBuilder.substring(0, stringBuilder.length() - 1));
        }
    }

    public void updatePrices(final String products, final String price) {
        final int roundedPrice = (int) Math.ceil(Double.parseDouble(price));
        if (roundedPrice > 0) {
            handler.post(new Runnable() {
                public void run() {
                    if (isProduct(products)) {
                        String stringBuilder = "";
                        String[] pricesRaw = getPricesForProducts(products).split("_");
                        int[] pricesExisting = new int[preference.getPreferenceInt(KEY_SMART_PRICE)];
                        for (int i = 0; i < pricesExisting.length; i++) {
                            pricesExisting[i] = Integer.parseInt(pricesRaw[i]);
                            if (i == roundedPrice) {
                                pricesExisting[i]++;
                            }
                            stringBuilder = stringBuilder + pricesExisting[i] + "_";
                        }
                        updateUsage(products, stringBuilder);
                    } else {
                        addProduct(products, price);
                    }
                }
            });
        }
    }

    public void deleteUsage(String product) {
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " LIKE ?";
        String[] selectionArgs = {product};
        getWritableDatabase().delete(DatabaseContract.UsageContractEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void addCategory(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID, getCategoryId());
        contentValues.put(DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME, name);
        getWritableDatabase().insert(DatabaseContract.CategoryContractEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor getCategory() {
        String[] list = {DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID,
                DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME};
        return getReadableDatabase().query(DatabaseContract.CategoryContractEntry.TABLE_NAME, list, null, null, null, null, null);
    }

    public Cursor getCategoryAsc() {
        String[] list = {DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID,
                DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME};
        return getReadableDatabase().query(DatabaseContract.CategoryContractEntry.TABLE_NAME, list, null, null, null, null, DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME + " ASC");
    }

    public void deleteCategory(int id) {
        String selection = DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        getWritableDatabase().delete(DatabaseContract.CategoryContractEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateCategory(int id, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME, name);
        String selection = DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        getWritableDatabase().update(DatabaseContract.CategoryContractEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    public int getCategoryId() {
        int number = 0;
        Cursor cursor = getCategory();
        if (cursor.moveToLast()) {
            number = cursor.getInt(0) + 1;
        }
        cursor.close();
        return number;
    }

    public Cursor getUsageAsc(int categoryId) {
        String[] list = {DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS,
                DatabaseContract.UsageContractEntry.COLUMN_NAME_PRICES};
        String selection = DatabaseContract.UsageContractEntry.COLUMN_NAME_CATEGORY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        return getReadableDatabase().query(DatabaseContract.UsageContractEntry.TABLE_NAME, list, selection, selectionArgs, null, null, DatabaseContract.UsageContractEntry.COLUMN_NAME_PRODUCTS + " ASC");
    }

    public Cursor getCategory(String name) {
        String[] list = {DatabaseContract.CategoryContractEntry.COLUMN_NAME_ID,
                DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME};
        String selection = DatabaseContract.CategoryContractEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = {name};
        return getReadableDatabase().query(DatabaseContract.CategoryContractEntry.TABLE_NAME, list, selection, selectionArgs, null, null, null);
    }

}
