package com.akitektuo.smartlist.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.LightExcelAdapter;
import com.akitektuo.smartlist.model.ExcelModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by AoD Akitektuo on 01-Sep-17 at 14:38.
 */

public class FolderFragment extends Fragment {

    private RecyclerView listExcel;
    private List<ExcelModel> excelModels;
    private TextView textNoFiles;

    public FolderFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listExcel = (RecyclerView) getActivity().findViewById(R.id.list_light_excel);
        textNoFiles = (TextView) getActivity().findViewById(R.id.text_light_no_files);
        excelModels = new ArrayList<>();
        populateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    public void populateList() {
        scanItems();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        listExcel.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listExcel.getContext(),
                layoutManager.getOrientation());
        listExcel.addItemDecoration(dividerItemDecoration);
        listExcel.setAdapter(new LightExcelAdapter(getContext(), excelModels));
    }

    public void scanItems() {
        excelModels.clear();
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root + File.separator + "SmartList");
        if (dir.listFiles().length > 0) {
            try {
                for (File f : dir.listFiles()) {
                    if (f.isFile()) {
                        excelModels.add(new ExcelModel(f.getName(), f.length()));
                    }
                }
                Collections.sort(excelModels, new Comparator<ExcelModel>() {
                    @Override
                    public int compare(ExcelModel o1, ExcelModel o2) {
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                textNoFiles.setVisibility(View.GONE);
                listExcel.setVisibility(View.VISIBLE);
                if (listExcel.getAdapter() != null) {
                    listExcel.getAdapter().notifyDataSetChanged();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                textNoFiles.setVisibility(View.VISIBLE);
                listExcel.setVisibility(View.GONE);
            }
        } else {
            textNoFiles.setVisibility(View.VISIBLE);
            listExcel.setVisibility(View.GONE);
        }
    }

}
