package com.example.maptarget;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GoogleMap mMap;
	private LocationManager mLocationService;
	private ArrayList<Marker> mMarkers;
	private boolean mLocationSet = false;
	private Dialog mGpsDialog;
	private final static int SET_TARGET = 3007;

	static public enum target_type {
		FRIEND, ENEMY
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mLocationService = (LocationManager) getSystemService(LOCATION_SERVICE);

		//  Build gps alert
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Location Services Not Active");
		builder.setMessage("Please enable Location Services and GPS");
		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface,
							int i) {
						// Show location settings when the user acknowledges
						// the alert dialog
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton("Exit",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface,
							int i) {
						// Exit Application
						finish();
					}
				});
		this.mGpsDialog = builder.create();
		this.mGpsDialog.setCanceledOnTouchOutside(false);
		
		// Check if enabled and if not send user to the GPS settings
		if (!this.mLocationService
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			this.mGpsDialog.show();
		}
		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services
													// are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else { // Google Play Services are available

			this.mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

			this.mMarkers = new ArrayList<Marker>();

			this.mLocationService.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new android.location.LocationListener() {
				@Override
				public void onProviderEnabled(String provider) {
					if (mGpsDialog.isShowing()) {
						mGpsDialog.dismiss();
					}
				}
				@Override
				public void onProviderDisabled(String provider) {
					if (!mLocationSet) {
						mGpsDialog.show();
					}
				}
				@Override
				public void onLocationChanged(Location location) {
					if (!mLocationSet) {

						adjustMap();
						mLocationSet = true;
						invalidateOptionsMenu();
					}
				}
				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}
			});

			GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location location) {

//					if (!mLocationSet) {
//
//						adjustMap();
//						mLocationSet = true;
//						invalidateOptionsMenu();
//					}
				}
			};
			this.mMap.setOnMyLocationChangeListener(locationChangeListener);

			// Enabling MyLocation Layer of Google Map
			this.mMap.setMyLocationEnabled(true);

		}
	}

	@Override
	protected void onRestart() { 

		// Check if location set and if enabled and if not send user to the GPS
		// settings
		if (this.mLocationSet) {
			this.adjustMap();
		} else if (!this.mLocationService
				.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !this.mGpsDialog.isShowing()) {

			this.mGpsDialog.show();
		}

		super.onRestart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.getItem(0).setEnabled(this.mLocationSet);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_set_target:
			this.showSetTarget();
			return true;
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void showSetTarget() {
		Intent intent = new Intent(this, SetTargetActivity.class);
		startActivityForResult(intent, SET_TARGET);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case (SET_TARGET):
				if (!this.mLocationSet) {
					Toast.makeText(this, "Location is not yet set by GPS",
							Toast.LENGTH_LONG).show();
				} else {

					Bundle bundle = intent.getExtras();
					String name = bundle
							.getString(SetTargetActivity.EXTRA_NAME);
					int azimuth = bundle
							.getInt(SetTargetActivity.EXTRA_AZIMUTH);
					int distance = bundle
							.getInt(SetTargetActivity.EXTRA_DISTANCE_IN_METERS);
					target_type type = (target_type) bundle
							.getSerializable(SetTargetActivity.EXTRA_TARGET_TYPE);

//					Location currloc = this.mLocationService // TODO: FIX BUG ` IF GPS DISABLED
//							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					Location currloc = this.mMap.getMyLocation();

					com.javadocmd.simplelatlng.LatLng currPoint = new com.javadocmd.simplelatlng.LatLng(
							currloc.getLatitude(), currloc.getLongitude());

					com.javadocmd.simplelatlng.LatLng destPointLib = LatLngTool
							.travel(currPoint, azimuth, distance,
									LengthUnit.METER);

					LatLng destPointGoogle = new LatLng(
							destPointLib.getLatitude(),
							destPointLib.getLongitude());

					Marker destMark = this.mMap.addMarker(new MarkerOptions()
							.position(destPointGoogle).title(name)
							.snippet(type.toString()));

					if (type == MainActivity.target_type.FRIEND) {
						destMark.setIcon(BitmapDescriptorFactory
								.fromResource(R.drawable.friend));
					} else {
						destMark.setIcon(BitmapDescriptorFactory
								.fromResource(R.drawable.enemy));
					}

					this.mMarkers.add(destMark);

					this.adjustMap();

					break;
				}
			}
		}
	}

	private void adjustMap() {

		Location location = this.mLocationService
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (location == null) {
			location = this.mMap.getMyLocation();
		}
		
		LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));

		if (this.mMarkers.size() > 0) {

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(loc);

			for (Marker marker : this.mMarkers) {
				builder.include(marker.getPosition());
			}

			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
					builder.build(),
					getResources().getDisplayMetrics().widthPixels,
					getResources().getDisplayMetrics().heightPixels,
					getResources().getDisplayMetrics().heightPixels / 6);
			this.mMap.animateCamera(cu);

		}
	}

}