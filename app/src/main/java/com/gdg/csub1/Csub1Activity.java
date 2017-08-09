package com.gdg.csub1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Csub1Activity extends Activity implements OnClickListener {
	public static final String filename = "myplaces.txt";
	public static final int MAX_LIST = 64;
	public static String[] places;
	public static double[] dlatlon;
	public static int currentSelection=0;
	public static Context ctx;
	public static int nList = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        //Called when the activity is first created.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx = getApplicationContext();
	}

	public void onClick(View v) { }

	public void onClick1(View v) {
		//Toast.makeText(getApplicationContext(), "California State University, Bakersfield", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ListBuildingsActivity.class); 
		startActivity(intent); 
	}

	public void onClick2(View v) {
		Intent intent = new Intent(this, ListBuildingsActivity.class);
		startActivity(intent); 
	}

	public void onClick4(View v) {
		Intent intent = new Intent(this, ListYourMenuActivity.class); 
		startActivity(intent); 
	}

	public void onClick5(View v) {
		Toast.makeText(getApplicationContext(), "your car", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, ListYourCarActivity.class); 
		startActivity(intent); 
	}
}
