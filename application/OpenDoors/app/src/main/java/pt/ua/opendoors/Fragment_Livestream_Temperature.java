package pt.ua.opendoors;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Livestream_Temperature extends Fragment {

    private LineChart mChar;
    private boolean gogo = false;
    private boolean wait = false;
    private TextView tempAual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_temperature, null);
        mChar = view.findViewById(R.id.temperatureGraph);
        tempAual = (TextView) view.findViewById(R.id.tempAtual);

        sendNetworkRequest1("{\"name\": \"newinstance\", \"format\": \"json\", \"auto.offset.reset\": \"earliest\"}");
        sendNetworkRequest4("{\"name\": \"newinstance\", \"format\": \"json\", \"auto.offset.reset\": \"earliest\"}");

        return view;
    }

    private void sendNetworkRequest1(String dfp) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getKafkaCreate(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                sendNetworkRequest2("{\"topics\":[\"24temperature\"]}");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendNetworkRequest2(String dfp) {

        //TODO
        //ALTERAR IP
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstance/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getKafkaSubscribe(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                getNetworkResponse3();
            }



            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro 2", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getNetworkResponse3() {

        String URL = "http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstance/records";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("onResponseTemperature", response.toString());

                        mChar.setDragEnabled(false);
                        mChar.setScaleEnabled(true);
                        ArrayList<Entry> yValues = new ArrayList<>();

                        for (int i = 0 ; i < response.length() && i < 10; i++) {
                            JSONObject j = null;

                            try {
                                j = response.getJSONObject(i);
                                JSONObject data = j.getJSONObject("value");
                                Double temperature = (Double) data.get("temperature");

                                yValues.add(new Entry( i, Float.parseFloat(temperature-75 + "")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        LineDataSet set1 = new LineDataSet(yValues, "Varia????o da Temperatura ao longo do tempo");

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
                        //mChar.setVisibility(View.VISIBLE);
                        sendNetworkRequest4("{\"name\": \"newinstance3\", \"format\": \"json\", \"auto.offset.reset\": \"earliest\"}");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onErrorTemperature", error.toString());

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("Accept","application/vnd.kafka.json.v2+json");
                        return params;
                    }
        };
        requestQueue.add(objectRequest);
    }

    private void sendNetworkRequest4(String dfp) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getKafkaCreate(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                sendNetworkRequest5("{\"topics\":[\"24avg_temperature\"]}");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendNetworkRequest5(String dfp) {

        //TODO
        //ALTERAR IP
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstance3/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getKafkaSubscribe(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                getNetworkResponse6();
            }



            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro 2", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getNetworkResponse6() {

        String URL = "http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstance3/records";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        //PROCESS
                        tempAual.setText("10");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onErrorTemperature2", error.toString());

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Accept","application/vnd.kafka.json.v2+json");
                return params;
            }
        };
        requestQueue.add(objectRequest);
    }



}
