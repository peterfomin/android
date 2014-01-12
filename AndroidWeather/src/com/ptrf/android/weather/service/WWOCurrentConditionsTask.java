package com.ptrf.android.weather.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.WeatherData;

/**
 * Weather service task to receive current conditions from api.worldweatheronline.com.
 */
public class WWOCurrentConditionsTask extends WeatherServiceTask {

	private static final String SERVICE_KEY = "wwoServiceKey";
	private static final String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?key=%s&q=%s&format=json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WWOCurrentConditionsTask(Context context) {
		super(context);
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		//get shared preferences
		SharedPreferences preferences = getSharedPreferences();
		//get service key
		String key = preferences.getString(SERVICE_KEY, null);
		
		//if key is not specified then report it as error w/out calling the service
		if (key == null || key.trim().equals("")) {
			throw new Exception(getString(R.string.msg_wwoServiceSpecifyKey));
		}
		
		String query = null;
		if (deviceLocation != null) {
			query = String.format("%.6f,%.6f", deviceLocation.getLatitude(), deviceLocation.getLongitude());
		} else {
			query = enteredLocation;
			//TODO: verify the rules
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
		
		JSONObject data = json.getJSONObject("data");
		JSONArray currentConditions = data.getJSONArray("current_condition");
		if (currentConditions.length() > 0) {
			JSONObject currentCondition = currentConditions.getJSONObject(0);
			result.setTemperature(currentCondition.getString("temp_F")); //TODO: add celcious
			result.setWeather(getArrayValues(currentCondition.getJSONArray("weatherDesc"), "value"));
			result.setWind(currentCondition.getString("winddir16Point"));
		}
		result.setLocation(getArrayValues(data.getJSONArray("request"), "query"));
		
//		JSONObject displayLocation = currentObservation.getJSONObject("display_location");
//		result.setLatitude(displayLocation.getString("latitude"));
//		result.setLongitude(displayLocation.getString("longitude"));
		
		//set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(getString(R.string.msg_wwoServiceDataProvidedBy));
		
		return result;
	}

	@Override
	protected void checkError(JSONObject json) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Iterates over array passed in and extracts given object property.
	 * @param array array to iterate over
	 * @param property
	 * @return
	 * @throws JSONException
	 */
	private String getArrayValues(JSONArray array, String property) throws JSONException {
		StringBuilder builder = new StringBuilder();
		
		if (array != null) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				builder.append(object.get(property));
			}
		}
		return builder.toString();
	}

}