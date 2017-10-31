package sany.com.mmpapp.model;

/**
 * Created by sunj7 on 16-12-2.
 */
public class Route {
        private String  routeDest;
        private String  pceiIdName;
        private String  pceiId;
        private String  routeId;

    public Route(String routeDest, String pceiIdName, String pceiId, String routeId) {
        this.routeDest = routeDest;
        this.pceiIdName = pceiIdName;
        this.pceiId = pceiId;
        this.routeId = routeId;
    }

    public String getPceiIdName() {
        return pceiIdName;
    }

    public void setPceiIdName(String pceiIdName) {
        this.pceiIdName = pceiIdName;
    }

    public String getPceiId() {
        return pceiId;
    }

    public void setPceiId(String pceiId) {
        this.pceiId = pceiId;
    }

    public String getRouteDest() {
        return routeDest;
    }

    public void setRouteDest(String routeDest) {
        this.routeDest = routeDest;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
