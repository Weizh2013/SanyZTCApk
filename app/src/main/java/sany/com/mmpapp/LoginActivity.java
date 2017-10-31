package sany.com.mmpapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import sany.com.mmpapp.Util.MD5Util;
import sany.com.mmpapp.business.driver.DriverMainActivity;
import sany.com.mmpapp.business.enterprise.EnterpriseMainActivity;
import sany.com.mmpapp.business.government.GovernmentMainActivity;
import sany.com.mmpapp.globalconst.APIInterface;
import sany.com.mmpapp.globalconst.SharedPreferConst;
import sany.com.mmpapp.http.HttpClientInstance;
import sany.com.mmpapp.http.HttpConnTool;
import sany.com.mmpapp.http.HttpIOException;

@SuppressWarnings("deprecation")
public class LoginActivity extends Activity implements View.OnClickListener {
    private MmpApp mmpApp;
    private Button btn_Login;
    private EditText et_LoginName;
    private TextView tv_ErrTipUsr;
    private EditText et_Passwd;
    private TextView tv_ErrTipPass;
    private CheckBox cb_autologin;
    private boolean autologinflag;
    private JSONObject jsonRep;
    private  String usrName;
    private  String usrPass;
    private final static int SIG_GOOD_HTTP = 0x10;
    private final static int SIG_BAD_HTTP = 0x20;
    private final static int SIG_BAD_JSON = 0x30;
    protected static final int ILL_URL = 0x40;
    protected static final int HOST_TIME_OUT = 0x41;
    protected static final int OK_HOST = 0x42;
    private ProgressBar progBar_Log = null;
    private SharedPreferences prefer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mmpApp=(MmpApp)getApplication();
        prefer=this.getSharedPreferences("SP",MODE_PRIVATE);
        btn_Login=(Button)findViewById(R.id.loginBtn);
        et_LoginName=(EditText)findViewById(R.id.LoginName);
        tv_ErrTipUsr=(TextView)findViewById(R.id.usernameerrorid);
        et_Passwd=(EditText)findViewById(R.id.Password);
        tv_ErrTipPass=(TextView)findViewById(R.id.passworderrorid);
        cb_autologin=(CheckBox)findViewById(R.id.autologinCheckBox);
        progBar_Log=(ProgressBar)findViewById(R.id.probar_log);
        btn_Login.setOnClickListener(this);
        showSavedUsrName();
    }

    Handler loginHandler=new Handler(){

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            progBar_Log.setVisibility(View.GONE);
            switch (msg.what){
                case SIG_GOOD_HTTP:
                    try{
                        trySaveUserInfo();
                    }catch (JSONException e){
                        Toast.makeText(LoginActivity.this,R.string.errorlogin_tip,Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SIG_BAD_HTTP:
                    Toast.makeText(LoginActivity.this, R.string.errorlogin_timeout,
                            Toast.LENGTH_SHORT).show();
                    break;
                case SIG_BAD_JSON:
                    Toast.makeText(LoginActivity.this, R.string.errorlogin_tip,
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                if(isCorrectInput()){
                    String url = APIInterface.HOST + APIInterface.mobiledologin;
                    LoginThread loginThread = new LoginThread(usrName, usrPass, url);
                    progBar_Log.setVisibility(View.VISIBLE);
                    new Thread(loginThread).start();
                }
        }
    }

    private void showSavedUsrName(){
            String userName = prefer.getString(
                    SharedPreferConst.PREFER_ITEM_USRNAME, null);
            String password=prefer.getString(SharedPreferConst.PREFER_ITEM_PASSWORD,null);
            boolean autologin=prefer.getBoolean(SharedPreferConst.PREFER_ITEM_AUTOLOGIN,false);
            if(autologin){
                cb_autologin.setChecked(autologin);
            }else{
                cb_autologin.setChecked(autologin);
            }
            if (userName != null)
                et_LoginName.setText(userName);
            if(password!=null){
                et_Passwd.setText(password);
            }
            if(autologin){
                if(isCorrectInput()){
                    String url = APIInterface.HOST + APIInterface.mobiledologin;
                    LoginThread loginThread = new LoginThread(usrName, usrPass, url);
                    progBar_Log.setVisibility(View.VISIBLE);
                    new Thread(loginThread).start();
                }
            }else {
                et_Passwd.setText("");
            }
    }

    // 判断是否为正确的输入
    private boolean isCorrectInput() {
        boolean result = true;
        usrName = et_LoginName.getEditableText().toString().trim();
        if (usrName == null || usrName.isEmpty()) {
            result = false;
            tv_ErrTipUsr.setVisibility(View.VISIBLE);
            tv_ErrTipUsr.setText(R.string.inputlogin_nouser);
        } else {
            tv_ErrTipUsr.setVisibility(View.INVISIBLE);
        }
        usrPass = et_Passwd.getEditableText().toString().trim();
        if (usrPass == null || usrPass.isEmpty()) {
            result = false;
            tv_ErrTipPass.setVisibility(View.VISIBLE);
            tv_ErrTipPass.setText(R.string.inputlogin_nopasswd);
        } else {
            tv_ErrTipPass.setVisibility(View.INVISIBLE);
        }
        return result;
    }

    // 保存用户资料
    private void trySaveUserInfo() throws JSONException {
        boolean loginSuccess = false;
        String tempdriver;
        String tempStartTime;
        String tempEndTime;
        String tempType;
        String tempId;
        String tempLat;
        String tempLng;
        loginSuccess = jsonRep.getBoolean("state");
        if (loginSuccess) {
            //取全局数据
            JSONObject jsonData = jsonRep.getJSONObject("data");
            tempdriver=jsonData.getString("driver");
            tempStartTime=jsonData.getString("transportStartTime");
            tempEndTime=jsonData.getString("transportEndTime");
            tempType=jsonData.getString("staffType");
            tempId=jsonData.getString("staffId");
            tempLat=jsonData.getString("initLat");
            tempLng=jsonData.getString("initLng");
            if(tempStartTime!=null||tempStartTime!=""){
                mmpApp.setTransportStartTime(tempStartTime);
            }
            if(tempEndTime!=null||tempEndTime!=""){
                mmpApp.setTransportEndTime(tempEndTime);
            }
            if(tempLat!=null||tempLat!=""){
                mmpApp.setInitLat(Double.parseDouble(tempLat));
            }
            if(tempLng!=null||tempLng!=""){
                mmpApp.setInitLng(Double.parseDouble(tempLng));
            }
            if(tempType!=null||tempType!=""){
                mmpApp.setStaffType(Integer.parseInt(tempType));
            }else {
                Toast.makeText(LoginActivity.this, R.string.ill_stafftype,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(tempId!=null||tempId!=""){
                mmpApp.setStaffId(Integer.parseInt(tempId));
            }
            if(tempdriver!=null||tempdriver!=""){
                mmpApp.setDriver(Integer.parseInt(tempdriver));
            }
            mmpApp.setUserCode(usrName);
            if ("0".equals(tempType)){
                saveUsrInfo(usrName);
                Intent nextActivity = new Intent(LoginActivity.this,
                        GovernmentMainActivity.class);
                startActivity(nextActivity);
                finish();
            }else if(("1".equals(tempType))&&("0".equals( tempdriver))) {
                saveUsrInfo(usrName);
                Intent nextActivity = new Intent(LoginActivity.this,
                        EnterpriseMainActivity.class);
                startActivity(nextActivity);
                finish();
            }else if("1".equals(tempdriver)){
                saveUsrInfo(usrName);
                Intent nextActivity = new Intent(LoginActivity.this,
                        DriverMainActivity.class);
                startActivity(nextActivity);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, R.string.ill_stafftype,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, R.string.errorlogin_tip,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveUsrInfo(String userName) {
        if(cb_autologin.isChecked()) {
            return prefer.edit().putString(SharedPreferConst.PREFER_ITEM_USRNAME, userName)
                    .putString(SharedPreferConst.PREFER_ITEM_PASSWORD, usrPass)
                    .putBoolean(SharedPreferConst.PREFER_ITEM_AUTOLOGIN, true)
                    .commit();
        }else{
            return prefer.edit().putString(SharedPreferConst.PREFER_ITEM_USRNAME, userName)
                    .putString(SharedPreferConst.PREFER_ITEM_PASSWORD, usrPass)
                    .putBoolean(SharedPreferConst.PREFER_ITEM_AUTOLOGIN, false)
                    .commit();
        }
    }

    class LoginThread implements Runnable {
        private String name;
        private String passWd;
        private String fullUrl;
        /**
         *
         * @param name
         *            用户名
         * @param passwd
         *            密码
         * @param url
         *            地址
         */
        public LoginThread(String name, String passwd, String url) {
            // TODO Auto-generated constructor stub
            this.name = name;
            this.passWd = passwd;
            this.fullUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            DefaultHttpClient client = HttpClientInstance.getInstance();
            HttpConnTool tool = new HttpConnTool(fullUrl, client);
            List<NameValuePair> paraLists = new ArrayList<NameValuePair>();
            BasicNameValuePair param_Name = new BasicNameValuePair(
                    "user_code", name);
            paraLists.add(param_Name);
            BasicNameValuePair param_Passwd = new BasicNameValuePair(
                    "password", MD5Util.crypt(passWd));
            paraLists.add(param_Passwd);
            try {
                String strRep = tool.executeRequest(paraLists);
                jsonRep = new JSONObject(strRep);
                loginHandler.sendEmptyMessage(SIG_GOOD_HTTP);
            } catch (HttpIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                loginHandler.sendEmptyMessage(SIG_BAD_HTTP);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                loginHandler.sendEmptyMessage(SIG_BAD_JSON);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                loginHandler.sendEmptyMessage(SIG_BAD_HTTP);
            }
        }
    }
}
