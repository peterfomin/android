package com.ptrf.android.weather.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.util.ImageUtility;

/**
 * Weather service task to receive current conditions from api.wunderground.com.
 */
public class WUCurrentConditionsTask extends WeatherServiceTask {

	private static final String SERVICE_KEY = "wuServiceKey";
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
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		//get shared preferences
		SharedPreferences preferences = getSharedPreferences();
		//get service key
		String key = preferences.getString(SERVICE_KEY, null);
		
		//if key is not specified then report it as error w/out calling the service
		if (key == null || key.trim().equals("")) {
			throw new Exception(getString(R.string.msg_wuServiceSpecifyKey));
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

		Temperature temperature = new Temperature();
		temperature.setValueC(currentObservation.getString("temp_c"));
		temperature.setValueF(currentObservation.getString("temp_f"));
		result.setTemperature(temperature);
		
		Temperature feelsLike = new Temperature();
		feelsLike.setValueC(currentObservation.getString("feelslike_c"));
		feelsLike.setValueF(currentObservation.getString("feelslike_f"));
		result.setFeelsLike(feelsLike);
		
		result.setWeather(currentObservation.getString("weather"));
		String urlAddress = currentObservation.getString("icon_url");
		if (urlAddress != null && urlAddress.trim().length() > 0) {
			result.setWeatherImage(ImageUtility.createImageFromURL(urlAddress));
		}
		Wind wind = new Wind();
		wind.setDirection(currentObservation.getString("wind_dir"));
		wind.setSpeedKph(currentObservation.getString("wind_kph"));
		wind.setSpeedMph(currentObservation.getString("wind_mph"));
		result.setWind(wind);

		result.setHumidity(currentObservation.getString("relative_humidity"));
		
		result.setLatitude(displayLocation.getString("latitude"));
		result.setLongitude(displayLocation.getString("longitude"));
		
		//set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(getString(R.string.msg_wuServiceDataProvidedBy));
		
		return result;
	}
	
	/**
	 * Checks for error json element and throws exception if found.
	 * @param json
	 * @throws Exception
	 */
	@Override
	protected void checkError(JSONObject json) throws Exception {
		
		JSONObject response = json.getJSONObject("response");
		if (! response.isNull("error")) {
			JSONObject error = response.getJSONObject("error");
			throw new Exception(error.getString("description"));
		}
		if (! response.isNull("results")) {
			//JSONObject error = response.getJSONObject("results");
			//TODO: handle multiple locations returned
			throw new Exception(getString(R.string.msg_wuServiceLocationNotUnique));
		}
	}
}