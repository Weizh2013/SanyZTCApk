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

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.PolylineOptions;
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

public class RouteDisplayActivity extends Activity implements AMap.OnCameraChangeListener{
    private MmpApp mmpApp;
    //标题栏
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private TextView tv_RightBtn;
    private  String  workSiteName;
    private  String  pceiIdName;
    private String rmId;
    //地图
    private AMap aMap;
    private MapView mapView;

    private JSONObject jsonRep;   //接收的JSON
    private JSONArray routeDataJSONArray;
    //返回数据
    private String piWorkSiteMapCoordinates;
    private String rmMapOriginLonlat;
    private String cfMapCoordinates;
    private int cfType;
    private int piWorkSiteType;
    private int cfZoneType;
    private int piWorkSiteZoneType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_display);
        mmpApp=(MmpApp)getApplication();
        Intent intent=getIntent();
        workSiteName= intent.getStringExtra("workSiteName");
        pceiIdName= intent.getStringExtra("pceiIdName");
        rmId= intent.getStringExtra("routeId");
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
        tv_Title.setText(workSiteName+"--"+pceiIdName);
        mapView=(MapView)findViewById(R.id.routedisplaymap);
        mapView.onCreate(savedInstanceState);
        init();
       queryRouteData(rmId);

    }
    Handler routeDataHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        routeDataJSONArray=jsonRep.getJSONArray("rows");
                        //返回只有一个数据
                            JSONObject tempJSONObject=routeDataJSONArray.getJSONObject(0);
                            piWorkSiteMapCoordinates=tempJSONObject.getString("piWorkSiteMapCoordinates");
                            rmMapOriginLonlat=tempJSONObject.getString("rmMapOriginLonlat");
                            cfMapCoordinates=tempJSONObject.getString("cfMapCoordinates");
                            cfType=tempJSONObject.getInt("cfType");
                            piWorkSiteType=tempJSONObject.getInt("piWorkSiteType");
                            cfZoneType=tempJSONObject.getInt("cfZoneType");
                            piWorkSiteZoneType=tempJSONObject.getInt("piWorkSiteZoneType");
                            drawElecFence(piWorkSiteMapCoordinates,piWorkSiteType,piWorkSiteZoneType,workSiteName) ;
                            drawElecFence(cfMapCoordinates,cfType,cfZoneType,pceiIdName) ;
                            drawRoute(rmMapOriginLonlat);
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


    private  void drawElecFence(String mapCoordinates,int elecFenceType,int zoneType,String elecFenceName){
        if((zoneType==1)||(zoneType==2)){
            String[] tempLatLngArray= mapCoordinates.split(";");
            List<LatLng> points = new ArrayList<LatLng>();
            for (int i = 0; i < tempLatLngArray.length; i++) {
                String[] point = tempLatLngArray[i].split(",");
                points.add(new LatLng(Double.parseDouble(point[1]),Double.parseDouble(point[0])));
            }
            PolygonOptions polygonOptions=new PolygonOptions();
            polygonOptions.addAll(points);
            if(elecFenceType==2){
                aMap.addPolygon(polygonOptions.strokeColor(Color.argb(50, 1, 1, 1)).fillColor(Color.argb(50, 1, 1, 1)));
            }else if(elecFenceType==3){
                aMap.addPolygon(polygonOptions.strokeColor(Color.argb(50, 234, 1, 1)).fillColor(Color.argb(50, 234, 1, 1)));
            }
            aMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(0)));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            //用于导航的经纬度
            TextOptions textOptions = new TextOptions().position(points.get(0))
                    .text(elecFenceName).fontColor(Color.BLACK)
                    .backgroundColor(Color.YELLOW).fontSize(40).rotate(0).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                    .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
            aMap.addText(textOptions);
        }
    }

    private void drawRoute(String mapCoordinates){
        String[] tempLatLngArray= mapCoordinates.split(";");
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < tempLatLngArray.length; i++) {
            String[] point = tempLatLngArray[i].split(",");
            points.add(new LatLng(Double.parseDouble(point[1]),Double.parseDouble(point[0])));
        }
       aMap.addPolyline((new PolylineOptions()).addAll(points).width(10).color(Color.BLUE));
       aMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(points.size()/2)));
       aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }


    private void queryRouteData(String rmId){
        String url = APIInterface.DATAHOST + APIInterface.getRouteByRmId;
        RouteDataThread routeDataThread = new RouteDataThread(rmId, url);
        new Thread(routeDataThread).start();

    }

    //地图初始化
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setAllGesturesEnabled (true);
        uiSettings.setCompassEnabled(true);
        aMap.setOnCameraChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(), mmpApp.getInitLng())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }
    //内部类，路线数据请求线程
    class RouteDataThread implements Runnable {
        private String rmId;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public RouteDataThread(String rmId,String url) {
            // TODO Auto-generated constructor stub
            this.rmId=rmId;
            this.fullUrl = url;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_Name = new BasicNameValuePair(
                    "rmId", rmId);
            paraLists.add(param_Name);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                routeDataHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                routeDataHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                routeDataHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                routeDataHandler.sendEmptyMessage(Http.SIG_BAD);
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
