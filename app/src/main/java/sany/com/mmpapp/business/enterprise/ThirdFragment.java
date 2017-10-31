package sany.com.mmpapp.business.enterprise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
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
import sany.com.mmpapp.business.enterprise.thirdActivity.EnterpriseAlarmoftypeActivity;
import sany.com.mmpapp.business.enterprise.thirdActivity.VechileCountActivity;
import sany.com.mmpapp.chart.DynamicBarChart;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;

/**
 * Created by sunj7 on 16-8-19.
 */
public class ThirdFragment extends Fragment {
    private MmpApp mmpApp;
    private EnterpriseMainActivity mMainActivity;
    private ViewPager viewPager;
    private LinearLayout tab_transporttimes;
    private LinearLayout tab_alarminfo;
    private PagerAdapter pagerAdapter = null;
    private List<View> views = new ArrayList<View>();
    private LinearLayout ll_daycount;
    private TextView tv_daycount;
    private LinearLayout ll_monthcount;
    private TextView tv_monthcount;
    private LinearLayout ll_yearcount;
    private TextView tv_yearcount;
    private TextView tv_charttitle;
    private LinearLayout ll_container;
    private DynamicBarChart mDynamicBarChart;
    private LinearLayout ll_typeofalarm;
    private TextView tv_typeofalarm;
    private LinearLayout ll_vehicleofalarm;
    private TextView tv_vehicleofalarm;
    private GraphicalView chartView;
    private List<double[]> values;
    private String statStartTime;
    private String statEndTime;
    private JSONObject jsonRep;
    private JSONArray statPageDataJSONArray;
    public static final int SIG_GOOD_MONTH = 0x11;
    public static final int SIG_GOOD_YEAR = 0x12;
    public static final int SIG_GOOD_ALARM = 0X13;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enterprise_fg3, container, false);
        mMainActivity = (EnterpriseMainActivity) getActivity();
        mmpApp = (MmpApp) mMainActivity.getApplication();
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        //请求统计数据的时间处理
        String[] startEndTime = Time.startAndEndTime(mmpApp.getTransportStartTime(), mmpApp.getTransportEndTime());
        statStartTime = startEndTime[0];
        statEndTime = startEndTime[1];
        //tab的加载
        tab_transporttimes = (LinearLayout) view.findViewById(R.id.tab_transporttimes);
        tab_alarminfo = (LinearLayout) view.findViewById(R.id.tab_alarminfo);
        tab_transporttimes.setBackgroundColor(Color.parseColor("#00BFFF"));
        tab_alarminfo.setBackgroundColor(Color.parseColor("#888888"));
        //加载tab的子VIEW
        View tab01 = inflater.inflate(R.layout.tab_transporttimes, null);
        ll_daycount = (LinearLayout) tab01.findViewById(R.id.daycount);
        tv_daycount = (TextView) tab01.findViewById(R.id.daycount_text);
        ll_daycount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mMainActivity,"daycount",Toast.LENGTH_SHORT).show();
                String countStr = (String) tv_daycount.getText();
                if ("0".equals(countStr)) {
                    return;
                } else {
                    Intent nextIntent = new Intent(mMainActivity, VechileCountActivity.class);
                    nextIntent.putExtra("startTime", statStartTime);
                    nextIntent.putExtra("endTime", statEndTime);
                    startActivity(nextIntent);
                }
            }
        });
        ll_monthcount = (LinearLayout) tab01.findViewById(R.id.monthcount);
        tv_monthcount = (TextView) tab01.findViewById(R.id.monthcount_text);
        ll_monthcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mMainActivity,"monthcount",Toast.LENGTH_SHORT).show();
                queryDayCountOfMonth(statStartTime, statEndTime);
            }
        });
        ll_yearcount = (LinearLayout) tab01.findViewById(R.id.yearcount);
        tv_yearcount = (TextView) tab01.findViewById(R.id.yearcount_text);
        ll_yearcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainActivity,"yearcount",Toast.LENGTH_SHORT).show();
                queryMonthCountOfYear(statStartTime, statEndTime);
            }
        });
        tv_charttitle = (TextView) tab01.findViewById(R.id.charttitle);
        ll_container = (LinearLayout) tab01.findViewById(R.id.container);
        View tab02 = inflater.inflate(R.layout.tab_alarminfo, null);
        ll_typeofalarm = (LinearLayout) tab02.findViewById(R.id.typeofalarm);
        ll_typeofalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainActivity, "type", Toast.LENGTH_SHORT).show();
                String temp=tv_typeofalarm.getText().toString();
                if("0".equals(temp)){
                    Display display=getView().getDisplay();
                    int height=display.getHeight();
                    Toast toast=Toast.makeText(mMainActivity, "无统计信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,height/5*3);
                    toast.show();
                }else {
                    Intent nextIntent = new Intent(mMainActivity, EnterpriseAlarmoftypeActivity.class);
                    nextIntent.putExtra("startTime", statStartTime);
                    nextIntent.putExtra("endTime", statEndTime);
                    nextIntent.putExtra("type", "0");
                    mMainActivity.startActivity(nextIntent);
                }
            }
        });
        tv_typeofalarm = (TextView) tab02.findViewById(R.id.typeofalarm_text);
        ll_vehicleofalarm = (LinearLayout) tab02.findViewById(R.id.vehicleofalarm);
        ll_vehicleofalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp=tv_vehicleofalarm.getText().toString();
                if("0".equals(temp)){
                    Display display=getView().getDisplay();
                    int height=display.getHeight();
                    Toast toast=Toast.makeText(mMainActivity, "无统计信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,height/5*3);
                    toast.show();
                }else {
                    // Toast.makeText(mMainActivity,"vehicle",Toast.LENGTH_SHORT).show();
                    Intent nextIntent = new Intent(mMainActivity, EnterpriseAlarmoftypeActivity.class);
                    nextIntent.putExtra("startTime", statStartTime);
                    nextIntent.putExtra("endTime", statEndTime);
                    nextIntent.putExtra("type", "1");
                    mMainActivity.startActivity(nextIntent);
                }
            }
        });
        tv_vehicleofalarm = (TextView) tab02.findViewById(R.id.vehicleofalarm_text);
        views.add(tab01);
        views.add(tab02);
        //加载完成
        pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = views.get(position);
                container.addView(view);
                return view;
            }
        };
        viewPager.setAdapter(pagerAdapter);
        setOnListeners();
        queryStatPageDataOfEnterprise(statStartTime, statEndTime);
        return view;
    }


    private void setOnListeners() {
        ButtonListener listener = new ButtonListener();
        tab_transporttimes.setOnClickListener(listener);
        tab_alarminfo.setOnClickListener(listener);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                int currentItem = viewPager.getCurrentItem();
                resetImg();
                switch (currentItem) {
                    case 0:
                        tab_transporttimes.setBackgroundColor(0xFF00BFFF);
                        queryStatPageDataOfEnterprise(statStartTime, statEndTime);
                        break;
                    case 1:
                        tab_alarminfo.setBackgroundColor(0xFF00BFFF);
                        queryAlarmOfTypeAndVehicle(statStartTime, statEndTime);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 将所有的图片切换为暗色
     */
    private void resetImg() {
        tab_transporttimes.setBackgroundColor(Color.GRAY);
        tab_alarminfo.setBackgroundColor(Color.GRAY);
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            resetImg();
            switch (v.getId()) {
                case R.id.tab_transporttimes:
                    viewPager.setCurrentItem(0);
                    tab_transporttimes.setBackgroundColor(0xFF00BFFF);
                    break;
                case R.id.tab_alarminfo:
                    viewPager.setCurrentItem(1);
                    tab_alarminfo.setBackgroundColor(0xFF00BFFF);
                    break;
                default:
                    break;
            }
        }
    }

    //加载图表
    private void addDynamicBarchart(Context context, double[] times, String[] tablesOfX, String str1, String str2) {
        ll_container.removeAllViews();
        mDynamicBarChart = new DynamicBarChart(context, times, tablesOfX, str1, str2);
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


    Handler statPageDataHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        statPageDataJSONArray = jsonRep.getJSONArray("rows");
                        for (int i = 0; i < statPageDataJSONArray.length(); i++) {
                            String tempstr;
                            JSONObject tempObject = statPageDataJSONArray.getJSONObject(i);
                            if ("当日趟次".equals(tempObject.getString("label"))) {
                                tempstr = tempObject.getString("value");
                                tempstr = tempstr.substring(0, tempstr.length() - 2);
                                tv_daycount.setText(tempstr);
                            } else if ("当月趟次".equals(tempObject.getString("label"))) {
                                tempstr = tempObject.getString("value");
                                tempstr = tempstr.substring(0, tempstr.length() - 2);
                                tv_monthcount.setText(tempstr);
                            } else if ("当年趟次".equals(tempObject.getString("label"))) {
                                tempstr = tempObject.getString("value");
                                tempstr = tempstr.substring(0, tempstr.length() - 2);
                                tv_yearcount.setText(tempstr);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_MONTH:
                    try {
                        statPageDataJSONArray = jsonRep.getJSONArray("rows");
                        double[] tempTime = new double[statPageDataJSONArray.length()];
                        String[] tempTables = new String[statPageDataJSONArray.length()];
                        for (int i = 0; i < statPageDataJSONArray.length(); i++) {
                            JSONObject tempObject = statPageDataJSONArray.getJSONObject(i);
                            tempTime[i] = (Double.parseDouble(tempObject.getString("times")));
                            tempTables[i] = (tempObject.getString("gpsDay"));
                        }
                        tv_charttitle.setText("当月统计");
                        tv_charttitle.setVisibility(View.VISIBLE);
                        addDynamicBarchart(mMainActivity.getApplicationContext(), tempTime, tempTables, "", "日期");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_YEAR:
                    try {
                        statPageDataJSONArray = jsonRep.getJSONArray("rows");
                        double[] tempTime = new double[statPageDataJSONArray.length()];
                        String[] tempTables = new String[statPageDataJSONArray.length()];
                        for (int i = 0; i < statPageDataJSONArray.length(); i++) {
                            JSONObject tempObject = statPageDataJSONArray.getJSONObject(i);
                            tempTime[i] = (Double.parseDouble(tempObject.getString("times")));
                            tempTables[i] = tempObject.getString("gpsMonth");
                        }
                        tv_charttitle.setText("当年统计");
                        tv_charttitle.setVisibility(View.VISIBLE);
                        addDynamicBarchart(mMainActivity.getApplicationContext(), tempTime, tempTables, "", "月份");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_ALARM:
                    try {
                        statPageDataJSONArray = jsonRep.getJSONArray("rows");
                        for (int i = 0; i < statPageDataJSONArray.length(); i++) {
                            JSONObject tempObject = statPageDataJSONArray.getJSONObject(i);
                            if ("按报警类型统计".equals(tempObject.getString("label"))) {
                                tv_typeofalarm.setText("" + tempObject.getString("value"));
                            } else if ("按报警车辆统计".equals(tempObject.getString("label"))) {
                                tv_vehicleofalarm.setText("" + tempObject.getString("value"));
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


    //以下是请求接口数据（主要有六个接口）
    private void queryStatPageDataOfEnterprise(String startTime, String endTime) {
        String url = APIInterface.DATAHOST + APIInterface.getStatPageStatData;
        StatePageDataThread statePageDataThread = new StatePageDataThread(startTime, endTime, url);
        new Thread(statePageDataThread).start();
    }

    private void queryDayCountOfMonth(String startTime, String endTime) {
        String url = APIInterface.DATAHOST + APIInterface.getDayCountOfMonth;
        DayCountOfMonthThread dayCountOfMonthThread = new DayCountOfMonthThread(startTime, endTime, url);
        new Thread(dayCountOfMonthThread).start();
    }

    private void queryMonthCountOfYear(String startTime, String endTime) {
        String url = APIInterface.DATAHOST + APIInterface.getMonthCountOfYear;
        MonthCountOfYearThread monthCountOfYearThread = new MonthCountOfYearThread(startTime, endTime, url);
        new Thread(monthCountOfYearThread).start();
    }

    private void queryAlarmOfTypeAndVehicle(String startTime, String endTime) {
        String url = APIInterface.DATAHOST + APIInterface.getAlarmofTypeAndVechile;
        AlarmOfTypeAndVehicleThread alarmOfTypeAndVehicleThread = new AlarmOfTypeAndVehicleThread(startTime, endTime, url);
        new Thread(alarmOfTypeAndVehicleThread).start();
    }

    class StatePageDataThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;
        /**
         * @param url 地址
         */
        public StatePageDataThread(String startTime, String endTime, String url) {
            // TODO Auto-generated constructor stub
            this.estartTime = startTime;
            this.eendTime = endTime;
            this.fullUrl = url;
        }
//getStatPageStatData.do?startTime=2017-03-14+20%3A00%3A00&endTime=2017-03-15+04%3A00%3A00
        //{"rows":[{"value":"0.0","label":"当日趟次"},{"value":"0.0","label":"当月趟次"},{"value":"144.0","label":"当年趟次"}]}
        //{"rows":[{"value":"0.0","label":"当日趟次"},{"value":"168.0","label":"当月趟次"},{"value":"168.0","label":"当年趟次"}]}
        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair startTime1 = new BasicNameValuePair(
                    "startTime", estartTime);
            paraLists.add(startTime1);
            BasicNameValuePair endTime1 = new BasicNameValuePair(
                    "endTime", eendTime);
            paraLists.add(endTime1);
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
         * @param url 地址
         */
        public DayCountOfMonthThread(String startTime, String endTime, String url) {
            // TODO Auto-generated constructor stub
            this.estartTime = startTime;
            this.eendTime = endTime;
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
                    "endTime", eendTime);
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
         * @param url 地址
         */
        public MonthCountOfYearThread(String startTime, String endTime, String url) {
            // TODO Auto-generated constructor stub
            this.estartTime = startTime;
            this.eendTime = endTime;
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
                    "endTime", eendTime);
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

    class AlarmOfTypeAndVehicleThread implements Runnable {
        private String estartTime;
        private String eendTime;
        private String fullUrl;

        /**
         * @param url 地址
         */
        public AlarmOfTypeAndVehicleThread(String startTime, String endTime, String url) {
            // TODO Auto-generated constructor stub
            this.estartTime = startTime;
            this.eendTime = endTime;
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
                    "endTime", eendTime);
            paraLists.add(endTime);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                statPageDataHandler.sendEmptyMessage(SIG_GOOD_ALARM);
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
}
