package sany.com.mmpapp.business.government.centerActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sany.com.mmpapp.R;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;

public class GprojectDetailActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private TextView tv_piName;
    private TextView tv_efName;
    private LinearLayout ll_piConsFiels;
    private LinearLayout ll_piTranUnitName;
    private TextView tv_piConsUnitName;
    private TextView tv_startTime;
    private TextView tv_endTime;
    private TextView tv_shipTime;
    private TextView tv_vehicle;
    private TextView tv_route;
    private TextView tv_pass;
    private TextView tv_reject;
    private String piId;
    private String piWorkSiteId;
    private JSONObject jsonRep;   //接收的JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gproject_detail);
        tv_LeftBtn = (TextView) findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        //设置表头
        frameLayout = (FrameLayout) findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title = (TextView) findViewById(R.id.module_title);
        tv_Title.setText("工程详情");
        //具体的内容
        //获取传输过来的数据
        Intent intent = getIntent();
        piId = intent.getStringExtra("piId");
        piWorkSiteId = intent.getStringExtra("piWorkSiteId");
        String piName = intent.getStringExtra("piName");
        String efName = intent.getStringExtra("efName");
        String piConsFiels = intent.getStringExtra("piConsFiels");
        String piTranUnitName = intent.getStringExtra("piTranUnitName");
        String piConsUnitName = intent.getStringExtra("piConsUnitName");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        String shipStartTime = intent.getStringExtra("shipStartTime");
        String shipEndTime = intent.getStringExtra("shipEndTime");
        tv_piName = (TextView) findViewById(R.id.piName);
        tv_efName = (TextView) findViewById(R.id.efName);
        ll_piConsFiels = (LinearLayout) findViewById(R.id.piConsFiels);
        ll_piTranUnitName = (LinearLayout) findViewById(R.id.piTranUnitName);
        tv_piConsUnitName = (TextView) findViewById(R.id.piConsUnitName);
        tv_startTime = (TextView) findViewById(R.id.startTime);
        tv_endTime = (TextView) findViewById(R.id.endTime);
        tv_shipTime = (TextView) findViewById(R.id.shipTime);
        tv_vehicle = (TextView) findViewById(R.id.vehicle);
        tv_route = (TextView) findViewById(R.id.route);
        tv_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求车辆列表
                Intent nextIntent = new Intent(GprojectDetailActivity.this, GCenterVehicleListActivity.class);
                nextIntent.putExtra("piId", piId);
                startActivity(nextIntent);
            }
        });
        tv_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求路线蔚展示
                Intent nextIntent = new Intent(GprojectDetailActivity.this, GCenterDisplayRouteActivity.class);
                nextIntent.putExtra("ws", piWorkSiteId);
                nextIntent.putExtra("piId", piId);
                startActivity(nextIntent);
            }
        });
        tv_piName.setText(piName);
        tv_efName.setText(efName);
        //消纳厂定义为1，动态添加数据消纳厂数据
        String[] consFiels = piConsFiels.split(";");
        ll_piConsFiels.addView(addView(consFiels, 1));
        //动态添加运输单位数据
        String[] tranUnitName = piTranUnitName.split(",");
        ll_piTranUnitName.addView(addView(tranUnitName, 2));
        //建设单位是否为""
        if ("null".equals(piConsUnitName)) {
            tv_piConsUnitName.setText("无");
        } else {
            tv_piConsUnitName.setText(piConsUnitName);
        }
        tv_startTime.setText(startTime);
        tv_endTime.setText(endTime);
        tv_shipTime.setText(shipStartTime + "--" + shipEndTime);
        tv_pass = (TextView) findViewById(R.id.pass);
        tv_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalSubmit(piId, "pass");

            }
        });
        tv_reject = (TextView) findViewById(R.id.reject);
        tv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvalSubmit(piId, "reject");
            }
        });
    }

    //动态添加元素布局；
    private View addView(String[] str1, int type) {
        // TODO 动态添加布局(java方式)
        LinearLayout view = new LinearLayout(this);
        //只有一个元素的情况
        if (str1.length == 1) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);//设置布局参数
            view.setOrientation(LinearLayout.HORIZONTAL);// 设置子View的Linearlayout// 为垂直方向布局
            //定义子View中两个元素的布局
            TextView tv1 = new TextView(this);
            tv1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            tv1.setGravity(Gravity.CENTER);
            tv1.setWidth(0);
            TextView tv2 = new TextView(this);
            tv2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            tv2.setGravity(Gravity.CENTER);
            tv2.setWidth(0);
            if (type == 1) {
                tv1.setText("消纳场");
            } else {
                tv1.setText("运输单位");
            }
            tv2.setText(str1[0]);
            view.addView(tv1);//将TextView 添加到子View 中
            view.addView(tv2);//将TextView 添加到子View 中

        } else {
            //多个元素的情况
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);//设置布局参数
            view.setOrientation(LinearLayout.VERTICAL);// 设置子View的Linearlayout// 为垂直方向布局
            for (int i = 0; i < str1.length; i++) {
                //view下添加子元素
                LinearLayout viewChild = new LinearLayout(this);
                viewChild.setLayoutParams(lp);
                viewChild.setOrientation(LinearLayout.HORIZONTAL);
                viewChild.setGravity(Gravity.CENTER_HORIZONTAL);
                viewChild.setPadding(0, 5, 0, 5);
                //在viewChild下添加TextView
                TextView tv1 = new TextView(this);
                tv1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                tv1.setGravity(Gravity.CENTER);
                tv1.setWidth(0);
                TextView tv2 = new TextView(this);
                tv2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                tv2.setGravity(Gravity.CENTER);
                tv2.setWidth(0);
                if (i == 0) {
                    if (type == 1) {
                        tv1.setText("消纳场");
                    } else {
                        tv1.setText("运输单位");
                    }
                } else {
                    tv1.setText(" ");
                }
                tv2.setText(str1[i]);
                viewChild.addView(tv1);//将TextView 添加到子View 中
                viewChild.addView(tv2);//将TextView 添加到子View 中
                view.addView(viewChild);
            }
        }
        return view;
    }

    private void approvalSubmit(String piId, String submit) {
        String url = "";
        if ("pass".equals(submit)) {
            url = APIInterface.DATAHOST + APIInterface.projAgree;
        } else if ("reject".equals(submit)) {
            url = APIInterface.DATAHOST + APIInterface.rejectProj;
        }
        if ("".equals(url)) return;
        ApprovalSubmitThread approvalSubmitThread = new ApprovalSubmitThread(piId, url);
        new Thread(approvalSubmitThread).start();
    }

    Handler approvalSubmitHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        String state = jsonRep.getString("state");
                        if ("true".equals(state)) {
                            Toast.makeText(GprojectDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            finish();
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


    class ApprovalSubmitThread implements Runnable {
        private String piId;
        private String fullUrl;
        /**
         * @param url 地址
         */
        public ApprovalSubmitThread(String piId, String url) {
            // TODO Auto-generated constructor stub
            this.piId = piId;
            this.fullUrl = url;
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
                jsonRep = new JSONObject(strRep);
                approvalSubmitHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                approvalSubmitHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                approvalSubmitHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                approvalSubmitHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
