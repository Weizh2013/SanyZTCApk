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
import sany.com.mmpapp.model.Alarm;
import sany.com.mmpapp.model.Enterprise;
/**
 * Created by sunj7 on 16-12-12.
 */
public class AlarmExpandListAdapter extends BaseExpandableListAdapter {
    private GovernmentMainActivity mMainActivity ;//必须要传进来
    private List<Enterprise> enterpriseList=null;
    private List<Alarm> alarmList=null;
    private Context ctx;
    public  AlarmExpandListAdapter(List<Enterprise> groupList,List<Alarm> childList,Context ctx,GovernmentMainActivity mMainActivity){
        this.enterpriseList=groupList;
        this.alarmList=childList;
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
        for(Alarm alarm:alarmList){
            if(alarm.getEiName().equals(eiName)){
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
        List<Alarm> childAlarmList=new ArrayList<Alarm>();
        for(Alarm alarm:alarmList){
            if(alarm.getEiName().equals(eiName)){
                childAlarmList.add(alarm);
            }
        }
        if(childAlarmList.size()>0){
            return childAlarmList.get(childPosition);
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
        LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.listview_alarm_item, null);
        TextView tv_alarmlabel=(TextView)layout.findViewById(R.id.alarmlabel);
        TextView tv_alarmvalue=(TextView)layout.findViewById(R.id.alarmvalue);
        Alarm alarm=(Alarm)getChild(groupPosition,childPosition);
        tv_alarmlabel.setText(alarm.getLabel());
        tv_alarmvalue.setText(""+alarm.getValue());
        return layout;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
