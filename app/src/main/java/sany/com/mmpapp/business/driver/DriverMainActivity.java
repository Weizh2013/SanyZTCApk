package sany.com.mmpapp.business.driver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Marker;
import sany.com.mmpapp.LoginActivity;
import sany.com.mmpapp.R;
import sany.com.mmpapp.globalconst.SharedPreferConst;

public class DriverMainActivity extends FragmentActivity implements View.OnClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnCameraChangeListener {
    private TextView titleLeft;
    private TextView titleTv;
    // 定义4个Fragment对象
    private DFirstFragment fg1;
    private DSecondFragment fg2;
    private DThirdFragment fg3;
    private DFourFragment fg4;
    // 帧布局对象，用来存放Fragment对象
    private FrameLayout frameLayout;
    // 定义每个选项中的相关控件
    private RelativeLayout firstLayout;
    private RelativeLayout secondLayout;
    private RelativeLayout thirdLayout;
    private RelativeLayout fourthLayout;
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private ImageView fourthImage;
    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    private TextView fourthText;
    // 定义几个颜色
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xff000000;
    private int dark = 0xffff0000;
    // 定义FragmentManager对象管理器
    private FragmentManager fragmentManager;
    private SharedPreferences prefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefer=this.getSharedPreferences("SP",MODE_PRIVATE);
        int i=0;
        //司机的界面与企业的界面相同，载入同一个xml；
        setContentView(R.layout.activity_enterprise_main);
        fragmentManager = getSupportFragmentManager();
        initView(); // 初始化界面控件
        setChioceItem(i); // 初始化页面加载时显示第一个选项卡
    }

    /**
     * 初始化页面
     *
     */
    private void initView() {
        // 初始化页面标题栏
        titleLeft = (TextView) findViewById(R.id.titleLeft_text);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUsrInfo();
                startActivity(new Intent(DriverMainActivity.this, LoginActivity.class));
            }
        });
        titleTv = (TextView) findViewById(R.id.title_text_tv);
        titleTv.setText("首 页");
// 初始化底部导航栏的控件
        firstImage = (ImageView) findViewById(R.id.first_image);
        secondImage = (ImageView) findViewById(R.id.second_image);
        thirdImage = (ImageView) findViewById(R.id.third_image);
        fourthImage = (ImageView) findViewById(R.id.fourth_image);
        firstText = (TextView) findViewById(R.id.first_text);
        secondText = (TextView) findViewById(R.id.second_text);
        thirdText = (TextView) findViewById(R.id.third_text);
        fourthText = (TextView) findViewById(R.id.fourth_text);
        firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
        secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
        thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
        fourthLayout = (RelativeLayout) findViewById(R.id.fourth_layout);
        firstLayout.setOnClickListener(this);
        secondLayout.setOnClickListener(this);
        thirdLayout.setOnClickListener(this);
        fourthLayout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_layout:
                setChioceItem(0);
                break;
            case R.id.second_layout:
                setChioceItem(1);
                break;
            case R.id.third_layout:
                setChioceItem(2);
                break;
            case R.id.fourth_layout:
                setChioceItem(3);
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
                if (fg1 == null) {
                    fg1 = new DFirstFragment();
                    fragmentTransaction.add(R.id.content, fg1);
                } else {
               // 如果不为空，则直接将它显示出来
                    fragmentTransaction.show(fg1);
                }
                titleTv.setText("首 页");
                break;
            case 1:
                secondImage.setImageResource(R.drawable.iconrealmonitoractive);
                secondText.setTextColor(dark);
                //secondLayout.setBackgroundColor(gray);
                if (fg2 == null) {
                    fg2 = new DSecondFragment();
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
                    fg3 = new DThirdFragment();
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
                    fg4 = new DFourFragment();
                    fragmentTransaction.add(R.id.content, fg4);
                } else {
                    fragmentTransaction.show(fg4);
                }
                titleLeft.setVisibility(View.INVISIBLE);
                titleTv.setText("关 于");
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
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

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
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    public boolean saveUsrInfo() {
        return prefer.edit().putString(SharedPreferConst.PREFER_ITEM_USRNAME, "")
                .putString(SharedPreferConst.PREFER_ITEM_PASSWORD,"")
                .putBoolean(SharedPreferConst.PREFER_ITEM_AUTOLOGIN,false)
                .commit();
    }
}