package com.gdg.csub1;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ListBuildingsActivity extends ListActivity {
    boolean isSortedAZ = false;
    boolean isSortedDistance = false;
    ListView listView;
    EditText editText;
    Location myLocation, tempLoc;
    ArrayAdapter<String> adapter;
    ArrayList<String> lst;
    String[][] location;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Called when the activity is first created.
        super.onCreate(savedInstanceState);
        myLocation = new Location("");
        Csub1Activity.places = getResources().getStringArray(R.array.campus_buildings);
        location =  new String[Csub1Activity.places.length][4];
        String[] dll = getResources().getStringArray(R.array.campus_dlatlon);
        int len = dll.length;
        int size = Csub1Activity.places.length;
        Csub1Activity.dlatlon = new double[len];
        len = len / 2;
        //Toast.makeText(getBaseContext(), "Len: " + len, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getBaseContext(), "sd[0]: " + dll[0], Toast.LENGTH_SHORT).show();
        //Toast.makeText(getBaseContext(), "sd[1]: " + dll[1], Toast.LENGTH_SHORT).show();
        //move decimal point of all lat/lon...
        for (int i = 0; i < len; i++) {
            Csub1Activity.dlatlon[i * 2] = Double.valueOf(dll[i * 2]);
            Csub1Activity.dlatlon[i * 2 + 1] = Double.valueOf(dll[i * 2 + 1]);
        }

        for (int i = 0; i < Csub1Activity.places.length; i++) {
            location[i][0] = Csub1Activity.places[i];
            location[i][1] = Csub1Activity.dlatlon[i * 2]+"";
            location[i][2] = Csub1Activity.dlatlon[i * 2 + 1]+"";
        }

        initListView();
        displayBuildings();

        // search function on text change
        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    /*if (charSequence.toString().equals("")) {
                        displayBuildings();
                    } else {
                        searchList(charSequence.toString());
                        Toast.makeText(getBaseContext(), charSequence.toString() , Toast.LENGTH_SHORT).show();
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }*/
                }

                public void afterTextChanged(Editable editable) {
                }
            });
        }
    }

    public void searchList(String s) {
        for (String locationName:Csub1Activity.places) {
            if (!locationName.contains(s)) {
                lst.remove(locationName);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void onClickBackToTop(View view) {
        listView.smoothScrollToPosition(0);
    }

    public void onclickSortAZ(View view) {
        // Check button for A-Z sort
        if (isSortedDistance)
            isSortedDistance = false;
        if (!isSortedAZ)
            isSortedAZ = true;
        displayBuildings();
    }

    public void onclickSortDistance(View view) {
        // Check button for A-Z sort
        if (isSortedAZ)
            isSortedAZ = false;
        if (!isSortedDistance && getCurrentLocation())
            isSortedDistance = true;
        displayBuildings();
    }

    public boolean getCurrentLocation() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            myLocation.setLongitude(location.getLongitude());
            myLocation.setLatitude(location.getLatitude());
            myLocation.setAltitude(location.getAltitude());
            return true;
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "No GPS signal found." , Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void displayBuildings() {
        // sort by alphabetical
        if (isSortedAZ) {
            // create 2d array to store location name/longitude/latitude
            // sorting the 2d array using comparator
            Arrays.sort(location, new Comparator<String[]>() {
                public int compare(String[] s1, String[] s2) {
                    return s1[0].compareTo(s2[0]);
                }
            });
            // repopulate arrays with sorted data
            for (int i = 0; i < Csub1Activity.places.length; i++) {
                Csub1Activity.places[i] = location[i][0];
                Csub1Activity.dlatlon[i * 2] = Double.valueOf(location[i][1]);
                Csub1Activity.dlatlon[i * 2 + 1] = Double.valueOf(location[i][2]);
            }
        }
        // sort by distance
        if (isSortedDistance) {
            //getCurrentLocation();
            tempLoc = new Location("");
            // store location name/longitude/latitude/distance
            int size = Csub1Activity.places.length;
            for (int i = 0; i < size; i++) {
                tempLoc.setLongitude(Double.valueOf(location[i][2]));
                tempLoc.setLatitude(Double.valueOf(location[i][1]));
                location[i][3] = myLocation.distanceTo(tempLoc)+"";
            }

            // sorting the 2d array by distance using comparator
            Arrays.sort(location, new Comparator<String[]>() {
                public int compare(final String[] a, final String[] b) {
                    final Double dist1 = Double.valueOf(a[3]);
                    final Double dist2 = Double.valueOf(b[3]);
                    return dist1.compareTo(dist2);
                }
            });

            // repopulate arrays with sorted data
            String temp[] = new String[Csub1Activity.places.length];
            for (int i = 0; i < size; i++) {
                Csub1Activity.places[i] = location[i][0];
                Csub1Activity.dlatlon[i * 2] = Double.valueOf(location[i][1]);
                Csub1Activity.dlatlon[i * 2 + 1] = Double.valueOf(location[i][2]);
            }
        }

        // refresh adapter
        adapter.notifyDataSetChanged();
    }

    // setting up listview with header and footer buttons
    public void initListView() {
        if (adapter == null) {
            adapter = new ArrayAdapter<String>(this, R.layout.main2, R.id.mainDisplay, Csub1Activity.places);
            listView = getListView();
            View header = getLayoutInflater().inflate(R.layout.header, null);
            View footer = getLayoutInflater().inflate(R.layout.footer, null);
            listView.addHeaderView(header, null, false);
            listView.addFooterView(footer, null, false);
            setListAdapter(adapter);
            editText = (EditText) findViewById(R.id.textsearch);
            listView.setTextFilterEnabled(true);
        }
    }

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        //Jump to CompassActivity!
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CompassActivity.class);
        Csub1Activity.currentSelection = position-1;
        startActivity(intent);
    }
}
