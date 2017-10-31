package sany.com.mmpapp.model;

/**
 * Created by sunj7 on 17-1-11.
 */
public class TransportTimes {
    private String workSiteName;
    private String consFieldName;
    private String gpsTime;


    public TransportTimes(String workSiteName, String consFieldName, String gpsTime) {
        this.workSiteName = workSiteName;
        this.consFieldName = consFieldName;
        this.gpsTime = gpsTime;
    }

    public String getWorkSiteName() {
        return workSiteName;
    }

    public void setWorkSiteName(String workSiteName) {
        this.workSiteName = workSiteName;
    }

    public String getConsFieldName() {
        return consFieldName;
    }

    public void setConsFieldName(String consFieldName) {
        this.consFieldName = consFieldName;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }
}
