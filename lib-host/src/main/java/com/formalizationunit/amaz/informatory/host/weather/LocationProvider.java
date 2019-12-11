package com.formalizationunit.amaz.informatory.host.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.Nullable;

import static android.content.Context.LOCATION_SERVICE;

class LocationProvider {
    /*
    private final LocationManager mLocationManager;
    private static final long LOCATION_REFRESH_TIME_MS = 600000;
    private static final long LOCATION_REFRESH_DISTANCE_M = 1000;
    */

    LocationProvider(Context context) {
        /*
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, LOCATION_REFRESH_TIME_MS, LOCATION_REFRESH_DISTANCE_M, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
        /*/
    }

    @SuppressLint("MissingPermission")
    @Nullable
    static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }
}
