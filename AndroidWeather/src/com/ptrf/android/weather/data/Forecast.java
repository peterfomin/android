package com.ptrf.android.weather.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Forecast data.
 */
public class Forecast extends WeatherData {

	private List<WeatherForecast> weatherForecast = new ArrayList<WeatherForecast>();

	public void add(WeatherForecast forecast) {
		getWeatherForecast().add(forecast);
	}

	public List<WeatherForecast> getWeatherForecast() {
		return weatherForecast;
	}

	@Override
	public String toString() {
		return String.format("Forecast [weatherForecast=%s, super=%s]",
				weatherForecast, super.toString());
	}
	
}
