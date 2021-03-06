package com.example.muhammadfahad.jslocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadfahad.jslocation.bean.DataBean;
import com.example.muhammadfahad.jslocation.bean.InfoBean;
import com.example.muhammadfahad.jslocation.dao.DBHelper;
import com.example.muhammadfahad.jslocation.receiver.NetworkChangeReceiver;
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
    LocationManager locationManager;
    Button btn,btnStop;
    TextView tv;
    Geocoder geocoder;
    SimpleDateFormat simpleDateFormat;
    private BroadcastReceiver mNetworkReceiver;
    private String provider,radius;
    EditText edtRadius;
    Criteria criteria;
    String dbFileLoc;
    DBHelper dbHelperLoc;
    int recId=0;
    TelephonyManager tm;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private InfoBean infoBean;
    private EditText edtMob,edtCnic,edtChannel,edtIncome;
    private ProgressDialog progressDialog;
    @SuppressWarnings("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        setLockScreenOrientation(false);
        unregisterReceiver(mNetworkReceiver);
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
        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        provider=locationManager.getBestProvider(criteria,true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public static void dialog(boolean value, final Context context){

        if(value){
            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(context,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            };
            handler.postDelayed(delayrunnable, 3000);
        }else {
            Intent intent=new Intent(context,NoInternetActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(new Intent(context,NoInternetActivity.class));
        }
    }

    protected void setLockScreenOrientation(boolean lock) {
        if (Build.VERSION.SDK_INT >= 18) {
            setRequestedOrientation(lock?ActivityInfo.SCREEN_ORIENTATION_LOCKED:ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            return;
        }

        if (lock) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case 0: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break; // value 1
                case 2: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); break; // value 9
                case 1: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break; // value 0
                case 3: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break; // value 8
            }
        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // value 10
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("edit_text_value",tv.getText().toString());
    }

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.logcat();
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Fetching location...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn= (Button) findViewById(R.id.button);
        edtRadius=(EditText) findViewById(R.id.editText);
        dbFileLoc= Environment.getExternalStorageDirectory()+ File.separator+"JSLocation_loc_DB.db";
        dbHelperLoc=new DBHelper(getApplicationContext(),dbFileLoc);
        simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        geocoder = new Geocoder(this, Locale.getDefault());
        tv=(TextView)findViewById(R.id.textView);
        btnStop=(Button)findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(this);
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        edtChannel=(EditText)findViewById(R.id.editTextChannel);
        edtCnic=(EditText)findViewById(R.id.editTextCnic);
        edtMob=(EditText)findViewById(R.id.editTextMob);
        edtIncome=(EditText)findViewById(R.id.editTextIncome);

//        edtChannel.setEnabled(false);
        edtChannel.setText("location");
        if(sharedpreferences!=null){
            edtCnic.setText(sharedpreferences.getString("Cnic",""));
            edtChannel.setText(sharedpreferences.getString("Channel",""));
            edtIncome.setText(sharedpreferences.getString("Income",""));
            edtMob.setText(sharedpreferences.getString("MobileNo",""));
        }
        infoBean=new InfoBean(edtMob.getText().toString(),edtCnic.getText().toString(),
                edtChannel.getText().toString(),edtIncome.getText().toString());
        tv.setText("");
        if(savedInstanceState!=null){
            tv.setText(savedInstanceState.getCharSequence("edit_text_value"));
        }
        btn.setOnClickListener(this);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
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
                if (TextUtils.isEmpty(edtMob.getText().toString()) && TextUtils.isEmpty(edtCnic.getText().toString()) && TextUtils.isEmpty(edtChannel.getText().toString()) && TextUtils.isEmpty(edtRadius.getText().toString())) {
                    Toast.makeText(this, "Empty fields not allowed...", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(edtMob.getText().toString())) {
                        edtMob.setError("Please enter Mobile No.");
                    }
                    if (TextUtils.isEmpty(edtCnic.getText().toString())) {
                        edtCnic.setError("Please enter Cnic No.");
                    }
                    if (TextUtils.isEmpty(edtChannel.getText().toString())) {
                        edtChannel.setError("Please enter Channel Id.");
                    }
                    if (TextUtils.isEmpty(edtIncome.getText().toString())) {
                        edtIncome.setError("Please enter Channel Income.");
                    }
                    if (TextUtils.isEmpty(edtRadius.getText().toString())) {
                        edtRadius.setError("Please enter Radius.");
                    }
                    if (!TextUtils.isEmpty(edtMob.getText().toString())
                            && !TextUtils.isEmpty(edtCnic.getText().toString())
                            && !TextUtils.isEmpty(edtChannel.getText().toString())
                            && !TextUtils.isEmpty(edtIncome.getText().toString())
                            && !TextUtils.isEmpty(edtRadius.getText().toString().trim())) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("MobileNo", edtMob.getText().toString());
                        editor.putString("Cnic", edtCnic.getText().toString());
                        editor.putString("Channel", edtChannel.getText().toString());
                        editor.putString("Income", edtIncome.getText().toString());
                        editor.commit();
                        radius=edtRadius.getText().toString();
                        Toast.makeText(MainActivity.this, "Location fetched inserting data...", Toast.LENGTH_SHORT).show();
                        locationManager.requestSingleUpdate(provider, this, null);
                        setLockScreenOrientation(true);
                        progressDialog.show();
                    }

                }

        }
        if(v.getId()==R.id.buttonStop){
            Toast.makeText(MainActivity.this, "Stop location...", Toast.LENGTH_SHORT).show();
            locationManager.removeUpdates(this);
            setLockScreenOrientation(false);

        }
    }

    public class LocationAsync extends AsyncTask<Location,Void,List<DataBean>>{
        List<Address> addresses = null;
        Request request;
        Response response;
        JSONObject object;
        JSONArray array;
        OkHttpClient client;
        List<DataBean> beanList;
        String text="";

        Snackbar snackbar;
        @Override
        protected void onPreExecute() {
            client=new OkHttpClient();
            beanList=new ArrayList<>();



        }

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
                beanList.add(new DataBean(1,recId,"Accuracy",String.valueOf(locations[0].getAccuracy()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Altitude",String.valueOf(locations[0].getAltitude()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Bearing",String.valueOf(locations[0].getBearing()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"ElapsedRealtimeNanos",String.valueOf(locations[0].getElapsedRealtimeNanos()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Latitude",String.valueOf(locations[0].getLatitude()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Longitude",String.valueOf(locations[0].getLongitude()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Provider",String.valueOf(locations[0].getProvider()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Speed",String.valueOf(((locations[0].getSpeed()*3600)/1000)),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Time",simpleDateFormat.format(new Date(Long.parseLong(String.valueOf(locations[0].getTime())))),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Address",String.valueOf(addresses.get(0).getAddressLine(0)),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Known Name",String.valueOf(addresses.get(0).getFeatureName()),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"PlaceName",array.getJSONObject(1).getString("name"),new Date().toString(),tm.getDeviceId(),infoBean));
                beanList.add(new DataBean(1,recId,"Radius",radius,new Date().toString(),tm.getDeviceId(),infoBean));

                request = new Request.Builder()
                        .url("https://mfahad88.000webhostapp.com/js/Data.php?catId="+beanList.get(0).getCatId()+"&recId="+beanList.get(0).getRecId()+"&attribute="+beanList.get(0).getAttribute()+"&value="+beanList.get(0).getValue()+"&mobileIMEI="+beanList.get(0).getMobileIMEI()+"&recordDate="+beanList.get(0).getRecordDate()+"&mobileNo="+beanList.get(0).getInfoBean().getMobileNo()+"&cnicNo="+beanList.get(0).getInfoBean().getCnicNo()+"&channelId="+beanList.get(0).getInfoBean().getChannelId()+"&income="+beanList.get(0).getInfoBean().getIncome())
                        .build();
                response = client.newCall(request).execute();

                text=response.body().string();
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbHelperLoc.insertDetail(beanList);
            snackbar=Snackbar.make(findViewById(R.id.relativeMain),text,Snackbar.LENGTH_SHORT);
            return beanList;
        }

        @Override
        protected void onPostExecute(List<DataBean> dataBeen) {
            if(dataBeen.size()>0){
                for(int i=0;i<dataBeen.size();i++){
                    tv.append(dataBeen.get(i).getAttribute()+" : "+dataBeen.get(i).getValue()+"\n");
                }
                progressDialog.dismiss();
                snackbar.show();
                dataBeen.clear();
               //Toast.makeText(MainActivity.this, request.url().toString(), Toast.LENGTH_SHORT).show();
                setLockScreenOrientation(false);
            }

        }
    }



}
