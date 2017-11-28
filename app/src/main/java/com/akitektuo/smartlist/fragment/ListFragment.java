package com.akitektuo.smartlist.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.LightListAdapter;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.model.ListModel;
import com.akitektuo.smartlist.util.Preference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.handler;
import static com.akitektuo.smartlist.util.Constant.totalCount;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class ListFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelper database;
    private RecyclerView list;
    private TextView textResult;
    private List<ListModel> listModels;
    private int layoutId;
    private Preference preference;

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
        textResult = getActivity().findViewById(R.id.text_light_result);
        listModels = new ArrayList<>();
        getActivity().findViewById(R.id.button_light_delete_all).setOnClickListener(this);
        totalCount = 0;
        populateList();
    }

    private void populateList() {
        Cursor cursor = database.getList();
        if (cursor.moveToFirst()) {
            do {
                listModels.add(new ListModel(cursor.getInt(0), cursor.getString(1), preference.getPreferenceString(KEY_CURRENCY), cursor.getString(2), 1));
                totalCount += Double.parseDouble(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        listModels.add(new ListModel(listModels.size() + 1, "", preference.getPreferenceString(KEY_CURRENCY), "", 0));
        list.setAdapter(new LightListAdapter(getContext(), listModels, textResult));
        list.smoothScrollToPosition(listModels.size() - 1);
        textResult.setText(getString(R.string.total_price, new DecimalFormat("0.#").format(totalCount), preference.getPreferenceString(KEY_CURRENCY)));
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
                totalCount = 0;
                textResult.setText(getContext().getString(R.string.total_price, new DecimalFormat("0.#").format(totalCount), preference.getPreferenceString(KEY_CURRENCY)));
                list.getAdapter().notifyDataSetChanged();
            }
        });
        builderDelete.setNegativeButton("Cancel", null);
        AlertDialog dialogDelete = builderDelete.create();
        dialogDelete.show();
    }
}
