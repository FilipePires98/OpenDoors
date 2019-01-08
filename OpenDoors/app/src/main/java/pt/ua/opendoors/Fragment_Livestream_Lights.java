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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class Fragment_Livestream_Lights extends Fragment {

    private LineChart mChar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_livestream_lights, null);
        mChar = view.findViewById(R.id.lightGraph);

        sendNetworkRequest1("{\"name\": \"newinstanceL\", \"format\": \"json\", \"auto.offset.reset\": \"earliest\"}");

        return view;
    }

    private void sendNetworkRequest1(String dfp) {

        //TODO
        //ALTERAR IP
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getKafkaCreate(dfp);

        //TODO
        //VERIFICAR SE O TOPICO É O CORRETO

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                sendNetworkRequest2("{\"topics\":[\"24light\"]}");
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
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstanceL/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getKafkaSubscribe(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                getNetworkResponse();
            }



            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro 2", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getNetworkResponse() {

        //TODO
        //ALTERAR IP
        String URL = "http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstanceL/records";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("onResponseLight", response.toString());


                        mChar.setDragEnabled(false);
                        mChar.setScaleEnabled(true);
                        ArrayList<Entry> yValuesVisible = new ArrayList<>();
                        ArrayList<Entry> yValuesInfrared = new ArrayList<>();


                        for (int i = 0 ; i < response.length() && i < 10; i++) {
                            JSONObject j = null;

                            try {

                                //TODO
                                //FAZER CORRETAMENTE O PROCESSAMENTO
                                j = response.getJSONObject(i);
                                JSONObject data = j.getJSONObject("value");
                                Double visible = (Double) data.get("visible");
                                Double infrared = (Double) data.get("infrared");

                                yValuesVisible.add(new Entry( i, Float.parseFloat(visible + "")));
                                yValuesInfrared.add(new Entry( i, Float.parseFloat(infrared + "")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        LineDataSet set1 = new LineDataSet(yValuesVisible, "Variação da Visible ao longo do tempo");
                        LineDataSet set2 = new LineDataSet(yValuesInfrared, "Variação da Infravermelhos ao longo do tempo");


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

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("onErrorLight", error.toString());

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

