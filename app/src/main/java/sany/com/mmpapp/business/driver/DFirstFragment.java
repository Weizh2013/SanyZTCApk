package sany.com.mmpapp.business.driver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.adapter.PrjExpandListAdapter;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Project;
import sany.com.mmpapp.model.ProjectInfo;
import sany.com.mmpapp.model.Route;

/**
 * Created by sunj7 on 17-1-10.
 */
public class DFirstFragment extends Fragment {
    private MmpApp mmpApp;
    private DriverMainActivity mMainActivity;
    private ExpandableListView apprCertExpandList;
    private TextView tv_prompt;
    private List<Project> groupList;
    private List<ProjectInfo> childList;
    private PrjExpandListAdapter apprCertExpandListAdapter;//与工程共用一个适配器
    private JSONObject jsonRep;
    private JSONArray projectJSONArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_fg1, container, false);
        groupList = new ArrayList<Project>();
        childList = new ArrayList<ProjectInfo>();
        mMainActivity = (DriverMainActivity) getActivity();
        mmpApp = (MmpApp) mMainActivity.getApplication();
        apprCertExpandList = (ExpandableListView) view.findViewById(R.id.expandApprCertList);
        tv_prompt = (TextView) view.findViewById(R.id.prompt);
        queryApprCert();
        return view;
    }

    Handler apprCertListHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        projectJSONArray = jsonRep.getJSONArray("rows");
                        String temp = projectJSONArray.get(0).toString();
                        if (temp.equals("null")) {
                            return;
                        } else {
                            tv_prompt.setVisibility(View.GONE);
                            for (int i = 0; i < projectJSONArray.length(); i++) {
                                //工程数据的组合
                                JSONObject tempJSONObject = projectJSONArray.getJSONObject(i);
                                String pi_name = tempJSONObject.getString("acNum");//为了兼容工程信息，用核准证号代替原来的工程号
                                String pi_id = pi_name;//为了兼容企业的列表
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
                                apprCertExpandListAdapter = new PrjExpandListAdapter(groupList, childList, mMainActivity.getApplicationContext(), mMainActivity);
                                apprCertExpandList.setAdapter(apprCertExpandListAdapter);
                                apprCertExpandList.expandGroup(0);
                                apprCertExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                    @Override
                                    public void onGroupExpand(int groupPosition) {
                                        int count = apprCertExpandList.getExpandableListAdapter().getGroupCount();
                                        for (int j = 0; j < count; j++) {
                                            if (j != groupPosition) {
                                                apprCertExpandList.collapseGroup(j);
                                            }
                                        }
                                    }
                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case Http.SIG_BAD:
                    Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                case Http.SIG_BAD_JSON:
                    Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                case Http.ILL_URL:
                    Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                case Http.HOST_TIME_OUT:
                    Toast.makeText(mMainActivity,"网络异常，请求超时",Toast.LENGTH_SHORT).show();
                   break;
                case Http.OK_HOST:

                    break;
                default:
                    break;

            }
        }

    };

    //后台通过登录的用户名，关联车，通过车辆去查相关的核准证;
    //无参数
    private void queryApprCert() {
        //核准证数据的请求
        String url = APIInterface.DATAHOST + APIInterface.getApprCert;
        ApprCertThread apprCertThread = new ApprCertThread(url);
        new Thread(apprCertThread).start();
    }


    //内部类，核准证数据的请求线程
    class ApprCertThread implements Runnable {
        private String fullUrl;

        /**
         * @param url 地址
         */
        public ApprCertThread(String url) {
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
                apprCertListHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                apprCertListHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                apprCertListHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                apprCertListHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
