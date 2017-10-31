package sany.com.mmpapp.Util;

/**
 * Created by sunj7 on 16-8-23.
 */
public class Byte2HexStr {
    private Byte2HexStr(){}

    public static String parse(byte[] b){
        return parse(b,false);
    }

    public static String parse(byte[] b,boolean isWithSpace){
        if(null==b||b.length<=0) return "";
        StringBuffer sb=new StringBuffer();
        String stmp="";
        for(int n=0;n<b.length;n++){
            stmp=(Integer.toHexString(b[n]&0XFF));
            if(stmp.length()==1) stmp="0"+stmp;
            if(isWithSpace) stmp=stmp+" ";
            sb.append(stmp);
        }
        String ret=sb.toString().toUpperCase();
        if(isWithSpace){
            return ret.substring(0,ret.length()-1);
        }else{
            return ret;
        }
    }
}
