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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Stats_Temperature extends Fragment implements TimePickerDialog.OnTimeSetListener{


    SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
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
        View view = inflater.inflate(R.layout.fragment_stats_temperature, null);

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
                        String dateBegin = "12-12-2018 "+tvBegin.getText()+":00";
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
                        String dateEnd = "12-12-2018 "+tvEnd.getText()+":00";
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
        mChar = view.findViewById(R.id.temperatureGraph);


        drawG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DataFromPersistence dfp = new DataFromPersistence();
                dfp.setBeginTimestampStats(begin);
                dfp.setEndTimestampStats(end);
                dfp.setStore(1l);

                sendNetworkRequest("{\"store\":1, \"min\": \""+begin+"\",\"max\": \""+end+"\"}");
                Log.d("dsfsdsdfs","{\"store\":1, \"min\": \""+begin+"\",\"max\": \""+end+"\"}");
            }
        });

        return view;
    }

    private void sendNetworkRequest(String dfp) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.11.68:8080/OpenDoors_Persistence-1.0/regist/")
                .addConverterFactory(ScalarsConverterFactory.create());


        Retrofit retrofit = builder.build();
        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getTemperatureStats(dfp);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                try {
                    JSONObject j = new JSONObject(response.body());
                    JSONArray temperature = j.getJSONArray("temperature");
                    //JSONArray time = j.getJSONArray("time");

                    mChar.setDragEnabled(false);
                    mChar.setScaleEnabled(true);

                    ArrayList<Entry> yValues = new ArrayList<>();

                    for (int i = 0 ; i < temperature.length() ; i++) {
                        yValues.add(new Entry( i, (int) temperature.get(i) ));
                    }

                    Log.d("sdsdf", yValues.toString());

                    LineDataSet set1 = new LineDataSet(yValues, "Variação da Temperatura ao longo do tempo");

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
                    mChar.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Something went Wrong :(", Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}


/*


JSONObject jsonvalue =new JSONObject();
try {
    jsonvalue.put("store", 1);
    jsonvalue.put("min", begin);
    jsonvalue.put("max", end);
} catch (JSONException e) {
    e.printStackTrace();
}

StringRequest stringRequest = new StringRequest(Request.Method.POST, URLL,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    //Creating JsonObject from response String
                    JSONObject jsonObject= new JSONObject(response.toString());
                    //extracting json array from response string
                    JSONArray jsonArray = jsonObject.getJSONArray("arrname");
                    JSONObject jsonRow = jsonArray.getJSONObject(0);
                    //get value from jsonRow
                    String resultStr = jsonRow.getString("result");
                } catch (JSONException e) {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters = new HashMap<String,String>();
                parameters.put("parameter","sad");

                return parameters;
            }

        };
        requestLight.add(stringRequest);
 */