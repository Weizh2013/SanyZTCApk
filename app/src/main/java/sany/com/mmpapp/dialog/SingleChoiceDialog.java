package sany.com.mmpapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class SingleChoiceDialog extends DialogFragment {
	private String title;
	private String[] items;

	public interface DialogListener {
		public void onSingleChoiceClick(DialogFragment dialog, int which);
	}

	DialogListener listener;

	public SingleChoiceDialog(String title, String[] choiceItems) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.items = choiceItems;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			listener = (DialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implemens DialogListener");

		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title).setItems(items, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				listener.onSingleChoiceClick(SingleChoiceDialog.this, which);
			}

		});
		return builder.create();
	}

}
