package sany.com.mmpapp.business.driver.ThirdActivity;

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
import sany.com.mmpapp.adapter.TransportTimesAdapter;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.TransportTimes;

public class TransportTimesDetailActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private String startTime;
    private String endTime;
    private ListView lv_transporttimesdetail;
    private TransportTimesAdapter transportTimesAdapter;
    private List<TransportTimes> transportTimesList=new ArrayList<TransportTimes>();
    private JSONObject jsonRep;
    private JSONArray transportTimesDetailJSONArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_times_detail);
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
        tv_Title.setText("报警详情");
        lv_transporttimesdetail=(ListView)findViewById(R.id.transportdetail);
        Intent intent=getIntent();
        startTime=intent.getStringExtra("startTime");
        endTime=intent.getStringExtra("endTime");
        queryTransportTimesDetail(startTime, endTime);
    }
    private void queryTransportTimesDetail(String startTime,String endTime){
        String url = APIInterface.DATAHOST + APIInterface.getTripDetaiByfStaff;
        TransportTimesDetailThread  transportTimesDetailThread = new TransportTimesDetailThread(startTime,endTime,url);
        new Thread(transportTimesDetailThread).start();
    }

    Handler transportTimesHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        transportTimesDetailJSONArray=jsonRep.getJSONArray("rows");

                       /* {"rows":[{"efMapCoordinates":"113.007041,28.111678;113.016311,28.111678;113.016311,28.10759;113.007041,28.10759",
                       "efName":"得斯勤",
                       "efZoneType":1,
                       "efType":2,
                       "efId":99}]}*/
                        String tempworkSiteName="";
                        String tempconsFieldName="";
                        String tempgpsTime="";
                        for(int i=0;i<transportTimesDetailJSONArray.length();i++){
                            //工程数据的组合
                            JSONObject tempJSONObject=transportTimesDetailJSONArray.getJSONObject(i);
                            tempworkSiteName=tempJSONObject.getString("workSiteName");
                            tempconsFieldName=tempJSONObject.getString("consFieldName");
                            tempgpsTime=tempJSONObject.getString("gpsTime");
                            transportTimesList.add(new TransportTimes(tempworkSiteName, tempconsFieldName,tempgpsTime));
                        }
                        transportTimesAdapter=new TransportTimesAdapter(TransportTimesDetailActivity.this,transportTimesList);
                        lv_transporttimesdetail.setAdapter(transportTimesAdapter);
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


    class TransportTimesDetailThread implements Runnable {
        private String startTime1;
        private String endTime1;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public TransportTimesDetailThread(String startTime,String endTime,String url) {
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
                    "endTime", endTime1);
            paraLists.add(endTime);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                transportTimesHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                transportTimesHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                transportTimesHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                transportTimesHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
