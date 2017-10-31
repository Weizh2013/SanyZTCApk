package sany.com.mmpapp.business.government;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
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
import sany.com.mmpapp.adapter.ToolsListAdapter;
import sany.com.mmpapp.business.government.fourActivity.ToolsActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.Http;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;

/**
 * Created by sunj7 on 16-12-8.
 */
public class GfourFragment extends Fragment {
    private MmpApp mmpApp;
    private GovernmentMainActivity mMainActivity;
    private GridView gridView;
    private ToolsListAdapter adapter;
    private int type;
    private JSONObject jsonRep;
    private JSONArray elecfenceJSONArray;
    private int[] tipsArray=new int[]{0,0,0};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mmpApp=(MmpApp)getActivity().getApplication();
        mMainActivity = (GovernmentMainActivity) getActivity();
        View view = inflater.inflate(R.layout.government_fg4, container, false);
        gridView=(GridView)view.findViewById(R.id.tool);
        adapter=new ToolsListAdapter(mMainActivity.getApplicationContext(),tipsArray);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mMainActivity.getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();//测试用
                switch (position) {
                    case 0:
                        type=2;
                        break;
                    case 1:
                        type=3;
                        break;
                    case 2:
                        type=5;
                        break;
                }
                //根据电字围栏的类型，请求电子围栏；
                queryElecfence(type);
            }
            //请求相关数据，并根据请求的数据进行跳转；

        });
        return view;
    }

    Handler toolsElecfenceHandler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case Http.SIG_GOOD:
                    try{
                        //获取电子围栏数据，如果为0，不跳转，如是有，则根据不同的类弄进行表头的设置；
                        elecfenceJSONArray=jsonRep.getJSONArray("rows");
                        if(elecfenceJSONArray.length()!=0) {
                            mmpApp.setJsonArray(elecfenceJSONArray);
                            Intent intent=new Intent(mMainActivity,ToolsActivity.class);
                           if (type==2){
                                intent.putExtra("title","工地列表");
                            }else if(type==3){
                                intent.putExtra("title","消纳场");
                            }else if(type==5){
                                intent.putExtra("title","禁区列表");
                            }
                            startActivity(intent);
                        }else{
                            Toast.makeText(mMainActivity,"无数据",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
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


    private void queryElecfence(int eftype){
        String url = APIInterface.DATAHOST + APIInterface.getElecfenceListOfEftype;
        ToolsElecfenceThread toolsElecfenceThread = new ToolsElecfenceThread(eftype, url);
        new Thread(toolsElecfenceThread).start();
    }

    class ToolsElecfenceThread implements Runnable {
        private int efType;
        private String fullUrl;
        /**
         *
         * @param efType
         *            电子围栏类型

         * @param url
         *            地址
         */
        public ToolsElecfenceThread(int efType, String url) {
            // TODO Auto-generated constructor stub
            this.efType = efType;
            this.fullUrl=url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_efType = new BasicNameValuePair(
                    "efType", ""+efType);
            paraLists.add(param_efType);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                toolsElecfenceHandler.sendEmptyMessage(Http.SIG_GOOD);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                toolsElecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                toolsElecfenceHandler.sendEmptyMessage(Http.SIG_BAD_JSON);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                toolsElecfenceHandler.sendEmptyMessage(Http.SIG_BAD);
            }
        }
    }









}

