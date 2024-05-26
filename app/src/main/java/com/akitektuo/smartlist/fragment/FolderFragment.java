package com.akitektuo.smartlist.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.LightExcelAdapter;
import com.akitektuo.smartlist.model.ExcelModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by AoD Akitektuo on 01-Sep-17 at 14:38.
 */

public class FolderFragment extends Fragment {

    private RecyclerView listExcel;
    private List<ExcelModel> excelModels;
    private TextView textNoFiles;
    private int layoutId;

    public FolderFragment() {
        layoutId = R.layout.fragment_folder;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("layout_id", layoutId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listExcel = getActivity().findViewById(R.id.list_light_excel);
        textNoFiles = getActivity().findViewById(R.id.text_light_no_files);
        excelModels = new ArrayList<>();
        populateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layout_id");
        }
        return inflater.inflate(layoutId, container, false);
    }

    public void populateList() {
        scanItems();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        listExcel.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listExcel.getContext(),
                layoutManager.getOrientation());
        listExcel.addItemDecoration(dividerItemDecoration);
        listExcel.setAdapter(new LightExcelAdapter(getActivity(), excelModels));
    }

    public void scanItems() {
        excelModels.clear();
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root + File.separator + "SmartList");
        if (dir.exists()) {
            try {
                if (dir.listFiles().length > 0) {
                    for (File f : dir.listFiles()) {
                        if (f.isFile()) {
                            excelModels.add(new ExcelModel(f.getName(), f.length(), new Date(f.lastModified())));
                        }
                    }
                    Collections.sort(excelModels, new Comparator<ExcelModel>() {
                        @Override
                        public int compare(ExcelModel o1, ExcelModel o2) {
                            return o2.getDate().compareTo(o1.getDate());
                        }
                    });
                    textNoFiles.setVisibility(View.GONE);
                    listExcel.setVisibility(View.VISIBLE);
                    if (listExcel.getAdapter() != null) {
                        listExcel.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    textNoFiles.setVisibility(View.VISIBLE);
                    listExcel.setVisibility(View.GONE);
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
