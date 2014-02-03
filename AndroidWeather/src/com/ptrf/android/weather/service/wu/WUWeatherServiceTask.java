package com.ptrf.android.weather.service.wu;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Base Weather service task for api.wunderground.com.
 */
public abstract class WUWeatherServiceTask extends WeatherServiceTask {

	private static final String SERVICE_KEY = "wuServiceKey";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WUWeatherServiceTask(Context context) {
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
			throw new Exception(getString(R.string.msg_wuServiceSpecifyKey));
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
			//wunderground.com service requires spaces to be replaced with '_'
			//' +' means one or more consecutive spaces
			query = query.replaceAll(" +", "_");
		}
		return query;
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