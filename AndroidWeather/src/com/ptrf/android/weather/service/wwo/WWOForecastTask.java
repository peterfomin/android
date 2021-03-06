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
import com.ptrf.android.weather.data.DailyForecast;
import com.ptrf.android.weather.data.Wind;
import com.ptrf.android.weather.util.ImageUtility;

/**
 * Weather service task to receive forecast from api.worldweatheronline.com.
 */
public class WWOForecastTask extends WWOWeatherServiceTask {

	/**
	 * Service URL.
	 */
	private static final String URL = "http://api.worldweatheronline.com/premium/v1/weather.ashx?key=%s&num_of_days=%s&q=%s&includelocation=yes&format=json";

	/**
	 * Creates new instance of the task.
	 * @param context current activity
	 */
	public WWOForecastTask(Context context) {
		super(context);
	}
	
	/**
	 * Returns the service request url for the service call.
	 */
	@SuppressLint("DefaultLocale")
	@Override
	protected String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception {
		int forecastDays = 5;
		//replace placeholders in the URL with key and query
		return String.format(URL, getServiceKey(), forecastDays, getQuery(deviceLocation, enteredLocation));
	}
	
	/**
	 * Returns an instance of Forecast extension of WeatherData retrieved from the JSON object.
	 */
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
				DailyForecast weatherForecast = createWeatherForecast(dailyForecast);
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
	private DailyForecast createWeatherForecast(JSONObject dailyForecast) throws JSONException {
		DailyForecast forecast = new DailyForecast();
		
		String date = dailyForecast.getString("date");
		//convert date to the short day name
		String day = convertToDayName(date);

		//if conversion was successful then day will not be null
		//otherwise use original date value
		forecast.setDay(day == null ? date : day);
		
		Temperature temperatureLow = new Temperature();
		temperatureLow.setValueC(dailyForecast.getString("mintempC"));
		temperatureLow.setValueF(dailyForecast.getString("mintempF"));
		forecast.setTemperatureLow(temperatureLow);
		
		Temperature temperatureHigh = new Temperature();
		temperatureHigh.setValueC(dailyForecast.getString("maxtempC"));
		temperatureHigh.setValueF(dailyForecast.getString("maxtempF"));
		forecast.setTemperatureHigh(temperatureHigh);
		
		JSONObject hourlyForecast = dailyForecast.getJSONArray("hourly").getJSONObject(0);
		
		forecast.setWeather(getFirstArrayValue(hourlyForecast.getJSONArray("weatherDesc"), "value"));
		String urlAddress = getFirstArrayValue(hourlyForecast.getJSONArray("weatherIconUrl"), "value");
		if (urlAddress != null && urlAddress.trim().length() > 0) {
			forecast.setWeatherImage(ImageUtility.createImageFromURL(urlAddress));
		}
		
		Wind wind = new Wind();
		wind.setDirection(hourlyForecast.getString("winddir16Point"));
		wind.setSpeedKph(hourlyForecast.getString("windspeedKmph"));
		wind.setSpeedMph(hourlyForecast.getString("windspeedMiles"));
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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