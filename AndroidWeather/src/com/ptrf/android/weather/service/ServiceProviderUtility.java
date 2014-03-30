package com.ptrf.android.weather.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ptrf.android.weather.R;

/**
 * Weather Service Provider utility to obtain instances of ServiceProvider based on the weather service provider preferences.
 */
public abstract class ServiceProviderUtility {
	
	/**
	 * Creates a new instance of the WeatherServiceTask implementation based on the currently selected weather service provider.
	 * @param context activity
	 * @return ServiceProvider instance
	 * @throws Exception if provider can't be located
	 */
	public static ServiceProvider getServiceProvider(Context context) throws Exception {
		//get application shared properties
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		//get currently selected currently selected weather service provider
		String serviceProviderValue = preferences.getString("serviceProvider", null);
		
		//obtain the enumeration constant associated with the provider name
		ServiceProvider serviceProvider = null;
		try {
			serviceProvider = ServiceProvider.valueOf(serviceProviderValue);
		} catch (Exception e) {
			throw new Exception(context.getString(R.string.msg_selectServiceProvider), e);
		}

		return serviceProvider;
	}
}
