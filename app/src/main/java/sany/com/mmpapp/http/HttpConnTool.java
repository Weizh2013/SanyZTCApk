package sany.com.mmpapp.http;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by sunj7 on 16-8-22.
 */
@SuppressWarnings("deprecation")
public class HttpConnTool {
private String baseUrl;
private HttpPost httpPost;
private DefaultHttpClient httpclient;
private HttpResponse httpResponse;
private final static String HTTPENCODER = HTTP.UTF_8;

        public HttpConnTool(String requestURL, DefaultHttpClient client) {

            this.baseUrl = requestURL;
            this.httpclient = client;
            this.httpPost = new HttpPost(baseUrl);
            if (httpclient == null) {
                httpPost.abort();
            }
        }

        /**
         * 提交HTTP请求
         *
         * @param params
         *            请求参数
         * @return JSON字符串形式
         * @throws HttpIOException
         * @throws IOException
         */
        public String executeRequest(List<NameValuePair> params)
                throws HttpIOException {
            String res_str = null;
            synchronized (httpclient) {
                if (httpclient != null && httpPost != null && !httpPost.isAborted()) {
                    try {
                        // 第一步设置httpPost请求参数及编码
                        httpPost.setEntity(new UrlEncodedFormEntity(params,
                                HTTPENCODER));
                        // 第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象
                        httpResponse = httpclient.execute(httpPost);
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    } catch (ClientProtocolException e) {
                        return null;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        throw new HttpIOException(e);
                    }

                    if (httpResponse != null
                            && (httpResponse.getStatusLine().getStatusCode() == 200)) {

                        // 第三步，使用getEntity方法活得返回结果
                        try {
                            res_str = EntityUtils.toString(
                                    httpResponse.getEntity(), HTTPENCODER);

                        } catch (ParseException e) {
                            Log.e("", "返回结果转换异常:ParseException");
                        } catch (IOException e) {
                            Log.e("", "返回结果输出异常:IOException");
                        }
                    }

                    List<Cookie> cookies = httpclient.getCookieStore().getCookies();
                    for (int i = 0; i < cookies.size(); i++) {

                        // 这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值

                        if ("JSESSIONID".equals(cookies.get(i).getName())) {

                            String JSESSIONID = cookies.get(i).getValue();

                            break;

                        }

                    }

                    httpPost.abort();
                    httpclient.getConnectionManager().closeExpiredConnections();
                }

                Log.d("", "返回结果" + res_str);

                return res_str;
            }

        }

}
