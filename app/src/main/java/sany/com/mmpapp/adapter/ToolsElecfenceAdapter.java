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
 * Created by sunj7 on 16-12-21.
 */
public class ToolsElecfenceAdapter extends ArrayAdapter<ElecFence> {
    private Context ctx;
    public ToolsElecfenceAdapter(Context ctx ,List<ElecFence> elecfenceArrayList) {
        super(ctx,R.layout.listview_toolselecfence_item,elecfenceArrayList);
        this.ctx=ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_toolselecfence_item, null);
        }
        String   efName = getItem(position).getEfName();
        TextView siteName=(TextView)convertView.findViewById(R.id.siteName);
        siteName.setText(efName);
        String operateStr=getItem(position).getOperationStr();
        TextView operatestr=(TextView)convertView.findViewById(R.id.operateStr);
        operatestr.setText(operateStr);
        return convertView;
    }
}
