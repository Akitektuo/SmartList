package com.akitektuo.smartlist.fragment;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.LightExcelAdapter;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.model.ExcelModel;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL;
import static com.akitektuo.smartlist.util.Constant.preference;
import static com.akitektuo.smartlist.util.Constant.totalCount;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class ExcelFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private RecyclerView listExcel;
    private List<ExcelModel> excelModels;
    private DatabaseHelper database;

    public ExcelFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listExcel = (RecyclerView) getActivity().findViewById(R.id.list_light_excel);
        excelModels = new ArrayList<>();
        Switch switchExcel = (Switch) getActivity().findViewById(R.id.switch_light_excel);
        switchExcel.setChecked(preference.getPreferenceBoolean(KEY_TOTAL));
        switchExcel.setOnCheckedChangeListener(this);
        getActivity().findViewById(R.id.button_export_excel).setOnClickListener(this);
        database = new DatabaseHelper(getContext());
        populateList();
    }

    private void populateList() {
        scanItems();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        listExcel.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listExcel.getContext(),
                layoutManager.getOrientation());
        listExcel.addItemDecoration(dividerItemDecoration);
        listExcel.setAdapter(new LightExcelAdapter(getContext(), excelModels));
        goToLastItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_excel, container, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_settings_total:
                preference.setPreference(KEY_TOTAL, b);
                break;
        }
    }

    private void scanItems() {
        excelModels.clear();
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root + File.separator + "SmartList");
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                excelModels.add(new ExcelModel(f.getName(), f.length()));
            }
        }
        Collections.sort(excelModels, new Comparator<ExcelModel>() {
            @Override
            public int compare(ExcelModel o1, ExcelModel o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_export_excel:
                exportToExcel(database.getList());
                break;
        }
    }

    private void exportToExcel(Cursor cursor) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartList", "SmartList_" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".xls");
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
                    sheet.addCell(new Label(0, position, "Total"));
                    sheet.addCell(new Label(1, position, new DecimalFormat("0.#").format(totalCount)));
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
        scanItems();
        listExcel.getAdapter().notifyDataSetChanged();
        goToLastItem();
    }

    private void goToLastItem() {
        listExcel.smoothScrollToPosition(excelModels.size() - 1);
    }
}
