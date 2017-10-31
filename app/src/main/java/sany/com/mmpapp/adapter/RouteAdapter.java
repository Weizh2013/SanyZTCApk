package sany.com.mmpapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.business.enterprise.firstActivity.RouteDisplayActivity;
import sany.com.mmpapp.model.ProjectInfo;
import sany.com.mmpapp.model.Route;
/**
 * Created by sunj7 on 16-12-5.
 * 路线适配器
 */
public class RouteAdapter extends ArrayAdapter<Route> {
       private Activity mMainActivity ;//必须要传进来
       private ProjectInfo projectInfo;
       private  Context ctx;
       public RouteAdapter(Context ctx ,List<Route> routeArrayList, Activity mMainActivity,ProjectInfo projectInfo) {
                super(ctx, R.layout.listview_route_item,routeArrayList);
           this.ctx=ctx;
           this.mMainActivity=mMainActivity;
           this.projectInfo=projectInfo;
        }
@Override
public View getView(int position, View convertView, ViewGroup parent) {
        // if we weren't given a view, inflate one
        if (null == convertView) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_route_item, null);
        }

        final Route  route = getItem(position);
        TextView pceiIdNameNo=(TextView)convertView.findViewById(R.id.pceiIdNameNo);
        pceiIdNameNo.setText("消纳场"+(position+1));
        LinearLayout layoutrouteDestLine=(LinearLayout)convertView.findViewById(R.id.routeDestLine);
        TextView pceiIdName =
        (TextView)convertView.findViewById(R.id.pceiIdName);
        pceiIdName.setText(route.getPceiIdName());
        TextView routeDest =
        (TextView)convertView.findViewById(R.id.routeDest);
        routeDest.setText(route.getRouteDest());
        layoutrouteDestLine.setOnClickListener(new View.OnClickListener() {
         @Override
                public void onClick(View v) {
             Intent intent=new Intent(mMainActivity, RouteDisplayActivity.class);
             intent.putExtra("workSiteName",projectInfo.getWorkSiteName());
             intent.putExtra("pceiIdName",route.getPceiIdName());
             intent.putExtra("routeId",route.getRouteId());
             mMainActivity.startActivity(intent);
                        }
        });
        return convertView;
        }
}
