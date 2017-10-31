package sany.com.mmpapp.dialog;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by sunj7 on 16-11-21.
 */
public class MyDatePickerDialog extends DatePickerDialog {
    public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
        // super.onStop();
    }
}
