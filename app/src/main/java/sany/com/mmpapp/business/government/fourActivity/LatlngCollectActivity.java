package sany.com.mmpapp.business.government.fourActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;

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

public class LatlngCollectActivity extends Activity implements AMap.OnCameraChangeListener,AMapLocationListener, LocationSource {
    private MmpApp mmpApp;
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private String efName;
    private String efNo;
    private AMap aMap;
    private MapView mapView;
    private Button startCollect;
    private Button endCollect;
    private Button collectsubmit;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocationSource.OnLocationChangedListener mListener;
    public final static int MSG_LOCATION_START = 0;
    public final static int MSG_LOCATION_TIME = 1;
    public final static int MSG_LOCATION_STOP= 2;
    public final static int MSG_LOCATION=3;
    //用于计算两个点之间的距离
    private LatLng start;
    private LatLng end;

    boolean startfinish=false;//用于判断是不是采集第一个点
    boolean startcollect=false;
    private ArrayList<LatLng> collectLatLngList=new ArrayList<LatLng>();
    private MarkerOptions markerOption;

    //数据上传部分的处理部分
    private JSONObject jsonRep;
    private JSONArray elecfenceJSONArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latlng_collect);
        mmpApp=(MmpApp)getApplication();
        Intent intent=getIntent();
        efName=intent.getStringExtra("efName");
        efNo=intent.getStringExtra("efNo");

        tv_LeftBtn=(TextView)findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        startCollect=(Button)findViewById(R.id.startcollect);
        startCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                setUpMap();
                startcollect=true;
                collectLatLngList.clear();
                if(locationClient!=null){
                    locationClient.stopLocation();
                    locationClient.startLocation();
                }else{
                    setupLocationClient();
                }
            }
        });
        endCollect=(Button)findViewById(R.id.endcollect);
        endCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_LOCATION_STOP;
                mHandler.sendMessage(msg);
                if(locationClient!=null) {
                    locationClient.stopLocation();
                }
                startcollect=false;
            }
        });

        collectsubmit=(Button)findViewById(R.id.collectsubmit);
        collectsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(LatlngCollectActivity.this,"submitLatLng",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(LatlngCollectActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("特别提示");
                builder.setMessage("确认是" + efName + "数据采集？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCollectLatLng();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();


            }
        });

        frameLayout=(FrameLayout)findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title=(TextView)findViewById(R.id.module_title);
        tv_Title.setText("经纬度数据采集");

        mapView=(MapView)findViewById(R.id.latlngcollect);
        mapView.onCreate(savedInstanceState);
        init();
    }

    //打开定位
    @Override
    public   void activate(LocationSource.OnLocationChangedListener listener){
        mListener=listener;
        String serviceString= Context.LOCATION_SERVICE;
        LocationManager locationManager=(LocationManager) getSystemService(serviceString);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT).show();
        }
        if(locationClient==null) {
          setupLocationClient();
        }
    }


    //启动定位
    private void setupLocationClient(){
        locationClient = new AMapLocationClient(this);
        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setInterval(2000);
        locationClient.setLocationListener(this);
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    //处理所有的定位以及数据上传后的返回Handler
    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_LOCATION_START://采集第一个数据
                    collectLatLngList.add(end);
                     break;
                case MSG_LOCATION_TIME://采集第一个数据后的多个数据的处理
                    collectLatLngList.add(end);
                    List<LatLng> points = new ArrayList<LatLng>();
                    points.add(start);
                    points.add(end);
                    //把新采集的点与上一个点用线连接起来；
                    aMap.addPolyline((new PolylineOptions()).addAll(points).width(10).color(Color.BLUE));
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(end));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //显示新点的位置
                    mListener.onLocationChanged((AMapLocation) msg.obj);
                   break;
               case MSG_LOCATION_STOP://采集完成处理，把所有点连线画出；
                    //清除地图上所有的点
                    aMap.clear();
                    //把第-个点加到点集里，
                    if(collectLatLngList.size()<4) return;
                    collectLatLngList.add(collectLatLngList.get(0));
                    //把所有的点用线连接起来；
                    aMap.addPolyline((new PolylineOptions()).addAll(collectLatLngList).width(10).color(Color.BLUE));
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(end));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    break;
                case MSG_LOCATION://位置数据的获取展示
                    mListener.onLocationChanged((AMapLocation)msg.obj);
                    break;
                case Http.SIG_GOOD:
                    try {
                        int returnrows=jsonRep.getInt("rows");
                        if(returnrows==1){
                            Toast.makeText(LatlngCollectActivity.this,"采集数据上传成功！",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            setResult(1001,intent);
                            finish();

                        }else{
                            Toast.makeText(LatlngCollectActivity.this,"采集数据上异常！",Toast.LENGTH_SHORT).show();
                            //需要保存数据，后再传。

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
        };
    };

    //地图初始化
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
   /*     uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);*/
        uiSettings.setAllGesturesEnabled (true);
        uiSettings.setCompassEnabled(true);
        aMap.setOnCameraChangeListener(this);

        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(), mmpApp.getInitLng())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        setUpMap();
    }



    /**
     * 设置地图上点属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.point4));// 设置小蓝点的图标
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
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
        deactivate();
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

    //采点处理
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            if(aMapLocation.getLongitude()!=0){
                //如是要没有开始采集，只是显示坐标
                if(!startcollect){
                    Message msg = mHandler.obtainMessage();
                    msg.obj = aMapLocation;
                    msg.what = MSG_LOCATION;
                    mHandler.sendMessage(msg);
                }else {
                    //当开始采集时，
                    //1、第一次采集
                    if (!startfinish) {
                        Message msg = mHandler.obtainMessage();
                        msg.obj = aMapLocation;
                        msg.what = MSG_LOCATION_START;
                        mHandler.sendMessage(msg);
                        start = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        end = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        startfinish = true;
                    } else {
                        //2.非第一次采集
                        start = new LatLng(end.latitude, end.longitude);
                        end = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        double distance = AMapUtils.calculateLineDistance(start, end);
                        if (distance > 2) {
                            Message msg = mHandler.obtainMessage();
                            msg.obj = aMapLocation;
                            msg.what = MSG_LOCATION_TIME;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

    //以上是地图方面的内容，以下是数据的上传以及
    private  void updateCollectLatLng(){
        String url = APIInterface.DATAHOST + APIInterface.updateEfMapCoordinates;
        UpdateCollectLatLngsThread updateCollectLatLngsThread = new UpdateCollectLatLngsThread(efNo,collectLatLngList, url);

        new Thread(updateCollectLatLngsThread).start();

    }

    class UpdateCollectLatLngsThread  implements Runnable {
        private String ef_no;
        private ArrayList<LatLng> upDateCollectLatLngList;
        private String fullUrl;


        /**
         *
         * @param ef_no
         *            电子围栏编号
         *
         *@param ef_mapCoordinates;

         * @param url
         *            地址
         */
        public UpdateCollectLatLngsThread(String efNo,ArrayList<LatLng> collectLagLngList , String url) {
            // TODO Auto-generated constructor stub
            this.ef_no = efNo;
            this.upDateCollectLatLngList=collectLagLngList;
            this.fullUrl=url;


        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_ef_no = new BasicNameValuePair(
                    "ef_no", ""+ef_no);
            paraLists.add(param_ef_no);
            String tempelecfenceStr="";
            for(int i=0;i<(upDateCollectLatLngList.size()-1);i++){
                tempelecfenceStr=tempelecfenceStr+upDateCollectLatLngList.get(i).longitude+","+upDateCollectLatLngList.get(i).latitude+";";
            };
            tempelecfenceStr=tempelecfenceStr+upDateCollectLatLngList.get(upDateCollectLatLngList.size()-1).longitude+","+
                    upDateCollectLatLngList.get(upDateCollectLatLngList.size()-1).latitude;
            BasicNameValuePair param_ef_mapCoordinates = new BasicNameValuePair(
                    "ef_mapCoordinates", ""+tempelecfenceStr);
            paraLists.add(param_ef_mapCoordinates);

            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                mHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                mHandler.sendEmptyMessage(Http.SIG_BAD_JSON);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(Http.SIG_BAD);
            }

        }

    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }




}
