package com.cs407.lab4_milestone2;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }

    public void updateLocationInfo(@NonNull Location location) {
        Log.i("LocationInfo", location.toString());
        TextView lat, lon, alt, acc;
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        alt = findViewById(R.id.altitude);
        acc = findViewById(R.id.accuracy);

        lat.setText("Latitude: " + location.getLatitude());
        lon.setText("Longitude: " + location.getLongitude());
        alt.setText("Altitude: " + location.getAltitude());
        acc.setText("Accuracy: " + location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            String address = "Could not find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() > 0) {
                Log.i("PlaceInfo", listAddresses.get(0).toString());
                address = "Address: \n";
                Address adr = listAddresses.get(0);
                if (adr.getSubThoroughfare() != null) {
                    address += adr.getSubThoroughfare() + " ";
                }
                if (adr.getThoroughfare() != null) {
                    address += adr.getThoroughfare() + "\n";
                }
                if (adr.getLocality() != null) {
                    address += adr.getLocality() + "\n";
                }
                if (adr.getPostalCode() != null) {
                    address += adr.getPostalCode() + "\n";
                }
                if (adr.getCountryName() != null) {
                    address += adr.getCountryName() + "\n";
                }
            }
            TextView adrView = findViewById(R.id.address);
            adrView.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
            startListening();
        }
    }
}