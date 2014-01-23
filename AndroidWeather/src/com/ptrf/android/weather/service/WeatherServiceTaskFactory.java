package com.ptrf.android.weather.service;

import com.ptrf.android.weather.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Weather Service Task Factory class to create new instances of WeatherServiceTask implementations based on the weather service provider preferences.
 */
public abstract class WeatherServiceTaskFactory {
	
	/**
	 * Creates a new instance of the WeatherServiceTask implementation based on the currently selected weather service provider.
	 * @param context activity
	 * @return WeatherServiceTask implementation
	 * @throws Exception if provider can't be created
	 */
	public static WeatherServiceTask createWeatherServiceTask(Context context) throws Exception {
		WeatherServiceTask task = null;
		
		//get application shared properties
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		//get currently selected currently selected weather service provider
		String serviceProvider = preferences.getString("serviceProvider", null);
		
		//create a specific WeatherServiceTask implementation based on the currently selected weather service provider 
		if(WUCurrentConditionsTask.class.getName().equals(serviceProvider)) {
			task = new WUCurrentConditionsTask(context);
		} else if (WWOCurrentConditionsTask.class.getName().equals(serviceProvider)) {
			task = new WWOCurrentConditionsTask(context);
		} else if (MockedCurrentConditionsTask.class.getName().equals(serviceProvider)) {
			task = new MockedCurrentConditionsTask(context);
		} else {
			throw new Exception(context.getString(R.string.msg_selectServiceProvider));
		}
		
		return task;
	}
}
