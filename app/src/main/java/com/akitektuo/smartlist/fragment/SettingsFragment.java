package com.akitektuo.smartlist.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.activity.material.ListActivity;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.util.Preference;

import java.util.ArrayList;
import java.util.List;

import static com.akitektuo.smartlist.util.Constant.KEY_DESIGN;
import static com.akitektuo.smartlist.util.Constant.KEY_SMART_PRICE;
import static com.akitektuo.smartlist.util.Constant.KEY_STATS_RANGE;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelper database;
    private int layoutId;
    private Preference preference;

    public SettingsFragment() {
        layoutId = R.layout.fragment_settings;
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

        getActivity().findViewById(R.id.layout_light_products).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_limit).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_design).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_graph_columns).setOnClickListener(this);

        database = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layout_id");
        }
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_light_products:
                removeProduct();
                break;
            case R.id.layout_light_limit:
                setLimit();
                break;
            case R.id.layout_light_design:
                switchDesign();
                break;
            case R.id.layout_light_graph_columns:
                setGraphColumns();
                break;
        }
    }

    private void switchDesign() {
        preference.setPreference(KEY_DESIGN, 1);
        startActivity(new Intent(getActivity(), ListActivity.class));
        getActivity().finish();
    }

    private void removeProduct() {
        AlertDialog.Builder builderRecommendations = new AlertDialog.Builder(getContext());
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
            }
        });
        builderRecommendations.setNeutralButton("Cancel", null);
        AlertDialog alertDialogRecommendations = builderRecommendations.create();
        alertDialogRecommendations.show();
    }

    private void setLimit() {
        AlertDialog.Builder builderFill = new AlertDialog.Builder(getContext());
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_light_fill, null);
        final EditText editLimit = viewDialog.findViewById(R.id.edit_dialog_light_limit);
        final SeekBar barLimit = viewDialog.findViewById(R.id.bar_light_limit);
        barLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    editLimit.setText(String.valueOf(i + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        barLimit.setProgress(preference.getPreferenceInt(KEY_SMART_PRICE) - 2);
        editLimit.setText(String.valueOf(preference.getPreferenceInt(KEY_SMART_PRICE) - 1));
        editLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String limitInput = editable.toString();
                if (limitInput.isEmpty()) {
                    barLimit.setProgress(0);
                } else if (checkInteger(limitInput)) {
                    if (limitInput.equals("0")) {
                        barLimit.setProgress(0);
                    } else if (Integer.parseInt(limitInput) > 1000) {
                        barLimit.setProgress(999);
                    } else {
                        barLimit.setProgress(Integer.parseInt(limitInput));
                    }
                }
            }
        });
        builderFill.setView(viewDialog);
        builderFill.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preference.setPreference(KEY_SMART_PRICE, barLimit.getProgress() + 1);
            }
        });
        builderFill.setNeutralButton("Cancel", null);
        builderFill.show();
    }

    private boolean checkInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setGraphColumns() {
        AlertDialog.Builder builderColumns = new AlertDialog.Builder(getContext());
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_light_graph_columns, null);
        final EditText editColumns = viewDialog.findViewById(R.id.edit_dialog_light_graph_columns);
        final SeekBar barColumns = viewDialog.findViewById(R.id.bar_light_columns);
        barColumns.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    editColumns.setText(String.valueOf(i + 2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        barColumns.setProgress(preference.getPreferenceInt(KEY_STATS_RANGE));
        editColumns.setText(String.valueOf(preference.getPreferenceInt(KEY_STATS_RANGE)));
        editColumns.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String columnsInput = editable.toString();
                if (columnsInput.isEmpty()) {
                    barColumns.setProgress(0);
                } else if (checkInteger(columnsInput)) {
                    if (columnsInput.equals("0")) {
                        barColumns.setProgress(0);
                    } else if (Integer.parseInt(columnsInput) < 2) {
                        barColumns.setProgress(0);
                    } else if (Integer.parseInt(columnsInput) > 31) {
                        barColumns.setProgress(29);
                    } else {
                        barColumns.setProgress(Integer.parseInt(columnsInput) - 2);
                    }
                }
            }
        });
        builderColumns.setView(viewDialog);
        builderColumns.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preference.setPreference(KEY_STATS_RANGE, barColumns.getProgress() + 2);
            }
        });
        builderColumns.setNeutralButton("Cancel", null);
        builderColumns.show();
    }
}
