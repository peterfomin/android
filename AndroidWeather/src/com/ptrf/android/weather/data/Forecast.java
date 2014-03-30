package com.ptrf.android.weather.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Forecast data.
 */
public class Forecast extends WeatherData {

	private List<DailyForecast> dailyForecasts = new ArrayList<DailyForecast>();

	public void add(DailyForecast forecast) {
		getDailyForecasts().add(forecast);
	}

	public List<DailyForecast> getDailyForecasts() {
		return dailyForecasts;
	}

	@Override
	public String toString() {
		return String.format("Forecast [dailyForecast=%s, super=%s]",
				dailyForecasts, super.toString());
	}
	
}
