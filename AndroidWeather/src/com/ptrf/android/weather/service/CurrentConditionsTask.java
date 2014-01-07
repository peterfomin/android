package com.ptrf.android.weather.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ptrf.android.weather.WeatherData;

/**
 * Weather service task.
 *
 */
public class CurrentConditionsTask extends WeatherServiceTask {

	public CurrentConditionsTask(Context context) {
		super(context);
	}

	@Override
	protected String createRequestUrl(String[] args) {
		//i.e. http://api.wunderground.com/api/%s/conditions/q/%s.json
		String url = args[0];
		//i.e. key
		String key = args[1];
		//i.e. CA/San_Francisco
		String location = args[2];
		
		return String.format(url, key, location);
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