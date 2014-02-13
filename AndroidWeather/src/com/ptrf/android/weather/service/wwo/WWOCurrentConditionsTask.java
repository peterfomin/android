package com.ptrf.android.weather.service.wwo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.data.CurrentConditions;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.util.ImageUtility;

/**
 * Weather service task to receive current conditions from api.worldweatheronline.com.
 */
public class WWOCurrentConditionsTask extends WWOWeatherServiceTask {

	private static final String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?key=%s&q=%s&includelocation=yes&format=json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WWOCurrentConditionsTask(Context context) {
		super(context);
	}
	
	/**
	 * Returns the service request url for the service call.
	 */
	@SuppressLint("DefaultLocale")
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
		
		JSONObject data = json.getJSONObject("data");
		JSONArray currentConditions = data.getJSONArray("current_condition");
		if (currentConditions.length() > 0) {
			JSONObject currentCondition = currentConditions.getJSONObject(0);
			
			Temperature temperature = new Temperature();
			temperature.setValueF(currentCondition.getString("temp_F"));
			temperature.setValueC(currentCondition.getString("temp_C"));
			result.setTemperature(temperature);
			
			//No feels like element available
			
			result.setWeather(getFirstArrayValue(currentCondition.getJSONArray("weatherDesc"), "value"));
			String urlAddress = getFirstArrayValue(currentCondition.getJSONArray("weatherIconUrl"), "value");
			if (urlAddress != null && urlAddress.trim().length() > 0) {
				result.setWeatherImage(ImageUtility.createImageFromURL(urlAddress));
			}
			
			Wind wind = new Wind();
			wind.setDirection(currentCondition.getString("winddir16Point"));
			wind.setSpeedKph(currentCondition.getString("windspeedKmph"));
			wind.setSpeedMph(currentCondition.getString("windspeedMiles"));
			result.setWind(wind);
			
			result.setHumidity(currentCondition.getString("humidity") + "%");
		}
		
		StringBuilder location = new StringBuilder();
		JSONArray areas = data.getJSONArray("nearest_area");
		if (areas.length() > 0) {
			//pick the first one
			JSONObject area = areas.getJSONObject(0);
			
			//set location returned
			location.append(getFirstArrayValue(area.getJSONArray("areaName"), "value"));
			location.append(",");
			location.append(getFirstArrayValue(area.getJSONArray("region"), "value"));
			result.setLocation(location.toString());
			
			//set GPS coordinates
			result.setLatitude(area.getString("latitude"));
			result.setLongitude(area.getString("longitude"));
		}
		
		//set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(getString(R.string.msg_wwoServiceDataProvidedBy));
		
		return result;
	}

}