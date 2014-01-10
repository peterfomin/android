package com.ptrf.android.weather.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.ptrf.android.weather.WeatherData;

/**
 * Weather service task to receive current conditions.
 */
public class WUCurrentConditionsTask extends WeatherServiceTask {

	private static final String URL = "http://api.wunderground.com/api/%s/conditions/q/%s.json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WUCurrentConditionsTask(Context context) {
		super(context);
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected String createRequestUrl(SharedPreferences preferences, Location deviceLocation, String enteredLocation) {
		
		String key = preferences.getString("serviceKey", "");
		
		//if key is not specified then report it as error w/out calling the service
		if (key == null || key.trim().equals("")) {
			throw new IllegalArgumentException("Please specify a valid service key in the settings.");
		}
		
		String query = null;
		if (deviceLocation != null) {
			query = String.format("%.6f,%.6f", deviceLocation.getLatitude(), deviceLocation.getLongitude());
		} else {
			query = enteredLocation;
			//wunderground.com service requires spaces to be replaced with '_'
			//' +' means one or more consecutive spaces
			query = query.replaceAll(" +", "_");
		}
		
		//replace placeholders in the URL with key and query
		return String.format(URL, key, query);
	}

	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		WeatherData result = new WeatherData();
		
		JSONObject currentObservation = json.getJSONObject("current_observation");
		JSONObject displayLocation = currentObservation.getJSONObject("display_location");
		result.setLocation(displayLocation.getString("full"));
		result.setTemperature(currentObservation.getString("temperature_string"));
		result.setWeather(currentObservation.getString("weather"));
		result.setWind(currentObservation.getString("wind_string"));
		result.setLatitude(displayLocation.getString("latitude"));
		result.setLongitude(displayLocation.getString("longitude"));
		
		return result;
	}
	
}