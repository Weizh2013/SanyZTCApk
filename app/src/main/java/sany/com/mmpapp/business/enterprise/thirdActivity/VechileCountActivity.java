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
import sany.com.mmpapp.adapter.VehicleAdapter;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Vehicle;

public class VechileCountActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private String startTime;
    private String endTime;

    private JSONObject jsonRep;
    private JSONArray vehicleCountJSONArray;

    private ListView lv_vehiclecount;
    private List<Vehicle> vehicleCountList=new ArrayList<Vehicle>();
    private VehicleAdapter vehicleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vechile_count);
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
        tv_Title.setText("车辆运输趟次");
        lv_vehiclecount=(ListView)findViewById(R.id.vehiclecountlist);
        Intent intent=getIntent();
        startTime=intent.getStringExtra("startTime");
        endTime=intent.getStringExtra("endTime");
        queryVehicleCount(startTime, endTime);






    }


    private void queryVehicleCount(String startTime,String endTime){
        String url = APIInterface.DATAHOST + APIInterface.getVechileCount;
        VechileCountThread vechileCountThread = new VechileCountThread(startTime,endTime, url);
        new Thread(vechileCountThread).start();
    }

    Handler vehicleCountHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        vehicleCountJSONArray=jsonRep.getJSONArray("rows");
                        for(int i=0;i<vehicleCountJSONArray.length();i++){
                            JSONObject   tempObject=vehicleCountJSONArray.getJSONObject(i);
                            Vehicle tempVehicle=new Vehicle(tempObject.getString("ev_vehiNO"),"",tempObject.getInt("times"));
                            vehicleCountList.add(tempVehicle);
                        }
                        vehicleAdapter=new VehicleAdapter(VechileCountActivity.this,vehicleCountList,11);
                        lv_vehiclecount.setAdapter(vehicleAdapter);




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

    class VechileCountThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public VechileCountThread(String startTime,String endTime,String url) {
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
                vehicleCountHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                vehicleCountHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                vehicleCountHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                vehicleCountHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }





}
