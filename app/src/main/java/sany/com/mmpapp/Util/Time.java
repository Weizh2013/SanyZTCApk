package sany.com.mmpapp.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sunj7 on 17-1-16.
 */

public class Time {
    static String[] startEndTime={"",""};

    public static String[] startAndEndTime(String startTime,String endTime){
        //请求统计数据的时间处理
        long time = System.currentTimeMillis();//获取当天的系统时间
        Date curDate = new Date(time);         //转换成日期
        Date preDate = new Date(time - 24 * 60 * 60 * 1000);//获取前一天的时间毫秒数
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strcurDate = format.format(curDate);//格式化时间
        String strpreDate = format.format(preDate);
        String temppreDateStartTime = strpreDate.substring(0, 10) + " " + startTime + ":00";//
        String temppreDateEndTime = strpreDate.substring(0, 10) + " " + endTime + ":00";
        //如果结束时间大于开始时间，
        try {
            if (compDate(format.parse(temppreDateStartTime), format.parse(temppreDateEndTime)) == 1) {//取当天时间
                startEndTime[0] = strcurDate.substring(0, 10) + " " + startTime + ":00";
                startEndTime[1] = strcurDate.substring(0, 10) + " " + endTime + ":00";
            } else {//开始时间取前一天，结束时间取当天
                startEndTime[0] = strpreDate.substring(0, 10) + " " + startTime + ":00";
                startEndTime[1] = strcurDate.substring(0, 10) + " " + endTime + ":00";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startEndTime;
    }
    public static int compDate(Date date1, Date date2) {
        int re = 0;
        long timeBetweenLong = date2.getTime() - date1.getTime();
        if (timeBetweenLong >= 0) {
            re = 1;
        } else if (timeBetweenLong < 0) {
            re = -1;
        }
        return re;
    }
}
