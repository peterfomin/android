package com.ptrf.android.weather.service.wu;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.data.CurrentConditions;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.util.ImageUtility;

/**
 * Weather service task to receive current conditions from api.wunderground.com.
 */
public class WUCurrentConditionsTask extends WUWeatherServiceTask {

	/**
	 * Service URL.
	 */
	private static final String URL = "http://api.wunderground.com/api/%s/conditions/q/%s.json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WUCurrentConditionsTask(Context context) {
		super(context);
	}

	/**
	 * Returns the service request url for the service call.
	 */
	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		//replace placeholders in the URL with key and query
		return String.format(URL, getServiceKey(), getQuery(deviceLocation, enteredLocation));
	}
	
	/**
	 * Returns an instance of CurrentConditions extension of WeatherData retrieved from the JSON object.
	 */
	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		CurrentConditions result = new CurrentConditions();
		
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

}