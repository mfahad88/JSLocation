package com.example.muhammadfahad.jslocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadfahad.jslocation.bean.DataBean;
import com.example.muhammadfahad.jslocation.bean.InfoBean;
import com.example.muhammadfahad.jslocation.dao.DBHelper;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener,View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int M_MAX_ENTRIES = 5 ;
    GeoDataClient mGeoDataClient;
    PlaceDetectionClient mPlaceDetectionClient;
    LocationManager locationManager;
    Button btn,btnStop;
    TextView tv;
    Geocoder geocoder;
    SimpleDateFormat simpleDateFormat;


    private String provider,radius;
    EditText edtRadius;
    RadioGroup locationGroup,locationCriteria;
    RadioButton radioLocation,radioCriteria;
    Criteria criteria;
    String dbFileLoc;
    DBHelper dbHelperLoc;
    List<DataBean> beanList;
    OkHttpClient client;
    int recId=0;
    List<Address> addresses = null;
    Request request;
    Response response;
    JSONObject object;
    JSONArray array;

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setBearingAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(true);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        provider=locationManager.getBestProvider(criteria,true);
    }


    @SuppressWarnings("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.logcat();
        beanList=new ArrayList<>();
        client=new OkHttpClient();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn= (Button) findViewById(R.id.button);
        edtRadius=(EditText) findViewById(R.id.editText);
        dbFileLoc= Environment.getExternalStorageDirectory()+ File.separator+"JSLocation_loc_DB.db";
        dbHelperLoc=new DBHelper(getApplicationContext(),dbFileLoc);
        //locationGroup=(RadioGroup)findViewById(R.id.locationGroup);
        //locationCriteria=(RadioGroup)findViewById(R.id.locationCriteria);
        btn.setOnClickListener(this);

        simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        geocoder = new Geocoder(this, Locale.getDefault());
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        tv=(TextView)findViewById(R.id.textView);
        btnStop=(Button)findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(this);
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);



        tv.setText("");
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_LOGS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*3*1,1000,this);
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            recId++;
            /*location.setLatitude(24.808443);
           location.setLongitude(67.03604);*/

            LocationAsync async =new LocationAsync();
            async.execute(location);

        }
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

    @SuppressWarnings("MissingPermission")
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){
            tv.setText("");
            if(!TextUtils.isEmpty(edtRadius.getText().toString().trim())) {
                radius=edtRadius.getText().toString();
                Toast.makeText(MainActivity.this, "Location fetched inserting data...", Toast.LENGTH_SHORT).show();
                locationManager.requestSingleUpdate(provider, this, null);
                // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,30000,0,this);
            }
        }
        if(v.getId()==R.id.buttonStop){
            Toast.makeText(MainActivity.this, "Stop location...", Toast.LENGTH_SHORT).show();
            locationManager.removeUpdates(this);
        }
    }

    public class LocationAsync extends AsyncTask<Location,Void,List<DataBean>>{
        List<Address> addresses = null;
        Request request;
        Response response;
        JSONObject object;
        JSONArray array;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected List<DataBean> doInBackground(Location... locations) {
            try{

                addresses = geocoder.getFromLocation(locations[0].getLatitude(), locations[0].getLongitude(), 1);
                request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+locations[0].getLatitude()+","+locations[0].getLongitude()+"&radius="+radius+"&key=AIzaSyAMly2uKnHT14gr3sYXOKSrytvw25SlcsA")
                        .build();

                response = client.newCall(request).execute();
                object = new JSONObject(response.body().string());
                array = object.getJSONArray("results");
                beanList.add(new DataBean(1,recId,"Accuracy",String.valueOf(locations[0].getAccuracy()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Altitude",String.valueOf(locations[0].getAltitude()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Bearing",String.valueOf(locations[0].getBearing()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"ElapsedRealtimeNanos",String.valueOf(locations[0].getElapsedRealtimeNanos()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Latitude",String.valueOf(locations[0].getLatitude()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Longitude",String.valueOf(locations[0].getLongitude()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Provider",String.valueOf(locations[0].getProvider()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Speed",String.valueOf(((locations[0].getSpeed()*3600)/1000)),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Time",simpleDateFormat.format(new Date(Long.parseLong(String.valueOf(locations[0].getTime())))),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Address",String.valueOf(addresses.get(0).getAddressLine(0)),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Known Name",String.valueOf(addresses.get(0).getFeatureName()),new Date().toString()));
                beanList.add(new DataBean(1,recId,"PlaceName",array.getJSONObject(1).getString("name"),new Date().toString()));
                beanList.add(new DataBean(1,recId,"Radius",radius,new Date().toString()));

            }catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHelperLoc.insertDetail(beanList);
            return beanList;
        }

        @Override
        protected void onPostExecute(List<DataBean> dataBeen) {
            if(dataBeen.size()>0){
                for(int i=0;i<dataBeen.size();i++){
                    tv.append(dataBeen.get(i).getAttribute()+" : "+dataBeen.get(i).getValue()+"\n");
                }

                Toast.makeText(MainActivity.this, "Record inserted...", Toast.LENGTH_SHORT).show();
                dataBeen.clear();
            }

        }
    }



}
