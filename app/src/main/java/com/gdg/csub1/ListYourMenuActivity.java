package com.gdg.csub1;

import java.io.File;
//import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class ListYourMenuActivity extends Activity {
	Button button1;
	Button button2;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// Called when the activity is first created.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yourplacesmenu);
        addListenerOnButton1();
        addListenerOnButton2();
    }
    void addListenerOnButton1() {
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View arg0) {
				//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cs.csub.edu"));
				//startActivity(browserIntent);
				//
				//Check for file existence...
				File file = new File(getExternalFilesDir(null), Csub1Activity.filename);
				if (file.exists()) {
					Intent intent = new Intent(); 
					intent.setClass(getApplicationContext(), ListYourPlacesActivity.class);
					startActivity(intent); 
				}
			}
		});
	}
	void addListenerOnButton2() {
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			//@Override
			public void onClick(View arg0) {
				//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				//startActivity(browserIntent);
				if (Csub1Activity.nList >= Csub1Activity.MAX_LIST) {
					Toast.makeText(getBaseContext(), "Max items reached.", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(); 
				intent.setClass(getApplicationContext(), AddPlaceActivity.class);
				startActivity(intent); 
			}
		});
    }
}
