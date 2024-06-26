package com.akitektuo.smartlist.activity.material;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.activity.light.MainActivity;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.util.Preference;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.akitektuo.smartlist.util.Constant.COLOR_BLACK;
import static com.akitektuo.smartlist.util.Constant.COLOR_BLUE;
import static com.akitektuo.smartlist.util.Constant.COLOR_GREEN;
import static com.akitektuo.smartlist.util.Constant.COLOR_ORANGE;
import static com.akitektuo.smartlist.util.Constant.COLOR_RED;
import static com.akitektuo.smartlist.util.Constant.COLOR_YELLOW;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_AED;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_AUD;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CAD;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CHF;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CNY;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_EUR;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_GBP;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_JPY;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_KRW;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_RON;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_RUB;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_SEK;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_USD;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL_WANTED;
import static com.akitektuo.smartlist.util.Constant.KEY_COLOR;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_DESIGN;
import static com.akitektuo.smartlist.util.Constant.KEY_NIGHT;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;
import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.KEY_STORAGE;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL_COUNT;
import static com.akitektuo.smartlist.util.Constant.PRICE_LIMIT;
import static com.akitektuo.smartlist.util.Constant.STORAGE_EXTERNAL;
import static com.akitektuo.smartlist.util.Constant.STORAGE_INTERNAL;

public class SettingsActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private SwitchButton switchRecommendations;
    private SwitchButton switchFill;
    private SwitchButton switchNight;
    private SwitchButton switchTotal;
    private DatabaseHelper database;
    private RelativeLayout layoutHeader;
    private LinearLayout layoutMain;
    private List<ImageView> imageViews;
    private List<TextView> textViews;
    private TextView textHeaderUtilities;
    private TextView textHeaderPersonalization;
    private TextView textHeaderAdvanced;
    private TextView textSettings;
    private TextView textCurrency;
    private TextView textRecommendations;
    private TextView textProducts;
    private TextView textFill;
    private TextView textLimit;
    private TextView textColor;
    private TextView textNight;
    private TextView textExcel;
    private TextView textTotal;
    private TextView textStorage;
    private TextView textDesign;
    private ImageView imageCurrency;
    private ImageView imageRecommendation;
    private ImageView imageProducts;
    private ImageView imageFill;
    private ImageView imageLimit;
    private ImageView imageColor;
    private ImageView imageNight;
    private ImageView imageExcel;
    private ImageView imageTotal;
    private ImageView imageStorage;
    private ImageView imageDesign;
    private Drawable drawableInternal;
    private Drawable drawableExternal;
    private File path;
    private Button buttonBack;
    private Preference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layoutHeader = findViewById(R.id.layout_settings_header);
        layoutMain = findViewById(R.id.layout_main_settings);
        buttonBack = findViewById(R.id.button_back);
        imageViews = new ArrayList<>();
        imageViews.add((ImageView) findViewById(R.id.image_settings_0));
        imageViews.add((ImageView) findViewById(R.id.image_settings_1));
        imageViews.add((ImageView) findViewById(R.id.image_settings_2));
        imageViews.add((ImageView) findViewById(R.id.image_settings_3));
        imageViews.add((ImageView) findViewById(R.id.image_settings_4));
        imageViews.add((ImageView) findViewById(R.id.image_settings_5));
        imageViews.add((ImageView) findViewById(R.id.image_settings_6));
        imageViews.add((ImageView) findViewById(R.id.image_settings_7));
        textViews = new ArrayList<>();
        textViews.add((TextView) findViewById(R.id.text_settings_0));
        textViews.add((TextView) findViewById(R.id.text_settings_1));
        textViews.add((TextView) findViewById(R.id.text_settings_2));
        textViews.add((TextView) findViewById(R.id.text_settings_3));
        textViews.add((TextView) findViewById(R.id.text_settings_4));
        textViews.add((TextView) findViewById(R.id.text_settings_5));
        textViews.add((TextView) findViewById(R.id.text_settings_6));
        textViews.add((TextView) findViewById(R.id.text_settings_7));
        textViews.add((TextView) findViewById(R.id.text_settings_8));
        textViews.add((TextView) findViewById(R.id.text_settings_9));
        textViews.add((TextView) findViewById(R.id.text_settings_10));
        textHeaderUtilities = findViewById(R.id.text_settings_utilities);
        textHeaderPersonalization = findViewById(R.id.text_settings_personalization);
        textHeaderAdvanced = findViewById(R.id.text_settings_advanced);
        textSettings = findViewById(R.id.text_title_settings);
        textCurrency = findViewById(R.id.text_settings_currency);
        textRecommendations = findViewById(R.id.text_settings_recommendations);
        textProducts = findViewById(R.id.text_settings_products);
        textFill = findViewById(R.id.text_settings_fill);
        textLimit = findViewById(R.id.text_settings_limit);
        textColor = findViewById(R.id.text_settings_color);
        textNight = findViewById(R.id.text_settings_night);
        textStorage = findViewById(R.id.text_settings_storage);
        textExcel = findViewById(R.id.text_settings_excel);
        textTotal = findViewById(R.id.text_settings_total);
        textDesign = findViewById(R.id.text_settings_design);
        imageCurrency = findViewById(R.id.image_settings_currency);
        imageRecommendation = findViewById(R.id.image_settings_recommendations);
        imageProducts = findViewById(R.id.image_settings_products);
        imageFill = findViewById(R.id.image_settings_fill);
        imageLimit = findViewById(R.id.image_settings_limit);
        imageColor = findViewById(R.id.image_settings_color);
        imageNight = findViewById(R.id.image_settings_night);
        imageStorage = findViewById(R.id.image_settings_storage);
        imageExcel = findViewById(R.id.image_settings_excel);
        imageTotal = findViewById(R.id.image_settings_total);
        imageDesign = findViewById(R.id.image_settings_design);
        LinearLayout layoutColor = findViewById(R.id.layout_color);
        database = new DatabaseHelper(this);
        preference = new Preference(this);
        buttonBack.setOnClickListener(this);
        findViewById(R.id.layout_currency).setOnClickListener(this);
        switchRecommendations = findViewById(R.id.switch_settings_recommendations);
        switchRecommendations.setChecked(preference.getPreferenceBoolean(KEY_RECOMMENDATIONS));
        switchRecommendations.setOnCheckedChangeListener(this);
        findViewById(R.id.layout_products).setOnClickListener(this);
        switchFill = findViewById(R.id.switch_settings_fill);
        switchFill.setChecked(preference.getPreferenceBoolean(KEY_AUTO_FILL));
        switchFill.setOnCheckedChangeListener(this);
        findViewById(R.id.layout_limit).setOnClickListener(this);
        switchNight = findViewById(R.id.switch_settings_night);
        switchNight.setChecked(preference.getPreferenceBoolean(KEY_NIGHT));
        switchNight.setOnCheckedChangeListener(this);
        layoutColor.setOnClickListener(this);
        findViewById(R.id.layout_storage).setOnClickListener(this);
        findViewById(R.id.layout_excel).setOnClickListener(this);
        switchTotal = findViewById(R.id.switch_settings_total);
        switchTotal.setChecked(preference.getPreferenceBoolean(KEY_TOTAL));
        switchTotal.setOnCheckedChangeListener(this);
        findViewById(R.id.layout_design).setOnClickListener(this);
        refreshForColor(preference.getPreferenceString(KEY_COLOR));
        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ListActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                startActivity(new Intent(this, ListActivity.class));
                finish();
                break;
            case R.id.layout_currency:
                AlertDialog.Builder builderCurrency = new AlertDialog.Builder(this);
                builderCurrency.setTitle("Select currency");
                builderCurrency.setItems(R.array.currency, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String currency = "";
                        switch (i) {
                            case 0:
                                currency = CURRENCY_RON;
                                break;
                            case 1:
                                currency = CURRENCY_USD;
                                break;
                            case 2:
                                currency = CURRENCY_EUR;
                                break;
                            case 3:
                                currency = CURRENCY_JPY;
                                break;
                            case 4:
                                currency = CURRENCY_GBP;
                                break;
                            case 5:
                                currency = CURRENCY_AUD;
                                break;
                            case 6:
                                currency = CURRENCY_CAD;
                                break;
                            case 7:
                                currency = CURRENCY_CHF;
                                break;
                            case 8:
                                currency = CURRENCY_CNY;
                                break;
                            case 9:
                                currency = CURRENCY_RUB;
                                break;
                            case 10:
                                currency = CURRENCY_KRW;
                                break;
                            case 11:
                                currency = CURRENCY_SEK;
                                break;
                            case 12:
                                currency = CURRENCY_AED;
                                break;
                        }
                        preference.setPreference(KEY_CURRENCY, currency);
                        Toast.makeText(getApplicationContext(), "Currency set to " + currency + ".", Toast.LENGTH_SHORT).show();
                    }
                });
                builderCurrency.setNeutralButton("Cancel", null);
                AlertDialog alertDialogCurrency = builderCurrency.create();
                alertDialogCurrency.show();
                break;
            case R.id.layout_products:
                AlertDialog.Builder builderRecommendations = new AlertDialog.Builder(this);
                builderRecommendations.setTitle("Select product to delete");
                List<String> listProducts = new ArrayList<>();
                Cursor cursorProducts = database.getUsage();
                if (cursorProducts.moveToFirst()) {
                    do {
                        listProducts.add(cursorProducts.getString(0));
                    } while (cursorProducts.moveToNext());
                }
                final String[] arrayProducts = listProducts.toArray(new String[listProducts.size()]);
                builderRecommendations.setItems(arrayProducts, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.deleteUsage(arrayProducts[i]);
                        Toast.makeText(getApplicationContext(), "Successfully deleted " + arrayProducts[i] + " product.", Toast.LENGTH_SHORT).show();
                    }
                });
                builderRecommendations.setNeutralButton("Cancel", null);
                AlertDialog alertDialogRecommendations = builderRecommendations.create();
                alertDialogRecommendations.show();
                break;
            case R.id.layout_limit:
                AlertDialog.Builder builderFill = new AlertDialog.Builder(this);
                View viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_fill, null);
                final TextView textDialog = viewDialog.findViewById(R.id.text_dialog_limit);
                final EditText editLimit = viewDialog.findViewById(R.id.edit_dialog_limit);
                switch (preference.getPreferenceString(KEY_COLOR)) {
                    case COLOR_BLUE:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlue));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlue));
                        break;
                    case COLOR_YELLOW:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryYellow));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryYellow));
                        break;
                    case COLOR_RED:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryRed));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryRed));
                        break;
                    case COLOR_GREEN:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryGreen));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryGreen));
                        break;
                    case COLOR_ORANGE:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryOrange));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryOrange));
                        break;
                    case COLOR_BLACK:
                        textDialog.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlack));
                        editLimit.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryBlack));
                        break;
                }
                editLimit.setText(String.valueOf(preference.getPreferenceInt(KEY_SMART_PRICE) - 1));
                builderFill.setView(viewDialog);
                builderFill.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String limit = editLimit.getText().toString();
                        if (limit.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Set the limit.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Integer.parseInt(limit) < PRICE_LIMIT) {
                                preference.setPreference(KEY_SMART_PRICE, Integer.parseInt(limit) + 1);
                            } else {
                                Toast.makeText(getApplicationContext(), "Limit too high.", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getApplicationContext(), "Limit set to " + limit + ".", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builderFill.setNeutralButton("Cancel", null);
                builderFill.show();
                break;
            case R.id.layout_color:
                AlertDialog.Builder builderColor = new AlertDialog.Builder(this);
                builderColor.setTitle("Select color");
                builderColor.setItems(R.array.color, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String color = null;
                        switch (i) {
                            case 0:
                                color = COLOR_BLUE;
                                break;
                            case 1:
                                color = COLOR_YELLOW;
                                break;
                            case 2:
                                color = COLOR_RED;
                                break;
                            case 3:
                                color = COLOR_GREEN;
                                break;
                            case 4:
                                color = COLOR_ORANGE;
                                break;
                            case 5:
                                color = COLOR_BLACK;
                                break;
                        }
                        if (preference.getPreferenceBoolean(KEY_NIGHT) && i == 5) {
                            switchNight.setChecked(false);
                            preference.setPreference(KEY_NIGHT, switchNight.isChecked());
                            Toast.makeText(getBaseContext(), "Switched to day mode", Toast.LENGTH_SHORT).show();
                        }
                        refreshForColor(color);
                        preference.setPreference(KEY_COLOR, color);
                        Toast.makeText(getApplicationContext(), "Color set to " + color + ".", Toast.LENGTH_SHORT).show();
                    }
                });
                builderColor.setNeutralButton("Cancel", null);
                AlertDialog alertDialogColor = builderColor.create();
                alertDialogColor.show();
                break;
            case R.id.layout_storage:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String storageLocation = "Storage switched to %1$s";
                    switch (preference.getPreferenceInt(KEY_STORAGE)) {
                        case STORAGE_INTERNAL:
                            preference.setPreference(KEY_STORAGE, STORAGE_EXTERNAL);
                            storageLocation = String.format(storageLocation, "external storage");
                            break;
                        case STORAGE_EXTERNAL:
                            preference.setPreference(KEY_STORAGE, STORAGE_INTERNAL);
                            storageLocation = String.format(storageLocation, "internal storage");
                            break;
                    }
                    Toast.makeText(this, storageLocation, Toast.LENGTH_SHORT).show();
                    changeStorageSettings();
                } else {
                    Toast.makeText(this, "Micro SD card not detected", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_excel:
                exportToExcel(database.getList());
                break;
            case R.id.layout_design:
                preference.setPreference(KEY_DESIGN, 1);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }

    private void changeStorageSettings() {
        switch (preference.getPreferenceInt(KEY_STORAGE)) {
            case STORAGE_INTERNAL:
                imageStorage.setImageDrawable(drawableInternal);
                path = Environment.getExternalStorageDirectory();
                break;
            case STORAGE_EXTERNAL:
                imageStorage.setImageDrawable(drawableExternal);
                path = Environment.getExternalStorageDirectory();
                break;
        }
    }

    private void refreshForColor(String color) {
        switch (color) {
            case COLOR_BLUE:
                setColor(R.style.Theme_Blue, R.color.colorPrimaryBlue, R.color.colorPrimaryDarkBlue);
                imageCurrency.setImageDrawable(getResources().getDrawable(R.drawable.currency_blue));
                imageRecommendation.setImageDrawable(getResources().getDrawable(R.drawable.recommendation_blue));
                imageProducts.setImageDrawable(getResources().getDrawable(R.drawable.product_blue));
                imageFill.setImageDrawable(getResources().getDrawable(R.drawable.fill_blue));
                imageLimit.setImageDrawable(getResources().getDrawable(R.drawable.limit_blue));
                imageColor.setImageDrawable(getResources().getDrawable(R.drawable.color_blue));
                imageNight.setImageDrawable(getResources().getDrawable(R.drawable.night_blue));
                imageExcel.setImageDrawable(getResources().getDrawable(R.drawable.excel_blue));
                imageTotal.setImageDrawable(getResources().getDrawable(R.drawable.total_blue));
                imageDesign.setImageDrawable(getResources().getDrawable(R.drawable.design_blue));
                drawableInternal = getResources().getDrawable(R.drawable.internal_storage_blue);
                drawableExternal = getResources().getDrawable(R.drawable.external_storage_blue);
                break;
            case COLOR_YELLOW:
                setColor(R.style.Theme_Yellow, R.color.colorPrimaryYellow, R.color.colorPrimaryDarkYellow);
                imageCurrency.setImageDrawable(getResources().getDrawable(R.drawable.currency_yellow));
                imageRecommendation.setImageDrawable(getResources().getDrawable(R.drawable.recommendation_yellow));
                imageProducts.setImageDrawable(getResources().getDrawable(R.drawable.product_yellow));
                imageFill.setImageDrawable(getResources().getDrawable(R.drawable.fill_yellow));
                imageLimit.setImageDrawable(getResources().getDrawable(R.drawable.limit_yellow));
                imageColor.setImageDrawable(getResources().getDrawable(R.drawable.color_yellow));
                imageNight.setImageDrawable(getResources().getDrawable(R.drawable.night_yellow));
                imageExcel.setImageDrawable(getResources().getDrawable(R.drawable.excel_yellow));
                imageTotal.setImageDrawable(getResources().getDrawable(R.drawable.total_yellow));
                imageDesign.setImageDrawable(getResources().getDrawable(R.drawable.design_yellow));
                drawableInternal = getResources().getDrawable(R.drawable.internal_storage_yellow);
                drawableExternal = getResources().getDrawable(R.drawable.external_storage_yellow);
                break;
            case COLOR_RED:
                setColor(R.style.Theme_Red, R.color.colorPrimaryRed, R.color.colorPrimaryDarkRed);
                imageCurrency.setImageDrawable(getResources().getDrawable(R.drawable.currency_red));
                imageRecommendation.setImageDrawable(getResources().getDrawable(R.drawable.recommendation_red));
                imageProducts.setImageDrawable(getResources().getDrawable(R.drawable.product_red));
                imageFill.setImageDrawable(getResources().getDrawable(R.drawable.fill_red));
                imageLimit.setImageDrawable(getResources().getDrawable(R.drawable.limit_red));
                imageColor.setImageDrawable(getResources().getDrawable(R.drawable.color_red));
                imageNight.setImageDrawable(getResources().getDrawable(R.drawable.night_red));
                imageExcel.setImageDrawable(getResources().getDrawable(R.drawable.excel_red));
                imageTotal.setImageDrawable(getResources().getDrawable(R.drawable.total_red));
                imageDesign.setImageDrawable(getResources().getDrawable(R.drawable.design_red));
                drawableInternal = getResources().getDrawable(R.drawable.internal_storage_red);
                drawableExternal = getResources().getDrawable(R.drawable.external_storage_red);
                break;
            case COLOR_GREEN:
                setColor(R.style.Theme_Green, R.color.colorPrimaryGreen, R.color.colorPrimaryDarkGreen);
                imageCurrency.setImageDrawable(getResources().getDrawable(R.drawable.currency_green));
                imageRecommendation.setImageDrawable(getResources().getDrawable(R.drawable.recommendation_green));
                imageProducts.setImageDrawable(getResources().getDrawable(R.drawable.product_green));
                imageFill.setImageDrawable(getResources().getDrawable(R.drawable.fill_green));
                imageLimit.setImageDrawable(getResources().getDrawable(R.drawable.limit_green));
                imageColor.setImageDrawable(getResources().getDrawable(R.drawable.color_green));
                imageNight.setImageDrawable(getResources().getDrawable(R.drawable.night_green));
                imageExcel.setImageDrawable(getResources().getDrawable(R.drawable.excel_green));
                imageTotal.setImageDrawable(getResources().getDrawable(R.drawable.total_green));
                imageDesign.setImageDrawable(getResources().getDrawable(R.drawable.design_green));
                drawableInternal = getResources().getDrawable(R.drawable.internal_storage_green);
                drawableExternal = getResources().getDrawable(R.drawable.external_storage_green);
                break;
            case COLOR_ORANGE:
                setColor(R.style.Theme_Orange, R.color.colorPrimaryOrange, R.color.colorPrimaryDarkOrange);
                imageCurrency.setImageDrawable(getResources().getDrawable(R.drawable.currency_orange));
                imageRecommendation.setImageDrawable(getResources().getDrawable(R.drawable.recommendation_orange));
                imageProducts.setImageDrawable(getResources().getDrawable(R.drawable.product_orange));
                imageFill.setImageDrawable(getResources().getDrawable(R.drawable.fill_orange));
                imageLimit.setImageDrawable(getResources().getDrawable(R.drawable.limit_orange));
                imageColor.setImageDrawable(getResources().getDrawable(R.drawable.color_orange));
                imageNight.setImageDrawable(getResources().getDrawable(R.drawable.night_orange));
                imageExcel.setImageDrawable(getResources().getDrawable(R.drawable.excel_orange));
                imageTotal.setImageDrawable(getResources().getDrawable(R.drawable.total_orange));
                imageDesign.setImageDrawable(getResources().getDrawable(R.drawable.design_orange));
                drawableInternal = getResources().getDrawable(R.drawable.internal_storage_orange);
                drawableExternal = getResources().getDrawable(R.drawable.external_storage_orange);
                break;
            case COLOR_BLACK:
                setColor(R.style.Theme_Black, R.color.colorPrimaryBlack, R.color.colorPrimaryDarkBlack);
                imageCurrency.setImageDrawable(getResources().getDrawable(R.drawable.currency_black));
                imageRecommendation.setImageDrawable(getResources().getDrawable(R.drawable.recommendation_black));
                imageProducts.setImageDrawable(getResources().getDrawable(R.drawable.product_black));
                imageFill.setImageDrawable(getResources().getDrawable(R.drawable.fill_black));
                imageLimit.setImageDrawable(getResources().getDrawable(R.drawable.limit_black));
                imageColor.setImageDrawable(getResources().getDrawable(R.drawable.color_black));
                imageNight.setImageDrawable(getResources().getDrawable(R.drawable.night_black));
                imageExcel.setImageDrawable(getResources().getDrawable(R.drawable.excel_black));
                imageTotal.setImageDrawable(getResources().getDrawable(R.drawable.total_black));
                imageDesign.setImageDrawable(getResources().getDrawable(R.drawable.design_black));
                drawableInternal = getResources().getDrawable(R.drawable.internal_storage_black);
                drawableExternal = getResources().getDrawable(R.drawable.external_storage_black);
                break;
        }
        changeStorageSettings();
        if (preference.getPreferenceBoolean(KEY_NIGHT)) {
            buttonBack.setBackground(getResources().getDrawable(R.drawable.back_black));
            layoutMain.setBackgroundColor(getResources().getColor(R.color.colorPrimaryBlack));
            textSettings.setTextColor(getResources().getColor(R.color.colorPrimaryDarkBlack));
            for (TextView textView : textViews) {
                textView.setTextColor(getResources().getColor(R.color.trackBasic));
            }
            textHeaderUtilities.setTextColor(getResources().getColor(R.color.colorPrimaryDarkBlack));
            textHeaderPersonalization.setTextColor(getResources().getColor(R.color.colorPrimaryDarkBlack));
            textHeaderAdvanced.setTextColor(getResources().getColor(R.color.colorPrimaryDarkBlack));
        } else {
            buttonBack.setBackground(getResources().getDrawable(R.drawable.back_white));
            layoutMain.setBackgroundColor(getResources().getColor(R.color.background));
            textSettings.setTextColor(getResources().getColor(R.color.white));
            for (TextView textView : textViews) {
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
            }
            textHeaderUtilities.setTextColor(getResources().getColor(R.color.white));
            textHeaderPersonalization.setTextColor(getResources().getColor(R.color.white));
            textHeaderAdvanced.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void setColor(int theme, int colorPrimary, int colorPrimaryDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setTheme(theme);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(colorPrimaryDark));
        }
        layoutHeader.setBackgroundColor(getResources().getColor(colorPrimary));
        for (ImageView x : imageViews) {
            x.setBackgroundColor(getResources().getColor(colorPrimary));
        }
        textCurrency.setTextColor(getResources().getColor(colorPrimary));
        textRecommendations.setTextColor(getResources().getColor(colorPrimary));
        textProducts.setTextColor(getResources().getColor(colorPrimary));
        textFill.setTextColor(getResources().getColor(colorPrimary));
        textLimit.setTextColor(getResources().getColor(colorPrimary));
        textColor.setTextColor(getResources().getColor(colorPrimary));
        textNight.setTextColor(getResources().getColor(colorPrimary));
        textStorage.setTextColor(getResources().getColor(colorPrimary));
        textExcel.setTextColor(getResources().getColor(colorPrimary));
        textTotal.setTextColor(getResources().getColor(colorPrimary));
        textDesign.setTextColor(getResources().getColor(colorPrimary));
        switchRecommendations.setTintColor(getResources().getColor(colorPrimary));
        switchFill.setTintColor(getResources().getColor(colorPrimary));
        switchNight.setTintColor(getResources().getColor(colorPrimary));
        switchTotal.setTintColor(getResources().getColor(colorPrimary));
        textHeaderUtilities.setBackgroundColor(getResources().getColor(colorPrimary));
        textHeaderPersonalization.setBackgroundColor(getResources().getColor(colorPrimary));
        textHeaderAdvanced.setBackgroundColor(getResources().getColor(colorPrimary));
    }

    private void exportToExcel(Cursor cursor) {
        File file = new File(path + File.separator + "SmartList", "SmartList_" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".xls");
        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                Toast.makeText(getApplicationContext(), "Smart list failed to generate the file, please check the permissions or switch to internal storage.", Toast.LENGTH_SHORT).show();
            }
        }

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("NewList", 0);

            try {
                Toast.makeText(getApplicationContext(), "Excel generated successfully.", Toast.LENGTH_SHORT).show();
                int position = 0;
                sheet.addCell(new Label(0, 0, "Price")); // column and row
                sheet.addCell(new Label(1, 0, "Product"));
                sheet.addCell(new Label(2, 0, "Time added"));
                if (cursor.moveToFirst()) {
                    do {
                        int i = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, i, cursor.getString(1)));
                        sheet.addCell(new Label(1, i, cursor.getString(2)));
                        sheet.addCell(new Label(2, i, cursor.getString(3)));
                        position = i;
                    } while (cursor.moveToNext());
                }
                //closing cursor
                cursor.close();
                if (preference.getPreferenceBoolean(KEY_TOTAL)) {
                    position += 2;
                    sheet.addCell(new Label(0, position, "Total"));
                    sheet.addCell(new Label(1, position, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)))));
                }
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builderOpenXls = new AlertDialog.Builder(this);
        builderOpenXls.setTitle("Open Excel File");
        builderOpenXls.setMessage("Are you sure you want to open the file now? You can see it in Internal Storage -> SmartList - > SmartList.xls");
        builderOpenXls.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openGeneratedFile();
            }
        });
        builderOpenXls.setNegativeButton("Cancel", null);
        AlertDialog dialogOpenXls = builderOpenXls.create();
        dialogOpenXls.show();
    }

    private void openGeneratedFile() {
        File file = new File(path + File.separator + "SmartList", "SmartList_" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".xls");
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        intent.setDataAndType(FileProvider.getUriForFile(this, getPackageName() + ".com.akitektuo.smartlist", file), "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_settings_recommendations:
                preference.setPreference(KEY_RECOMMENDATIONS, isChecked);
                if (isChecked && preference.getPreferenceBoolean(KEY_AUTO_FILL_WANTED)) {
                    switchFill.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                } else if (!isChecked) {
                    switchFill.setChecked(false);
                    preference.setPreference(KEY_AUTO_FILL, false);
                }
                break;
            case R.id.switch_settings_fill:
                preference.setPreference(KEY_AUTO_FILL, isChecked);
                if (isChecked && !preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
                    switchRecommendations.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                    preference.setPreference(KEY_AUTO_FILL_WANTED, true);
                } else if (isChecked) {
                    preference.setPreference(KEY_AUTO_FILL_WANTED, true);
                } else if (preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
                    preference.setPreference(KEY_AUTO_FILL_WANTED, false);
                }
                break;
            case R.id.switch_settings_night:
                if (preference.getPreferenceString(KEY_COLOR).equals("black")) {
                    switchNight.setChecked(false);
                    Toast.makeText(this, "Please change the color for night mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                preference.setPreference(KEY_NIGHT, isChecked);
                refreshForColor(preference.getPreferenceString(KEY_COLOR));
                break;
            case R.id.switch_settings_total:
                preference.setPreference(KEY_TOTAL, isChecked);
                break;
        }
    }
}
