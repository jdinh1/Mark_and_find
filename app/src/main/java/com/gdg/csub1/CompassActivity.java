package com.gdg.csub1;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CompassActivity extends Activity {
	private final double degreesToRadians = 3.14159265 / 180.0;
	private final float metersToFeet = 3.28084f;
	private final int boxColor       = Color.rgb(255,55,55);
	private final int circleColor    = Color.rgb(200,5,5);
	private final int arrowColor     = Color.rgb(255,255,200);
	private final int latlonColor    = Color.rgb(255,255,255);
	private final int altColor       = Color.rgb(255,255,0);
	private final int directionColor = Color.rgb(120,120,120);
	private final int buildingColor  = Color.rgb(240,240,255);
	private final int distColor      = Color.rgb(255,255,20);
	//
	static int fcolor = 0;
	private CompassView1 compassView;
	//
	private static SensorManager sensorService;
	private Sensor sensor=null;
	private LocationManager mlocManager; 
	private LocationListener mlocListener; 
	public double glat=0.0, glon=0.0, galtitude=0.0;
	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Called when the activity is first created.
		super.onCreate(savedInstanceState);
		super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		compassView = new CompassView1(this);
		setContentView(compassView);
		//
		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if (sensor != null) {
			sensorService.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");
		} else {
			Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
			Toast.makeText(this, "ORIENTATION Sensor not found", Toast.LENGTH_LONG).show();
			finish();
		}
		mlocManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE); 
		mlocListener = new MyLocationListener(); 
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener); 
		//
		//
		//Bundle extras = getIntent().getExtras();
		//// Get data via the key
		//String value1 = extras.getString("x");
		//   fcolor = 0;
		//if (value1 != null) {
		//	// Do something with the data
		//    //Toast.makeText(getApplicationContext(), "passed in bundle: "+value1, Toast.LENGTH_SHORT).show();
		//    if (value1.equalsIgnoreCase("banana"))    fcolor = Color.rgb(200,200,60);
		//    if (value1.equalsIgnoreCase("blueberry")) fcolor = Color.rgb(30,10,120);
		//    if (value1.equalsIgnoreCase("kiwifruit")) fcolor = Color.rgb(30,230,20);
		//    if (value1.equalsIgnoreCase("mango"))     fcolor = Color.rgb(240,160,20);
		//} 
	}

	class CompassView1 extends View {
		private Paint paint;
		private float myAzimuth = 0.0f;
		private float lat=0.0f, lon=0.0f, alt=0.0f;
		public float bearing = 0.0f;
		public double distanceToTarget = 0.0;
		private float tverts[];
		private Path path;
		public CompassView1(Context context) {
			super(context);
			init();
		}
		private void init() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setTextSize(25);
			paint.setStyle(Paint.Style.STROKE);
			//paint.setColor(boxColor);
			tverts = new float[8];
			path = new Path();
		    path.setFillType(Path.FillType.WINDING);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int xCenter = getMeasuredWidth() / 2;
			int yCenter = getMeasuredHeight() / 4;
			float radius = (float) (Math.min(xCenter, yCenter) * 0.85);
			paint.setColor(boxColor);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
			//
			double angb = 180.0 - ( -(double)myAzimuth + (double)this.bearing );
			angb *= degreesToRadians;
			double ang = angb;
			float rad2 = radius*0.5f;
			//			
		    tverts[0] = (float)Math.sin(ang) * radius + xCenter;
		    tverts[1] = (float)Math.cos(ang) * radius + yCenter;
		    tverts[2] = (float)Math.sin(ang-0.35) * rad2 + xCenter;
		    tverts[3] = (float)Math.cos(ang-0.35) * rad2 + yCenter;
		    tverts[4] = (float)Math.sin(ang+0.35) * rad2 + xCenter;
		    tverts[5] = (float)Math.cos(ang+0.35) * rad2 + yCenter;
			float xto3 = (float)Math.sin(ang) * rad2 + xCenter;
			float yto3 = (float)Math.cos(ang) * rad2 + yCenter;
			//
			paint.setColor(circleColor);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawCircle(xCenter, yCenter, radius, paint);
			//
			paint.setColor(arrowColor);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			this.path.setFillType(Path.FillType.WINDING); 
			this.path.moveTo(tverts[0],tverts[1]);
			this.path.lineTo(tverts[2],tverts[3]);
			this.path.lineTo(tverts[4],tverts[5]);
			this.path.lineTo(tverts[0],tverts[1]);
			this.path.close();
		    canvas.drawPath(this.path, paint); 
			this.path.reset();
			//
			paint.setStrokeWidth(8);
			canvas.drawLine(xCenter, yCenter, xto3, yto3, paint);
			//-------------------------------------------------
			//-------------------------------------------------
			//Draw the compass as a narrow line (for reference)
			angb = 180.0 - ( -(double)myAzimuth );
			angb *= degreesToRadians;
			ang = angb;
			xto3 = (float)Math.sin(ang) * (radius-10.0f) + xCenter;
			yto3 = (float)Math.cos(ang) * (radius-10.0f) + yCenter;
			paint.setStrokeWidth(1);
			paint.setColor(Color.rgb(140,140,140));
			canvas.drawLine(xCenter, yCenter, xto3, yto3, paint);
			canvas.drawCircle(xto3, yto3, 10.0f, paint);
			//-------------------------------------------------
			//-------------------------------------------------
			paint.setColor(arrowColor);
			paint.setStrokeWidth(2);
			paint.setTextAlign(Paint.Align.CENTER);
			float m1 = (int)(myAzimuth * 10.0f) / 10.0f;
			paint.setTextSize(25);
			canvas.drawText(String.valueOf(m1), xCenter, yCenter+40.0f, paint);
			paint.setTextAlign(Paint.Align.CENTER);
			paint.setTextSize(35);
			paint.setColor(directionColor);
			canvas.drawText("Arrow points to", xCenter, yCenter+radius+40, paint);
			paint.setTextSize(60);
			int y = yCenter + (int)radius + 110;
			int yinc = 80;
			paint.setColor(buildingColor);
			canvas.drawText(Csub1Activity.places[Csub1Activity.currentSelection], xCenter, y, paint); y += yinc;
			paint.setColor(distColor);
			yinc = 64;
			paint.setTextSize(80);
			double dist = distanceToTarget * metersToFeet;
			if (dist > 5280.0) {
				//change distance to miles
				dist = dist / 5280.0;
				int idist = (int)(dist * 1000.0);
				double ddist = (double)idist / 1000.0;
				canvas.drawText(ddist+"", xCenter, y, paint); y+=yinc;
				paint.setTextSize(50);
				canvas.drawText("miles", xCenter, y, paint);
			}
			else {
				int idist = (int)(dist * 10.0);
				double ddist = (double)idist / 10.0;
				canvas.drawText(ddist+"", xCenter, y, paint); y+=yinc;
				paint.setTextSize(50);
				canvas.drawText("feet", xCenter, y, paint);
			}
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(latlonColor);
			paint.setTextSize(25);
			y = yCenter + (int)radius + 300;
			yinc = 26;
			canvas.drawText("lat: "+this.lat, 20, y, paint); y += yinc;
			canvas.drawText("lon: "+this.lon, 20, y, paint); y += yinc;
			paint.setColor(altColor);
			int malt = (int)( this.alt * metersToFeet );
			canvas.drawText("alt: "+malt, 20, y, paint); y += yinc;
			//canvas.drawText("azimuth: "+myAzimuth, 20, y, paint); y += yinc;
			canvas.drawText("bearing: "+bearing, 20, y, paint); y += yinc;
		}

		public void updateData(float myAzimuth) {
			float diff = this.myAzimuth - myAzimuth;
			if (Math.abs(diff) > 0.1) {
				this.myAzimuth = myAzimuth;
				invalidate(); //draw the canvas
			}
		}

		public void updateGpsData(double lat, double lon, double alt, double dist, float bearing) {
			this.lat = (float)lat;
			this.lon = (float)lon;
			this.alt = (float)alt;
			this.distanceToTarget = dist;
			this.bearing = bearing;
			invalidate(); //causes a canvas draw
		}
	}

	class MyLocationListener implements LocationListener { 
		//public final double ll[] = {
		//		35.349031,-119.103751
		//		//32.349,-119.16122
		//};

		//@Override 
		public void onLocationChanged(Location loc) { 
			//loc.getLatitude(); 
			//loc.getLongitude(); 
			//String Text = “My current location is: “ + 
			//“Latitud = “ + loc.getLatitude() + 
			//“Longitud = “ + loc.getLongitude(); 
			//Toast.makeText( getApplicationContext(), 
			//Text, 
			//Toast.LENGTH_SHORT).show(); 
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			double altitude=0.0;
			if (loc.hasAltitude()) altitude = loc.getAltitude();
			glat = lat;
			glon = lon;
			galtitude = altitude;
			//
			Location dloc = new Location(loc);
			int aspot = Csub1Activity.currentSelection * 2;
			double ssslat = Csub1Activity.dlatlon[aspot];
			double ssslon = Csub1Activity.dlatlon[aspot+1];
			dloc.setLatitude(ssslat);
			dloc.setLongitude(ssslon);
			double dist = loc.distanceTo(dloc);
			//
			float bearing = (float)getBearing(lat,lon,ssslat,ssslon);
			compassView.updateGpsData(lat,lon,altitude,dist,bearing);
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
	
	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		//@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }
		//@Override
		public void onSensorChanged(SensorEvent event) {
			// angle between the magnetic north direction
			// 0=North, 90=East, 180=South, 270=West
			float azimuth = event.values[0];
			if (glat != 0.0) {
				//http://stackoverflow.com/questions/5479753/using-orientation-sensor-to-point-towards-a-specific-location
				GeomagneticField geoField =
						new GeomagneticField((float)glat,
											 (float)glon,
											 (float)galtitude,
											 System.currentTimeMillis()); 
				 azimuth += geoField.getDeclination(); // converts magnetic north into true north 
			}
			compassView.updateData(azimuth);
		}
	};
	@Override
	protected void onDestroy() {
		if (sensor != null) { sensorService.unregisterListener(mySensorEventListener); }
		super.onDestroy();
	}
	@Override 
	public void onPause() { 
		if (sensor != null) {
			sensorService.unregisterListener(mySensorEventListener);
			Toast.makeText( getApplicationContext(), "Magnetic updates stopped", Toast.LENGTH_SHORT).show();
			sensor = null;
		}
		mlocManager.removeUpdates(mlocListener); 
		Toast.makeText( getApplicationContext(), "GPS updates stopped.", Toast.LENGTH_SHORT).show();
		super.onPause(); 
		//if (sensor == null) { Toast.makeText( getApplicationContext(), "Sensor is null", Toast.LENGTH_SHORT).show(); }
	} 
	@Override 
	public void onResume() { 
		//Toast.makeText( getApplicationContext(), "onResume()...", Toast.LENGTH_SHORT).show();
		//if (sensor == null) { Toast.makeText( getApplicationContext(), "Sensor is null", Toast.LENGTH_SHORT).show(); }
		//if (sensor == null) {
			sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			//Toast.makeText( getApplicationContext(), "Sensor activated.", Toast.LENGTH_SHORT).show();
			if (sensor != null) {
				sensorService.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
				//Toast.makeText( getApplicationContext(), "Magnetic updates started.", Toast.LENGTH_SHORT).show();
			}
			mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener); 
			//Toast.makeText( getApplicationContext(), "GPS updates started.", Toast.LENGTH_SHORT).show();
		//}
	    super.onResume(); 
	}

	static double getBearing(double dlat1, double dlon1, double dlat2, double dlon2) {
		//Returns the initial bearing in degrees East from North.
		//Bearing will point from one GPS-location on the Earth's Surface to another GPS-location.
		//references:
		//http://www.movable-type.co.uk/scripts/latlong.html
		//
		final double degreesToRadians = (1.0/180.0) * 3.1415926535;
		double lat1 =  dlat1 * degreesToRadians;
		double lon1 = -dlon1 * degreesToRadians;
		double lat2 =  dlat2 * degreesToRadians;
		double lon2 = -dlon2 * degreesToRadians;
		//
		double dLon = (-lon2) - (-lon1);
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
		double bearingRadians = Math.atan2(y, x);
		//
		double bearingDegrees = (bearingRadians / 3.14159265358979) * 180.0;
		bearingDegrees = (bearingDegrees + 360.0) % 360.0;
		return bearingDegrees;
	}
}
