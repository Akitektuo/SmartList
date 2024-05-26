package com.akitektuo.smartlist.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.util.Preference;

import java.util.ArrayList;
import java.util.List;

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
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class TuneFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch switchRecommendations;
    private Switch switchFill;
    private int layoutId;
    private Preference preference;
    private DatabaseHelper database;

    public TuneFragment() {
        layoutId = R.layout.fragment_tune;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("layout_id", layoutId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preference = new Preference(getContext());
        database = new DatabaseHelper(getContext());

        switchRecommendations = getActivity().findViewById(R.id.switch_light_recommendations);
        switchFill = getActivity().findViewById(R.id.switch_light_fill);

        switchRecommendations.setChecked(preference.getPreferenceBoolean(KEY_RECOMMENDATIONS));
        switchFill.setChecked(preference.getPreferenceBoolean(KEY_AUTO_FILL));

        switchRecommendations.setOnCheckedChangeListener(this);
        switchFill.setOnCheckedChangeListener(this);

        getActivity().findViewById(R.id.layout_light_tune_recommendations).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_tune_fill).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_currency).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_categories).setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layout_id");
        }
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_light_recommendations:
                preference.setPreference(KEY_RECOMMENDATIONS, isChecked);
                if (isChecked && preference.getPreferenceBoolean(KEY_AUTO_FILL_WANTED)) {
                    switchFill.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                } else if (!isChecked) {
                    switchFill.setChecked(false);
                    preference.setPreference(KEY_AUTO_FILL, false);
                }
                break;
            case R.id.switch_light_fill:
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
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_light_currency:
                changeCurrency();
                break;
            case R.id.layout_light_tune_recommendations:
                switchRecommendations.setChecked(!switchRecommendations.isChecked());
                break;
            case R.id.layout_light_tune_fill:
                switchFill.setChecked(!switchFill.isChecked());
                break;
            case R.id.layout_light_categories:
                manageCategories();
                break;
        }
    }

    private void changeCurrency() {
        AlertDialog.Builder builderCurrency = new AlertDialog.Builder(getContext());
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
            }
        });
        builderCurrency.setNeutralButton("Cancel", null);
        AlertDialog alertDialogCurrency = builderCurrency.create();
        alertDialogCurrency.show();
    }

    private void manageCategories() {

        // WARNING: A lot of code

        // Dialog to pick a category
        AlertDialog.Builder builderCategories = new AlertDialog.Builder(getContext());
        builderCategories.setTitle("Select a category to modify");
        Cursor cursor = database.getCategoryAsc();
        final List<String> listCategories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                listCategories.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        builderCategories.setItems(listCategories.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                // Dialog to select products to move or edit option
                final int position = i;
                Cursor cursor = database.getCategory(listCategories.get(position));
                int id = 0;
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
                final int categoryId = id;
                cursor.close();
                cursor = database.getUsageAsc(categoryId);
                final List<String> listProducts = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        listProducts.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                final List<Integer> selectedItems = new ArrayList<>();
                final AlertDialog.Builder builderProducts = new AlertDialog.Builder(getContext());
                if (listProducts.size() == 0) {
                    builderProducts.setTitle("No products found");
                } else {
                    builderProducts.setTitle("Select products to move");
                }
                builderProducts.setMultiChoiceItems(listProducts.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            selectedItems.add(i);
                        } else if (selectedItems.contains(i)) {
                            selectedItems.remove(Integer.valueOf(i));
                        }
                    }
                });
                builderProducts.setNeutralButton("Cancel", null);
                if (listProducts.size() == 0) {
                    builderProducts.setTitle("No products found");
                } else {
                    builderProducts.setTitle("Select products to move");
                    builderProducts.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (selectedItems.size() != 0) {

                                // Dialog to select target
                                AlertDialog.Builder builderCurrency = new AlertDialog.Builder(getContext());
                                builderCurrency.setTitle("Move to category");
                                listCategories.remove(position);
                                builderCurrency.setItems(listCategories.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Cursor cursor = database.getCategory(listCategories.get(i));
                                        int categoryId = 0;
                                        if (cursor.moveToFirst()) {
                                            categoryId = cursor.getInt(0);
                                        }
                                        cursor.close();
                                        for (int x : selectedItems) {
                                            database.updateUsage(listProducts.get(x), categoryId);
                                        }
                                        Toast.makeText(getContext(), "Product(s) moved", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builderCurrency.setNeutralButton("Cancel", null);
                                AlertDialog alertDialogCurrency = builderCurrency.create();
                                alertDialogCurrency.show();
                            }
                        }
                    });
                }
                builderProducts.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Dialog to edit or delete a category
                        AlertDialog.Builder builderNewCategory = new AlertDialog.Builder(getContext());
                        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_light_category_input, null);
                        final EditText editCategoryName = viewDialog.findViewById(R.id.edit_dialog_light_category);
                        editCategoryName.setText(listCategories.get(position));
                        builderNewCategory.setView(viewDialog);
                        builderNewCategory.setTitle("Edit category");
                        builderNewCategory.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String categoryName = editCategoryName.getText().toString();
                                Cursor cursor = database.getCategory(categoryName);
                                if (cursor.moveToNext()) {
                                    Toast.makeText(getContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    database.updateCategory(categoryId, categoryName);
                                    Toast.makeText(getContext(), "Category updated", Toast.LENGTH_SHORT).show();
                                }
                                cursor.close();
                            }
                        });
                        if (categoryId != 0) {
                            builderNewCategory.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    database.updateUsage(categoryId);
                                    database.deleteCategory(categoryId);
                                    Toast.makeText(getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        builderNewCategory.setNeutralButton("Cancel", null);
                        builderNewCategory.show();
                    }
                });
                AlertDialog alertDialogProducts = builderProducts.create();
                alertDialogProducts.show();
            }
        });
        builderCategories.setNeutralButton("Cancel", null);
        builderCategories.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Dialog to save a new category
                AlertDialog.Builder builderNewCategory = new AlertDialog.Builder(getContext());
                View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_light_category_input, null);
                final EditText editCategoryName = viewDialog.findViewById(R.id.edit_dialog_light_category);
                builderNewCategory.setView(viewDialog);
                builderNewCategory.setTitle("Create a new category");
                builderNewCategory.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String categoryName = editCategoryName.getText().toString();
                        Cursor cursor = database.getCategory(categoryName);
                        if (cursor.moveToNext()) {
                            Toast.makeText(getContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            database.addCategory(categoryName);
                            Toast.makeText(getContext(), "Category " + categoryName + " saved", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    }
                });
                builderNewCategory.setNeutralButton("Cancel", null);
                builderNewCategory.show();
            }
        });
        AlertDialog alertDialogCategories = builderCategories.create();
        alertDialogCategories.show();
    }
}
