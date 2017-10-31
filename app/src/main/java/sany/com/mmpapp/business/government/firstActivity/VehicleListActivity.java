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

/**
 * Created by sunj7 on 16-12-9.
 */
public class VehicleListActivity extends Activity {

    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private ListView lv_vehiclelist;
    private List<Vehicle> vehicleList;
    private VehicleAdapter vehicleAdapter;
    private String statStartTime;
    private String statEndTime;
    private String type;
    private String parentActivity;
    private JSONObject jsonRep;   //接收的JSON
    private JSONArray vehicleJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        statStartTime=intent.getStringExtra("statStartTime");
        statEndTime=intent.getStringExtra("statEndTime");
        type=intent.getStringExtra("type");
        parentActivity=intent.getStringExtra("parentActivity");
        setContentView(R.layout.activity_vehicle_list);
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
        tv_Title.setText(R.string.vehiclelist);
        lv_vehiclelist=(ListView)findViewById(R.id.vehiclelist);
        vehicleList=new ArrayList<Vehicle>();
        //请求数据
        queryVehicleData(statStartTime, statEndTime, type);
    }

    private void queryVehicleData(String startTime,String endTime,String type){
        String url = APIInterface.DATAHOST + APIInterface.listWorkedInfo;
        VehicleThread  vehicleThread = new VehicleThread(startTime,endTime, type,url);
        new Thread(vehicleThread).start();
    }

    Handler vehicleHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        vehicleJSONArray=jsonRep.getJSONArray("rows");

                       /* {"rows":[{"efMapCoordinates":"113.007041,28.111678;113.016311,28.111678;113.016311,28.10759;113.007041,28.10759",
                       "efName":"得斯勤",
                       "efZoneType":1,
                       "efType":2,
                       "efId":99}]}*/
                        String tempev_vehiNo="";
                        String tempvehEiName;
                        String tempphoneNum;
                        String sfName="";
                        for(int i=0;i<vehicleJSONArray.length();i++){
                            //工程数据的组合
                            JSONObject tempJSONObject=vehicleJSONArray.getJSONObject(i);
                            tempev_vehiNo=tempJSONObject.getString("ev_vehiNo");
                            tempphoneNum= tempJSONObject.getString("phoneNum");
                            if("enterprise".equals(parentActivity)){
                                sfName=tempJSONObject.getString("sfName");
                                vehicleList.add(new Vehicle(tempev_vehiNo, "", sfName));
                            }else {
                                tempvehEiName = tempJSONObject.getString("vehEiName");
                                vehicleList.add(new Vehicle(tempev_vehiNo, tempvehEiName, tempphoneNum));
                            }
                        }
                        vehicleAdapter=new VehicleAdapter(getApplicationContext(),vehicleList,0);
                        lv_vehiclelist.setAdapter(vehicleAdapter);
                        lv_vehiclelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          /*      ElecFence elecFence=elecFenceList.get(position);
                                //显示界面
                                Intent intent=new Intent(ElecfenceListActivity.this, WorkSiteDisplayActivity.class);
                                intent.putExtra("workSiteName",elecFence.getEfName());
                                intent.putExtra("workSiteId",elecFence.getMapId());
                                startActivity(intent);*/
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
                vehicleHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                vehicleHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                vehicleHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                vehicleHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
