package sany.com.mmpapp.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.business.government.GovernmentMainActivity;
import sany.com.mmpapp.model.ProjectInfo;

/**
 * Created by sunj7 on 16-12-29.
 */
public class GprojectListAdapter extends ArrayAdapter<ProjectInfo> {
    private GovernmentMainActivity mMainActivity ;//必须要传进来
    private List<ProjectInfo> projectInfoList=new ArrayList<ProjectInfo>();

    private Context ctx;
    public GprojectListAdapter(Context ctx ,List<ProjectInfo> projectInfoList, GovernmentMainActivity mMainActivity) {
        super(ctx, R.layout.listview_gproject_item,projectInfoList);
        this.projectInfoList=projectInfoList;
        this.ctx=ctx;
        this.mMainActivity=mMainActivity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_gproject_item, null);
        }
        final ProjectInfo  projectInfo = getItem(position);
        TextView piName=(TextView)convertView.findViewById(R.id.piName);
        piName.setText(projectInfo.getPiName());
        TextView efName=(TextView)convertView.findViewById(R.id.efName);
        efName.setText(projectInfo.getEfName());
        return convertView;
    }

    @Override
    public int getCount() {
        return projectInfoList.size();
    }

    @Override
    public ProjectInfo getItem(int position) {
        return projectInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

