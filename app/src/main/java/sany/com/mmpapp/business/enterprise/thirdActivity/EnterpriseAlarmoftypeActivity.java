package sany.com.mmpapp.business.enterprise.thirdActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
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

import sany.com.mmpapp.R;
import sany.com.mmpapp.adapter.AlarmAdapter;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Alarm;

public class EnterpriseAlarmoftypeActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private String startTime;
    private String endTime;
    private String type;
    private ListView lv_typeOfAlarm;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList=new ArrayList<Alarm>();

    private JSONObject jsonRep;
    private JSONArray alarmJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_alarmoftype);
        tv_LeftBtn=(TextView)findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        frameLayout=(FrameLayout)findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title=(TextView)findViewById(R.id.module_title);

        Intent intent=getIntent();
        startTime=intent.getStringExtra("startTime");
        endTime=intent.getStringExtra("endTime");
        type=intent.getStringExtra("type");
        if("0".equals(type)) {
            tv_Title.setText("报警类型统计");
        }else if("1".equals(type)){
            tv_Title.setText("车辆报警统计");
        }
        lv_typeOfAlarm=(ListView)findViewById(R.id.typeofalarmlist);
        queryAlarmRecByType(startTime,endTime,type);
    }
    private void queryAlarmRecByType(String startTime,String endTime,String type){
        String url = APIInterface.DATAHOST + APIInterface.countAlarmRecByType;
        VehicleThread  vehicleThread = new VehicleThread(startTime,endTime, type,url);
        new Thread(vehicleThread).start();
    }

    Handler alarmHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        alarmJSONArray=jsonRep.getJSONArray("rows");

                       /* {"rows":[{"efMapCoordinates":"113.007041,28.111678;113.016311,28.111678;113.016311,28.10759;113.007041,28.10759",
                       "efName":"得斯勤",
                       "efZoneType":1,
                       "efType":2,
                       "efId":99}]}*/
                        String tempev_vehiNo="";
                        String tempvehEiName;
                        String tempphoneNum;
                        for(int i=0;i<alarmJSONArray.length();i++){
                            //工程数据的组合
                            JSONObject tempJSONObject=alarmJSONArray.getJSONObject(i);

                            String templabel=tempJSONObject.getString("label");
                            int tempvalue=tempJSONObject.getInt("value");

                            alarmList.add(new Alarm(templabel, tempvalue));


                        }
                        alarmAdapter=new AlarmAdapter(EnterpriseAlarmoftypeActivity.this,alarmList);
                        lv_typeOfAlarm.setAdapter(alarmAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case Http.SIG_BAD:

                    break;
                case Http.SIG_BAD_JSON:

                    break;


                default:
                    break;

            }
        }

    };


    class VehicleThread implements Runnable {
        private String startTime1;
        private String endTime1;
        private String type1;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public VehicleThread(String startTime,String endTime,String type,String url) {
            // TODO Auto-generated constructor stub
            this.startTime1=startTime;
            this.endTime1=endTime;
            this.type1=type;
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
                    "endTime", endTime1);
            paraLists.add(endTime);
            BasicNameValuePair type = new BasicNameValuePair(
                    "type", type1);
            paraLists.add(type);

            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                alarmHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                alarmHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                alarmHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                alarmHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }


}
