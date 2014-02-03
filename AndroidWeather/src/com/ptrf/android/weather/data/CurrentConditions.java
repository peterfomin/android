package com.ptrf.android.weather.data;

import android.graphics.drawable.Drawable;

/**
 * Current conditions weather data.
 */
public class CurrentConditions extends WeatherData {

	private Temperature temperature;
	private Temperature feelsLike;
	private String weather;
	private Drawable weatherImage;
	private Wind wind;
	private String humidity;
	
	public Temperature getTemperature() {
		return temperature;
	}

	public void setTemperature(Temperature temperature) {
		this.temperature = temperature;
	}

	public Temperature getFeelsLike() {
		return feelsLike;
	}

	public void setFeelsLike(Temperature feelsLike) {
		this.feelsLike = feelsLike;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public Drawable getWeatherImage() {
		return weatherImage;
	}

	public void setWeatherImage(Drawable weatherImage) {
		this.weatherImage = weatherImage;
	}

	public Wind getWind() {
		return wind;
	}

	public void setWind(Wind wind) {
		this.wind = wind;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	@Override
	public String toString() {
		return String
				.format("CurrentWeather [temperature=%s, feelsLike=%s, weather=%s, weatherImage=%s, wind=%s, humidity=%s, super=%s]",
						temperature, feelsLike, weather, weatherImage, wind, humidity, super.toString());
	}

}
