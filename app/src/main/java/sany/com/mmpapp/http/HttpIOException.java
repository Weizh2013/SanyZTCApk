package sany.com.mmpapp.http;

import java.io.IOException;

/**
 * Created by sunj7 on 16-8-22.
 */
public class HttpIOException extends Exception {
    private IOException err;
    private  static  final long serialVersionUID=1L;
    public HttpIOException(IOException e){
        this.err=e;
    }

    @Override
    public String getMessage(){
        if(err.getMessage()!=null){
            return "http请求错误"+err.getMessage();
        }else {
            return "http请求未知错误" ;
        }

    }
}
