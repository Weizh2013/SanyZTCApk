package sany.com.mmpapp.business.government.firstActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.adapter.GAlarmDetailAdapter;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Alarm;


public class AlarmrecListActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    /*  private TextView tv_RightBtn;*/
   /*    private ElecFence elecFence; //电子围栏*/
    private ListView lv_alarmlist;
    private List<Alarm> alarmDetailList=new ArrayList<Alarm>();
    private GAlarmDetailAdapter galarmDetailAdapter;
    private JSONObject jsonRep;   //接收的JSON
    private JSONArray alarmDetailJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmrec_list);
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
        tv_Title.setText("违规列表");
        lv_alarmlist=(ListView)findViewById(R.id.alarmlist);

    }

    @Override
    protected void onResume() {
        super.onResume();

        queryAlarmDetail();

    }

    Handler alarmDetailHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    alarmDetailList.clear();
                    try {
                        alarmDetailJSONArray = jsonRep.getJSONArray("rows");
                        if(alarmDetailJSONArray.length()==0){
                            onBackPressed();
                        }
                        for (int i = 0; i < alarmDetailJSONArray.length(); i++) {
                            JSONObject tempObject=alarmDetailJSONArray.getJSONObject(i);
                            int id = tempObject.getInt("id");
                            String phoneNum = tempObject.getString("phoneNum");
                            String evVehiNo = tempObject.getString("evVehiNo");
                            String sfName = tempObject.getString("sfName");
                            String eiName = tempObject.getString("eiName");
                            String startTime = tempObject.getString("startTime");
                            String paraNameShow = tempObject.getString("paraNameShow");
                            String dealer = tempObject.getString("dealer");
                            String dealTime = tempObject.getString("dealTime");
                            String statusName = tempObject.getString("statusName");
                            String content=tempObject.getString("dealContent");
                            Alarm alarm=new Alarm(id,phoneNum,evVehiNo,sfName,eiName,startTime,paraNameShow,dealer,dealTime,statusName,content);
                            alarmDetailList.add(alarm);
                        }
                        galarmDetailAdapter=new GAlarmDetailAdapter(AlarmrecListActivity.this,alarmDetailList);
                        lv_alarmlist.setAdapter(galarmDetailAdapter);
                        lv_alarmlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Alarm tempAlarm=alarmDetailList.get(position);
                                int alarmid = tempAlarm.getId();
                                String phoneNum = tempAlarm.getPhoneNum();
                                String evVehiNo = tempAlarm.getEvVehiNo();
                                String sfName = tempAlarm.getSfName();
                                String eiName = tempAlarm.getEiName();
                                String startTime = tempAlarm.getStartTime();
                                String paraNameShow = tempAlarm.getParaNameShow();
                                String dealer = tempAlarm.getDealer();
                                String dealTime = tempAlarm.getDealTime();
                                String statusName = tempAlarm.getStatusName();
                                String content=tempAlarm.getContent();
                                Intent intent=new Intent(AlarmrecListActivity.this,AlarmDealActivity.class);
                                intent.putExtra("alarmid",alarmid);
                                intent.putExtra("phoneNum",phoneNum);
                                intent.putExtra("evVehiNo",evVehiNo);
                                intent.putExtra("sfName",sfName);
                                intent.putExtra("eiName",eiName);
                                intent.putExtra("startTime",startTime);
                                intent.putExtra("paraNameShow",paraNameShow);
                                intent.putExtra("dealer",dealer);
                                intent.putExtra("dealTime",dealTime);
                                intent.putExtra("statusName",statusName);
                                intent.putExtra("content",content);
                                startActivity(intent);
                            }
                        });
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


    private void queryAlarmDetail(){
        String url = APIInterface.DATAHOST + APIInterface.getAlarmRecDetail;
        AlarmDetailThread alarmDetailThread = new AlarmDetailThread(url);
        new Thread(alarmDetailThread).start();
    }

    class AlarmDetailThread implements Runnable {

        private String fullUrl;

        /**
         * @param url 地址
         */
        public AlarmDetailThread( String url) {
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
                alarmDetailHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                alarmDetailHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                alarmDetailHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                alarmDetailHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }



}
