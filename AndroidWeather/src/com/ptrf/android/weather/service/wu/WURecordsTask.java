package com.ptrf.android.weather.service.wu;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.data.Record;
import com.ptrf.android.weather.data.Records;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;

/**
 * Weather service task to receive records from api.wunderground.com.
 */
public class WURecordsTask extends WUWeatherServiceTask {

	/**
	 * Service URL.
	 */
	private static final String URL = "http://api.wunderground.com/api/%s/almanac/q/%s.json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WURecordsTask(Context context) {
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
	 * Returns an instance of Records extension of WeatherData retrieved from the JSON object.
	 */
	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		Records result = new Records();
		
		JSONObject almanac = json.getJSONObject("almanac");
		result.setLow(createRecord(almanac.getJSONObject("temp_low")));
		result.setHigh(createRecord(almanac.getJSONObject("temp_high")));
		
		//set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(getString(R.string.msg_wuServiceDataProvidedBy));
		
		return result;
	}

	/**
	 * Creates an instance of Record from the json object.
	 * @param jsonObject temp_low / temp_low json object
	 * @return an instance of Record
	 * @throws JSONException 
	 */
	private Record createRecord(JSONObject temperature) throws JSONException {
		Record record = new Record(
				createTemperature(temperature.getJSONObject("normal")), 
				createTemperature(temperature.getJSONObject("record")), 
				temperature.getString("recordyear"));
		return record;
	}

	/**
	 * Creates an instance of Temperature from json object.
	 * @param temperatureValue normal / record json object
	 * @return an instance of Temperature
	 * @throws JSONException 
	 */
	private Temperature createTemperature(JSONObject temperatureValue) throws JSONException {
		Temperature temperature = new Temperature(temperatureValue.getString("F"), temperatureValue.getString("C"));
		return temperature;
	}

}