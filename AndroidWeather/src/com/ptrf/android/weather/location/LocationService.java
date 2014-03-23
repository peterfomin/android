package com.ptrf.android.weather.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Location service that is responsible for returning the current device location.
 */
public class LocationService implements LocationListener {
	private static final String TAG = LocationService.class.getName();
	
	/**
	 * LocationManager that is used to obtain last known location.
	 */
    private LocationManager locationManager;
    
    /**
     * Creates new instance of LocationService.
     * Initializes the LocationManager.
     * @param context
     */
	public LocationService(Context context) {
	    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	/**
	 * Adds itself as a location updates listener into location manager.
	 * An instance of the LocationListener must be registered with the LocationManager to receive updates so
	 * the LocationManager's getLastKnownLocation() method would return a valid value.
	 */
	public void startLocationUpdates() {
	    
		long minTime = 1000;
		float minDistance = 10;
		
		Log.d(TAG, String.format("Registering %s as location provider listener", this));
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(TAG, String.format("Registering %s as %s location provider listener", this, LocationManager.GPS_PROVIDER));
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Log.d(TAG, String.format("Registering %s as %s location provider listener", this, LocationManager.NETWORK_PROVIDER));
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
		}
	}
	
	/**
	 * Removes itself as a location updates listener.
	 */
	public void stopLocationUpdates() {
		Log.d(TAG, String.format("Removing %s as location provider listener", this));
		locationManager.removeUpdates(this);
	}
	
	/**
	 * Returns the location selected from GPS_PROVIDER and NETWORK_PROVIDER.
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

	//Did not have to provide any concrete implementation to get the last know location updated.
	
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
