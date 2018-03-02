/*
La calsse principale de l'application cette classe et la premiere page qui apparait lors de lancement de l'application
cette classe s'appelle WifiDemo.
ok je mets quelques modificqtion sur le projet




adasf
asd
as
fa
s
asas
 */



//Les packages et les imports des classes comme wifi manager, gps...
package com.djemi.wifidemo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
//import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WiFiDemo extends AppCompatActivity implements OnClickListener {

//initialisation des variables et objets utilisés dans cette classt
    DatabaseHelper mDatabaseHelper;
    LocationManager locationManager;
    double latitude;
    double longitude;
    double latitude2;
    double longitude2;
    String ssid3;
    int tmp = 0;
    WifiManager wifi;
    ListView lv;
    //TextView textStatus;
    Button buttonScan;
    Button favoris;
    TextView textv;
    int size = 0, size1 = 0;
    List<ScanResult> results;
    View view;
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    String[][] data;
    SimpleAdapter adapter;
    SimpleAdapter ada;
    //public List<List<String>> data= new ArrayList<List<String>>();
    int sizeGlobal;

    //cette methode est appellée quand l'application se crée
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_demo);
        //cree la base de données
        mDatabaseHelper = new DatabaseHelper(this);

        //textStatus = (TextView) findViewById(R.id.textStatus);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);
        favoris=(Button)findViewById(R.id.favori1);
        favoris.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent2;
                intent2 = new Intent(getApplicationContext(), Favoris.class);
                //intent2.putExtra("name", ssid3);
                startActivity(intent2);

            }
        });

        lv = (ListView) findViewById(R.id.list);

//initialisation de la localisation
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

// initialisation de wifi manager
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
//start le scan des réseaux disponibles
        wifi.startScan();


        this.adapter = new SimpleAdapter(this, arraylist, R.layout.row, new String[]{"text1", "text2"}, new int[]{R.id.SSID, R.id.sec});
        lv.setAdapter(this.adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // Object listitem=lv.getItemAtPosition(position);

                Intent intent;
                intent = new Intent(getApplicationContext(), oneWifi.class);
               // try {
                    //size = size1 - 1;
                int tempo=0;

                    Cursor data2=mDatabaseHelper.getData();
                while(data2.moveToNext()) {

                    //data2.moveToPosition(position);
                    //String ssid3 = data2.getString(position);
                    if(tempo==position) {
                        ssid3=data2.getString(1);
                        break;
                    }
                    tempo++;
                    // }

                    //} catch (Exception e) {
                    // }
                }
                //ada=new SimpleAdapter(getApplicationContext(), arraylist, R.layout.row, new String[]{"text1", "text2", "text3"}, new int[]{R.id.SSID, R.id.sec, R.id.puissance});
                //int pos = parent.getPositionForView(view);
                intent.putExtra("name", ssid3);
                //mDatabaseHelper.closeDB();
                startActivity(intent);
            }
        });
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                results = wifi.getScanResults();
                size = results.size();
                size1 = results.size();
                sizeGlobal = size;
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        this.getLocation();
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        latitude = location.getLatitude();
        longitude = location.getLongitude();


    }

// cette méthode est appellée quand on clique sur le bouton scan
    public void onClick(View view) {


        arraylist.clear();
        wifi.startScan();
        Random r;
        data = new String[size][2];
        //ArrayList<String> singleAddress = new ArrayList<String>();
        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try {
            size = size - 1;
            while (size >= 0) {

                r= new Random();
                latitude2= (float)(r.nextDouble()/10000)+latitude-0.00005;
                longitude2=(float)(r.nextDouble()/10000)+longitude-0.00005;


                data[size][0] = results.get(size).SSID;

                if (results.get(size).capabilities.contains("WEP")) data[size][1] = "Sécurisé";
                else if (results.get(size).capabilities.contains("WPA")) data[size][1] = "Sécurisé";
                else data[size][1] = "Non sécurisé";



                Cursor data1=mDatabaseHelper.getData();
                int ff=1;
                while(data1.moveToNext()){
                    if(data[size][0].equals(data1.getString(1))){ff = 0;}
                }
                if(ff==1) {
                    mDatabaseHelper.addData(data[size][0], data[size][1], latitude2, longitude2);
                }
                size--;
            }

        } catch (Exception e) {
        }


        try {
            //size = size1 - 1;
            Cursor data1=mDatabaseHelper.getData();
            int ff=1;
            while(data1.moveToNext()){


                HashMap<String, String> item = new HashMap<String, String>();

                item.put("text1", data1.getString(1));
                item.put("text2", data1.getString(2));
                arraylist.add(item);

                adapter.notifyDataSetChanged();

            }

        } catch (Exception e) {
        }



    }
// appellé quand on clique sur AFFICHER SUR LA CARTE
    public void afficherCarte(View view) {
        Intent intent = new Intent(this, AfficherCarte.class);
        //for (int i = 0; i < sizeGlobal; i++) {
        //    intent.putExtra(Integer.toString(i), data[i]);

        //}
        intent.putExtra("size", Integer.toString(sizeGlobal));
        startActivity(intent);
    }
    public void getLocation(){

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            tmp = 0;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L,500.0f, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    //mMap.clear();
                    // if (tmp == 0) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //     tmp++;
                    //  }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //if (tmp == 0) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //    tmp++;
                    //}

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }

    }
}
