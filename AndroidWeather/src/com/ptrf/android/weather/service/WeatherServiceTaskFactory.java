package com.ptrf.android.weather.service;

import android.content.Context;

/**
 * Weather Service Task Factory class to create new instances of WeatherServiceTask implementations based on the weather service provider preferences.
 */
public abstract class WeatherServiceTaskFactory {
	
	/**
	 * Creates a new instance of the WeatherServiceTask implementation based on the class name passed.
	 * @param context activity
	 * @param clazz class subclass of the WeatherServiceTask
	 * @return WeatherServiceTask implementation
	 * @throws Exception if WeatherServiceTask instance can't be created
	 */
	public static WeatherServiceTask createWeatherServiceTask(Context context, Class<? extends WeatherServiceTask> clazz) throws Exception {
		
		WeatherServiceTask task = null;
		try {
			//create new instance of the task using the constructor that takes instance of the Context as parameter.
			task = (WeatherServiceTask) clazz.getConstructor(Context.class).newInstance(context);
		} catch (Exception e) {
			throw new Exception("Failed to create an instance of the WeatherServiceTask using class="+ clazz, e);
		}

		return task;
	}
}
