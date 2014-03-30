package com.ptrf.android.weather.service;

import android.content.Context;

import com.ptrf.android.weather.R;

/**
 * Weather Service Task Factory class to create new instances of WeatherServiceTask implementations based on the weather service provider preferences.
 */
public abstract class WeatherServiceTaskFactory {
	
	/**
	 * Creates a new instance of the WeatherServiceTask implementation based on the class name passed.
	 * @param context activity
	 * @param className class name of the WeatherServiceTask
	 * @return WeatherServiceTask implementation
	 * @throws Exception if WeatherServiceTask can't be created
	 */
	public static WeatherServiceTask createWeatherServiceTask(Context context, String className) throws Exception {
		
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
