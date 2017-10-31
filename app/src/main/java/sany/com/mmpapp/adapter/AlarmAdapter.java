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
 * 企业登录时，按用按报警类型查看时展示的内容。
 */
public class AlarmAdapter extends ArrayAdapter<Alarm> {
    private Context ctx;

    public AlarmAdapter(Context ctx, List<Alarm> alarmArrayList) {
        super(ctx, R.layout.listview_alarm_item,alarmArrayList);
        this.ctx=ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_alarm_item, null);
        }

        final Alarm alarm = getItem(position);
        TextView alarmlabel=(TextView)convertView.findViewById(R.id.alarmlabel);
        alarmlabel.setText(alarm.getLabel());
        TextView alarmvalue =(TextView)convertView.findViewById(R.id.alarmvalue);
        alarmvalue.setText(""+alarm.getValue());
        return convertView;
    }
}
