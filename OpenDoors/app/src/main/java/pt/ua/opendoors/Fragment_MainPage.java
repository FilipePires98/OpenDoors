package pt.ua.opendoors;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_MainPage extends Fragment {

    Button ativar;
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
        //                                    Get Light
        // =========================================================================================
        TextView lc = view.findViewById(R.id.tempL);
        TextView li = view.findViewById(R.id.intenL);
        sendNetworkRequestL(lc, li, "{\"store\":1}");

        ativar = (Button) view.findViewById(R.id.ativar);

        ativar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).activate_schedule(v);
            }
        });



        return view;
    }

    private void sendNetworkRequestL(final TextView lc, final TextView li, String dfp) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-07.ua.pt:8081/OpenDoors_Persistence-1.0/regist/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getCurrentLight(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                try {
                    JSONObject j = new JSONObject(response.body());
                    if (j == null) {
                        lc.setText("Infrared Value: X");
                        li.setText("Intensity: X");
                    }else {
                        lc.setText("Infrared Value: "+j.getJSONArray("infrared").get(0).toString());
                        li.setText("Intensity: "+j.getJSONArray("visible").get(0).toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendNetworkRequest(final TextView t, String dfp) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-07.ua.pt:8081/OpenDoors_Persistence-1.0/regist/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getCurrentTemperature(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                try {
                    JSONObject j = new JSONObject(response.body());
                    if (j == null) {
                        t.setText("Value: XºC");
                    } else {
                        t.setText("Value: "+j.getJSONArray("temperature").get(0).toString()+"ºC");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException npe){
                    npe.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro", Toast.LENGTH_SHORT).show();

            }
        });

    }



}
