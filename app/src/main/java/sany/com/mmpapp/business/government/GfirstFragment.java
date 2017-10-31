package sany.com.mmpapp.business.government;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.Util.Time;
import sany.com.mmpapp.business.government.firstActivity.AlarmrecListActivity;
import sany.com.mmpapp.business.government.firstActivity.ElecfenceListActivity;
import sany.com.mmpapp.business.government.firstActivity.VehicleListActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;


/**
 * Created by sunj7 on 16-12-8.
 */
public class GfirstFragment extends Fragment {
    public final static int SIG_GOOD_ALARM = 0x11;
    private MmpApp mmpApp;
    private GovernmentMainActivity mMainActivity;
    private TextView tv_planCount;
    private LinearLayout ll_construction_site;
    private TextView btn_plant;
    private TextView tv_outplantCount;
    private LinearLayout ll_outlet_plant;
    private TextView btn_outplant;
    private TextView tv_vehicleCount;
    private LinearLayout ll_operating_vehicle;
    private TextView btn_vehicle;
    private LinearLayout ll_alarm;
    private TextView tv_alarmcount;
    private String statStartTime;
    private String statEndTime;
    private JSONObject jsonRep;
    private JSONArray statInfoJSONArray;
    private int construction_site;
    private int outlet_plant;
    private int operating_vehicle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainActivity = (GovernmentMainActivity) getActivity();
        mmpApp = (MmpApp) mMainActivity.getApplication();
        //获取界面无素
        View view = inflater.inflate(R.layout.government_fg1, container, false);
        tv_planCount = (TextView) view.findViewById(R.id.planCount);
        ll_construction_site = (LinearLayout) view.findViewById(R.id.construction_site);
        btn_plant = (TextView) view.findViewById(R.id.plant);
        tv_outplantCount = (TextView) view.findViewById(R.id.outplantCount);
        ll_outlet_plant = (LinearLayout) view.findViewById(R.id.outlet_plant);
        btn_outplant = (TextView) view.findViewById(R.id.outplant);
        tv_vehicleCount = (TextView) view.findViewById(R.id.vehicleCount);
        ll_operating_vehicle = (LinearLayout) view.findViewById(R.id.operating_vehicle);
        btn_vehicle = (TextView) view.findViewById(R.id.vehicle);
        ll_alarm = (LinearLayout) view.findViewById(R.id.alarm);
        tv_alarmcount = (TextView) view.findViewById(R.id.alarmcount);
        String[] startEndTime = Time.startAndEndTime(mmpApp.getTransportStartTime(), mmpApp.getTransportEndTime());
        statStartTime = startEndTime[0];
        statEndTime = startEndTime[1];

        ll_outlet_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outlet_plant == 0) {
                    Toast.makeText(mMainActivity, "无开工消纳场！", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(mMainActivity, ElecfenceListActivity.class);
                    intent.putExtra("statStartTime", statStartTime);
                    intent.putExtra("statEndTime", statEndTime);
                    intent.putExtra("type", "3");
                    mMainActivity.startActivity(intent);
                }
            }
        });
        ll_construction_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (construction_site == 0) {
                    Toast.makeText(mMainActivity, "无开工工地！", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mMainActivity, ElecfenceListActivity.class);
                    intent.putExtra("statStartTime", statStartTime);
                    intent.putExtra("statEndTime", statEndTime);
                    intent.putExtra("type", "2");
                    mMainActivity.startActivity(intent);
                }
            }
        });
        ll_operating_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operating_vehicle == 0) {
                    Toast.makeText(mMainActivity, "无开工车辆！", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mMainActivity, VehicleListActivity.class);
                    intent.putExtra("statStartTime", statStartTime);
                    intent.putExtra("statEndTime", statEndTime);
                    intent.putExtra("type", "1");
                    intent.putExtra("parentActivity", "government");
                    mMainActivity.startActivity(intent);
                }
            }
        });
        ll_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(tv_alarmcount.getText())) {
                    return;
                } else {
                    Intent intent = new Intent(mMainActivity, AlarmrecListActivity.class);
                    mMainActivity.startActivity(intent);
                }
            }
        });
        //时间处理结束；
        queryStatInfo(statStartTime, statEndTime);
        return view;
    }

    Handler statInfoHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        statInfoJSONArray = jsonRep.getJSONArray("rows");
                        for (int i = 0; i < statInfoJSONArray.length(); i++) {
                            //统计数据的加载
                            JSONObject tempJSONObject = statInfoJSONArray.getJSONObject(i);
                            if ("开工车辆".equals(tempJSONObject.getString("label"))) {
                                operating_vehicle = tempJSONObject.getInt("value");
                                btn_vehicle.setText("" + operating_vehicle);
                            } else if ("开工工地".equals(tempJSONObject.getString("label"))) {
                                construction_site = tempJSONObject.getInt("value");
                                btn_plant.setText("" + construction_site);
                            } else if ("开工消纳场".equals(tempJSONObject.getString("label"))) {
                                outlet_plant = tempJSONObject.getInt("value");
                                btn_outplant.setText("" + outlet_plant);
                            } else if ("vehicleCount".equals(tempJSONObject.getString("label"))) {
                                tv_vehicleCount.setText("" + tempJSONObject.getInt("value"));
                            } else if ("siteCount".equals(tempJSONObject.getString("label"))) {
                                tv_planCount.setText("" + tempJSONObject.getInt("value"));
                            } else if ("consumFieldCount".equals(tempJSONObject.getString("label"))) {
                                tv_outplantCount.setText("" + tempJSONObject.getInt("value"));
                            }
                        }
                        if (mmpApp.getDriver() == 2) {
                            //如果是执法者，则请求待处理违规
                            ll_alarm.setVisibility(View.VISIBLE);
                            queryAlarmCount();
                        } else {
                            ll_alarm.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SIG_GOOD_ALARM:
                    try {
                        statInfoJSONArray = jsonRep.getJSONArray("rows");
                        if (statInfoJSONArray.length() == 0) {
                            tv_alarmcount.setText("0");
                        } else {
                            JSONObject tempJSONObject = statInfoJSONArray.getJSONObject(0);
                            tv_alarmcount.setText("" + tempJSONObject.getInt("count"));
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

    private void queryStatInfo(String statStartTime, String statEndTime) {
        String url = APIInterface.DATAHOST + APIInterface.getInitStatInfo;
        StatInfoThread statInfoThread = new StatInfoThread(statStartTime, statEndTime, url);
        new Thread(statInfoThread).start();
    }

    private void queryAlarmCount() {
        String url = APIInterface.DATAHOST + APIInterface.getAlarmCountByStaffId;
        AlarmCountThread alarmCountThread = new AlarmCountThread(statStartTime, statEndTime, url);
        new Thread(alarmCountThread).start();
    }


    //内部类，统计数数据的请求线程
    class StatInfoThread implements Runnable {
        private String statStartTime;
        private String statEndTime;
        private String fullUrl;

        /**
         * @param url 地址
         */
        public StatInfoThread(String statStartTime, String statEndTime, String url) {
            // TODO Auto-generated constructor stub
            this.statStartTime = statStartTime;
            this.statEndTime = statEndTime;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair startTime = new BasicNameValuePair(
                    "startTime", statStartTime);
            paraLists.add(startTime);
            BasicNameValuePair endTime = new BasicNameValuePair(
                    "endTime", statEndTime);
            paraLists.add(endTime);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                statInfoHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                statInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                statInfoHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                statInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }

    //内部类，统计数数据的请求线程
    class AlarmCountThread implements Runnable {

        private String fullUrl;

        /**
         * @param url 地址
         */
        public AlarmCountThread(String statStartTime, String statEndTime, String url) {
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
                statInfoHandler.sendEmptyMessage(SIG_GOOD_ALARM);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                statInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                statInfoHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                statInfoHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
