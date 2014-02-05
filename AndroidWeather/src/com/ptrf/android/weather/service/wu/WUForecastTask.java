package com.ptrf.android.weather.service.wu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.data.Forecast;
import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.WeatherForecast;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.util.ImageUtility;

/**
 * Weather service task to receive forecast from api.wunderground.com.
 */
public class WUForecastTask extends WUWeatherServiceTask {

	private static final String URL = "http://api.wunderground.com/api/%s/forecast/q/%s.json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WUForecastTask(Context context) {
		super(context);
	}

	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		//replace placeholders in the URL with key and query
		return String.format(URL, getServiceKey(), getQuery(deviceLocation, enteredLocation));
	}
	
	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		Forecast result = new Forecast();
		
		//set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(getString(R.string.msg_wuServiceDataProvidedBy));

		JSONObject forecast = json.getJSONObject("forecast");
		JSONObject simpleForecast = forecast.getJSONObject("simpleforecast");
		JSONArray forecastArray = simpleForecast.getJSONArray("forecastday");
		
		if (forecastArray != null) {
			for (int i = 0; i < forecastArray.length(); i++) {
				JSONObject dailyForecast = forecastArray.getJSONObject(i);
				WeatherForecast weatherForecast = createWeatherForecast(dailyForecast);
				result.add(weatherForecast);
			}
		}
		
		return result;
	}

	/**
	 * Returns filled WeatherForecast instance based on the dailyForecast json element.
	 * @param dailyForecast
	 * @return WeatherForecast instance
	 * @throws JSONException
	 */
	private WeatherForecast createWeatherForecast(JSONObject dailyForecast) throws JSONException {
		WeatherForecast forecast = new WeatherForecast();
		
		JSONObject date = dailyForecast.getJSONObject("date");
		
		forecast.setDay(date.getString("weekday_short"));
		forecast.setTemperatureLow(createTemperature(dailyForecast, "low"));
		forecast.setTemperatureHigh(createTemperature(dailyForecast, "high"));
		
		forecast.setWeather(dailyForecast.getString("conditions"));
		String urlAddress = dailyForecast.getString("icon_url");
		if (urlAddress != null && urlAddress.trim().length() > 0) {
			forecast.setWeatherImage(ImageUtility.createImageFromURL(urlAddress));
		}
		
		forecast.setWind(createWind(dailyForecast, "maxwind"));
		
		forecast.setPrecipitation(dailyForecast.getString("pop") + "%");
		
		return forecast;
	}

	/**
	 * Returns instance of Wind created based on the wind data from dailyForecast json element.
	 * @param dailyForecast
	 * @param name json element name
	 * @return instance of Wind
	 * @throws JSONException
	 */
	private Wind createWind(JSONObject dailyForecast, String name) throws JSONException {
		JSONObject jsonWind = dailyForecast.getJSONObject(name);
		Wind wind = new Wind();
		wind.setDirection(jsonWind.getString("dir"));
		wind.setSpeedKph(jsonWind.getString("kph"));
		wind.setSpeedMph(jsonWind.getString("mph"));
		return wind;
	}

	/**
	 * Returns Temperature instance created based on the temperature data from dailyForecast json element.
	 * @param dailyForecast
	 * @param name json element name
	 * @return Temperature instance 
	 * @throws JSONException
	 */
	private Temperature createTemperature(JSONObject dailyForecast, String name) throws JSONException {
		JSONObject jsonTemperature = dailyForecast.getJSONObject(name);
		Temperature temperature = new Temperature();
		temperature.setValueC(jsonTemperature.getString("celsius"));
		temperature.setValueF(jsonTemperature.getString("fahrenheit"));
		return temperature;
	}
}