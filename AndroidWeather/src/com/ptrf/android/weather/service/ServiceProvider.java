package com.ptrf.android.weather.service;

import com.ptrf.android.weather.service.mocked.MockedCurrentConditionsTask;
import com.ptrf.android.weather.service.mocked.MockedForecastTask;
import com.ptrf.android.weather.service.mocked.MockedRecordsTask;
import com.ptrf.android.weather.service.wu.WUCurrentConditionsTask;
import com.ptrf.android.weather.service.wu.WUForecastTask;
import com.ptrf.android.weather.service.wu.WURecordsTask;
import com.ptrf.android.weather.service.wwo.WWOCurrentConditionsTask;
import com.ptrf.android.weather.service.wwo.WWOForecastTask;

/**
 * Enumeration holding constants defined for supported service providers and their current conditions, forecast and records tasks.
 */
public enum ServiceProvider {
	WeatherUnderground(WUCurrentConditionsTask.class, WUForecastTask.class, WURecordsTask.class), 
	WorldWeatherOnline(WWOCurrentConditionsTask.class, WWOForecastTask.class, null), 
	Mocked(MockedCurrentConditionsTask.class, MockedForecastTask.class, MockedRecordsTask.class);
	
	/**
	 * Service task for current conditions.
	 */
	private Class<? extends WeatherServiceTask> currentConditionsTaskClass;
	
	/**
	 * Service task for forecast.
	 */
	private Class<? extends WeatherServiceTask> forecastTaskClass;
	
	/**
	 * Service task for records.
	 */
	private Class<? extends WeatherServiceTask> recordsTaskClass;
	
	/**
	 * Creates new instance of the ServiceProvider.
	 * @param currentConditionsTaskClass
	 * @param forecastTaskClass
	 * @param recordsTaskClass
	 */
	ServiceProvider(
			Class<? extends WeatherServiceTask> currentConditionsTaskClass, 
			Class<? extends WeatherServiceTask> forecastTaskClass,
			Class<? extends WeatherServiceTask> recordsTaskClass) {
		this.currentConditionsTaskClass = currentConditionsTaskClass;
		this.forecastTaskClass = forecastTaskClass;
		this.recordsTaskClass = recordsTaskClass;
	}

	/**
	 * Returns the class to be used as the current conditions task.
	 * @return current conditions task class
	 */
	public Class<? extends WeatherServiceTask> getCurrentConditionsTaskClass() {
		return currentConditionsTaskClass;
	}

	/**
	 * Returns the the class to be used as the forecast task.
	 * @return forecast task class
	 */
	public Class<? extends WeatherServiceTask> getForecastTaskClass() {
		return forecastTaskClass;
	}

	/**
	 * Returns the class to be used as the records task.
	 * @return records task class
	 */
	public Class<? extends WeatherServiceTask> getRecordsTaskClass() {
		return recordsTaskClass;
	}

}
