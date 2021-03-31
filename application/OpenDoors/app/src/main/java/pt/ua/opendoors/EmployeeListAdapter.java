package pt.ua.opendoors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EmployeeListAdapter extends ArrayAdapter<Employee> {

    private static final String TAG = "PersonListAdapter";
    private Context mContext;
    private int mResource;

    /**
     *
     * @param context
     * @param resource
     * @param objects
     */
    public EmployeeListAdapter(Context context, int resource, List<Employee> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,ViewGroup parent) {
        String eName = getItem(position).geteName();
        long eCC = getItem(position).geteCC();
        long eLoja = getItem(position).geteLoja();

        Employee employee = new Employee(eName, eCC, eLoja);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.eName);
        TextView tvCC = convertView.findViewById(R.id.eCC);
        TextView tvLoja = convertView.findViewById(R.id.eLoja);

        tvName.setText(employee.geteName());
        tvCC.setText(employee.geteCC()+"");
        tvLoja.setText(employee.geteLoja()+"");

        return convertView;
    }
}
