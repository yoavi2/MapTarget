package com.example.maptarget;

import com.example.maptarget.MainActivity.target_type;
import com.google.android.gms.maps.model.LatLng;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddTargetOnLocationDialog extends DialogFragment {

	private EditText mETName;
	public static String TAG = "set_target_on_location_dialog";
	private LatLng mLocation;

	 public interface AddTargetOnLocationListener {
	        void addMarkerOnLocation(String name, target_type type, LatLng loc);
	    }
	
	public AddTargetOnLocationDialog() {
		// Empty constructor required for DialogFragment
	}

	public static AddTargetOnLocationDialog newInstance(String title, LatLng loc) {
		AddTargetOnLocationDialog frag = new AddTargetOnLocationDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putParcelable("location", loc);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_set_target_on_location,
				container);

		this.mETName = (EditText) view.findViewById(R.id.et_name);
		this.mETName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mETName.setCompoundDrawables(null, null, null, null);
			}
		});
		
		((Button) view.findViewById(R.id.btn_accept))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Validation
						if (mETName.getText().toString().trim().isEmpty()) {

							Toast.makeText(getActivity(),
									"Name field is mandatory!",
									Toast.LENGTH_LONG).show();

							// Draw pencil
							Drawable pencil = getResources().getDrawable(
									R.drawable.pencil2);
							pencil.setBounds(0, 0, pencil.getIntrinsicWidth(),
									pencil.getIntrinsicHeight());
							mETName.setCompoundDrawables(null, null, pencil,
									null);
						} else {
							MainActivity.target_type tt_type = MainActivity.target_type.FRIEND;
							if (((RadioGroup) getView().findViewById(R.id.rg_target_type))
									.getCheckedRadioButtonId() == R.id.rb_enemy) {
								tt_type = MainActivity.target_type.ENEMY;
							}
							
							 AddTargetOnLocationListener activity = (AddTargetOnLocationListener) getActivity();
					            activity.addMarkerOnLocation(mETName.getText().toString(), tt_type, mLocation);
					            		
							dismiss();
						}
					}
				});

		((Button) view.findViewById(R.id.btn_cancel))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						dismiss();
					}
				});

		String title = getArguments().getString("title", "Add Target");
		getDialog().setTitle(title);
		this.mLocation = getArguments().getParcelable("location");
		// Show soft keyboard automatically
		this.mETName.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}

}
