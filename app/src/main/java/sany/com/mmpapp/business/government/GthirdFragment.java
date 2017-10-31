package sany.com.mmpapp.business.government;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.Util.Time;
import sany.com.mmpapp.adapter.AlarmExpandListAdapter;

import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Alarm;
import sany.com.mmpapp.model.Enterprise;


/**
 * Created by sunj7 on 16-12-8.
 */
public class GthirdFragment extends Fragment {
    private MmpApp mmpApp;
    private GovernmentMainActivity mMainActivity;

    private String statStartTime;
    private String statEndTime;
    private JSONObject jsonRep;
    private ExpandableListView alarmExpandList;
    private TextView tv_prompt;
    private List<Enterprise> groupList;
    private List<Alarm> childList;
    private AlarmExpandListAdapter alarmExpandListAdapter;
    private JSONArray alarmJSONArray;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainActivity = (GovernmentMainActivity) getActivity();
        mmpApp=(MmpApp)mMainActivity.getApplication();
        View view = inflater.inflate(R.layout.government_fg3, container, false);
        groupList = new ArrayList<Enterprise>();
        childList = new ArrayList<Alarm>();
        mMainActivity = (GovernmentMainActivity) getActivity();

        alarmExpandList=(ExpandableListView)view.findViewById(R.id.expandalarmList);
        tv_prompt=(TextView)view.findViewById(R.id.prompt);
        String[] startEndTime= Time.startAndEndTime(mmpApp.getTransportStartTime(), mmpApp.getTransportEndTime());
        statStartTime=startEndTime[0];
        statEndTime=startEndTime[1];
        startQueryAlarm(statStartTime,statEndTime);
        return view;
    }

    private void startQueryAlarm(String statStartTime,String statEndTime){
        String url = APIInterface.DATAHOST + APIInterface.countAlarmRecByType;
        AlarmThread alarmThread = new AlarmThread(statStartTime,statEndTime,url);
        new Thread(alarmThread).start();
    }

    Handler alarmListHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        alarmJSONArray=jsonRep.getJSONArray("rows");
                        for(int i=0;i<alarmJSONArray.length();i++){
                            //报警信息组合
                            JSONObject tempJSONObject=alarmJSONArray.getJSONObject(i);
                            String  eiName=tempJSONObject.getString("eiName");
                            String label=tempJSONObject.getString("label");
                            int  value=tempJSONObject.getInt("value");
                            Alarm alarm =new Alarm(eiName,label,value);
                           //过滤掉为“null”的报警信息
                            if(!("null".equals(label))) {
                                //如果是第一个数，直接加数据
                                if (groupList.size() == 0) {
                                    Enterprise enterprise = new Enterprise(eiName);
                                    groupList.add(enterprise);
                                } else {
                                    //如果不是第一个，遍历已有的元素，看数据是否已加载，
                                    int flag = 0;//数据是否加载的标志；
                                    for (Enterprise e : groupList) {
                                        if (e.getEiName().equals(eiName)) {
                                            flag = 1;
                                        }
                                    }
                                    //如果没有加载，则加载，
                                    if (flag == 0) {
                                        Enterprise enterprise = new Enterprise(eiName);
                                        groupList.add(enterprise);
                                    }
                                }
                                childList.add(alarm);
                            }
                        }
                        if(alarmJSONArray.length()==0){
                            tv_prompt.setVisibility(View.VISIBLE);

                        }else {
                            tv_prompt.setVisibility(View.GONE);
                            alarmExpandListAdapter = new AlarmExpandListAdapter(groupList, childList, mMainActivity.getApplicationContext(), mMainActivity);
                            alarmExpandList.setAdapter(alarmExpandListAdapter);
                            alarmExpandList.expandGroup(0);
                            alarmExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                                @Override
                                public void onGroupExpand(int groupPosition) {
                                    int count = alarmExpandList.getExpandableListAdapter().getGroupCount();
                                    for (int j = 0; j < count; j++) {
                                        if (j != groupPosition) {
                                            alarmExpandList.collapseGroup(j);
                                        }
                                    }
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

    //内部类，报警数据的获取
    class AlarmThread implements Runnable {
        private String startTime1;
        private String endTime1;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public AlarmThread(String startTime,String endTime,String url) {
            // TODO Auto-generated constructor stub
            this.startTime1=startTime;
            this.endTime1=endTime;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair startTime = new BasicNameValuePair(
                    "startTime", startTime1);
            paraLists.add(startTime);
            BasicNameValuePair endTime = new BasicNameValuePair(
                    "endTime",endTime1);
            paraLists.add(endTime);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                alarmListHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                alarmListHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                alarmListHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                alarmListHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }

}

