package sany.com.mmpapp.globalconst;

/**
 * Created by sunj7 on 16-11-7.
 */
public class APIInterface {
 /*
   public static String HOST="http://mmp.sany.com.cn:8010";
   public final static String DATAHOST="http://mmp.sany.com.cn:8010/mobile";*/

   public static String HOST="http://mmp.vkeyi.com/";
   public final static String DATAHOST="http://mmp.vkeyi.com/mobile";

    //

    //企业用启的所有接口

    public final static String mobiledologin="/mobiledologin.do";

    public final static String getInitStatInfoForEnte="/getInitStatInfoForEnte.do";
    public final static String listWorkedInfo="/listWorkedInfo.do";
    public final static String listAlarmRec="/listAlarmRec.do";
    public final static String getDevCurData="/getDevCurData.do";
    public final static String listVehi="/listVehi.do";
    public final static String getStatPageStatData="/getStatPageStatData.do";

    public final static String getVechileCount="/getVechileCount.do";
    public final static String getDayCountOfMonth="/getDayCountOfMonth.do";
    public final static String getMonthCountOfYear="/getMonthCountOfYear.do";
    public final static String getAlarmofTypeAndVechile="/getAlarmofTypeAndVechile.do";

    public final static String getProjectInfoByPITranUnit="/getProjectInfoByPITranUnit.do";
    public final static String geElecFenceByEfId="/geElecFenceByEfId.do";
    public final static String getApprCertOfBoss="/getApprCertOfBoss.do";
    public final static String getRouteByRmId="/getRouteByRmId.do";
    public final static String queryHistoryTrace="/queryHistoryTrace.do";

    //管理者的所有接口
    public final static String getInitStatInfo="/getInitStatInfo.do";
    public final static String listElecFence="/listElecFence.do";
    public final static String countAlarmRecByType="/countAlarmRecByType.do";
    public final static String listProj="/listProj.do";
    public final static String listProjVehi="/listProjVehi.do";
    public final static String getCfByPiId="/getCfByPiId.do";
    public final static String getWorkSiteByWs="/getWorkSiteByWs.do";
    public final static String projAgree="/projAgree.do";
    public final static String rejectProj="/rejectProj.do";
    //获取需要采集电子围栏的数据列表；
    public final static String getElecfenceListOfEftype="/getElecfenceListOfEftype.do";
    public final static String updateEfMapCoordinates="/updateEfMapCoordinates.do";
    public final static String listVehicleForRtm="/listVehicleForRtm.do";

    //员工接口
    public final static String getApprCert="/getApprCert.do";
    public final static String getGetStatPageStatDataofStaff="/getStatPageStatDataofStaff.do";
    public final static String getDayCountOfMonthByStaff="/getDayCountOfMonthByStaff.do";
    public final static String getMonthCountOfYearByStaff="/getMonthCountOfYearByStaff.do";
    public final static String getTripDetaiByfStaff="/getTripDetailByStaff.do";
    public final static String getVehicleInfoByStaffId="/getVehicleInfoByStaffId.do";
    //执法者接口
    public final static String getAlarmCountByStaffId="/getAlarmCountByStaffId.do";
    public final static String getAlarmRecDetail="/getAlarmRecDetail.do";
    public final static String updateAlarmrecAndAlarmdealrec="/updateAlarmrecAndAlarmdealrec.do";
}
