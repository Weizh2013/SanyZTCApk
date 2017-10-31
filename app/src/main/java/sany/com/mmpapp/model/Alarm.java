package sany.com.mmpapp.model;

/**
 * Created by sunj7 on 16-12-12.
 */
public class Alarm  {
    private String eiName;
    private String label;
    private int value;

    private String evVehiNo;
    private String sfName;
    private String paraNameShow;
    private String startTime;


    private int id;
    private String dealer;
    private String phoneNum;
    private String statusName;
    private String dealTime;
    private String content;

    public Alarm(int id, String phoneNum, String evVehiNo, String sfName, String eiName, String startTime, String paraNameShow, String dealer, String dealTime, String statusName,String content) {
        this.id = id;
        this.phoneNum = phoneNum;
        this.evVehiNo = evVehiNo;
        this.sfName = sfName;
        this.eiName = eiName;
        this.startTime = startTime;
        this.paraNameShow = paraNameShow;
        this.dealer = dealer;
        this.dealTime = dealTime;
        this.statusName = statusName;
        this.content=content;
    }

    public Alarm(String eiName, String label, int value) {
        this.eiName = eiName;
        this.label = label;
        this.value = value;
    }

    public Alarm(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public Alarm(String evVehiNo, String sfName, String paraNameShow, String startTime) {
        this.evVehiNo = evVehiNo;
        this.sfName = sfName;
        this.paraNameShow = paraNameShow;
        this.startTime = startTime;
    }

    public String getEiName() {
        return eiName;
    }

    public void setEiName(String eiName) {
        this.eiName = eiName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getEvVehiNo() {
        return evVehiNo;
    }

    public void setEvVehiNo(String evVehiNo) {
        this.evVehiNo = evVehiNo;
    }

    public String getParaNameShow() {
        return paraNameShow;
    }

    public void setParaNameShow(String paraNameShow) {
        this.paraNameShow = paraNameShow;
    }

    public String getSfName() {
        return sfName;
    }

    public void setSfName(String sfName) {
        this.sfName = sfName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
