package sany.com.mmpapp.business.enterprise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
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
import sany.com.mmpapp.adapter.EEnterpriseVehicleListAdapter;
import sany.com.mmpapp.business.enterprise.secondActivity.HistoryTrackActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.Enterprise;
import sany.com.mmpapp.model.Vehicle;

/**
 * Created by sunj7 on 16-8-19.
 */
public class SecondFragment extends Fragment implements AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener {
    private EnterpriseMainActivity mMainActivity;
    private MmpApp mmpApp;
    private SlidingDrawer mDrawer;
    private ExpandableListView elv_enterpriseVehicle;
    private ImageButton imbtn;
    private AMap aMap;
    private MapView mapView;
    private JSONObject jsonRep;
    private JSONArray vehicleListJSONArray;
    private Vehicle clickVehicle;
    private List<Enterprise> groupList;
    private List<Vehicle> childList;
    private EEnterpriseVehicleListAdapter enterpriseVehicleListAdapter;
    public final static int SIG_GOOD_REALTIME = 0x11;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainActivity = (EnterpriseMainActivity) getActivity();
        mmpApp = (MmpApp) mMainActivity.getApplication();
        View view = inflater.inflate(R.layout.government_fg2, container, false);
        mDrawer = (SlidingDrawer) view.findViewById(R.id.slidingdrawer);
        elv_enterpriseVehicle = (ExpandableListView) view.findViewById(R.id.enterprisevehicleList);
        groupList = new ArrayList<Enterprise>();
        childList = new ArrayList<Vehicle>();
        mDrawer.animateOpen();
        mDrawer.toggle();
        mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                imbtn.setBackgroundResource(R.drawable.ic_handle_close_default);
            }
        });
        mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                imbtn.setBackgroundResource(R.drawable.ic_handle_open_default);
            }
        });
        mDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {
            @Override
            public void onScrollStarted() {
            }
            @Override
            public void onScrollEnded() {
            }
        });
        imbtn = (ImageButton) view.findViewById(R.id.img_handle_switch);
        imbtn.setBackgroundResource(R.drawable.ic_handle_close_default);
        mapView = (MapView) view.findViewById(R.id.gsecondmap);
        mapView.onCreate(savedInstanceState);
        init();
        queryVehicleListData();
        return view;
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnCameraChangeListener(mMainActivity);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(), mmpApp.getInitLng())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }


    Handler vehicleListDataHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        vehicleListJSONArray = jsonRep.getJSONArray("rows");

                        for (int i = 0; i < vehicleListJSONArray.length(); i++) {
                            //JSON数据的提取
                            JSONObject tempJSONObject = vehicleListJSONArray.getJSONObject(i);
                            String tphoneNum = tempJSONObject.getString("evPhoneNum");
                            String ev_vehiNo = tempJSONObject.getString("evVehiNo");
                            String vehEiName = tempJSONObject.getString("eiName");
                            String tempisOnline = tempJSONObject.getString("evIsOnline");
                            int isOnline = 0;
                            if ("1".equals(tempisOnline)) {
                                isOnline = 1;
                            }
                            String onlineStatusTime = tempJSONObject.getString("evOnlineStatusTime");
                            Vehicle vehicle = new Vehicle(tphoneNum, ev_vehiNo, vehEiName, isOnline, onlineStatusTime);
                            childList.add(vehicle);
                            if (groupList.size() == 0) {
                                Enterprise enterprise = new Enterprise(vehEiName);
                                groupList.add(enterprise);
                            } else {
                                //如果不是第一个，遍历已有的元素，看数据是否已加载，
                                int flag = 0;//数据是否加载的标志；
                                for (Enterprise e : groupList) {
                                    if (e.getEiName().equals(vehEiName)) {
                                        flag = 1;
                                    }
                                }
                                //如果没有加载，则加载，
                                if (flag == 0) {
                                    Enterprise enterprise = new Enterprise(vehEiName);
                                    groupList.add(enterprise);
                                }
                            }
                        }
                        enterpriseVehicleListAdapter = new EEnterpriseVehicleListAdapter(groupList, childList, mMainActivity.getApplicationContext(), mMainActivity);
                        elv_enterpriseVehicle.setAdapter(enterpriseVehicleListAdapter);
                        elv_enterpriseVehicle.expandGroup(0);
                        elv_enterpriseVehicle.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            @Override
                            public void onGroupExpand(int groupPosition) {
                                int count = elv_enterpriseVehicle.getExpandableListAdapter().getGroupCount();
                                for (int j = 0; j < count; j++) {
                                    if (j != groupPosition) {
                                        elv_enterpriseVehicle.collapseGroup(j);
                                    }
                                }
                            }
                        });
                        elv_enterpriseVehicle.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                mDrawer.animateOpen();
                                clickVehicle = (Vehicle) enterpriseVehicleListAdapter.getChild(groupPosition, childPosition);
                                //查询数据，并在地图上打点
                                queryRealtimeDataofVehicle(clickVehicle.getPhoneNum());
                                /*  Toast.makeText(mMainActivity,vehicle.getPhoneNum()+":"+vehicle.getEv_vehiNo(),Toast.LENGTH_SHORT).show();*/
                                return true;
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case SIG_GOOD_REALTIME:
                    try {
                        if ("".equals(jsonRep.getString("devAry")) || "null".equals(jsonRep.getString("devAry"))) {
                            Toast.makeText(mMainActivity, "无最新数据", Toast.LENGTH_SHORT).show();

                        } else {
                            aMap.clear();
                            tempJSONArray = jsonRep.getJSONArray("devAry");
                            String angle = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("angle");
                            lat = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("mapLatitude");
                            lng = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("mapLongitude");
                            gpsTime = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("gpsTime");
                            speed = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("speed");
                            address = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getString("mapPosition");
                            JSONObject beanAlarmInfo = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getJSONObject("beanAlarmInfo");
                            JSONObject beanStatusInfo = tempJSONArray.getJSONObject(0).getJSONObject("dataAry").getJSONObject("beanStatusInfo");
                            String temploadState = beanStatusInfo.getString("loadState");
                            loadState = parseLoadState(temploadState);
                            String temptarpaulinState = beanStatusInfo.getString("tarpaulinState");
                            tarpaulinState = parseTarpaulinState(temptarpaulinState);
                            String tempriseState = beanStatusInfo.getString("riseState");
                            riseState = parseRiseState(tempriseState);
                            alarmContent = parseAlarmInfo(beanAlarmInfo);
                            drawMarkers();
                        }
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

    //数据的请求和加载
    private void queryVehicleListData() {
        String url = APIInterface.DATAHOST + APIInterface.listVehicleForRtm;
        VehicleListDataThread vehicleListDataThread = new VehicleListDataThread(url);
        new Thread(vehicleListDataThread).start();
    }

    //请求一台车的实时数据
    private void queryRealtimeDataofVehicle(String phoneNum) {
        String url = APIInterface.DATAHOST + APIInterface.getDevCurData;
        RealtimeDataThread realtimeDataThread = new RealtimeDataThread(phoneNum, url);
        new Thread(realtimeDataThread).start();
    }

    public void drawMarkers() {
        Marker marker = aMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.event_location)).title(clickVehicle.getEv_vehiNo()).draggable(true));
        marker.showInfoWindow();// 设置默认显示一个infowinfow
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
        aMap.moveCamera(CameraUpdateFactory.scrollBy(0, -340));
        View infoWindow = LayoutInflater.from(mMainActivity.getApplicationContext())
                .inflate(R.layout.map_info_window, null);
        ImageView img_CloseWindow = (ImageView) infoWindow
                .findViewById(R.id.img_closewindow);
        TextView tv_InfoWindowTitle = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_title);
        TextView tv_DevSpeed = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_speed);
        tv_DevSpeed.setText(speed+"km/h");
        TextView tv_DevLoadState = (TextView) infoWindow.findViewById(R.id.tv_infowindow_loadState);
        tv_DevLoadState.setText(loadState);
        TextView tv_DevTarpaulinState = (TextView) infoWindow.findViewById(R.id.tv_infowindow_tarpaulinState);
        tv_DevTarpaulinState.setText(tarpaulinState);
        TextView tv_DevRiseState = (TextView) infoWindow.findViewById(R.id.tv_infowindow_riseState);
        tv_DevRiseState.setText(riseState);
        TextView tv_DevAlarm = (TextView) infoWindow.findViewById(R.id.tv_infowindow_alarm);
        tv_DevAlarm.setText(alarmContent);
        TextView tv_DevGPSTime = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_gpstime);
        tv_DevGPSTime.setText(gpsTime);
        TextView tv_DevAddr = (TextView) infoWindow
                .findViewById(R.id.tv_infowindow_address);
        tv_DevAddr.setText(address);
        tv_InfoWindowTitle.setText(clickVehicle.getEv_vehiNo());
        img_CloseWindow.setOnClickListener(new InfoWindowBtnClickListener(marker));
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        //Toast.makeText(mMainActivity, "markerClick", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent nextIntent = new Intent(mMainActivity, HistoryTrackActivity.class);
        nextIntent.putExtra("vehiNo", clickVehicle.getEv_vehiNo());
        mMainActivity.startActivity(nextIntent);
    }


    //内部类 ,请求企业车辆列表
    class VehicleListDataThread implements Runnable {
        private String fullUrl;
        /**
         * @param url 地址
         */
        public VehicleListDataThread(String url) {
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
                vehicleListDataHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                vehicleListDataHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                vehicleListDataHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                vehicleListDataHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }


    class RealtimeDataThread implements Runnable {
        private String mVLicensePlateList;
        private String fullUrl;
        /**
         * @param url 地址
         */
        public RealtimeDataThread(String vLicensePlateList, String url) {
            // TODO Auto-generated constructor stub
            this.mVLicensePlateList = vLicensePlateList;
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
              /*  String jsonString1 = "{'devAry':[{'dataAry':{'alramInfo':'00000000','angle':'303','beanAlarmInfo':{'ECUCheat':'1','alarmInfo':0,'cameraFault':'0','driveOverTime':'0','driveTime':'1','emergencyAlarm':'0','emptyHeavyCarCheat':'0','fatigueDriving':'0','gnssAntennaBreak':'1','gnssAntennaFault':'1','gnssFault':'1','illegalRise':'1','illegalityFire':'1','illegalityMove':'0','inoutArea':'0','inoutPath':'1','iolMassAbr':'1','isStolen':'1','lcdFault':'1','noGpsSignal':'1','openCloseBoxCheat':'0','packingOverTime':'0','pathDeviate':'0','riseCheat':'0','speedingAlarm':'1','terminalOutage':'0','terminalUndervoltage':'0','ttsFault':'0','turnOnOneSide':'0','vssFault':'0','warning':'0'},'beanCsAlarmExtInfo':null,'beanJcAlarmExtInfo':null,'beanLxAlarmExtInfo':null,'beanStatusInfo':{'accState':'1','backDoor':'0','cabDoor':'0','centreDoor':'0','fixedPosi':'0','frontDoor':'0','isBigDipperPosi':'1','isGLONASSPosi':'0','isGPSPosi':'0','isGalileoPosi':'0','latitudeType':'1','loadState':'1','loadsState':'00','longitudeType':'1','ohterDoor':'0','operarorState':'1','posiIsEncryption':'1','riseState':'1','statusInfo':189,'tarpaulinState':'0'},'can_engineSpeed':'','can_engineState':'','can_vehicleSpeed':'','csAlarmExtInfo':null,'currStateId':1,'elevation':'100','engineFaultInfo':null,'engineStart':0,'ext':false,'gpsTime':'2016-12-01 14:36:57.0','jcAlarmExtInfo':null,'latitude':'28.302918','longitude':'112.915934','lxAlarmExtInfo':null,'mapLatitude':'28.29939396','mapLongitude':'112.9213582','mapPosition':'湖南省长沙市望城区;筲箕山,道士冲附近\\r\\n','mileage':'','oilmass':'','online':0,'phoneNum':'15395034904','serverTime':'2016-12-01 14:36:58.0','speed':'36.2','statusInfo':'000000BD','vehicleSpeed':''},'devid':'15395034904'}]}";
                try {
                    jsonRep = new JSONObject(jsonString1);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }*/
                vehicleListDataHandler.sendEmptyMessage(SIG_GOOD_REALTIME);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              /*  String jsonString = "{'devAry':[{'dataAry':{'alramInfo':'00000000','angle':'303','beanAlarmInfo':{'ECUCheat':'1','alarmInfo':0,'cameraFault':'0','driveOverTime':'0','driveTime':'1','emergencyAlarm':'0','emptyHeavyCarCheat':'0','fatigueDriving':'0','gnssAntennaBreak':'1','gnssAntennaFault':'1','gnssFault':'1','illegalRise':'0','illegalityFire':'1','illegalityMove':'0','inoutArea':'0','inoutPath':'0','iolMassAbr':'1','isStolen':'1','lcdFault':'0','noGpsSignal':'0','openCloseBoxCheat':'0','packingOverTime':'0','pathDeviate':'0','riseCheat':'0','speedingAlarm':'1','terminalOutage':'0','terminalUndervoltage':'0','ttsFault':'0','turnOnOneSide':'0','vssFault':'0','warning':'0'},'beanCsAlarmExtInfo':null,'beanJcAlarmExtInfo':null,'beanLxAlarmExtInfo':null,'beanStatusInfo':{'accState':'1','backDoor':'0','cabDoor':'0','centreDoor':'0','fixedPosi':'0','frontDoor':'0','isBigDipperPosi':'1','isGLONASSPosi':'0','isGPSPosi':'0','isGalileoPosi':'0','latitudeType':'1','loadState':'1','loadsState':'00','longitudeType':'1','ohterDoor':'0','operarorState':'1','posiIsEncryption':'1','riseState':'1','statusInfo':189,'tarpaulinState':'0'},'can_engineSpeed':'','can_engineState':'','can_vehicleSpeed':'','csAlarmExtInfo':null,'currStateId':1,'elevation':'100','engineFaultInfo':null,'engineStart':0,'ext':false,'gpsTime':'2016-12-01 14:36:57.0','jcAlarmExtInfo':null,'latitude':'28.302918','longitude':'112.915934','lxAlarmExtInfo':null,'mapLatitude':'28.29939396','mapLongitude':'112.9213582','mapPosition':'湖南省长沙市望城区;筲箕山,道士冲附近\\r\\n','mileage':'','oilmass':'','online':0,'phoneNum':'15395034904','serverTime':'2016-12-01 14:36:58.0','speed':'36.2','statusInfo':'000000BD','vehicleSpeed':''},'devid':'15395034904'}]}";
                try {
                    jsonRep = new JSONObject(jsonString);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }*/
                vehicleListDataHandler.sendEmptyMessage(Http.SIG_BAD);
             /*   vehicleListDataHandler.sendEmptyMessage(SIG_GOOD_REALTIME);*/
            } catch (Exception e) {
                e.printStackTrace();
                vehicleListDataHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
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

    private String parseTarpaulinState(String input) {
        String re = "未知";
        if ("0".equals(input)) {
            re = "打开";
        } else if ("1".equals(input)) {
            re = "关闭";
        }
        return re;
    }

    private String parseRiseState(String input) {
        String re = "未知";
        if ("0".equals(input)) {
            re = "未举升";
        } else if ("1".equals(input)) {
            re = "举升";
        }
        return re;
    }

    private String parseLoadState(String input) {
        String re = "未知";
        if ("0".equals(input)) {
            re = "无载重";
        } else if ("1".equals(input)) {
            re = "有载重";
        }
        return re;
    }

    private String parseAlarmInfo(JSONObject alarmInfo) {
        String re = "";
        try {
            if ("1".equals(alarmInfo.getString("emergencyAlarm"))) {

                re += "紧急报警;";
            }
            if ("1".equals(alarmInfo.getString("speedingAlarm"))) {

                re += "超速报警;";
            }
            if ("1".equals(alarmInfo.getString("fatigueDriving"))) {

                re += "疲劳驾驶;";
            }
            if ("1".equals(alarmInfo.getString("warning"))) {

                re += "预警;";
            }
            if ("1".equals(alarmInfo.getString("gnssFault"))) {
                re += "GNSS模块发生故障;";
            }
            if ("1".equals(alarmInfo.getString("gnssAntennaBreak"))) {
                re += "GNSS天线未接或被剪断;";
            }
            if ("1".equals(alarmInfo.getString("gnssAntennaFault"))) {
                re += "GNSS天线短路;";
            }
            if ("1".equals(alarmInfo.getString("terminalUndervoltage"))) {
                re += "终端主电源欠压;";
            }
            if ("1".equals(alarmInfo.getString("terminalOutage"))) {
                re += "终端主电源掉电;";
            }
            if ("1".equals(alarmInfo.getString("lcdFault"))) {
                re += "终端LCD或显示器故障;";
            }
            if ("1".equals(alarmInfo.getString("ttsFault"))) {
                re += "TTS模块故障;";
            }
            if ("1".equals(alarmInfo.getString("cameraFault"))) {
                re += "摄像头故障;";
            }
            if ("1".equals(alarmInfo.getString("noGpsSignal"))) {
                re += "GPS无信号;";
            }
            if ("1".equals(alarmInfo.getString("ECUCheat"))) {
                re += "ECU作弊;";
            }
            if ("1".equals(alarmInfo.getString("riseCheat"))) {
                re += "举升作弊;";
            }
            if ("1".equals(alarmInfo.getString("openCloseBoxCheat"))) {
                re += "开关箱作弊;";
            }
            if ("1".equals(alarmInfo.getString("emptyHeavyCarCheat"))) {
                re += "空重车作弊;";
            }
            if ("1".equals(alarmInfo.getString("stealWork"))) {
                re += "偷运;";
            }
            if ("1".equals(alarmInfo.getString("driveOverTime"))) {
                re += "当天累计驾驶超时;";
            }
            if ("1".equals(alarmInfo.getString("packingOverTime"))) {
                re += "超时停车;";
            }
            if ("1".equals(alarmInfo.getString("inoutArea"))) {

                re += "进出区域;";
            }
            if ("1".equals(alarmInfo.getString("inoutPath"))) {

                re += "进出路线;";
            }

            if ("1".equals(alarmInfo.getString("pathDeviate"))) {
                re += "路线偏离;";
            }
            if ("1".equals(alarmInfo.getString("vssFault"))) {
                re += "车辆VSS故障;";
            }
            if ("1".equals(alarmInfo.getString("iolMassAbr"))) {
                re += "车辆油量异常;";
            }
            if ("1".equals(alarmInfo.getString("isStolen"))) {
                re += "车辆被盗;";
            }
            if ("1".equals(alarmInfo.getString("illegalityFire"))) {
                re += "车辆非法点火;";
            }
            if ("1".equals(alarmInfo.getString("illegalityMove"))) {
                re += "车辆非法移位;";
            }
            if ("1".equals(alarmInfo.getString("turnOnOneSide"))) {

                re += "碰撞侧翻报警;";
            }
            if ("1".equals(alarmInfo.getString("illegalRise"))) {

                re += "非法举斗;";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return re;
        }
        return re;
    }
}

