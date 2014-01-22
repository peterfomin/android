package com.ptrf.android.weather.service;

import com.ptrf.android.weather.data.WeatherData;

/**
 * Interface defining result receiver.
 * This interface makes WeatherServiceTask independent of a specific Activity.
 */
public interface ResultReceiver {

	/**
	 * Receives the result of the WeatherServiceTask or an exception if an error occurred.
	 * @param result result
	 * @param throwable an exception if an error occurred
	 */
	public void receiveResult(WeatherData result, Throwable throwable);
}
