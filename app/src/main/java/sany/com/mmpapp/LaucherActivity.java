package sany.com.mmpapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LaucherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher);
        doWaiting();
    }
        Handler handler=new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                Intent nextActivity=new Intent(LaucherActivity.this,LoginActivity.class);
                startActivity(nextActivity);
                finish();
            }
        };


    private void doWaiting(){
        Thread waitThread=new Thread((new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(100);
                    handler.sendEmptyMessage(0x20);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }));
        waitThread.start();
    }
}
