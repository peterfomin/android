package com.ptrf.android.weather.service;

import com.ptrf.android.weather.service.mocked.MockedCurrentConditionsTask;
import com.ptrf.android.weather.service.mocked.MockedForecastTask;
import com.ptrf.android.weather.service.wu.WUCurrentConditionsTask;
import com.ptrf.android.weather.service.wu.WUForecastTask;
import com.ptrf.android.weather.service.wwo.WWOCurrentConditionsTask;
import com.ptrf.android.weather.service.wwo.WWOForecastTask;

/**
 * Enumeration holding constants defined for supported service providers.
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
	
	ServiceProvider(String currentConditionsTaskClass, String forecastTastClass) {
		this.currentConditionsTaskClass = currentConditionsTaskClass;
		this.forecastTaskClass = forecastTastClass;
	}

	public String getCurrentConditionsTaskClass() {
		return currentConditionsTaskClass;
	}

	public String getForecastTaskClass() {
		return forecastTaskClass;
	}

}
