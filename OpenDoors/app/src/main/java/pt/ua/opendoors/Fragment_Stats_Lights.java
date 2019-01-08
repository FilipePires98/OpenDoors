package pt.ua.opendoors;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Stats_Lights extends Fragment {

    SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
    Date date = new Date();

    private LineChart mChar;
    TextView tvBegin;
    TextView tvEnd;
    Boolean flag = false;
    Button drawG;
    Timestamp begin;
    Timestamp end;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats_lights, null);

        tvBegin = view.findViewById(R.id.tvBegin);
        tvEnd = view.findViewById(R.id.tvEnd);
        drawG = view.findViewById(R.id.draw);

        ImageButton ibBeginInterval = view.findViewById(R.id.dpBegin);
        ibBeginInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        tvBegin.setText(hourOfDay+":"+minutes);
                        String dateBegin = dateFormat.format(date)+" "+tvBegin.getText()+":00";
                        Date dateB = null;
                        try {
                            dateB = sdf.parse(dateBegin);
                            begin = new Timestamp(dateB.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (flag) {
                            drawG.setVisibility(View.VISIBLE);
                        } else {
                            flag = true;
                        }
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }

        });

        ImageButton ibEndInterval = view.findViewById(R.id.dpEnd);
        ibEndInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        tvEnd.setText(hourOfDay+":"+minutes);
                        String dateEnd = dateFormat.format(date)+" "+tvEnd.getText()+":00";
                        Date dateE = null;
                        try {
                            dateE = sdf.parse(dateEnd);
                            end = new Timestamp(dateE.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (flag) {
                            drawG.setVisibility(View.VISIBLE);
                        } else {
                            flag = true;
                        }
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }

        });

        mChar = view.findViewById(R.id.lightGraph);


        drawG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNetworkRequest("{\"store\":1, \"min\": \""+begin+"\",\"max\": \""+end+"\"}");
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
        Call<String> call = client.getLightStats(dfp);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.d("sdfsdf", response.body());
                try {

                    JSONObject j = new JSONObject(response.body());
                    JSONArray visible = j.getJSONArray("visible");
                    JSONArray infrared = j.getJSONArray("infrared");
                    //JSONArray time = j.getJSONArray("time");

                    mChar.setDragEnabled(false);
                    mChar.setScaleEnabled(true);

                    ArrayList<Entry> yValues = new ArrayList<>();
                    ArrayList<Entry> yValues1 = new ArrayList<>();

                    for (int i = 0 ; i < infrared.length() ; i++) {
                        yValues.add(new Entry( i, (int) infrared.get(i) ));
                    }

                    for (int i = 0 ; i < visible.length() ; i++) {
                        yValues1.add(new Entry( i, (int) visible.get(i) ));
                    }


                    LineDataSet set1 = new LineDataSet(yValues, "Variação da Infravermelhos ao longo do tempo");
                    LineDataSet set2 = new LineDataSet(yValues1, "Variação da Visible ao longo do tempo");

                    set1.setFillAlpha(110);
                    set1.setCircleColor(Color.rgb(255, 170, 0));
                    set1.setLineWidth(3f);
                    set1.setColor(Color.rgb(255, 170, 0));
                    set1.setValueTextColor(Color.rgb(0, 28, 49));
                    set1.setValueTextSize(13f);

                    set2.setFillAlpha(110);
                    set2.setCircleColor(Color.rgb(0, 28, 49));
                    set2.setLineWidth(3f);
                    set2.setColor(Color.rgb(0, 28, 49));
                    set2.setValueTextColor(Color.rgb(255, 170, 0));
                    set2.setValueTextSize(13f);

                    LineData data = new LineData(set1, set2);
                    mChar.setData(data);
                    mChar.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Nothing to Show :(", Toast.LENGTH_SHORT).show();
                    mChar.setVisibility(View.INVISIBLE);
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Something went Wrong :(", Toast.LENGTH_SHORT).show();
            }
        });

    }

  }
