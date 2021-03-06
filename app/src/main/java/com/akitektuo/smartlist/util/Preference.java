package com.akitektuo.smartlist.util;

import android.content.Context;
import android.content.SharedPreferences;

import static com.akitektuo.smartlist.util.Constant.COLOR_BLUE;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_EUR;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL_WANTED;
import static com.akitektuo.smartlist.util.Constant.KEY_COLOR;
import static com.akitektuo.smartlist.util.Constant.KEY_CREATED;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_DESIGN;
import static com.akitektuo.smartlist.util.Constant.KEY_INITIALIZE;
import static com.akitektuo.smartlist.util.Constant.KEY_NIGHT;
import static com.akitektuo.smartlist.util.Constant.KEY_OFFSET;
import static com.akitektuo.smartlist.util.Constant.KEY_OFFSET_UPDATE;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;
import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.KEY_STATS_RANGE;
import static com.akitektuo.smartlist.util.Constant.KEY_STORAGE;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL_COUNT;
import static com.akitektuo.smartlist.util.Constant.STORAGE_INTERNAL;

/**
 * Created by AoD Akitektuo on 18-Mar-17.
 */

public class Preference {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Preference(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(KEY_INITIALIZE, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    private void savePreferences() {
        editor.commit();
    }

    public void setPreference(String key, boolean bool) {
        editor.putBoolean(key, bool);
        savePreferences();
    }

    public void setPreference(String key, int num) {
        editor.putInt(key, num);
        savePreferences();
    }

    public void setPreference(String key, String string) {
        editor.putString(key, string);
        savePreferences();
    }

    public boolean getPreferenceBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getPreferenceInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public String getPreferenceString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setDefault() {
        setPreference(KEY_CURRENCY, CURRENCY_EUR);
        setPreference(KEY_RECOMMENDATIONS, true);
        setPreference(KEY_AUTO_FILL, true);
        setPreference(KEY_AUTO_FILL_WANTED, true);
        setPreference(KEY_SMART_PRICE, 101);
        setPreference(KEY_COLOR, COLOR_BLUE);
        setPreference(KEY_STORAGE, STORAGE_INTERNAL);
        setPreference(KEY_NIGHT, false);
        setPreference(KEY_TOTAL, false);
        setPreference(KEY_DESIGN, 1);
        setPreference(KEY_TOTAL_COUNT, "0");
        setPreference(KEY_OFFSET, "0");
        setPreference(KEY_OFFSET_UPDATE, false);
        setPreference(KEY_CREATED, true);
        setPreference(KEY_STATS_RANGE, 12);
    }
}
