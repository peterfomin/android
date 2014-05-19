package com.ptrf.android.weather.service.mocked;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.data.Record;
import com.ptrf.android.weather.data.Records;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Mocked weather service task to receive mocked weather records data.
 */
public class MockedRecordsTask extends WeatherServiceTask {

	/**
	 * Creates new instance of the task.
	 * 
	 * @param context current activity
	 */
	public MockedRecordsTask(Context context) {
		super(context);
	}

	/**
	 * Not used by the mocked task.
	 */
	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		return null;
	}

	/**
	 * Creates the mocked weather data.
	 */
	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		Records result = new Records();

		result.setLocation("Plymouth, MN");

		result.setHigh(new Record(new Temperature("60", "15"), new Temperature("72", "20"), "1999"));
		result.setLow(new Record(new Temperature("32", "0"), new Temperature("20", "10"), "2000"));
		
		// set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(MockedRecordsTask.class.getName());

		return result;
	}

	/**
	 * Not used by the mocked task.
	 * 
	 * @param json
	 * @throws Exception
	 */
	@Override
	protected void checkError(JSONObject json) throws Exception {
	}

	/**
	 * Calls {@link #createWeatherData(JSONObject)} method and returns its result.
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