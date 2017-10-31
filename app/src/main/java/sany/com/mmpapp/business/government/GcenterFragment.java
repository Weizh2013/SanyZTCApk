package sany.com.mmpapp.business.government;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import sany.com.mmpapp.adapter.GprojectListAdapter;
import sany.com.mmpapp.business.government.centerActivity.GprojectDetailActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.ProjectInfo;

/**
 * Created by sunj7 on 16-12-29.
 */
public class GcenterFragment extends Fragment{
    private MmpApp mmpApp;
    private GovernmentMainActivity mMainActivity;
    private ListView lv_project;
    private TextView tv_prompt;
    private GprojectListAdapter gprojectListAdapter;
    private JSONObject jsonRep;
    private JSONArray projectInfojsonArray;
    private List<ProjectInfo> projectInfoList;
    private int firstflag=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainActivity=(GovernmentMainActivity)getActivity();
        mmpApp=(MmpApp)getActivity().getApplication();
        projectInfoList=new ArrayList<ProjectInfo>();
        View view = inflater.inflate(R.layout.government_cfg, container, false);
        lv_project=(ListView)view.findViewById(R.id.lv_project);
        tv_prompt=(TextView)view.findViewById(R.id.prompt);

        queryprojectlist();
        return view;
    }

    private void queryprojectlist(){
        firstflag=1;
        String url = APIInterface.DATAHOST + APIInterface.listProj;
        ProjectListThread projectListThread = new ProjectListThread(url);
        new Thread(projectListThread).start();
    }

    Handler projectListHandler=  new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        projectInfojsonArray=jsonRep.getJSONArray("rows");
                        if(projectInfojsonArray.length()==0){
                            projectInfoList.clear();
                            gprojectListAdapter=new GprojectListAdapter(mMainActivity.getApplicationContext(),projectInfoList,mMainActivity);
                            lv_project.setAdapter(gprojectListAdapter);
                            tv_prompt.setVisibility(View.VISIBLE);
                            return;
                        }else{
                            tv_prompt.setVisibility(View.GONE);
                            projectInfoList.clear();
                            for(int i=0;i<projectInfojsonArray.length();i++){
                                JSONObject tempObject=projectInfojsonArray.getJSONObject(i);
                                String pi_id=tempObject.getString("piId");
                                String piWorkSite=tempObject.getString("piWorkSite");
                                String piName=tempObject.getString("piName");
                                String efName=tempObject.getString("efName");
                                String piConsFiels=tempObject.getString("piConsField");
                                String piTranUnitName=tempObject.getString("piTranUnitName");
                                String piConsUnitName=tempObject.getString("piConsUnitName");
                                String starTime=tempObject.getString("piStartTime");
                                String endTime=tempObject.getString("piEndTime");
                                String shipStartTime=tempObject.getString("piShipStartTime");
                                String shipEndTime=tempObject.getString("piShipEndTime");
                                projectInfoList.add(new ProjectInfo(pi_id,piWorkSite,piName,efName,
                                        piConsFiels,piTranUnitName,piConsUnitName,starTime,endTime,shipStartTime,shipEndTime));
                            }
                            gprojectListAdapter=new GprojectListAdapter(mMainActivity.getApplicationContext(),projectInfoList,mMainActivity);
                            lv_project.setAdapter(gprojectListAdapter);
                            lv_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    firstflag=0;
                                     ProjectInfo projectInfo=projectInfoList.get(position);
                                     Intent nextIntent=new Intent(mMainActivity, GprojectDetailActivity.class);
                                     nextIntent.putExtra("piId",projectInfo.getPi_id());
                                     nextIntent.putExtra("piWorkSiteId",projectInfo.getWorkSiteId());
                                     nextIntent.putExtra("piName",projectInfo.getPiName());
                                     nextIntent.putExtra("efName",projectInfo.getEfName());
                                     nextIntent.putExtra("piConsFiels",projectInfo.getPiConsFiels());
                                     nextIntent.putExtra("piTranUnitName",projectInfo.getPiTranUnitName());
                                     nextIntent.putExtra("piConsUnitName",projectInfo.getPiConsUnitName());
                                     nextIntent.putExtra("startTime",projectInfo.getStarTime());
                                     nextIntent.putExtra("endTime",projectInfo.getEndTime());
                                     nextIntent.putExtra("shipStartTime",projectInfo.getShipStartTime());
                                     nextIntent.putExtra("shipEndTime",projectInfo.getShipEndTime());
                                     mMainActivity.startActivity(nextIntent);
                                }
                            });
                        }
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
            BasicNameValuePair piStatus = new BasicNameValuePair(
                    "piStatus", "2");
            paraLists.add(piStatus);
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

    @Override
    public void onResume() {
        super.onResume();
        if(firstflag==0) {
            queryprojectlist();
        }
    }
}
