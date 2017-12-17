package com.akitektuo.smartlist.util;

import com.akitektuo.smartlist.model.CategoryModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Akitektuo on 17.12.2017.
 */

public class BarChartModule {

    private BarChart chart;
    private int type;

    public BarChartModule(BarChart chart, int type) {
        setChart(chart);
        setType(type);
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

        IAxisValueFormatter xAxisFormatter = null;
        switch (getType()) {
            case 0:
                xAxisFormatter = new DayAxisValueFormatter();
                break;
            case 1:
                break;
            case 2:
                break;
        }

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
        entries.add(new BarEntry(6, 10));
        entries.add(new BarEntry(7, 10));
        entries.add(new BarEntry(8, 10));
        entries.add(new BarEntry(9, 10));
        entries.add(new BarEntry(10, 10));
        entries.add(new BarEntry(11, 0));
        entries.add(new BarEntry(12, 10));

        BarDataSet dataSet;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(entries, "Last 12 days");

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

    public class DayAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int days = (int) value;
            return new SimpleDateFormat("d MMM", Locale.getDefault()).format(new Date(new Date().getTime() + getTimeForDays(days - 12)));
        }

        private long getTimeForDays(int days) {
            return days * 1000 * 60 * 60 * 24;
        }
    }

    public class MonthAxisValueFormatter implements IAxisValueFormatter {

        protected String[] mMonths = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        private BarLineChartBase<?> chart;

        public MonthAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            int months = (int) value;
            return new SimpleDateFormat("MMM yyy", Locale.getDefault()).format(new Date(new Date().getTime() + getTimeForMonths(months - 12)));
        }

        private long getTimeForMonths(int months) {
            return months * 4 * 7 * 1000 * 60 * 60 * 24;
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
