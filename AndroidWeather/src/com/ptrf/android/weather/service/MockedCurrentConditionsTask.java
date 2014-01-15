package com.ptrf.android.weather.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.WeatherData;

/**
 * Mocked weather service task to received mocked weather conditions.
 */
public class MockedCurrentConditionsTask extends WeatherServiceTask {

	/**
	 * Creates new instance of the task.
	 * 
	 * @param context
	 *            current activity
	 */
	public MockedCurrentConditionsTask(Context context) {
		super(context);
	}

	@SuppressLint("DefaultLocale")
	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		return null;
	}

	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		WeatherData result = new WeatherData();

		result.setLocation("Plymouth, MN");
		result.setTemperature("32F (0 C)");
		result.setWeather("Sunny");
		result.setWind("NNW 10 mph");

		// set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(MockedCurrentConditionsTask.class.getName());

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

}