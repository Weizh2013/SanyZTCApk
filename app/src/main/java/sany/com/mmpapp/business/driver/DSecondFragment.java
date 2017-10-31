package sany.com.mmpapp.business.driver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
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
/**
 * Created by sunj7 on 17-1-10.
 */
public class DSecondFragment extends Fragment implements AMap.InfoWindowAdapter,AMap.OnMarkerClickListener,AMap.OnInfoWindowClickListener {
    public final static  int SIG_GOOD_REALTIME=0x11;
    private DriverMainActivity mMainActivity;
    private MmpApp mmpApp;
    private AMap aMap;
    private MapView mapView;
    private JSONObject jsonRep;
    private JSONArray vehicleInfoJSONArray;
    private JSONArray tempJSONArray;
    private String speed;
    private String loadState;
    private String tarpaulinState;
    private String riseState;
    private String alarmContent;
    private String gpsTime;
    private String lat;
    private String lng;
    private String address;
    private String phoneNum;
    private String vehiNo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_fg2, container, false);
        mMainActivity = (DriverMainActivity) getActivity();
        mmpApp=(MmpApp)mMainActivity.getApplication();
        mapView=(MapView)view.findViewById(R.id.driverdmap);
        mapView.onCreate(savedInstanceState);
        init();
        queryVehicleInfo();
        return view;
    }


    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnCameraChangeListener(mMainActivity);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(), mmpApp.getInitLng())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void queryVehicleInfo(){
        String url = APIInterface.DATAHOST + APIInterface.getVehicleInfoByStaffId;
        VehicleInfoThread vehicleInfoThread = new VehicleInfoThread(url);
        new Thread(vehicleInfoThread).start();
    }

    //请求一台车的实时数据
    private void queryRealtimeDataofVehicle(String phoneNum){
        String url=APIInterface.DATAHOST+APIInterface.getDevCurData;
        RealtimeDataThread realtimeDataThread=new RealtimeDataThread(phoneNum,url);
        new Thread(realtimeDataThread).start();
    }

    Handler vehicleInfoHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        vehicleInfoJSONArray=jsonRep.getJSONArray("rows");
                            if(vehicleInfoJSONArray.length()==0){
                             Toast.makeText(mMainActivity,"对不起，您没有分配车辆",Toast.LENGTH_SHORT).show();
                            }else {
                                //JSON数据的提取,如果是正常，一个司机对应一台车
                                JSONObject tempJSONObject = vehicleInfoJSONArray.getJSONObject(0);
                                phoneNum=tempJSONObject.getString("ev_phoneNum");
                                vehiNo=tempJSONObject.getString("ev_vehiNo");
                                queryRealtimeDataofVehicle(phoneNum);
                            }
                   } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case SIG_GOOD_REALTIME:
                    try {
                        if("".equals(jsonRep.getString("devAry"))||"null".equals(jsonRep.getString("devAry"))) {
                            Toast.makeText(mMainActivity, "无最新数据", Toast.LENGTH_SHORT).show();

                        }else{
                            tempJSONArray = jsonRep.getJSONArray("devAry");
                            String angle=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("angle");
                            lat=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("mapLatitude");
                            lng=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("mapLongitude");
                            gpsTime=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("gpsTime");
                            speed=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("speed");

                            address=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("mapPosition");
                            JSONObject beanAlarmInfo=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getJSONObject("beanAlarmInfo");
                            JSONObject beanStatusInfo=tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getJSONObject("beanStatusInfo");
                            String temploadState=beanStatusInfo.getString("loadState");
                            loadState=parseLoadState(temploadState);
                            String temptarpaulinState=beanStatusInfo.getString("tarpaulinState");
                            tarpaulinState=parseTarpaulinState(temptarpaulinState);
                            String tempriseState=beanStatusInfo.getString("riseState");
                            riseState=parseRiseState(tempriseState);
                            alarmContent=parseAlarmInfo(beanAlarmInfo);
                            drawMarkers();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Http.SIG_BAD:
                    Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                case Http.SIG_BAD_JSON:
                    Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                case Http.ILL_URL:
                    Toast.makeText(mMainActivity,"数据异常",Toast.LENGTH_SHORT).show();
                    break;
                case Http.HOST_TIME_OUT:
                    Toast.makeText(mMainActivity,"网络异常，请求超时",Toast.LENGTH_SHORT).show();
                    break;
                case Http.OK_HOST:

                    break;
                default:
                    break;

            }
        }

    };



    class VehicleInfoThread implements Runnable {

        private String fullUrl;
        /**
         * @param url    地址
         */
        public VehicleInfoThread(String url) {
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
                vehicleInfoHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                vehicleInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                vehicleInfoHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                vehicleInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }


    class RealtimeDataThread implements Runnable {
        private String mVLicensePlateList;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public RealtimeDataThread(String vLicensePlateList,String url) {
            // TODO Auto-generated constructor stub
            this.mVLicensePlateList=vLicensePlateList;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param = new BasicNameValuePair(
                    "mVLicensePlateList", mVLicensePlateList);
            paraLists.add(param);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);



                vehicleInfoHandler.sendEmptyMessage(SIG_GOOD_REALTIME);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


                vehicleInfoHandler.sendEmptyMessage(Http.SIG_BAD);

            }  catch (Exception e) {
                e.printStackTrace();
                vehicleInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public View getInfoWindow(Marker marker) {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
        aMap.moveCamera(CameraUpdateFactory.scrollBy(0, -240));
        View infoWindow =  LayoutInflater.from(mMainActivity.getApplicationContext()).inflate(R.layout.map_info_window,
                null);
        ImageView img_CloseWindow = (ImageView) infoWindow
                .findViewById(R.id.img_closewindow);

        TextView tv_InfoWindowTitle = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_title);

        TextView tv_DevSpeed = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_speed);
        tv_DevSpeed.setText(speed+"km/h");
        TextView tv_DevLoadState=(TextView)infoWindow.findViewById(R.id.tv_infowindow_loadState);
        tv_DevLoadState.setText(loadState);
        TextView tv_DevTarpaulinState=(TextView)infoWindow.findViewById(R.id.tv_infowindow_tarpaulinState);
        tv_DevTarpaulinState.setText(tarpaulinState);
        TextView tv_DevRiseState=(TextView)infoWindow.findViewById(R.id.tv_infowindow_riseState);
        tv_DevRiseState.setText(riseState);
        TextView tv_DevAlarm=(TextView)infoWindow.findViewById(R.id.tv_infowindow_alarm);
        tv_DevAlarm.setText(alarmContent);
        TextView tv_DevGPSTime = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_gpstime);
        tv_DevGPSTime.setText(gpsTime);
        TextView tv_DevAddr = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_address);
        tv_DevAddr.setText(address);

        tv_InfoWindowTitle.setText(vehiNo);
        img_CloseWindow.setOnClickListener(new InfoWindowBtnClickListener(marker));
        LinearLayout ll=(LinearLayout)infoWindow.findViewById(R.id.historytrack);
        ll.setVisibility(View.GONE);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    class InfoWindowBtnClickListener implements View.OnClickListener {
        private Marker selMarker;
        public InfoWindowBtnClickListener(Marker marker) {
            // TODO Auto-generated constructor stub
            this.selMarker = marker;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.img_closewindow:
                    selMarker.hideInfoWindow();
                    selMarker.setIcon(BitmapDescriptorFactory
                            .fromResource(R.drawable.event_location));
                    break;
                default:
                    break;
            }
        }
    }

    public void drawMarkers() {
        Marker marker = aMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.event_location)).title(vehiNo).draggable(true));

        marker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    private String  parseTarpaulinState(String input) {
        String  re = "未知";
        if ("0".equals( input)) {
            re = "打开";
        } else if ("1" .equals(input)) {
            re = "关闭";
        }
        return re;
    }

    private String parseRiseState( String input) {
        String  re = "未知";
        if ("0".equals( input)) {
            re = "未举升";
        } else if ("1" .equals(input)) {
            re = "举升";
        }
        return re;
    }

    private String parseLoadState(String input) {
        String re = "未知";
        if ("0".equals( input)) {
            re = "无载重";
        } else if ("1" .equals(input)) {
            re = "有载重";
        }
        return re;
    }
    private String  parseAlarmInfo(JSONObject alarmInfo){
        String re= "";
        try {
            if("1".equals(alarmInfo.getString("emergencyAlarm"))) {

                re += "紧急报警;";
            }
            if("1".equals(alarmInfo.getString("speedingAlarm"))){

                re += "超速报警;";
            }
            if("1".equals(alarmInfo.getString("fatigueDriving"))){

                re += "疲劳驾驶;";
            }
            if("1".equals(alarmInfo.getString("warning"))){

                re += "预警;";
            }
            if("1".equals(alarmInfo.getString("gnssFault"))){
                re +=  "GNSS模块发生故障;";
            }
            if("1".equals(alarmInfo.getString("gnssAntennaBreak"))){
                re +=  "GNSS天线未接或被剪断;";
            }
            if("1".equals(alarmInfo.getString("gnssAntennaFault"))){
                re +=  "GNSS天线短路;";
            }
            if("1".equals(alarmInfo.getString("terminalUndervoltage"))){
                re +=  "终端主电源欠压;";
            }
            if("1".equals(alarmInfo.getString("terminalOutage"))){
                re += "终端主电源掉电;";
            }
            if("1".equals(alarmInfo.getString("lcdFault"))){
                re +=  "终端LCD或显示器故障;";
            }
            if("1".equals(alarmInfo.getString("ttsFault"))){
                re +=  "TTS模块故障;";
            }
            if("1".equals(alarmInfo.getString("cameraFault"))){
                re +=  "摄像头故障;";
            }
            if("1".equals(alarmInfo.getString("noGpsSignal"))){
                re += "GPS无信号;";
            }
            if("1".equals(alarmInfo.getString("ECUCheat"))){
                re +=  "ECU作弊;";
            }
            if("1".equals(alarmInfo.getString("riseCheat"))){
                re +=  "举升作弊;";
            }
            if("1".equals(alarmInfo.getString("openCloseBoxCheat"))){
                re +=  "开关箱作弊;";
            }
            if("1".equals(alarmInfo.getString("emptyHeavyCarCheat"))){
                re +=  "空重车作弊;";
            }
            if("1".equals(alarmInfo.getString("stealWork"))){
                re += "偷运;";
            }
            if("1".equals(alarmInfo.getString("driveOverTime"))){
                re +=  "当天累计驾驶超时;";
            }
            if("1".equals(alarmInfo.getString("packingOverTime"))){
                re +=  "超时停车;";
            }
            if("1".equals(alarmInfo.getString("inoutArea"))){

                re += "进出区域;";
            }
            if("1".equals(alarmInfo.getString("inoutPath"))){

                re +=  "进出路线;";
            }

            if("1".equals(alarmInfo.getString("pathDeviate"))){
                re +=  "路线偏离;";
            }
            if("1".equals(alarmInfo.getString("vssFault"))){
                re += "车辆VSS故障;";
            }
            if("1".equals(alarmInfo.getString("iolMassAbr"))){
                re +=  "车辆油量异常;";
            }
            if("1".equals(alarmInfo.getString("isStolen"))){
                re += "车辆被盗;";
            }
            if("1".equals(alarmInfo.getString("illegalityFire"))){
                re += "车辆非法点火;";
            }
            if("1".equals(alarmInfo.getString("illegalityMove"))){
                re  += "车辆非法移位;";
            }
            if("1".equals(alarmInfo.getString("turnOnOneSide"))){

                re += "碰撞侧翻报警;";
            }
            if("1".equals(alarmInfo.getString("illegalRise"))){

                re += "非法举斗;";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return re;
        }
        return re;
    }
}
