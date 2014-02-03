package com.ptrf.android.weather.service.wwo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
 * Weather service task to receive forecast from api.worldweatheronline.com.
 */
public class WWOForecastTask extends WWOWeatherServiceTask {

	private static final String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?key=%s&num_of_days=%s&q=%s&includelocation=yes&format=json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WWOForecastTask(Context context) {
		super(context);
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		int forecastDays = 5;
		//replace placeholders in the URL with key and query
		return String.format(URL, getServiceKey(), forecastDays, getQuery(deviceLocation, enteredLocation));
	}
	
	@Override
	protected WeatherData createWeatherData(JSONObject json) throws JSONException {
		Forecast result = new Forecast();
		
		//set Service Data Provided By Message specific to this implementation
		result.setProvidedBy(getString(R.string.msg_wwoServiceDataProvidedBy));
		
		JSONObject data = json.getJSONObject("data");
		JSONArray forecastArray = data.getJSONArray("weather");
		
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
		
		String date = dailyForecast.getString("date");
		//convert date to the short day name
		String day = convertToDayName(date);

		//if conversion was successful then day will not be null
		//otherwise use original date value
		forecast.setDay(day == null ? date : day);
		
		Temperature temperatureLow = new Temperature();
		temperatureLow.setValueC(dailyForecast.getString("tempMinC"));
		temperatureLow.setValueF(dailyForecast.getString("tempMinF"));
		forecast.setTemperatureLow(temperatureLow);
		
		Temperature temperatureHigh = new Temperature();
		temperatureHigh.setValueC(dailyForecast.getString("tempMaxC"));
		temperatureHigh.setValueF(dailyForecast.getString("tempMaxF"));
		forecast.setTemperatureHigh(temperatureHigh);
		
		forecast.setWeather(getFirstArrayValue(dailyForecast.getJSONArray("weatherDesc"), "value"));
		String urlAddress = getFirstArrayValue(dailyForecast.getJSONArray("weatherIconUrl"), "value");
		if (urlAddress != null && urlAddress.trim().length() > 0) {
			forecast.setWeatherImage(ImageUtility.createImageFromURL(urlAddress));
		}
		
		Wind wind = new Wind();
		wind.setDirection(dailyForecast.getString("winddir16Point"));
		wind.setSpeedKph(dailyForecast.getString("windspeedKmph"));
		wind.setSpeedMph(dailyForecast.getString("windspeedMiles"));
		forecast.setWind(wind);
		
		return forecast;
	}

	/**
	 * Converts date in format "2014-02-01" to short day name "Sun".
	 * @param date
	 * @return day name if conversion was successful
	 */
	@SuppressLint("SimpleDateFormat")
	private String convertToDayName(String date) {
		String day = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM");
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
		try {
			Date d = dateFormat.parse(date);
			day = dayFormat.format(d);
		} catch (ParseException e) {
			//do nothing, return null
		}
		return day;
	}

}