package com.akitektuo.smartlist.fragment;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.communicator.FileGenerationNotifier;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.util.Preference;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL;
import static com.akitektuo.smartlist.util.Constant.totalCount;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class ExcelFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private DatabaseHelper database;
    private FileGenerationNotifier notifier;
    private int layoutId;
    private Preference preference;

    public ExcelFragment() {
        layoutId = R.layout.fragment_excel;
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
        Switch switchExcel = getActivity().findViewById(R.id.switch_light_excel);
        switchExcel.setChecked(preference.getPreferenceBoolean(KEY_TOTAL));
        switchExcel.setOnCheckedChangeListener(this);
        getActivity().findViewById(R.id.button_export_excel).setOnClickListener(this);
        database = new DatabaseHelper(getContext());
        notifier = (FileGenerationNotifier) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layout_id");
        }
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_light_excel:
                preference.setPreference(KEY_TOTAL, b);
                break;
        }
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
        Toast.makeText(getContext(), "Excel generated", Toast.LENGTH_SHORT).show();
        notifier.change();
    }

}
