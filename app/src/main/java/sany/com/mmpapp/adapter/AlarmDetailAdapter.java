package sany.com.mmpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.model.Alarm;
/**
 * Created by sunj7 on 17-1-10.
 * 企业用户的报警详情
 *
 */
public class AlarmDetailAdapter extends ArrayAdapter<Alarm> {
    private Context ctx;

    public AlarmDetailAdapter(Context ctx, List<Alarm> alarmArrayList) {
        super(ctx, R.layout.listview_alarmdetail_item,alarmArrayList);
        this.ctx=ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_alarmdetail_item, null);
        }
        final Alarm alarm = getItem(position);
        TextView evVehiNo=(TextView)convertView.findViewById(R.id.evVehiNo);
        evVehiNo.setText(alarm.getEvVehiNo());
        TextView sfName=(TextView)convertView.findViewById(R.id.sfName);
        sfName.setText(alarm.getSfName());
        TextView paraNameShow=(TextView)convertView.findViewById(R.id.paraNameShow);
        paraNameShow.setText(alarm.getParaNameShow());
        TextView startTime=(TextView)convertView.findViewById(R.id.startTime);
        startTime.setText(alarm.getStartTime());
        return convertView;
    }
}

