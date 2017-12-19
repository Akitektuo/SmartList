package com.akitektuo.smartlist.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.model.CategoryModel;
import com.akitektuo.smartlist.model.ItemModel;
import com.akitektuo.smartlist.model.ProductModel;
import com.akitektuo.smartlist.util.BarChartModule;
import com.akitektuo.smartlist.util.Preference;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_TOTAL_COUNT;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class StatsFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelper database;
    private int layoutId;
    private Preference preference;
    private PieChart chartPie;
    private BarChartModule chartBarDays;
    private BarChartModule chartBarWeeks;
    private BarChartModule chartBarMonths;

    public StatsFragment() {
        layoutId = R.layout.fragment_stats;
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

        chartPie = getActivity().findViewById(R.id.chart_pie);

        chartBarDays = new BarChartModule(getContext(), (BarChart) getActivity().findViewById(R.id.chart_bar_days), 0);
        chartBarWeeks = new BarChartModule(getContext(), (BarChart) getActivity().findViewById(R.id.chart_bar_weeks), 1);
        chartBarMonths = new BarChartModule(getContext(), (BarChart) getActivity().findViewById(R.id.chart_bar_months), 2);

        setDataCharts();
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
    }

    public void setDataCharts() {
        Cursor cursorCategories = database.getCategory();
        List<CategoryModel> categories = new ArrayList<>();
        if (cursorCategories.moveToFirst()) {
            do {
                Cursor cursorProducts = database.getUsageAsc(cursorCategories.getInt(0));
                if (cursorProducts.moveToFirst()) {
                    List<ProductModel> products = new ArrayList<>();
                    do {
                        Cursor cursorList = database.getListForProduct(cursorProducts.getString(0));
                        if (cursorList.moveToFirst()) {
                            List<ItemModel> items = new ArrayList<>();
                            do {
                                try {
                                    items.add(new ItemModel(cursorList.getInt(1), new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).parse(cursorList.getString(2))));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } while (cursorList.moveToNext());
                            products.add(new ProductModel(cursorProducts.getString(0), items));
                        }
                        cursorList.close();
                    } while (cursorProducts.moveToNext());
                    categories.add(new CategoryModel(cursorCategories.getInt(0), cursorCategories.getString(1), products));
                }
                cursorProducts.close();
            } while (cursorCategories.moveToNext());
        }
        cursorCategories.close();

        setDataPie(categories);
        chartBarDays.setData(categories);
        chartBarWeeks.setData(categories);
        chartBarMonths.setData(categories);
    }

    private void setDataPie(List<CategoryModel> categories) {
        chartPie.setUsePercentValues(true);
        chartPie.getDescription().setEnabled(false);
        chartPie.setRotationEnabled(false);

        chartPie.setHighlightPerTapEnabled(false);
        chartPie.setDrawHoleEnabled(true);
        chartPie.setHoleColor(Color.WHITE);
        chartPie.setTransparentCircleColor(Color.WHITE);
        chartPie.setTransparentCircleAlpha(110);
        chartPie.setHoleRadius(58f);
        chartPie.setTransparentCircleRadius(61f);
        chartPie.setDrawCenterText(true);
        chartPie.setRotationAngle(0);

        chartPie.setCenterText(new SpannableString("Total Value\n" +
                new DecimalFormat("0.#").format(Double.parseDouble(preference.getPreferenceString(KEY_TOTAL_COUNT)))
                + "\n" + preference.getPreferenceString(KEY_CURRENCY)));

        Collections.sort(categories, new Comparator<CategoryModel>() {
            @Override
            public int compare(CategoryModel o1, CategoryModel o2) {
                return Double.compare(o2.getValue(), o1.getValue());
            }
        });

        ArrayList<PieEntry> entries = new ArrayList<>();

        int length = categories.size();
        if (length > 6) {
            length = 6;
        }
        double totalValue = 0;
        for (int i = 0; i < length; i++) {
            if (categories.get(i).getValue() == 0) {
                length = i;
                break;
            }
            totalValue += categories.get(i).getValue();
        }
        for (int i = 0; i < length; i++) {
            entries.add(new PieEntry((float) (100 * categories.get(i).getValue() / totalValue), categories.get(i).getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Costs per category");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chartPie.setData(data);

        chartPie.highlightValues(null);

        chartPie.invalidate();

        chartPie.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = chartPie.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

}
