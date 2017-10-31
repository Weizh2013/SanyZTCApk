package sany.com.mmpapp.business.enterprise.firstActivity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.Text;
import com.amap.api.maps2d.model.TextOptions;
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
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.map.GPSNaviActivity;
import sany.com.mmpapp.model.ElecFence;

public class WorkSiteDisplayActivity extends Activity implements AMap.OnCameraChangeListener{
    private MmpApp mmpApp;
    private String workSiteId;
    private String workSiteName;
    //标题栏
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private TextView tv_RightBtn;
    //地图
    private AMap aMap;
    private MapView mapView;
    private JSONObject jsonRep;   //接收的JSON
    private JSONArray elecFenceJSONArray;
    private ElecFence elecFence; //电子围栏
    private LatLng distpont;   //用于导航的经纬度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_site_display);
        mmpApp=(MmpApp)getApplication();
        //获取传入的数据
        Intent intent=getIntent();
        workSiteId=intent.getStringExtra("workSiteId");
        workSiteName=intent.getStringExtra("workSiteName");
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
        tv_Title.setText(workSiteName);
        tv_RightBtn=(TextView)findViewById(R.id.right_titletop);
        tv_RightBtn.setText(R.string.navigation);
        tv_RightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WorkSiteDisplayActivity.this, GPSNaviActivity.class);
                intent.putExtra("Lat",distpont.latitude);
                intent.putExtra("Lng",distpont.longitude);
                startActivity(intent);
            }
        });
        mapView=(MapView)findViewById(R.id.enterprisemap);
        mapView.onCreate(savedInstanceState);
        init();
        elecFence=new ElecFence();
        queryElecFence(workSiteId);
    }

    //地图初始化
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        aMap.setOnCameraChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(),mmpApp.getInitLng())));
        /*   aMap.moveCamera(CameraUpdateFactory.zoomTo(14));*/
    }

    private void queryElecFence(String  workSiteId){
        String url="";
        if(mmpApp.getStaffType()==1) {
            url= APIInterface.DATAHOST + APIInterface.geElecFenceByEfId;
        }else if(mmpApp.getStaffType()==0){
            url= APIInterface.DATAHOST + APIInterface.listElecFence;
        }
        ElecFenceThread elecFenceThread = new ElecFenceThread(workSiteId, url);
        new Thread(elecFenceThread).start();
    }

    Handler elecfenceHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        elecFenceJSONArray=jsonRep.getJSONArray("rows");

                       /* {"rows":[{"efMapCoordinates":"113.007041,28.111678;113.016311,28.111678;113.016311,28.10759;113.007041,28.10759",
                       "efName":"得斯勤",
                       "efZoneType":1,
                       "efType":2,
                       "efId":99}]}*/
                        for(int i=0;i<elecFenceJSONArray.length();i++){
                            //工程数据的组合
                            JSONObject tempJSONObject=elecFenceJSONArray.getJSONObject(i);
                            if(mmpApp.getStaffType()==1) {
                                elecFence.setEfMapCoordinates(tempJSONObject.getString("efMapCoordinates"));
                                elecFence.setEfName(tempJSONObject.getString("efName"));
                                elecFence.setEfZoneType(tempJSONObject.getInt("efZoneType"));
                                elecFence.setEfType(tempJSONObject.getInt("efType"));
                                elecFence.setEfId(tempJSONObject.getInt("efId"));
                            }else if(mmpApp.getStaffType()==0){
                                elecFence.setEfMapCoordinates(tempJSONObject.getString("ef_coordinates"));
                                elecFence.setEfName(tempJSONObject.getString("ef_name"));
                                elecFence.setEfZoneType(tempJSONObject.getInt("ef_zoneType"));
                                elecFence.setEfType(tempJSONObject.getInt("ef_type"));
                            }
                            drawElecFence(elecFence);
                         }
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

    private  void drawElecFence(ElecFence elecFence){
        if((elecFence.getEfZoneType()==1)||(elecFence.getEfZoneType()==2)){
            String[] tempLatLngArray=elecFence.getEfMapCoordinates().split(";");
            List<LatLng> points = new ArrayList<LatLng>();
            for (int i = 0; i < tempLatLngArray.length; i++) {
                String[] point = tempLatLngArray[i].split(",");
                points.add(new LatLng(Double.parseDouble(point[1]),Double.parseDouble(point[0])));
            }
            PolygonOptions polygonOptions=new PolygonOptions();
            polygonOptions.addAll(points);
            aMap.addPolygon(polygonOptions.strokeColor(Color.argb(50, 1, 1, 1)).fillColor(Color.argb(50, 1, 1, 1)));
            aMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(3)));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            //用于导航的经纬度
            TextOptions textOptions = new TextOptions().position(points.get(0))
                    .text(workSiteName).fontColor(Color.BLACK)
                    .backgroundColor(Color.YELLOW).fontSize(40).rotate(0).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                    .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
            aMap.addText(textOptions);
            distpont=points.get(0);
            Toast.makeText(this,""+distpont.latitude+distpont.longitude,Toast.LENGTH_SHORT).show();
        }
    }






    //内部类，电子围栏数数据的请求线程
    class ElecFenceThread implements Runnable {
        private String workSiteId;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public ElecFenceThread(String workSiteId,String url) {
            // TODO Auto-generated constructor stub
            this.workSiteId=workSiteId;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            if(mmpApp.getStaffType()==1) {
                BasicNameValuePair param_Name = new BasicNameValuePair(
                        "efId", workSiteId);
                paraLists.add(param_Name);
            }else if(mmpApp.getStaffType()==0){
                BasicNameValuePair param_Name = new BasicNameValuePair(
                        "ef_no", workSiteId);
                paraLists.add(param_Name);
            }
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                elecfenceHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                elecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                elecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                elecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }


    /**
     * 地图必须重写的方法
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }
}
