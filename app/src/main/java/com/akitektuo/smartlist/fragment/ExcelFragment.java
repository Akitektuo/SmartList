package com.akitektuo.smartlist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akitektuo.smartlist.R;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class ExcelFragment extends Fragment {

    public ExcelFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_excel, container, false);
    }

}
