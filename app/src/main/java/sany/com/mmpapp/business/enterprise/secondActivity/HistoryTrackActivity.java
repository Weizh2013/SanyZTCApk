package sany.com.mmpapp.business.enterprise.secondActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.dialog.DatePickerFragment;
import sany.com.mmpapp.dialog.SingleChoiceDialog;
import sany.com.mmpapp.dialog.TimePickerFragment;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;

public class HistoryTrackActivity extends Activity implements View.OnClickListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,SingleChoiceDialog.DialogListener{
    public final static int PAUSE=0x11;
    private MmpApp mmpApp;
    private MapView mapView;
    private AMap aMap;
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private TextView tv_RightBtn;
    private DatePickerFragment dateFragment;
    private TimePickerFragment timerFragment;
    private Calendar selStartDate;
    private Calendar selEndDate;
    private int selFlag = 0;//用于标识开始时间和结束时间;
    private TextView et_start_time; //用
    private TextView et_end_time;
    private TextView playSpeed;
    private String[] periodArr=new String[]{"500ms","100ms","50ms"};
    private int[] periodInt=new int[]{500,100,50};
    private int selPeriod=0;
    String strDateStart,strDateEnd;
    public int tracePeriod;
    private String vehiNo;
    private int page=1;
    private int pagesize=500;
    private JSONObject jsonRep;
    private JSONArray historyDataJSONArray=new JSONArray();
    private boolean drawflag=false;
    private boolean pauseFlag=false;
    private int drawcount=0;
    private ImageView img_ControlPlay;
    private TextView tv_ShowTime;
    private LinearLayout lay_PlayerContainer;
    private TextView tv_ShowTimeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_track);
        mmpApp=(MmpApp)getApplication();
        Intent intent=getIntent();
        vehiNo=intent.getStringExtra("vehiNo");
        tv_LeftBtn=(TextView)findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(this);
        frameLayout=(FrameLayout)findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title=(TextView)findViewById(R.id.module_title);
        tv_Title.setText("轨迹回放");
        tv_RightBtn=(TextView)findViewById(R.id.right_titletop);
        tv_RightBtn.setText("设置");
        img_ControlPlay = (ImageView) findViewById(R.id.img_player);
        img_ControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pauseFlag == false) {
                    pauseFlag = true;
                    img_ControlPlay.setImageResource(R.drawable.play);
                } else {
                    pauseFlag = false;
                    restartPaint();
                    img_ControlPlay.setImageResource(R.drawable.pause );
                }
            }
        });
        tv_ShowTime = (TextView) findViewById(R.id.tv_show_time);
        tv_ShowTimeTitle = (TextView) findViewById(R.id.tv_show_time_title);
        lay_PlayerContainer = (LinearLayout) findViewById(R.id.lay_player_container);
        lay_PlayerContainer.setVisibility(View.GONE);
        tv_RightBtn.setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.map_postrace);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }
    //地图初始化
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        com.amap.api.maps2d.UiSettings uiSettings=aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mmpApp.getInitLat(), mmpApp.getInitLng())));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }
    //地图必须重写的函数 begin
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mapView.onDestroy();
    }
    //地图必须重写的函数 end

    //界面点击事件的处理
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_titletop:
                displaySetting();
                break;
            case R.id.left_titletop:
                onBackPressed();
                finish();
             /* OnKeyDown(KEYCODE_BACK, new KeyEvent(KEYCODE_BACK, KeyEvent.ACTION_DOWN));*/
                break;
        }
    }
    //设置按钮点击后调用的函数
    private void displaySetting(){
        LayoutInflater layoutInflater=LayoutInflater.from(HistoryTrackActivity.this);
        final View view=layoutInflater.inflate(R.layout.setting_input_time, null);
        final TextView editText_st=(TextView)view.findViewById(R.id.startTime);
        et_start_time=editText_st;
        final TextView editText_et=(TextView)view.findViewById(R.id.endTime);
        et_end_time=editText_et;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String tmpDate=sdf.format(new Date());
        editText_st.setText(tmpDate);
        editText_et.setText(tmpDate);
        final TextView tv_playSpeed=(TextView)view.findViewById(R.id.playSpeed);
        playSpeed=tv_playSpeed;
        tv_playSpeed.setText(periodArr[0]);
        new AlertDialog.Builder(HistoryTrackActivity.this)
                .setView(view)
                .setPositiveButton("确定",new android.content.DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取出设置的数据，以备用；
                        //Toast.makeText(HistoryTrackActivity.this, et_start_time.getText() + ":" + et_end_time.getText() + ":" + periodInt[selPeriod], Toast.LENGTH_SHORT).show();
                        tracePeriod=periodInt[selPeriod];
                        Date dateStart, dateEnd, dateToday;

                        strDateStart=et_start_time.getText().toString();
                        strDateEnd=et_end_time.getText().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            dateStart = sdf.parse(strDateStart);
                            dateEnd = sdf.parse(strDateEnd);
                            dateToday = new Date();
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(HistoryTrackActivity.this, "无效时间",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (dateEnd.before(dateStart)
                                || dateEnd.after(dateToday)
                                || (dateEnd.getTime() - dateStart.getTime()) > 2 * 24 * 60 * 60
                                * 1000) {
                            Toast.makeText(HistoryTrackActivity.this,
                                    "结束时间要晚于开始时间，并且两者相差不大于48小时", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        queryHistoryData(vehiNo,strDateStart,strDateEnd,page,pagesize);
                        drawflag=false;
                        historyDataJSONArray=new JSONArray();
                        aMap.clear();
                    }

                }).setNegativeButton("取消",null).create().show();
        editText_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selFlag = 0;
                showDatePickDialog();
            }
        });
        editText_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selFlag=1;
                showDatePickDialog();
            }
        });
        tv_playSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChoiceDialog();
            }
        });

    }

    private void showDatePickDialog() {
        dateFragment = new DatePickerFragment(this);
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickDialog() {
        timerFragment = new TimePickerFragment(this);
        timerFragment.show(getFragmentManager(), "timePicker");
    }
    private  void showSingleChoiceDialog(){
        DialogFragment singleChoiceDiag = new SingleChoiceDialog("请选择回放周期",
                periodArr);
        singleChoiceDiag.show(getFragmentManager(), periodArr[0]);
    }
    //日期确定后，所选数据返回到显示框
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        if (selFlag==0){
            Calendar c=Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);
            selStartDate=(Calendar) c.clone();
            showTimePickDialog();
        }else  if(selFlag==1){
            Calendar c=Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth);
            selEndDate=(Calendar) c.clone();
            showTimePickDialog();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(selFlag==0){
            selStartDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
            selStartDate.set(Calendar.MINUTE,minute);
            String date=getFormatDate(selStartDate);
            et_start_time.setText(date);
        }else if(selFlag==1){
            selEndDate.set(Calendar.HOUR_OF_DAY,hourOfDay);
            selEndDate.set(Calendar.MINUTE,minute);
            String date=getFormatDate(selEndDate);
            et_end_time.setText(date);
        }
    }



    @Override
    public void onSingleChoiceClick(DialogFragment dialog, int which) {
        selPeriod = which;
        playSpeed.setText(periodArr[which]);
    }
    //日期格式化
    private String getFormatDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = sdf.format(cal.getTime());
        return date;
    }

    Handler historyDataHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        //请求历史数据
                        JSONArray tempJSONArray=jsonRep.getJSONArray("rows");
                        int total=jsonRep.getInt("total");
                        int lenght=tempJSONArray.length();
                        if(lenght>0){
                            for(int i=0;i<tempJSONArray.length();i++){
                                /*JSONObject tempObject=tempJSONArray.getJSONObject(i);*/
                                historyDataJSONArray.put(tempJSONArray.getJSONObject(i));
                            }
                        }
                        if(!drawflag){
                            drawflag=true;
                            lay_PlayerContainer.setVisibility(View.VISIBLE);
                            img_ControlPlay.setVisibility(View.VISIBLE);
                            img_ControlPlay.setImageResource(R.drawable.pause);
                            DrawHistoryTrack drawHistoryTrack=new DrawHistoryTrack();
                            Thread t=new Thread(drawHistoryTrack);
                            t.start();
                        }
                       if(page*pagesize<total){
                            page++;
                            queryHistoryData(vehiNo,strDateStart,strDateEnd,page,pagesize);
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
                case PAUSE:
                    tv_ShowTimeTitle.setText("暂停中");
                    break;
                default:
                    break;

            }
        }

    };


    //以下是轨迹回放数据的获取以及处理
    //1.数据的获取
    //2.数据的播放；
    //3.暂停以及续继的处理
    private  void queryHistoryData(String evhiNo,String startTime,String endTime,int page,int pagesize){
        String url = APIInterface.DATAHOST + APIInterface.queryHistoryTrace;
        HistoryTrackDataThread historyTrackDataThread = new HistoryTrackDataThread(evhiNo,startTime,endTime,page,pagesize,url);
        new Thread(historyTrackDataThread).start();
    }

    class HistoryTrackDataThread implements Runnable {
        private String vehiNo;
        private String startTime;
        private String endTime;
        private int page;
        private int pagesize;
        private String fullUrl;

        public HistoryTrackDataThread(String vehiNo,String startTime, String endTime,int page,int pagesize, String url) {
            // TODO Auto-generated constructor stub
            this.vehiNo=vehiNo;
            this.startTime = startTime;
            this.endTime = endTime;
            this.page=page;
            this.pagesize=pagesize;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_dateTime = new BasicNameValuePair(
                    "Datetime",Long.toString (new Date().getTime()));
            paraLists.add(param_dateTime);
            BasicNameValuePair param_startTime = new BasicNameValuePair(
                    "startTime", startTime);
            paraLists.add(param_startTime);
            BasicNameValuePair param_endTime = new BasicNameValuePair(
                    "endTime", endTime);
            paraLists.add(param_endTime);
            BasicNameValuePair param_vehiNo = new BasicNameValuePair(
                    "evVehiNo", vehiNo);
            paraLists.add(param_vehiNo);
            BasicNameValuePair param_page = new BasicNameValuePair(
                    "page", Integer.toString(page));
            paraLists.add(param_page);
            BasicNameValuePair param_pagesize= new BasicNameValuePair(
                    "pagesize", Integer.toString(pagesize));
            paraLists.add(param_pagesize);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                historyDataHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                historyDataHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                historyDataHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                historyDataHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    };

    class DrawHistoryTrack implements Runnable{
        @Override
        public void run() {
            JSONObject objStart = null, objEnd = null;
            for(drawcount=0;drawcount<historyDataJSONArray.length()-1;drawcount++){
                if (pauseFlag) {
                    historyDataHandler.sendEmptyMessage(PAUSE);
                    hangUpPaintThread();
                }
                try {
                    objStart = historyDataJSONArray.getJSONObject(drawcount);
                    objEnd = historyDataJSONArray.getJSONObject(drawcount + 1);
                  /*  if("".equals(objStart.getString("lat"))||"null".equals(objStart.getString("lat"))||"".equals(objEnd.getString("lng"))||"null".equals(objEnd.getString("lng"))) {
                        continue;
                    }*/
                    final LatLng pointStart = new LatLng(objStart.getDouble("lat"), objStart.getDouble("lng"));
                    final LatLng pointEnd = new LatLng(objEnd.getDouble("lat"), objEnd.getDouble("lng"));
                    final String time = objEnd.getString("locationTime");
                    historyDataHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                           drawSingleLine(pointStart, pointEnd,time);
                      /*      drawMarkers(pointStart);*/
                        }

                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(drawcount==(historyDataJSONArray.length()-1)) {
                historyDataHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawend();
                    }
                });
            }
        }
    }


    // 单个线段
    private void drawSingleLine(LatLng startPoint, LatLng endPoint, String time) {
      /*  List<LatLng> points = new ArrayList<LatLng>();
        points.add(startPoint);
        points.add(endPoint);*/

/*       aMap.addPolyline((new PolylineOptions()).addAll(points).width(10).color(Color.BLUE));*/

        PolylineOptions polyOp = new PolylineOptions();
        polyOp.add(startPoint, endPoint).width(5.0f).color(Color.RED);
        aMap.addPolyline(polyOp);
       tv_ShowTime.setText(time);
        tv_ShowTimeTitle.setText("正在播放中...." );
        if (drawcount % 5 == 0) {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(endPoint));
        }
        if (drawcount == historyDataJSONArray.length()) {
          /*  tv_ShowTimeTitle.setText("轨迹播放完毕");*/
        }
    }

    private  void drawend(){
        tv_ShowTimeTitle.setText("轨迹播放完毕");
        img_ControlPlay.setImageResource(R.drawable.play);
        pauseFlag=false;
        img_ControlPlay.setVisibility(View.INVISIBLE);
    }

    // 暂停

    private synchronized void hangUpPaintThread() {
        try {
            wait();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 重新启动
    private synchronized void restartPaint() {
        notifyAll();
    }

}
