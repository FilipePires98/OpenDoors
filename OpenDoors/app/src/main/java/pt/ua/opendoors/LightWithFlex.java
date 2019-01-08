package pt.ua.opendoors;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

public class LightWithFlex extends JobService {

    private static final String TAG = "LightFlex";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Flex On!");
        
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++){
                    try {
                        sendNetworkRequest1("{\"name\": \"newinstanceflex\", \"format\": \"json\", \"auto.offset.reset\": \"earliest\"}");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
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
                sendNetworkRequest2("{\"topics\":[\"24sit_employee\"]}");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void sendNetworkRequest2(String dfp) {

        //TODO
        //ALTERAR IP
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstanceflex/")
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

            }
        });

    }

    private void getNetworkResponse() {

        //TODO
        //ALTERAR IP
        String URL = "http://deti-engsoft-08.ua.pt:8082/consumers/newconsumer2/instances/newinstanceflex/records";
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0 ; i < response.length() && i < 10; i++) {
                            JSONObject j = null;

                            try {

                                //TODO
                                //FAZER CORRETAMENTE O PROCESSAMENTO
                                j = response.getJSONObject(i);
                                JSONObject data = j.getJSONObject("value");
                                Double sitted = (Double) data.get("sitted");
                                Log.d("sit:",sitted+"");

                                String URL = "http://deti-engsoft-09.ua.pt:8080/OpenDoors_DeviceController-1.0/devices/light/1/on";

                                RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                requestQueue.add(objectRequest);



                                String URLL = "http://deti-engsoft-09.ua.pt:8080/OpenDoors_DeviceController-1.0/devices/light/2/on";

                                RequestQueue requestQueue2 = Volley.newRequestQueue(getBaseContext());
                                JsonObjectRequest objectRequest2 = new JsonObjectRequest(Request.Method.GET, URLL, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                requestQueue.add(objectRequest);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

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
