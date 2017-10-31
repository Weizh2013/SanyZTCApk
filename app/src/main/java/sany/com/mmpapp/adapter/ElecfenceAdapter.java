package sany.com.mmpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.model.ElecFence;

/**
 * Created by sunj7 on 16-12-9.
 */
public class ElecfenceAdapter extends ArrayAdapter<ElecFence> {
    private Context ctx;
    public ElecfenceAdapter(Context ctx ,List<ElecFence> elecfenceArrayList) {
        super(ctx, R.layout.listview_elecfence_item,elecfenceArrayList);
        this.ctx=ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_elecfence_item, null);
        }
        final ElecFence elecFence = getItem(position);
        TextView siteName=(TextView)convertView.findViewById(R.id.siteName);
        siteName.setText(elecFence.getEfName());
        TextView efAreaName =(TextView)convertView.findViewById(R.id.efAreaName);
        efAreaName.setText(elecFence.getEfAreaName());
        return convertView;
    }
}
