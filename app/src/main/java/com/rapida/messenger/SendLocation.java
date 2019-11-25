package com.rapida.messenger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SendLocation extends FragmentActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private String str;
    private GoogleMap mMap;
    private TextView dialogText;
    private LinearLayout dialog;
    private RelativeLayout mainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_location);

        dialogText = findViewById(R.id.dialog_text);
        mainContainer = findViewById(R.id.send_location_container);
        dialog = findViewById(R.id.dialog);

        Button button = findViewById(R.id.location_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages").child(getIntent().getStringExtra("groupID"));
                    String key = reference.push().getKey();
                    reference = reference.child(key);
                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("messageType", "location");
                    hashMap.put("message", latitude +"###"+ longitude +"###"+str);
                    hashMap.put("messageTime", String.valueOf(new Date().getTime()));
                    hashMap.put("senderPhone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    reference.setValue(hashMap);
                    finish();
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},123);
            return;
        }

        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    getCurrentLocation(location);
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

        else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.wtf("If cond","gps");
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    getCurrentLocation(location);
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

    private void displayDialogBox(String str) {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        dialogText.setText(str);
        dialog.setVisibility(View.VISIBLE);
        mainContainer.setAlpha(0.2f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDialogBox() {
        dialog.setVisibility(View.INVISIBLE);
        mainContainer.setAlpha(1);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void getCurrentLocation(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        hideDialogBox();

        LatLng obj = new LatLng(latitude,longitude);

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            str = address +" ,"+ city +" ,"+ state +" ,"+ country;
            mMap.addMarker(new MarkerOptions().position(obj).title(str));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(obj,15));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayDialogBox("Finding your location");


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
