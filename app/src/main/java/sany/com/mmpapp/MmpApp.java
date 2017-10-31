package sany.com.mmpapp;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by sunj7 on 16-12-1.
 */
public class MmpApp extends Application {

    private static final String TAG = "JPush";
    private String userCode;
    private int staffType;
    private int staffId;
    private int driver;
    private String transportStartTime;
    private String  transportEndTime;
    private double initLat;
    private double initLng;
    private JSONArray jsonArray;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getStaffType() {
        return staffType;
    }

    public void setStaffType(int staffType) {
        this.staffType = staffType;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getDriver() {
        return driver;
    }

    public void setDriver(int driver) {
        this.driver = driver;
    }

    public String getTransportStartTime() {
        return transportStartTime;
    }

    public void setTransportStartTime(String transportStartTime) {
        this.transportStartTime = transportStartTime;
    }

    public String getTransportEndTime() {
        return transportEndTime;
    }

    public void setTransportEndTime(String transportEndTime) {
        this.transportEndTime = transportEndTime;
    }

    public double getInitLat() {
        return initLat;
    }

    public void setInitLat(double initLat) {
        this.initLat = initLat;
    }

    public double getInitLng() {
        return initLng;
    }

    public void setInitLng(double initLng) {
        this.initLng = initLng;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }
}
