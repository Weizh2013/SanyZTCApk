package sany.com.mmpapp.model;

/**
 * Created by sunj7 on 16-12-12.
 */
public class Vehicle {
    private String phoneNum;
    private String ev_vehiNo;
    private String vehEiName;
    private int isOnline;
    private String onlineStatusTime;
    private int count;

    public Vehicle(String ev_vehiNo, String str1,int count) {
        this.ev_vehiNo = ev_vehiNo;
        this.vehEiName=str1;
        this.count = count;
    }

    public Vehicle(String ev_vehiNo, String vehEiName, String phoneNum) {
        this.phoneNum = phoneNum;
        this.ev_vehiNo = ev_vehiNo;
        this.vehEiName = vehEiName;
    }

    public Vehicle(String phoneNum, String ev_vehiNo, String vehEiName, int isOnline, String onlineStatusTime) {
        this.phoneNum = phoneNum;
        this.ev_vehiNo = ev_vehiNo;
        this.vehEiName = vehEiName;
        this.isOnline = isOnline;
        this.onlineStatusTime = onlineStatusTime;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEv_vehiNo() {
        return ev_vehiNo;
    }

    public void setEv_vehiNo(String ev_vehiNo) {
        this.ev_vehiNo = ev_vehiNo;
    }

    public String getVehEiName() {
        return vehEiName;
    }

    public void setVehEiName(String vehEiName) {
        this.vehEiName = vehEiName;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getOnlineStatusTime() {
        return onlineStatusTime;
    }

    public void setOnlineStatusTime(String onlineStatusTime) {
        this.onlineStatusTime = onlineStatusTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
