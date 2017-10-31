package sany.com.mmpapp.business.government;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Marker;

import sany.com.mmpapp.LoginActivity;
import sany.com.mmpapp.R;
import sany.com.mmpapp.globalconst.SharedPreferConst;


public class GovernmentMainActivity extends FragmentActivity implements View.OnClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnCameraChangeListener {
    // 初始化顶部栏显示
    private TextView titleLeft;
    private TextView titleTv;
    // 定义4个Fragment对象
    private GfirstFragment fg1;
    private GsecondFragment fg2;
    private GthirdFragment fg3;
    private GfourFragment fg4;
    private GcenterFragment cfg;
    // 帧布局对象，用来存放Fragment对象
    private FrameLayout frameLayout;
    // 定义每个选项中的相关控件
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private RelativeLayout thirdLayout;
    private RelativeLayout fourthLayout;
    private RelativeLayout centerLayout;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private ImageView fourthImage;
    private ImageView centerImage;
    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    private TextView fourthText;
    private TextView centerText;
    // 定义几个颜色
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xff000000;
    private int dark = 0xffff0000;

    private SharedPreferences prefer;
    private int choiceindex;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefer=this.getSharedPreferences("SP",MODE_PRIVATE);
        choiceindex=0;
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        initView(); // 初始化界面控件
        setChioceItem(choiceindex); // 初始化页面加载时显示第一个选项卡*/
    }

    /**
     * 初始化页面
     */
    private void initView() {
// 初始化页面标题栏
        titleLeft=(TextView)findViewById(R.id.titleLeft_text);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this, LoginActivity.class));
                saveUsrInfo();
                startActivity(new Intent(GovernmentMainActivity.this, LoginActivity.class));
            }
        });
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("首 页");
// 初始化底部导航栏的控件
        firstImage = (ImageView) findViewById(R.id.first_image);
        secondImage = (ImageView) findViewById(R.id.second_image);
        thirdImage = (ImageView) findViewById(R.id.third_image);
        fourthImage = (ImageView) findViewById(R.id.fourth_image);
        centerImage=(ImageView)findViewById(R.id.center_image);

        firstText = (TextView) findViewById(R.id.first_text);
        secondText = (TextView) findViewById(R.id.second_text);
        thirdText = (TextView) findViewById(R.id.third_text);
        fourthText = (TextView) findViewById(R.id.fourth_text);
        centerText=(TextView)findViewById(R.id.center_text);
        firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
        thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
        fourthLayout = (RelativeLayout) findViewById(R.id.fourth_layout);
        centerLayout=(RelativeLayout)findViewById(R.id.center_layout);
        fourthText.setText(R.string.tools);
        firstLayout.setOnClickListener(this);
        secondLayout.setOnClickListener(this);
        thirdLayout.setOnClickListener(this);
        fourthLayout.setOnClickListener(this);
        centerLayout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_layout:
                choiceindex=0;
                setChioceItem(choiceindex);

                break;
            case R.id.second_layout:
                choiceindex=1;
                setChioceItem(choiceindex);
                break;
            case R.id.third_layout:
                choiceindex=2;
                setChioceItem(choiceindex);
                break;
            case R.id.fourth_layout:
                choiceindex=3;
                setChioceItem(choiceindex);
                break;
            case R.id.center_layout:
                choiceindex=4;
                setChioceItem(choiceindex);
                break;
            default:
                break;
        }
    }
    /**
     * 设置点击选项卡的事件处理
     *
     * @param index 选项卡的标号：0, 1, 2, 3
     */
    private void setChioceItem(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        clearChioce(); // 清空, 重置选项, 隐藏所有Fragment
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                firstImage.setImageResource(R.drawable.iconhomeactive);
                firstText.setTextColor(dark);
                // firstLayout.setBackgroundColor(gray);
// 如果fg1为空，则创建一个并添加到界面上
               // if (fg1 == null) {
                    fg1 = new GfirstFragment();
                    fragmentTransaction.add(R.id.content, fg1);
               // } else {
// 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg1);
              //  }
                titleTv.setText("首 页");
                break;
            case 1:
                secondImage.setImageResource(R.drawable.iconrealmonitoractive);
                secondText.setTextColor(dark);
                //secondLayout.setBackgroundColor(gray);
                if (fg2 == null) {
                    fg2 = new GsecondFragment();
                    fragmentTransaction.add(R.id.content, fg2);
                } else {
                    fragmentTransaction.show(fg2);
                }
                titleLeft.setVisibility(View.INVISIBLE);
                titleTv.setText("监 控");

                break;
            case 2:
                thirdImage.setImageResource(R.drawable.iconstatisanalysactive);
                thirdText.setTextColor(dark);
                //thirdLayout.setBackgroundColor(gray);
                if (fg3 == null) {
                    fg3 = new GthirdFragment();
                    fragmentTransaction.add(R.id.content, fg3);
                } else {
                    fragmentTransaction.show(fg3);
                }
                titleLeft.setVisibility(View.INVISIBLE);
                titleTv.setText("统 计");
                break;
            case 3:
                fourthImage.setImageResource(R.drawable.iconaboutactive);
                fourthText.setTextColor(dark);
                //fourthLayout.setBackgroundColor(gray);
                if (fg4 == null) {
                    fg4 = new GfourFragment();
                    fragmentTransaction.add(R.id.content,fg4);

                } else {
                    fragmentTransaction.show(fg4);
                }
                titleLeft.setVisibility(View.INVISIBLE);
                titleTv.setText("工 具");
                break;
            case 4:
                centerImage.setImageResource(R.drawable.zhengwu2);
                centerText.setTextColor(dark);
                //fourthLayout.setBackgroundColor(gray);
                    cfg = new GcenterFragment();
                    fragmentTransaction.add(R.id.content,cfg);
                    fragmentTransaction.show(cfg);
                titleLeft.setVisibility(View.INVISIBLE);
                titleTv.setText("政 务");
                break;
        }
        fragmentTransaction.commit(); // 提交
    }
    /**
     * 当选中其中一个选项卡时，其他选项卡重置为默认
     */
    private void clearChioce() {
        firstImage.setImageResource(R.drawable.iconhome);
        firstText.setTextColor(gray);
        firstLayout.setBackgroundColor(whirt);
        secondImage.setImageResource(R.drawable.iconrealmonitor);
        secondText.setTextColor(gray);
        secondLayout.setBackgroundColor(whirt);
        thirdImage.setImageResource(R.drawable.iconstatisanalys);
        thirdText.setTextColor(gray);
        thirdLayout.setBackgroundColor(whirt);
        fourthImage.setImageResource(R.drawable.iconabout);
        fourthText.setTextColor(gray);
        fourthLayout.setBackgroundColor(whirt);
        centerImage.setImageResource(R.drawable.zhengwu2);
        centerText.setTextColor(gray);
        centerLayout.setBackgroundColor(whirt);
    }
    /**
     * 隐藏Fragment
     *
     * @param fragmentTransaction
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        if (fg1 != null) {
            fragmentTransaction.hide(fg1);
        }
        if (fg2 != null) {
            fragmentTransaction.hide(fg2);
        }
        if (fg3 != null) {
            fragmentTransaction.hide(fg3);
        }
        if (fg4 != null) {
            fragmentTransaction.hide(fg4);
        }
        if (cfg != null) {
            fragmentTransaction.hide(cfg);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    public boolean saveUsrInfo() {
        return prefer.edit().putString(SharedPreferConst.PREFER_ITEM_USRNAME, "")
                .putString(SharedPreferConst.PREFER_ITEM_PASSWORD,"")
                .putBoolean(SharedPreferConst.PREFER_ITEM_AUTOLOGIN,false)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(GovernmentMainActivity.this,""+choiceindex,Toast.LENGTH_SHORT).show();
        setChioceItem(choiceindex);
    }
}