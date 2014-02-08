package com.ptrf.android.weather.service.mocked;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.data.Forecast;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.WeatherForecast;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Mocked weather service task to received mocked weather conditions.
 */
public class MockedForecastTask extends WeatherServiceTask {

	/**
	 * Creates new instance of the task.
	 * 
	 * @param context current activity
	 */
	public MockedForecastTask(Context context) {
		super(context);
	}

	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		return null;
	}

	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		Forecast result = new Forecast();

		String location = "Plymouth, MN";
		result.setLocation(location);
		// set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(MockedForecastTask.class.getName());

		result.add(createWeatherForecast("Mon", new Temperature("0", "32"), new Temperature("5", "41"), "Sunny", new Wind("NNW", "7", "10"), "10%"));
		result.add(createWeatherForecast("Tue", new Temperature("0", "32"), new Temperature("5", "41"), "Cloudy", new Wind("S", "0", "0"), "40%"));
		
		return result;
	}

	/**
	 * Checks for error json element and throws exception if found.
	 * 
	 * @param json
	 * @throws Exception
	 */
	@Override
	protected void checkError(JSONObject json) throws Exception {
	}

	/**
	 * No processing, only calling {@link #createWeatherData(JSONObject)} method.
	 */
	@Override
	protected WeatherData doInBackground(Object... args) {
		WeatherData data = null;
		try {
			data = createWeatherData(null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Create test instance of WeatherForecast.
	 * @param day
	 * @param low
	 * @param high
	 * @param weather
	 * @param wind
	 * @param precipitation
	 * @return instance of WeatherForecast.
	 */
	private WeatherForecast createWeatherForecast(String day, Temperature low, Temperature high, String weather, Wind wind, String precipitation) {
		WeatherForecast forecast = new WeatherForecast();
		
		forecast.setDay(day);
		forecast.setTemperatureLow(low);
		forecast.setTemperatureHigh(high);
		forecast.setWeather(weather);
		forecast.setWind(wind);
		forecast.setPrecipitation(precipitation);
		
		return forecast;
	}
}