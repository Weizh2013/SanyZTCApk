package sany.com.mmpapp.business.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.Util.Time;
import sany.com.mmpapp.adapter.PrjExpandListAdapter;
import sany.com.mmpapp.business.enterprise.firstActivity.EnterpriseAlarmActivity;
import sany.com.mmpapp.business.government.firstActivity.VehicleListActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Project;
import sany.com.mmpapp.model.ProjectInfo;
import sany.com.mmpapp.model.Route;

/**
 * Created by sunj7 on 16-8-19.
 */
public class FirstFragment extends Fragment implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupExpandListener {
    private MmpApp mmpApp;
    private EnterpriseMainActivity mMainActivity ;
    private ExpandableListView projectExpandList;
    private LinearLayout ll_runningvehicle;
    private LinearLayout ll_alarmtotal;
    private TextView tv_runningvehicle;
    private TextView tv_alarmtotal;
    private TextView tv_projectTitle;
    private List<Project> groupList;
    private List<ProjectInfo> childList;
    private PrjExpandListAdapter prjExpandListAdapter;
    private String statStartTime;
    private String statEndTime;
    private JSONObject jsonRep;
    private JSONArray projectJSONArray;
    public final static int SIG_GOOD_STATINFO=0x11;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enterprise_fg1, container, false);
        ll_runningvehicle=(LinearLayout)view.findViewById(R.id.runningvehicle);
        ll_runningvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainActivity,"running",Toast.LENGTH_SHORT).show();
                String temp=tv_runningvehicle.getText().toString();
                if("0".equals(temp)){
                    Toast toast=Toast.makeText(mMainActivity, "无开工车辆", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    Intent nextIntent = new Intent(mMainActivity, VehicleListActivity.class);
                    nextIntent.putExtra("statStartTime", statStartTime);
                    nextIntent.putExtra("statEndTime", statEndTime);
                    nextIntent.putExtra("type", "1");
                    nextIntent.putExtra("parentActivity", "enterprise");
                    mMainActivity.startActivity(nextIntent);
                }
            }
        });
        tv_runningvehicle=(TextView)view.findViewById(R.id.runningvehicle_text);
        ll_alarmtotal=(LinearLayout)view.findViewById(R.id.alarmtotal);
        ll_alarmtotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainActivity,"alarm",Toast.LENGTH_SHORT).show();
                String temp=tv_alarmtotal.getText().toString();
                if("0".equals(temp)){

                    Toast toast=Toast.makeText(mMainActivity, "无报警信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    Intent nextIntent = new Intent(mMainActivity, EnterpriseAlarmActivity.class);
                    nextIntent.putExtra("statStartTime", statStartTime);
                    nextIntent.putExtra("statEndTime", statEndTime);
                    mMainActivity.startActivity(nextIntent);
                }
            }
        });
        tv_alarmtotal=(TextView)view.findViewById(R.id.alarmtotal_text);
        tv_projectTitle=(TextView)view.findViewById(R.id.projectTitle);
        //加载工程列表
        groupList = new ArrayList<Project>();
        childList = new ArrayList<ProjectInfo>();
        mMainActivity = (EnterpriseMainActivity) getActivity();
        mmpApp=(MmpApp)mMainActivity.getApplication();
        String[] startEndTime= Time.startAndEndTime(mmpApp.getTransportStartTime(), mmpApp.getTransportEndTime());
        statStartTime=startEndTime[0];
        statEndTime=startEndTime[1];
        projectExpandList=(ExpandableListView)view.findViewById(R.id.expandPrjList);
        queryStatInforEnte(statStartTime, statEndTime);
        return view;
    }

    Handler projectListHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
           // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        projectJSONArray=jsonRep.getJSONArray("rows");
                        String temp = projectJSONArray.get(0).toString();
                        if (temp.equals("null")) {
                            return;
                        }else {
                            tv_projectTitle.setVisibility(View.VISIBLE);
                           for (int i = 0; i < projectJSONArray.length(); i++) {
                                //工程数据的组合
                                JSONObject tempJSONObject = projectJSONArray.getJSONObject(i);
                                String pi_name = tempJSONObject.getString("pi_name");
                                String pi_id = tempJSONObject.getString("pi_id");
                                String workSiteId = tempJSONObject.getString("workSiteId");
                                String workSiteName = tempJSONObject.getString("workSiteName");
                                String starTime = tempJSONObject.getString("starTime");
                                String endTime = tempJSONObject.getString("endTime");
                                String shipStartTime = tempJSONObject.getString("shipStartTime");
                                String shipEndTime = tempJSONObject.getString("shipEndTime");
                                ArrayList<Route> routeList = new ArrayList<Route>();
                                JSONArray routeJSONArray = tempJSONObject.getJSONArray("routeInf");
                                //路线数据的组合
                                for (int j = 0; j < routeJSONArray.length(); j++) {
                                    JSONObject tempRouteJSONObject = routeJSONArray.getJSONObject(j);
                                    String routeDest = tempRouteJSONObject.getString("routeDest");
                                    String pceiIdName = tempRouteJSONObject.getString("pceiIdName");
                                    String pceiId = tempRouteJSONObject.getString("pceiId");
                                    String routeId = tempRouteJSONObject.getString("routeId");
                                    Route tempRoute = new Route(routeDest, pceiIdName, pceiId, routeId);
                                    routeList.add(tempRoute);
                                }
                                ProjectInfo projectInfo = new ProjectInfo(pi_id, workSiteId, workSiteName, starTime, endTime, shipStartTime, shipEndTime, routeList);
                                Project project = new Project(pi_name, pi_id);
                                groupList.add(project);
                                childList.add(projectInfo);
                                prjExpandListAdapter = new PrjExpandListAdapter(groupList, childList, mMainActivity.getApplicationContext(), mMainActivity);
                                projectExpandList.setAdapter(prjExpandListAdapter);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_STATINFO:
                    try {
                        projectJSONArray=jsonRep.getJSONArray("rows");
                        for(int i=0;i<projectJSONArray.length();i++){
                            JSONObject tempJSONObject=projectJSONArray.getJSONObject(i);
                            if("开工车辆".equals(tempJSONObject.getString("label"))){
                                tv_runningvehicle.setText(""+tempJSONObject.getInt("value"));
                            }else if("报警总数".equals(tempJSONObject.getString("label"))){
                                tv_alarmtotal.setText(""+tempJSONObject.getInt("value"));
                            }
                        }
                        startQueryProject();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Http.SIG_BAD:

                    break;
                case Http.SIG_BAD_JSON:

                    break;
                case Http.ILL_URL:

                    break;
                case Http.HOST_TIME_OUT:

                    break;
                case Http.OK_HOST:

                    break;
                default:
                    break;

            }
        }

    };
    private void queryStatInforEnte(String startTime,String endTime){
        String url = APIInterface.DATAHOST + APIInterface.getInitStatInfoForEnte;
        StatInfoForEnteThread statInfoForEnteThread = new StatInfoForEnteThread(startTime,endTime, url);
        new Thread(statInfoForEnteThread).start();
    }

    private void startQueryProject() {
        //工程数据的请求
        String url = APIInterface.DATAHOST + APIInterface.getProjectInfoByPITranUnit;
        ProjectListThread projectListThread = new ProjectListThread( url);
        new Thread(projectListThread).start();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {

    }

    class StatInfoForEnteThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public StatInfoForEnteThread(String startTime,String endTime,String url) {
            // TODO Auto-generated constructor stub
            this.estartTime=startTime;
            this.eendTime=endTime;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair startTime = new BasicNameValuePair(
                    "startTime", estartTime);
            paraLists.add(startTime);
            BasicNameValuePair endTime = new BasicNameValuePair(
                    "endTime",eendTime);
            paraLists.add(endTime);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                projectListHandler.sendEmptyMessage(SIG_GOOD_STATINFO);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                projectListHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                projectListHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                projectListHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }





    //内部类，工程数数据的请求线程
    class ProjectListThread implements Runnable {
        private String fullUrl;
        /**
         * @param url    地址
         */
        public ProjectListThread(String url) {
            // TODO Auto-generated constructor stub
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                projectListHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                projectListHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                projectListHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                projectListHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }


}