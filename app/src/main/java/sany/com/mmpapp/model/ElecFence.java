package sany.com.mmpapp.model;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by sunj7 on 16-12-6.
 */
public class ElecFence {
    private String efName;
    private int efZoneType;
    private int efType;
    private int efId;
    private String efMapCoordinates;
    private String efAreaName;
    private String mapId;
    private String efNo;
    private String operationStr;





    public ElecFence() {
    }

    public ElecFence(String efName, String efAreaName, String mapId) {
        this.efName = efName;
        this.efAreaName = efAreaName;
        this.mapId = mapId;
    }

    public ElecFence(String efName, String operationStr) {
        this.efName = efName;
        this.operationStr = operationStr;
    }

    public String getEfAreaName() {
        return efAreaName;
    }

    public void setEfAreaName(String efAreaName) {
        this.efAreaName = efAreaName;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getEfName() {
        return efName;
    }

    public void setEfName(String efName) {
        this.efName = efName;
    }

    public int getEfZoneType() {
        return efZoneType;
    }

    public void setEfZoneType(int efZoneType) {
        this.efZoneType = efZoneType;
    }

    public int getEfType() {
        return efType;
    }

    public void setEfType(int efType) {
        this.efType = efType;
    }

    public int getEfId() {
        return efId;
    }

    public void setEfId(int efId) {
        this.efId = efId;
    }

    public String getEfMapCoordinates() {
        return efMapCoordinates;
    }

    public void setEfMapCoordinates(String efMapCoordinates) {
        this.efMapCoordinates = efMapCoordinates;
    }

    public String getEfNo() {
        return efNo;
    }

    public void setEfNo(String efNo) {
        this.efNo = efNo;
    }

    public String getOperationStr() {
        return operationStr;
    }

    public void setOperationStr(String operationStr) {
        this.operationStr = operationStr;
    }
}
