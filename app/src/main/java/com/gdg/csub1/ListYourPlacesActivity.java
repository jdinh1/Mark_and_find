package com.gdg.csub1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;

public class ListYourPlacesActivity extends ListActivity {
	int saveFlag=0;
	ListView listView;
	int position;
	private ArrayAdapter<String> mAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Called when the activity is first created.
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.listbuildings);
		String[] xp = new String[Csub1Activity.MAX_LIST];
		double[] xd = new double[Csub1Activity.MAX_LIST];
		//Load up the places and latlon arrays right here...
		Csub1Activity.nList = readPlacesFile(xp, xd);
		//
		//now reduce arrays to size n
		//
		//Toast.makeText(getBaseContext(), "returned n: " + n, Toast.LENGTH_SHORT).show();
		if (Csub1Activity.nList > 0) {
			Csub1Activity.places = new String[Csub1Activity.nList];
			Csub1Activity.dlatlon = new double[Csub1Activity.nList*2];
			for (int i=0; i<Csub1Activity.nList; i++) {
				Csub1Activity.places[i] = xp[i];
				Csub1Activity.dlatlon[i*2]   = xd[i*2];
				Csub1Activity.dlatlon[i*2+1] = xd[i*2+1];
			}
		}
		//
		mAdapter = new ArrayAdapter<String>(this, R.layout.main2, R.id.mainDisplay,Csub1Activity.places);
		setListAdapter(mAdapter);
		listView = getListView();
		listView.setTextFilterEnabled(true);
		//
		registerForContextMenu(listView);
		listItemClicked();
	}

	private void listItemClicked() {
		// TODO Auto-generated method stub
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			//@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				position=arg2;
				return false;
			}
		});
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, v.getId(), 0, "Edit Name");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Cancel");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle()=="Edit Name") { function3(item.getItemId()); return true;}
		if (item.getTitle()=="Delete") { function1(item.getItemId()); return true; }
		if (item.getTitle()=="Cancel") { function2(item.getItemId()); return true; }
		return false;
	}

	public void function3(int id) {
		String s = mAdapter.getItem(position);
		//Toast.makeText(this, "function 1 called:"+position, Toast.LENGTH_SHORT).show();
		//Toast.makeText(this, "Deleting: "+s, Toast.LENGTH_SHORT).show();
		//remove array element here...
		//rewrite the file, but skip the deleted element.
		if (rewriteFile(position,true)){} //finish();
		//finish();
	}

	public void function1(int id) {
		String s = mAdapter.getItem(position);
		//Toast.makeText(this, "function 1 called:"+position, Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "Deleting: "+s, Toast.LENGTH_SHORT).show();
		//remove array element here...
		//rewrite the file, but skip the deleted element.
		if (rewriteFile(position,false)) finish();
		finish();
	}
	public void function2(int id) {
		Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onListItemClick(ListView parent, View view, int position, long id) {
		//Single press by user
		//Jump to CompassActivity!
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), CompassActivity.class);
		Csub1Activity.currentSelection = position;
		startActivity(intent);
	}

	int readPlacesFile(String[] Places, double[] dlatlon) {
		int n=0;
		//read file from sd card.
		try {
			File myFile = new File(getExternalFilesDir(null), Csub1Activity.filename);
			if (!myFile.exists()) {
				Toast.makeText(getBaseContext(), "file not found", Toast.LENGTH_SHORT).show();
				return 0;
			}
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader( new InputStreamReader(fIn) );
			String aDataRow = "";
			//
			while(true) {
				aDataRow = myReader.readLine();
				if (aDataRow == null) break;
				Places[n] = aDataRow;
				aDataRow = myReader.readLine();
				dlatlon[n*2] = Double.parseDouble( aDataRow );
				aDataRow = myReader.readLine();
				dlatlon[n*2+1] = Double.parseDouble( aDataRow );
				n++;
				if (n >= Csub1Activity.MAX_LIST) break;
			}
			myReader.close();
			//Toast.makeText(getBaseContext(), "read " + nlines + " lines", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return n;
	}

	boolean rewriteFile(final int pos, boolean toEdit) {
		if (toEdit) { // rename location
			// create new dialog box
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			// create new edittext and set default text to original name
			final EditText input = new EditText(this);
			input.setText(Csub1Activity.places[pos]);
			// set up dialog box
			alertDialogBuilder.setView(input);
			alertDialogBuilder.setTitle("Enter Location Name");
			alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String newText = input.getText().toString();
					// if new name is empty, set back to old name
					if (newText.equals(null) || newText.equals("")) {
						newText = Csub1Activity.places[pos];
					}
					// replace with new name
					//String old = Csub1Activity.places[pos];
					Csub1Activity.places[pos] = Csub1Activity.places[pos].replace(Csub1Activity.places[pos], newText);
					rewriteFile();
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show dialog box
			alertDialog.show();
			return true;
		} else {  // delete location
			//first, remove element from global arrays...
			String[] xp = new String[Csub1Activity.MAX_LIST];
			double[] xd = new double[Csub1Activity.MAX_LIST];
			//
			int m = 0;
			//Copy records to temp arrays
			for (int i = 0; i < Csub1Activity.nList; i++) {
				if (i != pos && !toEdit) {
					xp[m] = Csub1Activity.places[i];
					xd[m * 2] = Csub1Activity.dlatlon[i * 2];
					xd[m * 2 + 1] = Csub1Activity.dlatlon[i * 2 + 1];
					m++;
				}
			}

			//Reduce size of array by one
			Csub1Activity.nList--;
			if (Csub1Activity.nList > 0) {
				//Copy temp to array
				Csub1Activity.places = new String[Csub1Activity.nList];
				Csub1Activity.dlatlon = new double[Csub1Activity.nList * 2];
				for (int i = 0; i < Csub1Activity.nList; i++) {
					Csub1Activity.places[i] = xp[i];
					Csub1Activity.dlatlon[i * 2] = xd[i * 2];
					Csub1Activity.dlatlon[i * 2 + 1] = xd[i * 2 + 1];
				}
			}
			// call rewrite
			if (rewriteFile())
				return true;
		}
		return false;
	}

	boolean rewriteFile() {
		//rewrite the file.
		FileOutputStream fop = null;
		File file;
		try {
			file = new File(getExternalFilesDir(null), Csub1Activity.filename);
			fop = new FileOutputStream(file, false); //true=append
			//if (!file.exists()) { file.createNewFile(); }
			//
			if (Csub1Activity.nList == 0) {
				file.delete();
				return true;
			}
			for (int i = 0; i < Csub1Activity.nList; i++) {
				String sp = Csub1Activity.places[i] + "\n";
				fop.write(sp.getBytes());
				sp = Double.toString(Csub1Activity.dlatlon[i * 2]) + "\n";
				fop.write(sp.getBytes());
				sp = Double.toString(Csub1Activity.dlatlon[i * 2 + 1]) + "\n";
				fop.write(sp.getBytes());
			}
			fop.flush();
			fop.close();
			Toast.makeText(getBaseContext(), "file saved", Toast.LENGTH_SHORT).show();
			return true;
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
