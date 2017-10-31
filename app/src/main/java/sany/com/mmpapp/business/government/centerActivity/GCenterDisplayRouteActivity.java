package sany.com.mmpapp.business.government.centerActivity;

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

public class GCenterDisplayRouteActivity extends Activity implements AMap.OnCameraChangeListener {
    private MmpApp mmpApp;
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private AMap aMap;
    private MapView mapView;
    private String piId;
    private String ws;
    private JSONObject jsonRep;   //接收的JSON
    private JSONArray routeDataJSONArray;
    private JSONArray pointJSONArray;
    private int colorcount=0;
    private int type=0;
    private int queryroutefinishflag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mmpApp=(MmpApp)getApplication();
        //与企业用户共用一个xml
        setContentView(R.layout.activity_route_display);
        tv_LeftBtn=(TextView)findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        frameLayout=(FrameLayout)findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title=(TextView)findViewById(R.id.module_title);
        tv_Title.setText("运输路线");
        mapView=(MapView)findViewById(R.id.routedisplaymap);
        mapView.onCreate(savedInstanceState);
        Intent intent=getIntent();
        piId=intent.getStringExtra("piId");
        ws=intent.getStringExtra("ws");
        init();
        queryElecfenceDataOfWs(ws);
    }

    //地图初始化
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setCompassEnabled(true);
        aMap.setOnCameraChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(), mmpApp.getInitLng())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    private void queryElecfenceDataOfWs(String ws){
        String url = APIInterface.DATAHOST + APIInterface.getWorkSiteByWs;
        ElecfenceDataOfWsThread elecfenceDataOfWsThread = new ElecfenceDataOfWsThread(ws, url);
        new Thread(elecfenceDataOfWsThread).start();
    }

    private void queryRouteDataOfpiId(String piId){
        queryroutefinishflag=1;
        String url = APIInterface.DATAHOST + APIInterface.getCfByPiId;
        RouteDataOfpiIdThread routeDataOfIdThread = new RouteDataOfpiIdThread(piId, url);
        new Thread(routeDataOfIdThread).start();
    }

    Handler routeDataOfpiIdHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        if(type==1){
                            String zoneType=jsonRep.getString("zoneType");
                            String zoneName=jsonRep.getString("zoneName");
                            pointJSONArray=jsonRep.getJSONArray("points");
                            drawElecfence(pointJSONArray,zoneType,zoneName);
                            if(queryroutefinishflag==0) {
                                queryRouteDataOfpiId(piId);
                            }
                        }else  if(type==2) {
                            for (int i = 0; i < routeDataJSONArray.length(); i++) {
                                //返回只有一个数据
                                JSONObject tempJSONObject = routeDataJSONArray.getJSONObject(i);
                                String tempmapOriginLonLat = tempJSONObject.getString("pc_mapOriginLonLat");
                                String tempef_name = tempJSONObject.getString("ef_name");
                                String temppc_eiId = tempJSONObject.getString("pc_eiId");
                                drawRoute(tempmapOriginLonLat);
                                queryElecfenceDataOfWs(temppc_eiId);
                            }
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

    //内部类，路线数据请求线程
    class ElecfenceDataOfWsThread implements Runnable {
        private String ws;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public ElecfenceDataOfWsThread(String ws,String url) {
            // TODO Auto-generated constructor stub
            this.ws=ws;
            this.fullUrl = url;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_ws = new BasicNameValuePair(
                    "ws", ws);
            paraLists.add(param_ws);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                type=1;
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }



    //内部类，路线数据请求线程
    class RouteDataOfpiIdThread implements Runnable {
        private String piId;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public RouteDataOfpiIdThread(String piId,String url) {
            // TODO Auto-generated constructor stub
            this.piId=piId;
            this.fullUrl = url;
            type=2;
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_piId = new BasicNameValuePair(
                    "piId", piId);
            paraLists.add(param_piId);
            try {
                String strRep = tool.executeRequest(paraLists);
                routeDataJSONArray = new JSONArray(strRep);
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                routeDataOfpiIdHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }


    //画电子围栏
    private void drawElecfence(JSONArray pointsArray,String zoneType,String zoneName){
        List<LatLng> points = new ArrayList<LatLng>();
        try {
        for (int i = 0; i < pointsArray.length(); i++) {
            JSONObject temppoint=(JSONObject)pointsArray.get(i);
                points.add(new LatLng(temppoint.getDouble("lat"),temppoint.getDouble("lng")));
        }
         JSONObject temppoint=(JSONObject)pointsArray.get(0);
         points.add(new LatLng(temppoint.getDouble("lat"),temppoint.getDouble("lng")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PolygonOptions polygonOptions=new PolygonOptions();
        polygonOptions.addAll(points);
        aMap.addPolygon(polygonOptions.strokeColor(Color.argb(50, 1, 1, 1)).fillColor(Color.argb(50, 1, 1, 1)));
      /*  aMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(3)));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));*/
        //用于导航的经纬度
        if(queryroutefinishflag==0) {
            TextOptions textOptions = new TextOptions().position(points.get(0))
                    .text(zoneName).fontColor(Color.BLACK)
                    .backgroundColor(Color.YELLOW).fontSize(40).rotate(0).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                    .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
            aMap.addText(textOptions);
        }else{
            TextOptions textOptions = new TextOptions().position(points.get(0))
                    .text(zoneName).fontColor(Color.BLACK)
                    .backgroundColor(Color.GREEN).fontSize(40).rotate(0).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                    .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
            aMap.addText(textOptions);
        }
    }

    //画路线
    private void drawRoute(String mapCoordinates){
        String[] tempLatLngArray= mapCoordinates.split(";");
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < tempLatLngArray.length; i++) {
            String[] point = tempLatLngArray[i].split(",");
            points.add(new LatLng(Double.parseDouble(point[1]),Double.parseDouble(point[0])));
        }
        if(colorcount%2==0){
            aMap.addPolyline((new PolylineOptions()).addAll(points).width(10).color(Color.BLUE));
        }else{
            aMap.addPolyline((new PolylineOptions()).addAll(points).width(10).color(Color.RED));
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(points.size() / 2)));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        colorcount++;
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
