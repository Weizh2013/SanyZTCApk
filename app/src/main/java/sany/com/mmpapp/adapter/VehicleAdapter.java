package sany.com.mmpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.model.Vehicle;
/**
 * Created by sunj7 on 16-12-12.
 */
public class VehicleAdapter extends ArrayAdapter<Vehicle> {
    private Context ctx;
    private int type;
    public VehicleAdapter(Context ctx ,List<Vehicle> vehiclesArrayList, int type) {
        super(ctx, R.layout.listview_vehicle_item,vehiclesArrayList);
        this.ctx=ctx;
        this.type=type;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_vehicle_item, null);
        }

        final Vehicle vehicle = getItem(position);
        TextView ev_vehiNo=(TextView)convertView.findViewById(R.id.ev_vehiNo);
        ev_vehiNo.setText(vehicle.getEv_vehiNo());
        TextView vehEiName =
                (TextView)convertView.findViewById(R.id.vehEiName);
        vehEiName.setText(vehicle.getVehEiName());
        TextView phoneNum=(TextView)convertView.findViewById(R.id.phoneNum);
        if(type==11){
            phoneNum.setText(""+vehicle.getCount());
        }else {
            phoneNum.setText(vehicle.getPhoneNum());
            //当来这个地方显示电话号码，主要是用于车辆位置的显示，现在车辆位置的显示用在监控里显示。
            //phoneNum.setVisibility(View.GONE);
        }
        return convertView;
    }
}
