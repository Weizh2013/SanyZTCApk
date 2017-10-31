package sany.com.mmpapp.dialog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
	private OnDateSetListener listener;


//	public DatePickerFragment() {
//		super();
//	}

	public DatePickerFragment(OnDateSetListener listener) {
		// TODO Auto-generated constructor stub
		this.listener = listener;
	}

	@Override
	public DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		// Create a new instance of DatePickerDialog and return it
		return new MyDatePickerDialog(getActivity(), listener, year, month, day);
	}



}
