package com.ptrf.android.weather.service;

import com.ptrf.android.weather.service.mocked.MockedCurrentConditionsTask;
import com.ptrf.android.weather.service.mocked.MockedForecastTask;
import com.ptrf.android.weather.service.wu.WUCurrentConditionsTask;
import com.ptrf.android.weather.service.wu.WUForecastTask;
import com.ptrf.android.weather.service.wwo.WWOCurrentConditionsTask;
import com.ptrf.android.weather.service.wwo.WWOForecastTask;

/**
 * Enumeration holding constants defined for supported service providers and their current conditions and forecast tasks.
 */
public enum ServiceProvider {
	WeatherUnderground(WUCurrentConditionsTask.class, WUForecastTask.class), 
	WorldWeatherOnline(WWOCurrentConditionsTask.class, WWOForecastTask.class), 
	Mocked(MockedCurrentConditionsTask.class, MockedForecastTask.class);
	
	/**
	 * Service task for current conditions.
	 */
	private Class<? extends WeatherServiceTask> currentConditionsTaskClass;
	
	/**
	 * Service task for forecast.
	 */
	private Class<? extends WeatherServiceTask> forecastTaskClass;
	
	/**
	 * Creates new instance of the ServiceProvider.
	 * @param currentConditionsTaskClass
	 * @param forecastTaskClass
	 */
	ServiceProvider(
			Class<? extends WeatherServiceTask> currentConditionsTaskClass, 
			Class<? extends WeatherServiceTask> forecastTaskClass) {
		this.currentConditionsTaskClass = currentConditionsTaskClass;
		this.forecastTaskClass = forecastTaskClass;
	}

	/**
	 * Returns the name of the class to be used as the current conditions task.
	 * @return current conditions task class
	 */
	public Class<? extends WeatherServiceTask> getCurrentConditionsTaskClass() {
		return currentConditionsTaskClass;
	}

	/**
	 * Returns the name of the class to be used as the forecast task.
	 * @return forecast task class
	 */
	public Class<? extends WeatherServiceTask> getForecastTaskClass() {
		return forecastTaskClass;
	}

}
