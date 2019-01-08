package pt.ua.opendoors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.google.gson.JsonDeserializationContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Employees extends Fragment {

    private FloatingActionButton fab;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Employees");
        View view = inflater.inflate(R.layout.fragment_employees,container, false);

        mListView = view.findViewById(R.id.listView);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager childFragMan = getChildFragmentManager();
                FragmentTransaction childFragTrans = childFragMan.beginTransaction();
                Fragment_Employee_Add addEmployee = new Fragment_Employee_Add();
                //childFragTrans.add(R.id.fragment_employees, addEmployee);
                childFragTrans.addToBackStack("Add Employee");
                childFragTrans.replace(R.id.fragment_employees, addEmployee);
                childFragTrans.commit();
                fab.hide();
            }
        });


        sendNetworkRequestL();

        return view;
    }

    private void sendNetworkRequestL() {
        String dfp = "{\"store\":1}";
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://deti-engsoft-07.ua.pt:8081/OpenDoors_Persistence-1.0/regist/")
                .addConverterFactory(ScalarsConverterFactory.create());

        Retrofit retrofit = builder.build();

        DataFromPersistenceClient client = retrofit.create(DataFromPersistenceClient.class);
        Call<String> call = client.getAllEmployees(dfp);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                JSONObject object = null;
                ArrayList<Employee> employeeList = new ArrayList<>();
                try {
                    object = new JSONObject(response.body());

                    JSONArray ccs = object.getJSONArray("cc");
                    JSONArray names = object.getJSONArray("name");

                    for (int i = 0 ; i < ccs.length(); i ++) {
                        employeeList.add(new Employee((String) names.get(i), (int) ccs.get(i), 1l));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("wer", object+"");


                EmployeeListAdapter adapter = new EmployeeListAdapter(getContext(), R.layout.adapter_view_layout, employeeList);
                mListView.setAdapter(adapter);

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Deu Erro", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.show();
    }
}
