package sany.com.mmpapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.business.driver.DriverMainActivity;
import sany.com.mmpapp.business.enterprise.firstActivity.VehicleApprcertActivity;
import sany.com.mmpapp.business.enterprise.firstActivity.WorkSiteDisplayActivity;
import sany.com.mmpapp.model.Project;
import sany.com.mmpapp.model.ProjectInfo;
import sany.com.mmpapp.model.Route;

/**
 * Created by sunj7 on 16-12-2
 * 工程数列表适配器
 */
public class PrjExpandListAdapter extends BaseExpandableListAdapter {
    private Activity mMainActivity ;//必须要传进来

    private List<Project> projectList=null;
    private List<ProjectInfo> projectInfoList=null;
    private Context ctx;
    public  PrjExpandListAdapter(List<Project> groupList,List<ProjectInfo> childList,Context ctx,Activity mMainActivity){
        this.projectList=groupList;
        this.projectInfoList=childList;
        this.ctx=ctx;
        this.mMainActivity=mMainActivity;
    }
    @Override
    public int getGroupCount() {
        int count=(projectList.size()>0) ? projectList.size():0;
        return  count;
    }
    //改写
    @Override
    public int getChildrenCount(int groupPosition) {
        String  prjid=projectList.get(groupPosition).getPi_id();
        int count=0;
        for(ProjectInfo projectInfo:projectInfoList){
            if(projectInfo.getPi_id().equals(prjid)){
                count++;
            }
        }
        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return projectList.get(groupPosition);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String prjid=projectList.get(groupPosition).getPi_id();
        List<ProjectInfo> childProjectInfoList=new ArrayList<ProjectInfo>();
        for(ProjectInfo projectInfo:projectInfoList){
            if(projectInfo.getPi_id().equals(prjid)){
                childProjectInfoList.add(projectInfo);
            }
        }
        if(childProjectInfoList.size()>0){
            return childProjectInfoList.get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout=(RelativeLayout)inflater.inflate(R.layout.listview_project_item, null);
        TextView tv_ProjectName=(TextView)layout.findViewById(R.id.project);
        ProgressBar progressBar=(ProgressBar)layout.findViewById(R.id.progbar_parentview);
        Project project=(Project)getGroup(groupPosition);
        tv_ProjectName.setText(project.getPi_name());
        if(project.isLoading())
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
        return layout;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.listview_projectinfo_item, null);
        LinearLayout layoutworkSiteLine=(LinearLayout)layout.findViewById(R.id.workSiteLine);
        TextView tv_workSiteName=(TextView)layout.findViewById(R.id.workSiteName);
        TextView tv_starTime=(TextView)layout.findViewById(R.id.starTime);
        TextView tv_endTime=(TextView)layout.findViewById(R.id.endTime);
        TextView tv_shipStartTime=(TextView)layout.findViewById(R.id.shipStartTime);
        TextView tv_shipEndTime=(TextView)layout.findViewById(R.id.shipEndTime);
        LinearLayout layoutappandvehicleLine=(LinearLayout)layout.findViewById(R.id.appandvehicleLine);
        TextView tv_appandvehicle=(TextView)layout.findViewById(R.id.appandvehicle);
        LinearLayout routelistLine=(LinearLayout)layout.findViewById(R.id.routelistLine);
        ListView lv_routelist=(ListView)layout.findViewById(R.id.routelist);
        ProjectInfo prjinfo=(ProjectInfo)getChild(groupPosition,childPosition);
        tv_workSiteName.setText(prjinfo.getWorkSiteName());
        tv_starTime.setText(prjinfo.getStarTime());
        tv_endTime.setText(prjinfo.getEndTime());
        tv_shipStartTime.setText(prjinfo.getShipStartTime());
        tv_shipEndTime.setText(prjinfo.getShipEndTime());
        layoutworkSiteLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectInfo projectInfo=(ProjectInfo)getChild(groupPosition,childPosition);
                //Toast.makeText(ctx,projectInfo.getWorkSiteId()+":"+projectInfo.getWorkSiteName(),Toast.LENGTH_SHORT).show();
                //页页的跳转
                Intent nextActivity = new Intent(mMainActivity,
                        WorkSiteDisplayActivity.class);
                nextActivity.putExtra("workSiteId", projectInfo.getWorkSiteId());
                nextActivity.putExtra("workSiteName",projectInfo.getWorkSiteName());
                mMainActivity.startActivity(nextActivity);
            }
        });
        if(mMainActivity.getClass().equals(DriverMainActivity.class)){
            layoutappandvehicleLine.setVisibility(View.GONE);

        }else {
            layoutappandvehicleLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectInfo projectInfo = (ProjectInfo) getChild(groupPosition, childPosition);
                    //Toast.makeText(ctx,projectInfo.getPi_id(),Toast.LENGTH_SHORT).show();
                    Intent nextActivity = new Intent(mMainActivity,
                            VehicleApprcertActivity.class);
                    nextActivity.putExtra("piId", projectInfo.getPi_id());
                    mMainActivity.startActivity(nextActivity);
                }
            });
        }
        List<Route> routeList=prjinfo.getRouteList();
        //设置路线列表的高度
        LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams) routelistLine.getLayoutParams();
        layoutParams.height=layoutParams.height*routeList.size();
        routelistLine.setLayoutParams(layoutParams);
        //路线适配器的
        RouteAdapter routeAdapter=new RouteAdapter(ctx,routeList, mMainActivity,prjinfo);
        lv_routelist.setAdapter(routeAdapter);
        return layout;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
