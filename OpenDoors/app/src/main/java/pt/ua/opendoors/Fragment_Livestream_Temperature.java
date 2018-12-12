package pt.ua.opendoors;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Livestream_Temperature extends Fragment {

    private LineChart mChar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_temperature, null);

        mChar = view.findViewById(R.id.temperatureGraph);
        //mChar.setOnChartGestureListener(Fragment_Stats_Temperature.this);
        //mChar.setOnChartValueSelectedListener(Fragment_Stats_Temperature.this);

        mChar.setDragEnabled(true);
        mChar.setScaleEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.addAll(getValues());

        LineDataSet set1 = new LineDataSet(yValues, "Variação da Temperatura em Tempo Real");

        set1.setFillAlpha(110);
        set1.setCircleColor(Color.rgb(255, 170, 0));
        set1.setLineWidth(3f);
        set1.setColor(Color.rgb(255, 170, 0));
        set1.setValueTextColor(Color.rgb(0, 28, 49));
        set1.setValueTextSize(13f);



        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChar.setData(data);

        return view;
    }

    public ArrayList<Entry> getValues() {
        ArrayList<Entry> tempValues = new ArrayList<Entry>();

        tempValues.add(new Entry(0,60f));
        tempValues.add(new Entry(1,57f));
        tempValues.add(new Entry(2,56f));
        tempValues.add(new Entry(3,59f));
        tempValues.add(new Entry(4,55f));
        tempValues.add(new Entry(5,61f));

        return tempValues;
    }

  }
