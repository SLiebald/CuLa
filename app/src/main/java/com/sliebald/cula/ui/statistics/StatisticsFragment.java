package com.sliebald.cula.ui.statistics;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sliebald.cula.R;
import com.sliebald.cula.data.database.pojos.StatisticsActivityEntry;
import com.sliebald.cula.data.database.pojos.StatisticsLibraryWordCount;
import com.sliebald.cula.databinding.FragmentStatisticsBinding;
import com.sliebald.cula.utilities.KnowledgeLevelUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StatisticsFragment extends Fragment {

    public static final String TAG = StatisticsFragment.class.getSimpleName();
    private FragmentStatisticsBinding mBinding;

    private StatisticsViewModel mViewModel;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null || getContext() == null)
            return;

        mViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container,
                false);

        mViewModel.getLibraryCount().observe(getViewLifecycleOwner(), libraryWordCountList -> {
            if (libraryWordCountList != null && libraryWordCountList.size() > 0) {
                updateWordCountGraph(libraryWordCountList);
            }
        });

        mViewModel.getActivity().observe(getViewLifecycleOwner(), activityList -> {
            if (activityList != null && activityList.size() > 0) {
                updateActivityGraph(fillMissingActivityDays(activityList));
            }
        });

        return mBinding.getRoot();
    }

    /**
     * Helper method to fill the list of StatisticsActivityEntries retrieved from the database
     * with 0 values for inactive days.
     *
     * @param activityList The list with all activities >0.
     * @return A full list for the last 14 days containing also 0 values (= no training).
     */
    private List<StatisticsActivityEntry> fillMissingActivityDays(List<StatisticsActivityEntry> activityList) {
        List<StatisticsActivityEntry> fullList = new ArrayList<>();
        int index = 0;
        for (String date : getLastDaysAsString()) {
            if (activityList != null && activityList.size() > index && activityList.get(index).date.equals(date)) {
                fullList.add(activityList.get(index));
                index++;
            } else {
                fullList.add(new StatisticsActivityEntry(date, 0));
            }
        }
        return fullList;
    }

    /**
     * Helper method to get the string representations of the last 14 days in the format 2018-07-17.
     *
     * @return The {@link List} of dates ast String.
     */
    private List<String> getLastDaysAsString() {
        int days = 14;
        List<String> dayList = new ArrayList<>(days);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        for (int counter = days; counter > 0; counter--) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dayList.add(new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(calendar.getTime()));
        }
        // one final entry to make the graph look nice
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        dayList.add(new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(calendar.getTime()));
        return dayList;
    }

    /**
     * Updates the activity Graph with the given list of {@link StatisticsActivityEntry}s
     *
     * @param activityList The entries to visualize
     */
    private void updateActivityGraph(List<StatisticsActivityEntry> activityList) {
        LineChart mChart = mBinding.chartActivity;
        // disable description and legend
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        // disable touch gestures
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // set the axis parameters
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelRotationAngle(310);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("dd.MM.", Locale.GERMANY).format(new Date((long) value));
            }
        });
        xAxis.setLabelCount(7, true);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setLabelCount(4, false);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setTextSize(14f);
        yAxis.setAxisMinimum(0f);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Integer.toString((int) value);
            }
        });
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);

        // add data, need floats for the X axis
        ArrayList<Entry> yValues = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
        for (StatisticsActivityEntry entry : activityList) {

            Date date;
            try {
                date = format.parse(entry.getDate());
                assert date != null;
                yValues.add(new Entry(date.getTime(), entry.getActivity()));
            } catch (ParseException e) {
                Log.d(TAG, "Exception parsing a date from the database: " + e);
            }
        }

        //create the line data set and format it.
        int color = ResourcesCompat.getColor(getResources(), R.color.secondaryColor, null);
        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(yValues, "");
        lineDataSet.setMode(LineDataSet.Mode.STEPPED);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setColor(color);
        lineDataSet.setFillColor(color);
        lineDataSet.setFillAlpha(100);

        // create a data object with the data set
        LineData data = new LineData(lineDataSet);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
        mChart.animateXY(500, 1500);
        mChart.invalidate();

    }


    private void updateWordCountGraph(@NonNull List<StatisticsLibraryWordCount> libraryWordCountList) {
        ArrayList<PieEntry> counts = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        Context context = requireContext();

        for (StatisticsLibraryWordCount entry : libraryWordCountList) {
            counts.add(new PieEntry((float) entry.getCount(), KnowledgeLevelUtils
                    .getNameByKnowledgeLevel(context, entry.level)));
            colors.add(KnowledgeLevelUtils.getColorByKnowledgeLevel(entry.level));

        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(counts, "");
        pieDataSet.setValueTextSize(22);
        pieDataSet.setColors(colors);

        //Set the formatter to not display floats but integers.
        ValueFormatter formatter = new ValueFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return Integer.toString((int) value);
            }
        };
        pieDataSet.setValueFormatter(formatter);

        Legend legend = mBinding.chartWordCount.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(18);
        legend.setFormSize(18);
        legend.setWordWrapEnabled(true);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        mBinding.chartWordCount.setHoleRadius(0);

        mBinding.chartWordCount.setTransparentCircleRadius(0);
        mBinding.chartWordCount.setDrawEntryLabels(false);
        mBinding.chartWordCount.setData(pieData);
        mBinding.chartWordCount.getDescription().setEnabled(false);
        mBinding.chartWordCount.getLegend().setEnabled(true);
        mBinding.chartWordCount.invalidate();
    }
}
