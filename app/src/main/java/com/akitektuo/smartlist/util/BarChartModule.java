package com.akitektuo.smartlist.util;

import android.content.Context;

import com.akitektuo.smartlist.model.CategoryModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.akitektuo.smartlist.util.Constant.KEY_STATS_RANGE;

/**
 * Created by Akitektuo on 17.12.2017.
 */

public class BarChartModule {

    private BarChart chart;
    private int type;
    private Context context;
    private Preference preference;

    public BarChartModule(Context context, BarChart chart, int type) {
        setChart(chart);
        setType(type);
        setContext(context);
        setPreference(new Preference(getContext()));
        initialize();
    }

    private void initialize() {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(12);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new TimeAxisValueFormatter();

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    public void setData(List<CategoryModel> categories) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(1, 15));
        entries.add(new BarEntry(2, 5));
        entries.add(new BarEntry(3, 10));
        entries.add(new BarEntry(4, 10));
        entries.add(new BarEntry(5, 10));
        entries.add(new BarEntry(6, 50));
        entries.add(new BarEntry(7, 10));
        entries.add(new BarEntry(8, 10));
        entries.add(new BarEntry(9, 10));
        entries.add(new BarEntry(10, 10));

        BarDataSet dataSet;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(entries, "Last " + preference.getPreferenceInt(KEY_STATS_RANGE) + " days");

            dataSet.setDrawIcons(false);
            dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    public BarChart getChart() {
        return chart;
    }

    public void setChart(BarChart chart) {
        this.chart = chart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public class TimeAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int time = (int) value - preference.getPreferenceInt(KEY_STATS_RANGE);
            String format = "";
            Calendar cal = Calendar.getInstance();
            switch (getType()) {
                case 0:
                    cal.add(Calendar.DATE, time);
                    format = new SimpleDateFormat("d MMM", Locale.getDefault()).format(cal.getTime());
                    break;
                case 1:
                    cal.add(Calendar.WEEK_OF_YEAR, time);
                    format = new SimpleDateFormat("w yyyy", Locale.getDefault()).format(cal.getTime());
                    break;
                case 2:
                    cal.add(Calendar.MONTH, time);
                    format = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(cal.getTime());
                    break;
            }
            return format;
        }

    }

    public class MyAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value);
        }
    }
}
