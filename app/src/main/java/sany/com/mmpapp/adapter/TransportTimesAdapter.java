package sany.com.mmpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.model.TransportTimes;
/**
 * Created by sunj7 on 17-1-11.
 */
public class TransportTimesAdapter extends ArrayAdapter<TransportTimes> {
    private Context ctx;
    private int type;
    public TransportTimesAdapter(Context ctx ,List<TransportTimes> vehiclesArrayList) {
        super(ctx, R.layout.listview_transporttimes_item,vehiclesArrayList);
        this.ctx=ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_transporttimes_item, null);
        }

        final TransportTimes tm = getItem(position);
        TextView ev_vehiNo=(TextView)convertView.findViewById(R.id.workSiteName);
        ev_vehiNo.setText(tm.getWorkSiteName());
        TextView vehEiName =(TextView)convertView.findViewById(R.id.consFieldName);
        vehEiName.setText(tm.getConsFieldName());
        TextView phoneNum=(TextView)convertView.findViewById(R.id.gpsTime);
        phoneNum.setText(""+tm.getGpsTime());
       return convertView;
    }
}
