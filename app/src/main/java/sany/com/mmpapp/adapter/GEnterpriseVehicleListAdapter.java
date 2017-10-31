package sany.com.mmpapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.business.government.GovernmentMainActivity;
import sany.com.mmpapp.model.Enterprise;
import sany.com.mmpapp.model.Vehicle;

/**
 * Created by sunj7 on 16-12-26.
 */
public class GEnterpriseVehicleListAdapter extends BaseExpandableListAdapter {
    private GovernmentMainActivity mMainActivity ;//必须要传进来
    private List<Enterprise> enterpriseList=null;
    private List<Vehicle> vehicleList=null;
    private Context ctx;
    public GEnterpriseVehicleListAdapter(List<Enterprise> groupList, List<Vehicle> childList, Context ctx, GovernmentMainActivity mMainActivity){
        this.enterpriseList=groupList;
        this.vehicleList=childList;
        this.ctx=ctx;
        this.mMainActivity=mMainActivity;
    }
    @Override
    public int getGroupCount() {
        int count=(enterpriseList.size()>0) ? enterpriseList.size():0;
        return  count;
    }
    //改写
    @Override
    public int getChildrenCount(int groupPosition) {
        String  eiName=enterpriseList.get(groupPosition).getEiName();
        int count=0;
            for(Vehicle vehicle:vehicleList){
            if(vehicle.getVehEiName().equals(eiName)){
                count++;
            }
        }
        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return enterpriseList.get(groupPosition);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String eiName=enterpriseList.get(groupPosition).getEiName();
        List<Vehicle> childVehicleList=new ArrayList<Vehicle>();
        for(Vehicle vehicle:vehicleList){
            if(vehicle.getVehEiName().equals(eiName)){
                childVehicleList.add(vehicle);
            }
        }
        if(childVehicleList.size()>0){
            return childVehicleList.get(childPosition);
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
        RelativeLayout layout=(RelativeLayout)inflater.inflate(R.layout.listview_enterprise_item, null);
        TextView tv_enterprise=(TextView)layout.findViewById(R.id.enterprise);
        Enterprise enterprise=(Enterprise)getGroup(groupPosition);
        tv_enterprise.setText(enterprise.getEiName());
        return layout;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.listview_envehicle_item, null);
        TextView tv_evVehiNo=(TextView)layout.findViewById(R.id.evVehiNo);
        TextView tv_vehiclestatuse=(TextView)layout.findViewById(R.id.vehiclestatus);
        TextView tv_onlinestatustime=(TextView)layout.findViewById(R.id.onlinestatustime);
        Vehicle vehicle=(Vehicle)getChild(groupPosition,childPosition);
        tv_onlinestatustime.setText(vehicle.getOnlineStatusTime());
        tv_evVehiNo.setText(vehicle.getEv_vehiNo());
        if(vehicle.getIsOnline()==1){
            tv_vehiclestatuse.setText("在线");
            tv_vehiclestatuse.setTextColor(ctx.getResources().getColor(R.color.red));
            tv_onlinestatustime.setTextColor(ctx.getResources().getColor(R.color.green));

        }else{
            tv_vehiclestatuse.setText("离线");
        }
        return layout;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

