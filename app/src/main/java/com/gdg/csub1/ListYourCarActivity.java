package com.gdg.csub1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class ListYourCarActivity extends Activity {
	//Show 2 buttons.
	//Activate save button when GPS is ready.
	Button button1;
	Button button2;
	int locationReady=0;
	private LocationManager mlocManager; 
	private LocationListener mlocListener; 
	public double glat=0.0, glon=0.0, galtitude=0.0;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// Called when the activity is first created.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yourcarmenu);
        addListenerOnButton1();
        addListenerOnButton2();
		button1 = (Button) findViewById(R.id.button1);
		button1.setEnabled(false);
		mlocManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE); 
		mlocListener = new MyLocationListener(); 
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener); 
	}
    void addListenerOnButton1() {
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View arg0) {
				//Save car lat/lon to file.
				FileOutputStream fop = null;
				File file;
				try {
					file = new File(getExternalFilesDir(null), "carloc.txt");
					fop = new FileOutputStream(file,false); //true=append
					if (!file.exists()) { file.createNewFile(); }
					String sp;
					sp = Double.toString(glat)+"\n";
					fop.write( sp.getBytes() );
					sp = Double.toString(glon)+"\n";
					fop.write( sp.getBytes() );
			        fop.flush();
			        fop.close();
					Toast.makeText(getBaseContext(), "Location saved!!!", Toast.LENGTH_SHORT).show();
				}
				catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	void addListenerOnButton2() {
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View arg0) {
				//Read carloc.txt file from sd card.
				try {
					File myFile = new File(getExternalFilesDir(null), "carloc.txt");
					if (!myFile.exists()) {
						Toast.makeText(getBaseContext(), "Save your cars' location first!", Toast.LENGTH_SHORT).show();
						return;
					}
					FileInputStream fIn = new FileInputStream(myFile);
					BufferedReader myReader = new BufferedReader( new InputStreamReader(fIn) );
					Csub1Activity.places = new String[1];
					Csub1Activity.dlatlon = new double[2];
					Csub1Activity.places[0] = "Your Car";
					String aDataRow = "";
					aDataRow = myReader.readLine();
					Csub1Activity.dlatlon[0] = Double.parseDouble( aDataRow );
					aDataRow = myReader.readLine();
					Csub1Activity.dlatlon[1] = Double.parseDouble( aDataRow );
					myReader.close();
				}
				catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				Intent intent = new Intent(); 
				Csub1Activity.currentSelection = 0;
				intent.setClass(getApplicationContext(), CompassActivity.class);
				startActivity(intent); 
			}
		});
    }

	class MyLocationListener implements LocationListener { 
		//@Override
		public void onLocationChanged(Location loc) { 
			glat = loc.getLatitude();
			glon = loc.getLongitude();
			locationReady = 1;
			button1 = (Button) findViewById(R.id.button1);
			button1.setEnabled(true);
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
