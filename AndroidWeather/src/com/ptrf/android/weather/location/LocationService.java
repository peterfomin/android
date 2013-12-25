package com.ptrf.android.weather.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Location service.
 *
 */
public class LocationService implements LocationListener {
	private static final String TAG = LocationService.class.getName();
	
	//private Context context;
    private LocationManager locationManager;
    
	public LocationService(Context context) {
		//this.context = context;
	    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    long minTime = 1000;
		float minDistance = 10;
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
		}
	}
	
	/**
	 * Returns the location selected from all providers.
	 */
	public Location getCurrentLocation() {
		
	    Location location = getLocationByProvider(LocationManager.GPS_PROVIDER);
	    if (location == null) {
	    	Log.d(TAG, "No GPS Location available");
	    	location = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
	    	if (location == null) {
	    		Log.d(TAG, "No Network Location available");
	    	}
	    }
	    
	    return location;
	}

	/**
	 * Returns the last known location from a specific provider (network/gps).
	 * @param provider
	 * @return last known location
	 */
	private Location getLocationByProvider(String provider) {
	    Location location = null;

	    try {
	        if (locationManager.isProviderEnabled(provider)) {
	            location = locationManager.getLastKnownLocation(provider);
	        }
	    } catch (Exception e) {
	        Log.e(TAG, "Failed to access location provider=" + provider, e);
	    }
	    return location;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, String.format("onLocationChanged: location=%s", location));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, String.format("onStatusChanged: provider=%s status=%s extras=%s", provider, status, extras));
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, String.format("onProviderEnabled: provider=%s", provider));
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, String.format("onProviderDisabled: provider=%s", provider));
	}

}
