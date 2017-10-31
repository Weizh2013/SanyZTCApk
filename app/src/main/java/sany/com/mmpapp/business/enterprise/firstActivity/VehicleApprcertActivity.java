package sany.com.mmpapp.business.enterprise.firstActivity;

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
import sany.com.mmpapp.adapter.VehicleApprcertAdapter;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.ElecFence;
import sany.com.mmpapp.model.VehicleApprcert;

public class VehicleApprcertActivity extends Activity {
    //标题栏
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private TextView tv_RightBtn;
    private String piId;
    private JSONObject jsonRep;   //接收的JSON
    private JSONArray vehicleApprecertJSONArray;
    private ElecFence elecFence; //电子围栏
    private ListView lv_vehicleApprecert;
    private List<VehicleApprcert> vehicleApprcertList;
    private VehicleApprcertAdapter vehicleApprcertAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_apprcert);
        Intent intent=getIntent();
        piId=intent.getStringExtra("piId");
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
        tv_Title.setText(R.string.title_vehicle_apprcert);
        lv_vehicleApprecert=(ListView)findViewById(R.id.vehicleapprcert_listview);
        vehicleApprcertList=new ArrayList<VehicleApprcert>();
        //请求数据进行展示
        queryVehicleApprecert(piId);
    }


    private void queryVehicleApprecert(String piId){
        String url = APIInterface.DATAHOST + APIInterface.getApprCertOfBoss;
        VehicleApprecertThread vehicleApprecertThread = new VehicleApprecertThread(piId, url);
        new Thread(vehicleApprecertThread).start();
    }

    Handler vehicleApprecertHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        vehicleApprecertJSONArray=jsonRep.getJSONArray("rows");
                        for(int i=0;i<vehicleApprecertJSONArray.length();i++){
                            //工程数据的组合
                            JSONObject tempJSONObject=vehicleApprecertJSONArray.getJSONObject(i);
                            vehicleApprcertList.add(new VehicleApprcert(tempJSONObject.getString("evVehiNo"),tempJSONObject.getString("acNum")));
                        }
                        vehicleApprcertAdapter=new VehicleApprcertAdapter(getApplicationContext(),vehicleApprcertList);
                        lv_vehicleApprecert.setAdapter(vehicleApprcertAdapter);

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



    class VehicleApprecertThread implements Runnable {
        private String piId;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public VehicleApprecertThread(String piId,String url) {
            // TODO Auto-generated constructor stub
            this.piId=piId;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_Name = new BasicNameValuePair(
                    "piId", piId);
            paraLists.add(param_Name);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                vehicleApprecertHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                vehicleApprecertHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                vehicleApprecertHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                vehicleApprecertHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
