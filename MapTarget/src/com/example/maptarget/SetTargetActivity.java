package com.example.maptarget;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SetTargetActivity extends ActionBarActivity {

	public final static String EXTRA_NAME = "com.example.maptarget.name";
	public final static String EXTRA_AZIMUTH = "com.example.maptarget.azimuth";
	public final static String EXTRA_DISTANCE_IN_METERS = "com.example.maptarget_distance";
	public final static String EXTRA_TARGET_TYPE = "com.example.maptarget.target_type";
	private EditText mETName;
	private NumberPicker mNPAzimuth;
	private EditText mETDistance;
	private Spinner mSpinnerDistanceUnit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_target);

		this.mETName = (EditText) findViewById(R.id.et_name);
		this.mETName.requestFocus();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		this.mNPAzimuth = (NumberPicker) findViewById(R.id.np_azimuth);
		this.mNPAzimuth.setMinValue(0);
		this.mNPAzimuth.setMaxValue(359);
		this.mNPAzimuth.setOnLongPressUpdateInterval(10);

		this.mETDistance = (EditText) findViewById(R.id.et_distance);
		
		this.mSpinnerDistanceUnit = (Spinner) findViewById(R.id.spinner_distance_type);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.distance_unit_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		this.mSpinnerDistanceUnit.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_target, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void accept(View view) {

		// Validation
		if (this.mETName.getText().toString().trim().isEmpty()) {

			Toast.makeText(this, "Name field is mandatory!", Toast.LENGTH_LONG)
					.show();

			// Draw pencil
			Drawable pencil = getResources().getDrawable(R.drawable.pencil2);
			pencil.setBounds(0, 0, pencil.getIntrinsicWidth(),
					pencil.getIntrinsicHeight());
			this.mETName.setCompoundDrawables(null, null, pencil, null);

		} else if (this.mETDistance.getText().toString().isEmpty()) {

			Toast.makeText(this, "Distance field is mandatory!",
					Toast.LENGTH_LONG).show();

			// Draw pencil
			Drawable pencil = getResources().getDrawable(R.drawable.pencil2);
			pencil.setBounds(0, 0, pencil.getIntrinsicWidth(),
					pencil.getIntrinsicHeight());
			this.mETDistance.setCompoundDrawables(null, null, pencil, null);

		} else {

			Intent intent = new Intent(this, MainActivity.class);

			intent.putExtra(EXTRA_NAME, this.mETName.getText().toString());

			intent.putExtra(EXTRA_AZIMUTH, this.mNPAzimuth.getValue());

			int distanceInMeters = Integer
					.parseInt(((EditText) findViewById(R.id.et_distance))
							.getText().toString());
			if (this.mSpinnerDistanceUnit.getSelectedItem().toString()
					.equals(getString(R.string.text_kilometer))) {
				distanceInMeters *= 1000;
			}
			intent.putExtra(EXTRA_DISTANCE_IN_METERS, distanceInMeters);

			MainActivity.target_type tt_type = MainActivity.target_type.FRIEND;
			if (((RadioGroup) findViewById(R.id.rg_target_type))
					.getCheckedRadioButtonId() == R.id.rb_enemy) {
				tt_type = MainActivity.target_type.ENEMY;
			}
			intent.putExtra(EXTRA_TARGET_TYPE, tt_type);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}

	public void cancel(View view) {
		finish();
	}

	public void onDistanceFieldClick(View view) {
		this.mETDistance.setCompoundDrawables(null, null, null, null);
	}

	public void onNameFieldClick(View view) {
		this.mETName.setCompoundDrawables(null, null, null, null);
	}

}
