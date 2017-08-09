package com.gdg.csub1;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPlaceActivity extends Activity {
	private static final boolean DEBUG = false;
	Button button;
	EditText mEdit;
	int locationReady=0;
	private LocationManager mlocManager; 
	private LocationListener mlocListener; 
	public double glat=0.0, glon=0.0, galtitude=0.0;
	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addplace);
 		addListenerOnButton();
 		mEdit = (EditText)findViewById(R.id.editText1); 
		button = (Button) findViewById(R.id.button1);
		button.setEnabled(false);
		mlocManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE); 
		mlocListener = new MyLocationListener(); 
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener); 
		if (DEBUG) locationReady=1;
		if (DEBUG) glat = 35.4444;
		if (DEBUG) glon = -119.4444;
		if (DEBUG) button = (Button) findViewById(R.id.button1);
		if (DEBUG) button.setEnabled(true);
	}
	@Override 
	public void onPause() { 
		mlocManager.removeUpdates(mlocListener); 
		Toast.makeText( getApplicationContext(), "GPS updates stopped.", Toast.LENGTH_SHORT).show();
		super.onPause(); 
	} 
	
	public void addListenerOnButton() {
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View arg0) {
				getStorageState();
				if (DEBUG) if (!mExternalStorageAvailable) Toast.makeText(getBaseContext(), "External Storage NOT Available", Toast.LENGTH_SHORT).show();
				if (DEBUG) if (!mExternalStorageWriteable) Toast.makeText(getBaseContext(), "External Storage NOT Writeable", Toast.LENGTH_SHORT).show();
				if (DEBUG) if (mExternalStorageAvailable) Toast.makeText(getBaseContext(), "External Storage IS Available", Toast.LENGTH_SHORT).show();
				if (DEBUG) if (mExternalStorageWriteable) Toast.makeText(getBaseContext(), "External Storage IS Writeable", Toast.LENGTH_SHORT).show();
				savePlaceToFile();
				mlocManager.removeUpdates(mlocListener); 
				if (DEBUG) Toast.makeText( getApplicationContext(), "GPS updates stopped.", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	void getStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			//We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			//We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			//Something else is wrong. It may be one of many other states,
			//but all we need to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

	boolean savePlaceToFile() {
		//write on SD card file data in the text box
		FileOutputStream fop = null;
		File file;
		try {
			file = new File(getExternalFilesDir(null), Csub1Activity.filename);
			fop = new FileOutputStream(file,true); //true=append
			if (!file.exists()) { file.createNewFile(); }
	        //
	        String sp = mEdit.getText().toString() + "\n"; 
			fop.write(sp.getBytes());
			//now, the lat/lon data!!!
			sp = Double.toString(glat)+"\n";
			fop.write( sp.getBytes() );
			sp = Double.toString(glon)+"\n";
			fop.write( sp.getBytes() );
	        //
	        fop.flush();
	        fop.close();
			Toast.makeText(getBaseContext(), "Writing: "+file.getAbsolutePath()+file.getName(), Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	class MyLocationListener implements LocationListener { 
		//@Override
		public void onLocationChanged(Location loc) { 
			glat = loc.getLatitude();
			glon = loc.getLongitude();
			locationReady = 1;
			button = (Button) findViewById(R.id.button1);
			button.setEnabled(true);
		}
		//@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { }
		//@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}
		//@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}
	}
}
