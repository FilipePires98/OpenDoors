package pt.ua.opendoors;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONObject;

public class Fragment_Controls extends Fragment implements CompoundButton.OnCheckedChangeListener {

    Switch switchTemperature;
    Switch switchLight;
    TextView textView;
    Button buttonRT;
    Button buttonRL;
    BubbleSeekBar  bsbTemperature;
    BubbleSeekBar  bsbLTemperature;
    BubbleSeekBar  bsbLIntensity;
    Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Controls");
        final View view = inflater.inflate(R.layout.fragment_controls,container, false);

        bsbTemperature = (BubbleSeekBar) view.findViewById(R.id.seekBarT);
        bsbLTemperature = view.findViewById(R.id.seekBarLT);
        bsbLIntensity = view.findViewById(R.id.seekBarLI);

        buttonRT = (Button) view.findViewById(R.id.regularTemperature) ;
        buttonRL = view.findViewById(R.id.regularLights);

        bsbTemperature.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                buttonRT.setVisibility(View.VISIBLE);

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        bsbLTemperature.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                if (!flag) {
                    flag = true;
                } else {
                    buttonRL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {


            }
        });

        bsbLIntensity.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                if (!flag) {
                    flag = true;
                } else {
                    buttonRL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        switchTemperature = view.findViewById(R.id.switch_auto_temperature);
        switchTemperature.setOnCheckedChangeListener(this);

        switchLight = view.findViewById(R.id.switch_auto_lights);
        switchLight.setOnCheckedChangeListener(this);

        Button buttonT = view.findViewById(R.id.regularTemperature);
        buttonT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String URL = "http://192.168.11.68:8080/OpenDoors_DeviceController-1.0/devices/temperature/regulate?temperature="+bsbTemperature.getProgress();

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getActivity(),"Temperature adjusted successfully",Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(),"Something went wrong :(",Toast.LENGTH_SHORT).show();
                            }
                        });
                requestQueue.add(objectRequest);

            }
        });

        Button buttonL = view.findViewById(R.id.regularLights);
        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "http://192.168.11.68:8080/OpenDoors_DeviceController-1.0/devices/light/2/regulate?temperature="+bsbLTemperature.getProgress()+"&intensity="+bsbLIntensity.getProgress();

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getActivity(),"Light adjusted successfully",Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(),"Something went wrong :(",Toast.LENGTH_SHORT).show();
                            }
                        });
                requestQueue.add(objectRequest);
            }
        });

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (switchTemperature.isChecked()) {
            switchTemperature.setText("OFF");
            bsbTemperature.setEnabled(false);
            buttonRT.setVisibility(View.INVISIBLE);
        } else if (!switchTemperature.isChecked()) {
            switchTemperature.setText("ON");
            bsbTemperature.setEnabled(true);
        }
        if (switchLight.isChecked()) {
            switchLight.setText("OFF");
            bsbLTemperature.setEnabled(false);
            bsbLIntensity.setEnabled(false);
            buttonRL.setVisibility(View.INVISIBLE);
            flag = false;
        } else if (!switchLight.isChecked()) {
            switchLight.setText("ON");
            bsbLIntensity.setEnabled(true);
            bsbLTemperature.setEnabled(true);
        }
    }


}
