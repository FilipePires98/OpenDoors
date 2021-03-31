package pt.ua.opendoors;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Stats_Clients extends Fragment {

    TextView select;
    TextView selectText;
    ImageButton selectIcon1;
    ImageButton selectIcon2;
    Spinner selectYear;
    Button drawG;
    private LineChart mChar;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
    Date date = new Date();

    String begin;
    String end;
    Timestamp Xend;
    Timestamp Xbegin;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats_clients, null);

        select = view.findViewById(R.id.select);
        selectText = view.findViewById(R.id.selectText);
        selectIcon1 = view.findViewById(R.id.selectIcon);
        selectIcon2 = view.findViewById(R.id.selectIcon2);
        drawG = view.findViewById(R.id.draw);
        mChar = view.findViewById(R.id.temperatureGraph);


        Spinner spinner = (Spinner) view.findViewById(R.id.chooseOption);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.chooseOption, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        select.setVisibility(View.VISIBLE);
                        selectText.setVisibility(View.VISIBLE);
                        selectIcon1.setVisibility(View.VISIBLE);
                        selectIcon2.setVisibility(View.INVISIBLE);
                        drawG.setVisibility(View.VISIBLE);
                        dayTopology();


                        break;
                    case 2:
                        select.setVisibility(View.VISIBLE);
                        selectText.setVisibility(View.VISIBLE);
                        selectIcon2.setVisibility(View.VISIBLE);
                        selectIcon1.setVisibility(View.INVISIBLE);
                        drawG.setVisibility(View.VISIBLE);
                        monthTopology();

                        break;
                    case 3:
                        select.setVisibility(View.VISIBLE);
                        selectText.setVisibility(View.VISIBLE);
                        selectIcon1.setVisibility(View.INVISIBLE);
                        selectIcon2.setVisibility(View.VISIBLE);
                        drawG.setVisibility(View.VISIBLE);
                        yearTopology();

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        drawG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", begin + "|  "+ end);

                //String dateEnd = dateFormat.format(date)+" "+tvEnd.getText()+":00";
                Date dateE = null;
                Date dateB = null;
                try {
                    dateE = sdf.parse(end);
                    dateB = sdf.parse(begin);
                    Xend = new Timestamp(dateE.getTime());
                    Xbegin = new Timestamp(dateB.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("TEST2", Xend + "|  "+ Xbegin);
                sendNetworkRequest("{\"store\":1, \"min\": \""+Xbegin+"\",\"max\": \""+Xend+"\"}");
                Log.d("dsfsdsdfs","{\"store\":1, \"min\": \""+begin+"\",\"max\": \""+end+"\"}");



            }
        });

        return view;
    }


    private void sendNetworkRequest(String dfp) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-07.ua.pt:8081/OpenDoors_Persistence-1.0/regist/")
                .addConverterFactory(ScalarsConverterFactory.create());


        Retrofit retrofit = builder.build();
        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getClients(dfp);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("heyyyyyyy", response.body());
                try {
                    JSONObject j = new JSONObject(response.body());

                    JSONArray visible = j.getJSONArray("time");
                    //JSONArray time = j.getJSONArray("time");

                    mChar.setDragEnabled(false);
                    mChar.setScaleEnabled(true);

                    ArrayList<Entry> yValues1 = new ArrayList<>();

                    for (int i = 0 ; i < visible.length() ; i++) {
                        yValues1.add(new Entry( i, (int) visible.get(i) ));
                    }

                    LineDataSet set2 = new LineDataSet(yValues1, "Variação longo do tempo");

                    set2.setFillAlpha(110);
                    set2.setCircleColor(Color.rgb(0, 28, 49));
                    set2.setLineWidth(3f);
                    set2.setColor(Color.rgb(0, 28, 49));
                    set2.setValueTextColor(Color.rgb(255, 170, 0));
                    set2.setValueTextSize(13f);

                    LineData data = new LineData(set2);
                    mChar.setData(data);
                    mChar.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Nothing to Show :(", Toast.LENGTH_SHORT).show();
                    mChar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Something went Wrong :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void monthTopology() {

        final Calendar today = Calendar.getInstance();

        selectIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        begin = selectedYear+"/"+selectedMonth+"/01 " + "00:00:00.00";
                        end = selectedYear+"/"+selectedMonth+"/30 " + "23:59:59.00";

                        selectText.setText(selectedYear+"/"+selectedMonth);
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder.setActivatedMonth(Calendar.DECEMBER)
                        .setMinYear(2014)
                        .setActivatedYear(2018)
                        .setMaxYear(2018)
                        .setMinMonth(Calendar.JANUARY)
                        .setTitle("Escolha o mês")
                        .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                        // .setMaxMonth(Calendar.OCTOBER)
                        // .setYearRange(1890, 1890)
                        // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                        //.showMonthOnly()
                        // .showYearOnly()
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {
                            }
                        })
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {
                            }
                        })
                        .build()
                        .show();


            }
        });
    }

    private void yearTopology() {
        final Calendar today = Calendar.getInstance();

        selectIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        begin = selectedYear+"/01/01 " + "00:00:00.00";
                        end = selectedYear+"/12/31 " + "23:59:59.00";

                        selectText.setText(selectedYear+"");
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder.setActivatedMonth(Calendar.DECEMBER)
                        .setMinYear(2012)
                        .setActivatedYear(2016)
                        .setMaxYear(2018)
                        .setMinMonth(Calendar.JANUARY)
                        .setTitle("Escolha o ano")
                        //.setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                        // .setMaxMonth(Calendar.OCTOBER)
                        // .setYearRange(1890, 1890)
                        // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                        //.showMonthOnly()
                        .showYearOnly()
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {
                            }
                        })
                        .build()
                        .show();


            }
        });
    }

    private void dayTopology() {
        selectIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        begin = year+"/"+month+"/"+dayOfMonth+" " + "00:00:00.00";
                        end = year+"/"+month+"/"+dayOfMonth+" " + "23:59:59.00";

                        selectText.setText(year+"/"+month+"/"+dayOfMonth+"");
                    }
                }, year, month,day);
                datePickerDialog.show();
            }

        });
    }
}
