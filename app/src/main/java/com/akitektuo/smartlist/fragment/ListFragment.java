package com.akitektuo.smartlist.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.LightListAdapter;
import com.akitektuo.smartlist.communicator.FileGenerationNotifier;
import com.akitektuo.smartlist.communicator.TotalUpdateNotifier;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.model.ListModel;
import com.akitektuo.smartlist.util.Preference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static android.app.Activity.RESULT_OK;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_OFFSET;
import static com.akitektuo.smartlist.util.Constant.KEY_OFFSET_UPDATE;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL_COUNT;
import static com.akitektuo.smartlist.util.Constant.handler;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class ListFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private DatabaseHelper database;
    private RecyclerView list;
    private LightListAdapter listAdapter;
    private TextView textResult;
    private List<ListModel> listModels;
    private int layoutId;
    private Preference preference;
    private Dialog dialogGenerateExcel;
    private FileGenerationNotifier notifierFiles;
    private Switch switchExcel;
    private TotalUpdateNotifier notifierTotal;

    public ListFragment() {
        layoutId = R.layout.fragment_list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("layout_id", layoutId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database = new DatabaseHelper(getContext());
        preference = new Preference(getContext());
        list = getActivity().findViewById(R.id.list_light_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(linearLayoutManager);
        list.setItemAnimator(new OvershootInLeftAnimator());
        textResult = getActivity().findViewById(R.id.text_light_result);
        listModels = new ArrayList<>();
        listAdapter = new LightListAdapter(getActivity(), listModels);
        list.setAdapter(new AlphaInAnimationAdapter(listAdapter));
        getActivity().findViewById(R.id.button_light_delete_all).setOnClickListener(this);
        getActivity().findViewById(R.id.button_light_excel_generate).setOnClickListener(this);
        getActivity().findViewById(R.id.button_light_voice).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_list_total).setOnClickListener(this);
        populateList();
        dialogGenerateExcel = new Dialog(getContext());
        dialogGenerateExcel.setContentView(R.layout.dialog_light_excel_generate);
        dialogGenerateExcel.findViewById(R.id.button_dialog_export_excel).setOnClickListener(this);
        dialogGenerateExcel.findViewById(R.id.layout_dialog_light_excel_total).setOnClickListener(this);
        switchExcel = dialogGenerateExcel.findViewById(R.id.switch_dialog_light_excel);
        switchExcel.setChecked(preference.getPreferenceBoolean(KEY_TOTAL));
        switchExcel.setOnCheckedChangeListener(this);
        notifierFiles = (FileGenerationNotifier) getActivity();
        notifierTotal = (TotalUpdateNotifier) getActivity();
    }

    public void populateList() {
        Cursor cursor = database.getList();
        double totalCount = 0;
        if (cursor.moveToFirst()) {
            do {
                listModels.add(new ListModel(cursor.getInt(0), cursor.getString(1), preference.getPreferenceString(KEY_CURRENCY), cursor.getString(2), 1));
                totalCount += Double.parseDouble(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
        listAdapter.notifyDataSetChanged();
        list.smoothScrollToPosition(listModels.size() - 1);
        updateTotal();
        preference.setPreference(KEY_TOTAL_COUNT, String.valueOf(totalCount));
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
            case R.id.button_light_delete_all:
                deleteAllItems();
                break;
            case R.id.button_light_excel_generate:
                dialogGenerateExcel.show();
                break;
            case R.id.button_dialog_export_excel:
                exportToExcel(database.getList());
                dialogGenerateExcel.dismiss();
                break;
            case R.id.layout_dialog_light_excel_total:
                switchExcel.setChecked(!switchExcel.isChecked());
                break;
            case R.id.layout_list_total:
                showOffsetDialog();
                break;
            case R.id.button_light_voice:
                promptSpeechInput();
                break;
        }
    }

    private void deleteAllItems() {
        AlertDialog.Builder builderDelete = new AlertDialog.Builder(getContext());
        builderDelete.setTitle("Delete All Items");
        builderDelete.setMessage("Are you sure you want to delete all items?");
        builderDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handler.post(new Runnable() {
                    public void run() {
                        for (int i = 1; i < database.getListNumberNew(); i++) {
                            database.deleteList(i);
                        }
                    }
                });
                listModels.clear();
                listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
                if (preference.getPreferenceBoolean(KEY_OFFSET_UPDATE)) {
                    double newOffset = Double.parseDouble(preference.getPreferenceString(KEY_OFFSET)) + Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT));
                    preference.setPreference(KEY_OFFSET, String.valueOf(newOffset));
                }
                preference.setPreference(KEY_TOTAL_COUNT, "0");
                updateTotal();
                list.getAdapter().notifyDataSetChanged();
                notifierTotal.listChanged(false);
            }
        });
        builderDelete.setNegativeButton("Cancel", null);
        AlertDialog dialogDelete = builderDelete.create();
        dialogDelete.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_dialog_light_excel:
                preference.setPreference(KEY_TOTAL, b);
                break;
        }
    }

    public void updateTotal() {
        try {
            textResult.setText(getContext().getString(R.string.total_price, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT))), preference.getPreferenceString(KEY_CURRENCY)));
        } catch (Exception e) {
            e.printStackTrace();
            preference.setPreference(KEY_TOTAL_COUNT, "0");
            updateTotal();
        }
    }

    private void exportToExcel(Cursor cursor) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", "SmartList_" + new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(new Date()) + ".xls");
        if (!file.exists()) {
            if (file.getParentFile().mkdirs()) {
                Snackbar.make(getActivity().findViewById(R.id.layout_fragment_excel), "Please check the permissions", Snackbar.LENGTH_LONG).setAction("Permissions", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }).show();
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
                cursor.close();
                if (preference.getPreferenceBoolean(KEY_TOTAL)) {
                    position += 2;
                    sheet.addCell(new Label(0, position, "Offset"));
                    sheet.addCell(new Label(1, position, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_OFFSET)))));
                    sheet.addCell(new Label(0, position + 1, "Total"));
                    sheet.addCell(new Label(1, position + 1, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)))));
                    sheet.addCell(new Label(0, position + 2, "Total with offset"));
                    sheet.addCell(new Label(1, position + 2, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)) + Double.parseDouble(preference.getPreferenceString(KEY_OFFSET)))));
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
        Toast.makeText(getContext(), "Excel generated", Toast.LENGTH_SHORT).show();
        notifierFiles.change();
    }

    private void showOffsetDialog() {
        AlertDialog.Builder builderOffset = new AlertDialog.Builder(getContext());
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_light_offset, null);
        builderOffset.setView(viewDialog);
        final EditText editOffset = viewDialog.findViewById(R.id.edit_dialog_light_limit);
        final TextView textTotal = viewDialog.findViewById(R.id.text_dialog_light_total);
        textTotal.setText(getString(R.string.total_price, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT))), preference.getPreferenceString(KEY_CURRENCY)));
        final TextView textTotalOffset = viewDialog.findViewById(R.id.text_dialog_light_total_offset);
        final Switch switchUpdateOffset = viewDialog.findViewById(R.id.switch_dialog_light_offset_update);
        switchUpdateOffset.setChecked(preference.getPreferenceBoolean(KEY_OFFSET_UPDATE));
        viewDialog.findViewById(R.id.layout_dialog_light_offset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchUpdateOffset.setChecked(!switchUpdateOffset.isChecked());
            }
        });
        switchUpdateOffset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preference.setPreference(KEY_OFFSET_UPDATE, b);
            }
        });
        try {
            Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT));
        } catch (Exception e) {
            preference.setPreference(KEY_TOTAL_COUNT, "0");
        }
        try {
            Double.parseDouble(preference.getPreferenceString(KEY_OFFSET));
        } catch (Exception e) {
            preference.setPreference(KEY_OFFSET, "0");
        }
        textTotalOffset.setText(getString(R.string.total_price_with_offset, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)) + Double.parseDouble(preference.getPreferenceString(KEY_OFFSET))), preference.getPreferenceString(KEY_CURRENCY)));
        editOffset.setText(new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_OFFSET))));
        editOffset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(editable.toString().isEmpty() || editable.toString().equals("-"))) {
                    textTotalOffset.setText(getString(R.string.total_price_with_offset, new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)) + Double.parseDouble(editable.toString())), preference.getPreferenceString(KEY_CURRENCY)));
                }
            }
        });
        builderOffset.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editOffset.getText().toString().isEmpty() || editOffset.getText().toString().equals("-") || editOffset.getText().toString().length() > 9) {
                    editOffset.setText("0");
                }
                preference.setPreference(KEY_OFFSET, editOffset.getText().toString());
            }
        });
        builderOffset.setNeutralButton("Close", null);
        builderOffset.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preference.setPreference(KEY_OFFSET, "0");
            }
        });
        builderOffset.show();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say \"[Product] costs [number]\"");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Speech not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String rec = result.get(0);
                    String[] res;
                    if (rec.contains(" costs ")) {
                        res = rec.split(" costs ");
                    } else if (rec.contains(" cost ")) {
                        res = rec.split(" cost ");
                    } else {
                        Toast.makeText(getContext(), "Invalid voice input, follow the pattern", Toast.LENGTH_SHORT).show();
                        promptSpeechInput();
                        return;
                    }
                    String product = res[0], price = res[1];
                    try {
                        Double.parseDouble(price);
                        database.addList(database.getListNumberNew(), price, product, new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                        database.updatePrices(product, price);
                        listAdapter.insert(listModels.size(), listModels.size(), price, product);
                        preference.setPreference(KEY_TOTAL_COUNT, String.valueOf(Double.valueOf(preference.getPreferenceString(KEY_TOTAL_COUNT)) + Double.valueOf(price)));
                        updateTotal();
                        notifierTotal.listChanged(true);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "The price is not a number", Toast.LENGTH_SHORT).show();
                        promptSpeechInput();
                        return;
                    }
                }
                break;
        }
    }

    public void goToLastItem() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.smoothScrollToPosition(listAdapter.getItemCount() - 1);
            }
        }, 100);
    }

}
