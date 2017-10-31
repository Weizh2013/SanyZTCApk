package sany.com.mmpapp.Util;

import java.security.MessageDigest;

/**
 * Created by sunj7 on 16-8-23.
 */
public class MD5Util {
    private MD5Util(){}
    /*
    * 计算字符串的MD5码
    * @param origin 需要计算MD5码的字符串
    * @return  计算出的MD5码
    * */
    public static String crypt(String origin){
        String returnString=null;
        try{
            returnString=new String(origin);
            MessageDigest md=MessageDigest.getInstance("MD5");
            returnString=Byte2HexStr.parse(md.digest(returnString.getBytes())).toUpperCase();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return returnString;
    }


}
