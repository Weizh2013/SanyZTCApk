package sany.com.mmpapp.dialog;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Created by sunj7 on 16-11-21.
 */
public class MyTimePickerDialog  extends TimePickerDialog {
    public MyTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }



    @Override
    protected void onStop() {
       // super.onStop();
    }
}
