package com.ptrf.android.weather.service.mocked;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.data.CurrentConditions;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Mocked weather service task to receive mocked weather conditions.
 */
public class MockedCurrentConditionsTask extends WeatherServiceTask {

	/**
	 * Creates new instance of the task.
	 * 
	 * @param context current activity
	 */
	public MockedCurrentConditionsTask(Context context) {
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
		CurrentConditions result = new CurrentConditions();

		result.setLocation("Plymouth, MN");

		Temperature temperature = new Temperature();
		temperature.setValueC("0");
		temperature.setValueF("32");
		result.setTemperature(temperature);
		
		Temperature feelsLike = new Temperature();
		feelsLike.setValueC("0");
		feelsLike.setValueF("32");
		result.setFeelsLike(feelsLike);
		
		result.setWeather("Sunny");
		
		Wind wind = new Wind();
		wind.setDirection("NNW");
		wind.setSpeedKph("10");
		wind.setSpeedMph("5");
		result.setWind(wind);
		
		result.setHumidity("33%");
		
		// set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(MockedCurrentConditionsTask.class.getName());

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