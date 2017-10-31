package sany.com.mmpapp.business.government.firstActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.R;
import sany.com.mmpapp.adapter.ElecfenceAdapter;
import sany.com.mmpapp.business.enterprise.firstActivity.WorkSiteDisplayActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;
import sany.com.mmpapp.model.ElecFence;

/**
 * Created by sunj7 on 16-12-9.
 */
public class ElecfenceListActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private ListView lv_elecfence;
    private List<ElecFence> elecFenceList;
    private ElecfenceAdapter elecFenceAdapter;
    private String statStartTime;
    private String statEndTime;
    private String type;
    private JSONObject jsonRep;   //接收的JSON
    private JSONArray elecFenceJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        statStartTime=intent.getStringExtra("statStartTime");
        statEndTime=intent.getStringExtra("statEndTime");
        type=intent.getStringExtra("type");
        setContentView(R.layout.activity_elecfence_list);
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
        if("2".equals(type)) {
            tv_Title.setText(R.string.construction_site_list);
        }else if("3".equals(type)){
            tv_Title.setText(R.string.outlet_plant);
        }
        lv_elecfence=(ListView)findViewById(R.id.elecfence);
        elecFenceList=new ArrayList<ElecFence>();
        //请求数据
        queryElecfenceData(statStartTime,statEndTime,type);
    }

    private void queryElecfenceData(String startTime,String endTime,String type){
        String url = APIInterface.DATAHOST + APIInterface.listWorkedInfo;
        ElecFence1Thread  elecFence1Thread = new ElecFence1Thread(startTime,endTime, type,url);
        new Thread(elecFence1Thread).start();
    }

    Handler elecfenceHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            // progBar_Log.setVisibility(View.GONE);
            switch (msg.what) {
                case Http.SIG_GOOD:
                    try {
                        elecFenceJSONArray=jsonRep.getJSONArray("rows");
                        String tempefName="";
                        String tempefAreaName;
                        String tempmapId;
                        for(int i=0;i<elecFenceJSONArray.length();i++){
                            //工程数据的组合
                            JSONObject tempJSONObject=elecFenceJSONArray.getJSONObject(i);
                            if("2".equals(type)) {
                                tempefName = tempJSONObject.getString("siteName");
                            }else if("3".equals(type)){
                                tempefName = tempJSONObject.getString("consumName");
                            }
                            tempefAreaName=tempJSONObject.getString("efAreaName");
                            tempmapId=tempJSONObject.getString("mapId");
                            elecFenceList.add(new ElecFence(tempefName, tempefAreaName, tempmapId));
                        }
                        elecFenceAdapter=new ElecfenceAdapter(getApplicationContext(),elecFenceList);
                        lv_elecfence.setAdapter(elecFenceAdapter);
                        lv_elecfence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ElecFence elecFence=elecFenceList.get(position);
                                //显示界面
                                Intent intent=new Intent(ElecfenceListActivity.this, WorkSiteDisplayActivity.class);
                                intent.putExtra("workSiteName",elecFence.getEfName());
                                intent.putExtra("workSiteId",elecFence.getMapId());
                                startActivity(intent);
                            }
                        });
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


    class ElecFence1Thread implements Runnable {
        private String startTime1;
        private String endTime1;
        private String type1;
        private String fullUrl;
        /**
         * @param url    地址
         */
        public ElecFence1Thread(String startTime,String endTime,String type,String url) {
            // TODO Auto-generated constructor stub
            this.startTime1=startTime;
            this.endTime1=endTime;
            this.type1=type;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair startTime = new BasicNameValuePair(
                    "startTime", startTime1);
            paraLists.add(startTime);
            BasicNameValuePair endTime = new BasicNameValuePair(
                    "endTime", endTime1);
            paraLists.add(endTime);
            BasicNameValuePair type = new BasicNameValuePair(
                    "type", type1);
            paraLists.add(type);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                elecfenceHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                elecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                elecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                elecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }
}
