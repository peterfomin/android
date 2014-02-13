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
	WeatherUnderground(WUCurrentConditionsTask.class.getName(), WUForecastTask.class.getName()), 
	WorldWeatherOnline(WWOCurrentConditionsTask.class.getName(), WWOForecastTask.class.getName()), 
	Mocked(MockedCurrentConditionsTask.class.getName(), MockedForecastTask.class.getName());
	
	/**
	 * Service task for current conditions.
	 */
	private String currentConditionsTaskClass;
	
	/**
	 * Service task for forecast.
	 */
	private String forecastTaskClass;
	
	/**
	 * Creates new instance of the ServiceProvider.
	 * @param currentConditionsTaskClass
	 * @param forecastTastClass
	 */
	ServiceProvider(String currentConditionsTaskClass, String forecastTastClass) {
		this.currentConditionsTaskClass = currentConditionsTaskClass;
		this.forecastTaskClass = forecastTastClass;
	}

	/**
	 * Returns the name of the class to be used as the current conditions task.
	 * @return current conditions task class name
	 */
	public String getCurrentConditionsTaskClass() {
		return currentConditionsTaskClass;
	}

	/**
	 * Returns the name of the class to be used as the forecast task.
	 * @return forecast task class name
	 */
	public String getForecastTaskClass() {
		return forecastTaskClass;
	}

}
