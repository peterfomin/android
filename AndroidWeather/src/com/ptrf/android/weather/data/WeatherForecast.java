package com.ptrf.android.weather.data;

import android.graphics.drawable.Drawable;

/**
 * Weather forecast data transfer object.
 */
public class WeatherForecast {

	private String day;
	private Temperature temperatureLow;
	private Temperature temperatureHigh;
	private String weather;
	private Drawable weatherImage;
	private Wind wind;
	private String precipitation;
	
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Temperature getTemperatureLow() {
		return temperatureLow;
	}
	
	public void setTemperatureLow(Temperature temperatureLow) {
		this.temperatureLow = temperatureLow;
	}
	
	public Temperature getTemperatureHigh() {
		return temperatureHigh;
	}
	
	public void setTemperatureHigh(Temperature temperatureHigh) {
		this.temperatureHigh = temperatureHigh;
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

	public String getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}

	@Override
	public String toString() {
		return String
				.format("WeatherForecast [day=%s temperatureLow=%s, temperatureHigh=%s, weather=%s, weatherImage=%s, wind=%s, precipitation=%s, super=%s]",
						day, temperatureLow, temperatureHigh, weather, weatherImage, wind, precipitation, super.toString());
	}
	
}
