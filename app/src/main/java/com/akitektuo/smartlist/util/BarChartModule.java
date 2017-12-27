package com.akitektuo.smartlist.util;

import android.content.Context;

import com.akitektuo.smartlist.model.CategoryModel;
import com.akitektuo.smartlist.model.ItemModel;
import com.akitektuo.smartlist.model.ProductModel;
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

import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
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
        getChart().setDrawBarShadow(false);
        getChart().setDrawValueAboveBar(true);
        getChart().getDescription().setEnabled(false);
        getChart().setMaxVisibleValueCount(12);
        getChart().setHighlightPerTapEnabled(false);
        getChart().setHighlightPerDragEnabled(false);
        getChart().setPinchZoom(true);
        getChart().setDrawGridBackground(false);

        IAxisValueFormatter xAxisFormatter = new TimeAxisValueFormatter();

        XAxis xAxis = getChart().getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = getChart().getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = getChart().getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = getChart().getLegend();
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

        Calendar calStart = getStartOfDay(Calendar.getInstance());
        Calendar calEnd = getStartOfDay(Calendar.getInstance());
        int time = -preference.getPreferenceInt(KEY_STATS_RANGE), type = 5;
        switch (getType()) {
            case 0:
                type = Calendar.DATE;
                break;
            case 1:
                type = Calendar.WEEK_OF_YEAR;
                break;
            case 2:
                type = Calendar.MONTH;
                break;
        }
        calStart.add(type, time);
        calEnd.add(type, time + 1);
        double average = 0;
        boolean startCount = false;
        int count = 0;
        for (int i = 1; i <= preference.getPreferenceInt(KEY_STATS_RANGE); i++) {
            calStart.add(type, 1);
            calEnd.add(type, 1);
            double totalValue = 0;
            for (CategoryModel category : categories) {
                for (ProductModel product : category.getProducts()) {
                    for (ItemModel item : product.getItems()) {
                        if (calStart.before(item.getCalendar()) && calEnd.after(item.getCalendar())) {
                            totalValue += item.getValue();
                        }
                    }
                }
            }
            average += totalValue;
            if (totalValue > 0) {
                startCount = true;
            }
            if (startCount) {
                count++;
            }
            entries.add(new BarEntry(i, (float) totalValue));
        }

        BarDataSet dataSet;

        if (getChart().getData() != null && getChart().getData().getDataSetCount() > 0) {
            dataSet = (BarDataSet) getChart().getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            getChart().getData().notifyDataChanged();
            getChart().notifyDataSetChanged();
        } else {
            String label = "Last " + preference.getPreferenceInt(KEY_STATS_RANGE);
            switch (getType()) {
                case 0:
                    label += " days";
                    break;
                case 1:
                    label += " weeks";
                    break;
                case 2:
                    label += " months";
                    break;
            }
            label += " with the average of " + new DecimalFormat("0.##").format(average / count) + " " + preference.getPreferenceString(KEY_CURRENCY);

            dataSet = new BarDataSet(entries, label);
            dataSet.setDrawIcons(false);
            switch (getType()) {
                case 0:
                    dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                    break;
                case 1:
                    dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                    break;
                case 2:
                    dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                    break;
            }


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(1f);

            getChart().setData(data);
        }
    }

    private Calendar getStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        switch (getType()) {
            case 1:
                cal.set(Calendar.DAY_OF_WEEK, 0);
                break;
            case 2:
                cal.set(Calendar.DAY_OF_MONTH, 0);
                break;
        }
        return cal;
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
