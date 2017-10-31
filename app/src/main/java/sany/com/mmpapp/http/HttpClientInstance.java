package sany.com.mmpapp.http;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

/**
 * Created by sunj7   on 16-8-22.
 * copy from vehicle
 * 单例模式HttpClient
 */
@SuppressWarnings("deprecation")
public class HttpClientInstance {
    private static DefaultHttpClient httpClient;
    private  final static int CONNTIMEOUT=5000;
    private  final static int SOCKETTIMEOUT=5000;

    private  static void initHttpClient(){
        httpClient=new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNTIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,SOCKETTIMEOUT);
    }

    public static DefaultHttpClient getInstance(){
        if (httpClient==null){
            initHttpClient();;
        }
        return httpClient;
    }




}
