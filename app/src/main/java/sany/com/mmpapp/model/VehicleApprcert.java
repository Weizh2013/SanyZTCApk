package sany.com.mmpapp.model;

/**
 * Created by sunj7 on 16-12-7.
 */
public class VehicleApprcert {
    private String acNum;
    private String evVehiNo;

    public VehicleApprcert( String evVehiNo,String acNum) {
        this.acNum = acNum;
        this.evVehiNo = evVehiNo;
    }

    public String getAcNum() {
        return acNum;
    }

    public void setAcNum(String acNum) {
        this.acNum = acNum;
    }

    public String getEvVehiNo() {
        return evVehiNo;
    }

    public void setEvVehiNo(String evVehiNo) {
        this.evVehiNo = evVehiNo;
    }
}
