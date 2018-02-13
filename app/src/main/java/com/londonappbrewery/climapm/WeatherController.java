package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "64e651a91a708e94ca01bceb0dac18de";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    //if using physical device and using ACCESS_COARSE_LOCATION in permissions use
    // LocationManager.NETWORK_PROVIDER
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;//since we are using emulator and we are using ACCESS_FINE_LOCATION in permission

    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;//will start or stop requesting location updates
    LocationListener mLocationListener;//will notify if the location is actually changed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);

        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to changeCityActivity we must use intent
                    //calling the intent constructor and pass (where u are, where to go)
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(myIntent);



            }
        });
    }

    // TODO: Add onResume() here:
    @Override
///one of the app lifecyle method that executes just after
// oncreate and just before user can
// interact with the activity
    protected void onResume() {
        super.onResume();
        Log.d("clima", "onResume() called");
        Intent myIntent = getIntent();
        String City = myIntent.getStringExtra("City");

        if(City != null){
            getWeatherForNewCity(City);
        }else{



        Log.d("clima", "Getting weather for current location");
        getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
        public void getWeatherForNewCity(String city) {

            RequestParams mRequestParams = new RequestParams();
            mRequestParams.put("q", city);
            mRequestParams.put("appid", APP_ID);
            letsDoSomeNetworking(mRequestParams);
        }
    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        //code that gets hold of a LocationManager and assigns that LocationManager object
        //to our mLocationManager member variable
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("clima", "onLocationChanged() callback received");
                String longitude =  String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("clima","the longitude is:" + longitude+"" +"the latitude is : " + latitude);

                RequestParams mRequestParams = new RequestParams();
                mRequestParams.put("lat",latitude);
                mRequestParams.put("lon",longitude);
                mRequestParams.put("appid",APP_ID);
                letsDoSomeNetworking(mRequestParams);

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d("clima", "onProviderDisabled callback received");
            }
        };
        //start make the locationManager start requesting updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //MAKE THE POP UP TO REQUEST PERMISSION ON DEVICE
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("clima","onRequestPermission() permission Granted!!");
                getWeatherForCurrentLocation();
            }else {
                Log.d("clima","Permission Denied = (");

            }
        }
    }
    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        //using james library
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("clima","success JSON"+ response.toString());
                //getting the JSONObject that we got as a response and passing it to our model
                //then storing it in a WeatherDataModel instance as weatherDataModel
                WeatherDataModel weatherDataModel =  WeatherDataModel.fromJson(response);
                //calling the updateUI method and passing it the object returned from the model as weatherDateModel
                updateUI(weatherDataModel);
            }

            @Override
            public void onFailure(int statusCode,Header[] headers,Throwable e,JSONObject response){
                Log.e("clima","Fail"+e.toString());
                Log.d("clima","statusCode"+ statusCode);
                Toast.makeText(WeatherController.this, "Request Fail!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Add updateUI() here:
    //updateUI takes in a WeatherDataModel object as parameter
    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());
        //getting image resourceID provided the name and location of the image
        int resourceID = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID) ;
    }

    // TODO: Add onPause() here:


    @Override
    protected void onPause() {

        super.onPause();
        if (mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
