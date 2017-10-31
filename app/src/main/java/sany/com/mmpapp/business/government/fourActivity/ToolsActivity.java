package sany.com.mmpapp.business.government.fourActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sany.com.mmpapp.MmpApp;
import sany.com.mmpapp.R;
import sany.com.mmpapp.adapter.ToolsElecfenceAdapter;
import sany.com.mmpapp.model.ElecFence;

public class ToolsActivity extends Activity {
    private FrameLayout frameLayout;
    private TextView tv_LeftBtn;
    private TextView tv_Title;
    private ListView lv_Elecfencelist;
    private ToolsElecfenceAdapter arrayAdapter;


    private MmpApp mmpApp;
    private String title;
    private JSONArray elecfenceJSONArray;
    private ArrayList<ElecFence> efNameList=new ArrayList<ElecFence>();
    private ArrayList<String> efNoList=new ArrayList<String>();
    int clickListPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        mmpApp=(MmpApp)getApplication();
        elecfenceJSONArray=mmpApp.getJsonArray();
        Intent intent=getIntent();
        title=intent.getStringExtra("title");

        tv_LeftBtn=(TextView)findViewById(R.id.left_titletop);
        tv_LeftBtn.setText("返回");
        tv_LeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        frameLayout=(FrameLayout)findViewById(R.id.left_top_setting_layout);
        frameLayout.setVisibility(View.VISIBLE);
        tv_Title=(TextView)findViewById(R.id.module_title);
        tv_Title.setText(title);
        lv_Elecfencelist=(ListView)findViewById(R.id.elecfencelist);
        //先取数据
        for(int i=0;i<elecfenceJSONArray.length();i++){
            try{
            JSONObject tempJSONObject=elecfenceJSONArray.getJSONObject(i);

            efNameList.add(new ElecFence(tempJSONObject.getString("efName"),"经纬度采集"));
            efNoList.add(tempJSONObject.getString("efNo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        arrayAdapter=new ToolsElecfenceAdapter(this,efNameList);
        lv_Elecfencelist.setAdapter(arrayAdapter);
        lv_Elecfencelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ToolsActivity.this,efNameList.get(position)+":"+efNoList.get(position),Toast.LENGTH_SHORT).show();
                Intent intentnext=new Intent(ToolsActivity.this,LatlngCollectActivity.class);
                intentnext.putExtra("efName",efNameList.get(position).getEfName());
                intentnext.putExtra("efNo",efNoList.get(position));
                clickListPosition=position;
                //startActivity(intentnext);
                startActivityForResult(intentnext,1000);
            }
        });

    }
    //改变已采集数据电子围栏的状态
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000&&resultCode==1001){
            efNameList.get(clickListPosition).setOperationStr("采集完成");
            lv_Elecfencelist.setAdapter(arrayAdapter);
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
