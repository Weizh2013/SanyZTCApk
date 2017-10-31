package sany.com.mmpapp.business.driver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;
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
import sany.com.mmpapp.Util.Time;
import sany.com.mmpapp.business.driver.ThirdActivity.TransportTimesDetailActivity;
import sany.com.mmpapp.chart.DynamicBarChart;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;

/**
 * Created by sunj7 on 17-1-10.
 */
public class DThirdFragment extends Fragment {
    private MmpApp mmpApp;
    private DriverMainActivity mMainActivity;
    private LinearLayout ll_daycount;
    private TextView tv_daycount;
    private LinearLayout ll_monthcount;
    private TextView tv_monthcount;
    private LinearLayout ll_yearcount;
    private TextView tv_yearcount;
    private TextView tv_charttitle;
    private LinearLayout ll_container;
    private DynamicBarChart mDynamicBarChart;
    private GraphicalView chartView;
    private List<double[]> values;
    private String statStartTime;
    private String statEndTime;

    private JSONObject jsonRep;
    private JSONArray statPageDataJSONArray;
    public static final int SIG_GOOD_MONTH=0x11;
    public static final int SIG_GOOD_YEAR=0x12;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_fg3, container, false);
        mMainActivity=(DriverMainActivity)getActivity();
        mmpApp=(MmpApp)mMainActivity.getApplication();
        String[] startEndTime= Time.startAndEndTime(mmpApp.getTransportStartTime(), mmpApp.getTransportEndTime());
        statStartTime=startEndTime[0];
        statEndTime=startEndTime[1];
        ll_daycount=(LinearLayout)view.findViewById(R.id.daycount);
        tv_daycount=(TextView)view.findViewById(R.id.daycount_text);
        ll_daycount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mMainActivity,"daycount",Toast.LENGTH_SHORT).show();
                String countStr= (String)tv_daycount.getText();
                if("0".equals(countStr)){
                    Toast toast= Toast.makeText(mMainActivity, "无数据", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    Intent nextIntent = new Intent(mMainActivity, TransportTimesDetailActivity.class);
                    nextIntent.putExtra("startTime", statStartTime);
                    nextIntent.putExtra("endTime", statEndTime);
                    startActivity(nextIntent);
                }
            }
        });
        ll_monthcount=(LinearLayout)view.findViewById(R.id.monthcount);
        tv_monthcount=(TextView)view.findViewById(R.id.monthcount_text);
        ll_monthcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mMainActivity,"monthcount",Toast.LENGTH_SHORT).show();
                String countStr= (String)tv_monthcount.getText();
                if("0".equals(countStr)){
                    Toast toast= Toast.makeText(mMainActivity, "无数据", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    queryDayCountOfMonthByStaff(statStartTime, statEndTime);
                }
            }
        });
        ll_yearcount=(LinearLayout)view.findViewById(R.id.yearcount);
        tv_yearcount=(TextView)view.findViewById(R.id.yearcount_text);
        ll_yearcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainActivity,"yearcount",Toast.LENGTH_SHORT).show();
                String countStr= (String)tv_yearcount.getText();
                if("0".equals(countStr)){
                    Toast toast= Toast.makeText(mMainActivity, "无数据", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else {
                    queryMonthCountOfYearByStaff(statStartTime, statEndTime);
                }
            }
        });
        tv_charttitle=(TextView)view.findViewById(R.id.charttitle);
        ll_container=(LinearLayout)view.findViewById(R.id.container);
        queryStatPageDataOfStaff(statStartTime,statEndTime);
        return view;
    }

    Handler statPageDataHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        statPageDataJSONArray=jsonRep.getJSONArray("rows");
                        for(int i=0;i<statPageDataJSONArray.length();i++){
                            JSONObject   tempObject=statPageDataJSONArray.getJSONObject(i);
                            if("当日趟次".equals(tempObject.getString("label"))){
                                tv_daycount.setText(tempObject.getString("value"));
                            }else    if("当月趟次".equals(tempObject.getString("label"))){
                                tv_monthcount.setText(tempObject.getString("value"));
                            }else    if("当年趟次".equals(tempObject.getString("label"))){
                                tv_yearcount.setText(tempObject.getString("value"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_MONTH:
                    try {
                        statPageDataJSONArray=jsonRep.getJSONArray("rows");
                        double[] tempTime=new double[statPageDataJSONArray.length()];
                        String[] tempTables=new String[statPageDataJSONArray.length()];
                        for(int i=0;i<statPageDataJSONArray.length();i++){
                            JSONObject   tempObject=statPageDataJSONArray.getJSONObject(i);
                            tempTime[i]=(Double.parseDouble(tempObject.getString("times")));
                            tempTables[i]=(tempObject.getString("gpsDay"));
                        }
                        tv_charttitle.setVisibility(View.VISIBLE);
                        tv_charttitle.setText("当月统计");
                        addDynamicBarchart(mMainActivity.getApplicationContext(), tempTime, tempTables, "", "日期");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_YEAR:
                    try {
                        statPageDataJSONArray=jsonRep.getJSONArray("rows");
                        double[] tempTime=new double[statPageDataJSONArray.length()];
                        String[] tempTables=new String[statPageDataJSONArray.length()];
                        for(int i=0;i<statPageDataJSONArray.length();i++){
                            JSONObject   tempObject=statPageDataJSONArray.getJSONObject(i);
                            tempTime[i]=(Double.parseDouble(tempObject.getString("times")));
                            tempTables[i]=tempObject.getString("gpsMonth");
                        }
                        tv_charttitle.setVisibility(View.VISIBLE);
                        tv_charttitle.setText("当年统计");
                        addDynamicBarchart(mMainActivity.getApplicationContext(), tempTime, tempTables, "", "月份");
                    } catch (JSONException e) {
                        e.printStackTrace();
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



    private void queryStatPageDataOfStaff(String startTime,String endTime){
        String url = APIInterface.DATAHOST + APIInterface.getGetStatPageStatDataofStaff;
        StatePageDataThread statePageDataThread = new StatePageDataThread(startTime,endTime, url);
        new Thread(statePageDataThread).start();
    }
    private void queryDayCountOfMonthByStaff(String startTime,String endTime){
        String url = APIInterface.DATAHOST + APIInterface.getDayCountOfMonthByStaff;
        DayCountOfMonthThread dayCountOfMonthThread = new DayCountOfMonthThread(startTime,endTime, url);
        new Thread(dayCountOfMonthThread).start();
    }
    private void queryMonthCountOfYearByStaff(String startTime,String endTime){
        String url = APIInterface.DATAHOST + APIInterface.getMonthCountOfYearByStaff;
        MonthCountOfYearThread monthCountOfYearThread = new MonthCountOfYearThread(startTime,endTime, url);
        new Thread(monthCountOfYearThread).start();
    }



    class StatePageDataThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public StatePageDataThread(String startTime,String endTime,String url) {
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
                statPageDataHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }

    class DayCountOfMonthThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public DayCountOfMonthThread(String startTime,String endTime,String url) {
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
                statPageDataHandler.sendEmptyMessage(SIG_GOOD_MONTH);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }

    class MonthCountOfYearThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public MonthCountOfYearThread(String startTime,String endTime,String url) {
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
                statPageDataHandler.sendEmptyMessage(SIG_GOOD_YEAR);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                statPageDataHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }



    //加载图表
    private void addDynamicBarchart(Context context ,double[] times,String[] tablesOfX,String str1,String str2) {
        ll_container.removeAllViews();
        mDynamicBarChart = new DynamicBarChart(context,times,tablesOfX,str1,str2);
        chartView = mDynamicBarChart.getChartView();
        chartView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        chartView.setBackgroundColor(Color.WHITE);
        ll_container.addView(chartView);
    }


    //更新图表
    private void updateChart() {
        //updateVaules(); //更新Values
        mDynamicBarChart.setValues(values);
        mDynamicBarChart.setView();
        ll_container.removeAllViews();
        chartView = mDynamicBarChart.getChartView();
        chartView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        chartView.setBackgroundColor(Color.BLACK);
        ll_container.addView(chartView);
    }
}
