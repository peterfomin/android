package com.ptrf.android.weather.service.mocked;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.data.Forecast;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.WeatherForecast;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Mocked weather service task to received mocked weather conditions.
 */
public class MockedForecastTask extends WeatherServiceTask {

	/**
	 * Creates new instance of the task.
	 * 
	 * @param context current activity
	 */
	public MockedForecastTask(Context context) {
		super(context);
	}

	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		return null;
	}

	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		Forecast result = new Forecast();

		result.setLocation("Plymouth, MN");
		// set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(MockedForecastTask.class.getName());

		WeatherForecast forecast = new WeatherForecast();
		result.add(forecast);

		forecast.setDay("Mon");
		
		Temperature temperatureLow = new Temperature();
		temperatureLow.setValueC("0");
		temperatureLow.setValueF("32");
		forecast.setTemperatureLow(temperatureLow);
		
		Temperature temperatureHigh = new Temperature();
		temperatureHigh.setValueC("5");
		temperatureHigh.setValueF("41");
		forecast.setTemperatureHigh(temperatureHigh);
		
		forecast.setWeather("Sunny");
		
		Wind wind = new Wind();
		wind.setDirection("NNW");
		wind.setSpeedKph("10");
		wind.setSpeedMph("5");
		forecast.setWind(wind);
		
		forecast.setPrecipitation("33%");
		
		return result;
	}

	/**
	 * Checks for error json element and throws exception if found.
	 * 
	 * @param json
	 * @throws Exception
	 */
	@Override
	protected void checkError(JSONObject json) throws Exception {
	}

	/**
	 * No processing, only calling {@link #createWeatherData(JSONObject)} method.
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