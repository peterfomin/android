package com.ptrf.android.weather.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ptrf.android.weather.R;

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
	public static WeatherServiceTask createWeatherServiceTask(Context context, boolean forecast) throws Exception {
		//get application shared properties
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		//get currently selected currently selected weather service provider
		String serviceProviderValue = preferences.getString("serviceProvider", null);
		
		//create a specific WeatherServiceTask implementation based on the currently selected weather service provider
		
		//obtain the enumeration constant associated with the provider name
		ServiceProvider serviceProvider = null;
		try {
			serviceProvider = ServiceProvider.valueOf(serviceProviderValue);
		} catch (Exception e) {
			throw new Exception(context.getString(R.string.msg_selectServiceProvider), e);
		}
		
		//get the providers' current conditions or forecast class name
		String className = (forecast ? serviceProvider.getForecastTaskClass() : serviceProvider.getCurrentConditionsTaskClass());
		
		WeatherServiceTask task = null;
		try {
			//get class instance using class name
			Class<?> clazz = Class.forName(className);
			//create new instance of the task using the constructor that takes instance of the Context as parameter.
			task = (WeatherServiceTask) clazz.getConstructor(Context.class).newInstance(context);
		} catch (Exception e) {
			throw new Exception(context.getString(R.string.msg_selectServiceProvider), e);
		}

		return task;
	}
}
