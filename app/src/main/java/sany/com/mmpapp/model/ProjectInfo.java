package sany.com.mmpapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunj7 on 16-12-2.
 */
public class ProjectInfo {
    private String pi_id;
    private String piName;
    private String efName;
    private String workSiteId;
    private String workSiteName;
    private String starTime;
    private String endTime;
    private String shipStartTime;
    private String shipEndTime;
    private ArrayList<Route> routeList;
    private String piConsFiels;
    private String piTranUnitName;
    private String piConsUnitName;
    public ProjectInfo(String pi_id, String workSiteId, String workSiteName, String starTime, String endTime, String shipStartTime, String shipEndTime, ArrayList<Route> routeList) {
        this.pi_id = pi_id;
        this.workSiteId = workSiteId;
        this.workSiteName = workSiteName;
        this.starTime = starTime;
        this.endTime = endTime;
        this.shipStartTime = shipStartTime;
        this.shipEndTime = shipEndTime;
        this.routeList = routeList;
    }

    public ProjectInfo(String pi_id,String workSiteId,String piName, String efName,String piConsFiels, String piTranUnitName,String piConsUnitName,String starTime, String endTime, String shipStartTime, String shipEndTime) {
        this.pi_id=pi_id;
        this.workSiteId=workSiteId;
        this.piName = piName;
        this.efName = efName;
        this.piConsFiels=piConsFiels;
        this.piTranUnitName=piTranUnitName;
        this.piConsUnitName=piConsUnitName;
        this.starTime=starTime;
        this.endTime=endTime;
        this.shipStartTime=shipStartTime;
        this.shipEndTime=shipEndTime;
    }




    public String getPi_id() {
        return pi_id;
    }

    public void setPi_id(String pi_id) {
        this.pi_id = pi_id;
    }

    public String getPiName() {
        return piName;
    }

    public void setPiName(String piName) {
        this.piName = piName;
    }

    public String getEfName() {
        return efName;
    }

    public void setEfName(String efName) {
        this.efName = efName;
    }

    public String getWorkSiteId() {
        return workSiteId;
    }

    public void setWorkSiteId(String workSiteId) {
        this.workSiteId = workSiteId;
    }

    public String getWorkSiteName() {
        return workSiteName;
    }

    public void setWorkSiteName(String workSiteName) {
        this.workSiteName = workSiteName;
    }

    public String getStarTime() {
        return starTime;
    }

    public void setStarTime(String starTime) {
        this.starTime = starTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getShipStartTime() {
        return shipStartTime;
    }

    public void setShipStartTime(String shipStartTime) {
        this.shipStartTime = shipStartTime;
    }

    public String getShipEndTime() {
        return shipEndTime;
    }

    public void setShipEndTime(String shipEndTime) {
        this.shipEndTime = shipEndTime;
    }

    public ArrayList<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(ArrayList<Route> routeList) {
        this.routeList = routeList;
    }

    public String getPiTranUnitName() {
        return piTranUnitName;
    }

    public void setPiTranUnitName(String piTranUnitName) {
        this.piTranUnitName = piTranUnitName;
    }

    public String getPiConsFiels() {
        return piConsFiels;
    }

    public void setPiConsFiels(String piConsFiels) {
        this.piConsFiels = piConsFiels;
    }

    public String getPiConsUnitName() {
        return piConsUnitName;
    }

    public void setPiConsUnitName(String piConsUnitName) {
        this.piConsUnitName = piConsUnitName;
    }
}
