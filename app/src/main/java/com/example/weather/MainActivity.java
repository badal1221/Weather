package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE=100;
    private ProgressBar prgbr;
    private TextView cityname,temp,cndn,tday;
    private LinearLayout llout;
    private EditText srchcity;
    private ImageView srchimg,imgg,btn;
    private RecyclerView recview;
    private RelativeLayout rlout;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private String ctname;
    Recyclerwethradapter adapter;
    Intent intent;
    ArrayList<wethr> arr=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //for potrait mode only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);
        recview=findViewById(R.id.recview);
        prgbr=findViewById(R.id.prgbr);
        cityname=findViewById(R.id.cityname);
        temp=findViewById(R.id.temp);
        cndn=findViewById(R.id.cndn);
        tday=findViewById(R.id.tday);
        llout=findViewById(R.id.llout);
        srchcity=findViewById(R.id.srchcity);
        srchimg=findViewById(R.id.srchimg);
        imgg=findViewById(R.id.imgg);
        rlout=findViewById(R.id.rlout);
        btn=findViewById(R.id.btn);
        recview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        adapter=new Recyclerwethradapter(this,arr);
        recview.setAdapter(adapter);

        //location permission

//        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
//        }
//        Location location=locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
//        Log.d("locn",location.getLongitude()+"+"+location.getLongitude());
//        ctname=getcitynm(location.getLongitude(),location.getLongitude());
//        ctname=getcitynm(20.284420,85.743620);
        prgbr.setVisibility(View.VISIBLE);
        getweatinfo("Bhubaneswar");
        srchimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prgbr.setVisibility(View.VISIBLE);
                String city=srchcity.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();
                    prgbr.setVisibility(View.GONE);
                }
                else{ getweatinfo(city); }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getcitynm(double lat, double longi){
         String ctname="City not found";
         Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());
         try{
              List<Address> addr=gcd.getFromLocation(lat,longi,1);
              for(Address add:addr){
                  if(add!=null){
                      String city=add.getLocality();
                      if(city!=null && !city.equals("")){ ctname=city; }
                      else{
                          Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show();
                      }
                  }
              }
         }catch(IOException e){
              e.printStackTrace();
              //Log.e("city",e.toString());
        }
         return ctname;
    }
    private void getweatinfo(String ctname){
        String url="http://api.weatherapi.com/v1/forecast.json?key=e574fbe197cc4a60bb6103115230402&q="+ctname+"&days=1&aqi=no&alerts=no";
        cityname.setText(ctname);
        AndroidNetworking.initialize(this);
        AndroidNetworking.get(url).setPriority(Priority.HIGH).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                   arr.clear();
                   prgbr.setVisibility(View.GONE);
                   try {
                       String temp1=response.getJSONObject("current").getString("temp_c").concat("°C");
                       temp.setText(temp1);
                       String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                       cndn.setText(condition);
                       String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                       Picasso.get().load("https:".concat(conditionIcon)).into(imgg);
                       int isday=response.getJSONObject("current").getInt("is_day");
                       int[] drawablearrayday=new int[]{R.drawable.bg2,R.drawable.bg3,R.drawable.bg5,R.drawable.bg6,R.drawable.bg7,R.drawable.bg8,R.drawable.bg9,R.drawable.bg10,R.drawable.bg11};
                       int[] drawablearraynight=new int[]{R.drawable.bg,R.drawable.bgn1,R.drawable.bgn2,R.drawable.bgn3,R.drawable.bgn4,R.drawable.bgn6,R.drawable.bgn7};
                       Random rand = new Random();
                       if(isday==1){
                           int i=rand.nextInt(drawablearrayday.length);
                           rlout.setBackgroundResource(drawablearrayday[i]);
                       }
                       else{
                           int i=rand.nextInt(drawablearraynight.length);
                           rlout.setBackgroundResource(drawablearraynight[i]);
                       }
                       JSONObject forecast=response.getJSONObject("forecast");
                       JSONObject forecstarr=forecast.getJSONArray("forecastday").getJSONObject(0);
                       JSONArray hr=forecstarr.getJSONArray("hour");
                       for(int i=0;i<hr.length();i++){
                           JSONObject hrobj=hr.getJSONObject(i);
                           String time1=hrobj.getString("time");
                           String temp2=hrobj.getString("temp_c");
                           String icoon=hrobj.getJSONObject("condition").getString("icon");
                           String wsp=hrobj.getString("wind_kph");
                           arr.add(new wethr(icoon,time1,temp2,wsp));
                       }
                       adapter.notifyDataSetChanged();
                   }catch(JSONException e){
                       e.printStackTrace();
                   }
            }

            @Override
            public void onError(ANError anError) {
                     Toast.makeText(MainActivity.this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();
                     cler();
            }
        });



//        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
//        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                   //prgbr.setVisibility(View.GONE);
//                   Log.d("msg",response.toString());
//                   arr.clear();
//                   try {
//                       String temp1=response.getJSONObject("current").getString("temp_c");
//                       temp.setText(temp1);
//                       String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
//                       cndn.setText(condition);
//                       String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
//                       Picasso.get().load("https:".concat(conditionIcon)).into(imgg);
//                       //int isday=response.getJSONObject("current").getInt("is_day");
//                       JSONObject forecast=response.getJSONObject("forecast");
//                       JSONObject forecstarr=forecast.getJSONArray("forecastday").getJSONObject(0);
//                       JSONArray hr=forecstarr.getJSONArray("hour");
//                       for(int i=0;i<hr.length();i++){
//                           JSONObject hrobj=hr.getJSONObject(i);
//                           String time1=hrobj.getString("time");
//                           String temp2=hrobj.getString("temp_c");
//                           String icoon=hrobj.getJSONObject("condition").getString("icon");
//                           String wsp=hrobj.getString("wind_kph");
//                           arr.add(new wethr(icoon,time1,temp2,wsp));
//                       }
//                       adapter.notifyDataSetChanged();
//                   }catch(JSONException e){
//                       e.printStackTrace();
//                   }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();
//            }
//        });
//        requestQueue.add(jsonObjectRequest);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("key1") && data.hasExtra("key2")) {
                prgbr.setVisibility(View.VISIBLE);
                String ct=getcitynm(Double.parseDouble(data.getExtras().getString("key1")), Double.parseDouble(data.getExtras().getString("key2")));
                if(ct.equals("City not found")){
                    cler();
                }
                else{
                    getweatinfo(ct);
                }
            }
        }
    }
    protected void cler(){
        arr.clear();
        cndn.setText("");
        temp.setText("");
        imgg.invalidate();
        imgg.setImageBitmap(null);
        srchcity.setText("");
        srchcity.setHint("Enter City Name");
        cityname.setText("CITY NAME");
        prgbr.setVisibility(View.GONE);
    }
}