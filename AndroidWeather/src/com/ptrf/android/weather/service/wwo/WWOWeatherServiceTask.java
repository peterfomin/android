package com.ptrf.android.weather.service.wwo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Base Weather service task for api.worldweatheronline.com.
 */
public abstract class WWOWeatherServiceTask extends WeatherServiceTask {

	private static final String SERVICE_KEY = "wwoServiceKey";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WWOWeatherServiceTask(Context context) {
		super(context);
	}

	/**
	 * Returns service key.
	 * @return service key.
	 */
	protected String getServiceKey() throws Exception {
		//get shared preferences
		SharedPreferences preferences = getSharedPreferences();
		//get service key
		String key = preferences.getString(SERVICE_KEY, null);
		
		//if key is not specified then report it as error w/out calling the service
		if (key == null || key.trim().equals("")) {
			throw new Exception(getString(R.string.msg_wwoServiceSpecifyKey));
		}
		
		return key;
	}
	
	/**
	 * Returns search query for the service call.
	 * @param deviceLocation device location
	 * @param enteredLocation entered location
	 * @return search query for the service call.
	 */
	@SuppressLint("DefaultLocale")
	protected String getQuery(Location deviceLocation, String enteredLocation) {
		String query = null;
		if (deviceLocation != null) {
			query = String.format("%.6f,%.6f", deviceLocation.getLatitude(), deviceLocation.getLongitude());
		} else {
			query = enteredLocation;
			//service requires spaces to be replaced with '_'
			//' +' means one or more consecutive spaces
			query = query.replaceAll(" +", "_");
		}
		return query;
	}

	@Override
	protected void checkError(JSONObject json) throws Exception {
		//no error element identified for this provider
	}
	
	/**
	 * Iterates over array passed in and extracts given object property.
	 * I.e. for "weatherDesc": [{"value": "Fog"}] it will return "Fog".
	 * @param array array to iterate over
	 * @param property json object property name
	 * @param separator value separator
	 * @return object property values
	 * @throws JSONException
	 */
	protected String getArrayValues(JSONArray array, String property, String separator) throws JSONException {
		StringBuilder builder = new StringBuilder();
		
		if (array != null) {
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				if (builder.length() > 0) {
					builder.append(separator);
				}
				builder.append(object.get(property));
			}
		}
		return builder.toString();
	}

	/**
	 * Extracts given object property from the first array element.
	 * I.e. for "weatherDesc": [{"value": "Fog"}] it will return "Fog".
	 * @param array array to iterate over
	 * @param property json object property name
	 * @return object property from the first array element
	 * @throws JSONException
	 */
	protected String getFirstArrayValue(JSONArray array, String property) throws JSONException {
//		if (array != null && array.length() > 0 ) {
//			return array.getJSONObject(0).getString(property);
//		} else {
//			return null;
//		}
		return (array != null && array.length() > 0 ? array.getJSONObject(0).getString(property) : null);
	}

}