package pt.ua.opendoors;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_MainPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Open Doors");
        View view = inflater.inflate(R.layout.fragment_mainpage,container, false);

        // =========================================================================================
        //                                    Get Temperature
        // =========================================================================================
        TextView t = view.findViewById(R.id.tempV);
        sendNetworkRequest(t, "{\"store\":1}");

        // =========================================================================================
        //                                    Get Lights
        // =========================================================================================
        /*String URLL = "http://192.168.11.68:8080/OpenDoors_DeviceController-1.0/devices/light/2/getValue";
        RequestQueue requestLight = Volley.newRequestQueue(getActivity());
        JsonObjectRequest objectRequestL = new JsonObjectRequest(Request.Method.GET, URLL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TextView lt = getActivity().findViewById(R.id.tempL);
                        TextView li = getActivity().findViewById(R.id.intenL);
                        try {
                            lt.setText("Temperature: "+response.get("temperature")+"º");
                            li.setText("Intensidade: "+response.get("intensity"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView t = (TextView) getActivity().findViewById(R.id.tempV);
                        t.setText(error.toString());
                    }
                });
        requestLight.add(objectRequestL);
        */

        return view;
    }

    private void sendNetworkRequest(final TextView t, String dfp) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.11.68:8080/OpenDoors_Persistence-1.0/regist/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getCurrentTemperature(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                try {
                    JSONObject j = new JSONObject(response.body());
                    t.setText(j.getJSONArray("temperature").get(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro", Toast.LENGTH_SHORT).show();

            }
        });

    }


}
