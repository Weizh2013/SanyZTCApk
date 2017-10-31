package sany.com.mmpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.model.VehicleApprcert;
/**
 * Created by sunj7 on 16-12-7.
 */
public class VehicleApprcertAdapter extends ArrayAdapter<VehicleApprcert> {
    private Context ctx;
    public VehicleApprcertAdapter(Context ctx ,List<VehicleApprcert> vehicleApprcertArrayList) {
        super(ctx, R.layout.listview_vehicleapprcert_item,vehicleApprcertArrayList);
        this.ctx=ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_vehicleapprcert_item, null);
        }
        final VehicleApprcert  vehicleApprcert = getItem(position);
        TextView evVehiNo=(TextView)convertView.findViewById(R.id.evVehiNo);
        evVehiNo.setText(vehicleApprcert.getEvVehiNo());
        TextView acNum =(TextView)convertView.findViewById(R.id.acNum);
        acNum.setText(vehicleApprcert.getAcNum());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
