package com.ptrf.android.weather.data;

import android.graphics.drawable.Drawable;

/**
 * Data transfer object that holds the weather data.
 */
public class WeatherData {

	private String location;
	private Temperature temperature;
	private Temperature feelsLike;
	private String weather;
	private Drawable weatherImage;
	private Wind wind;
	private String humidity;
	private String latitude;
	private String longitude;
	private String providedBy;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getProvidedBy() {
		return providedBy;
	}

	public void setProvidedBy(String providedBy) {
		this.providedBy = providedBy;
	}

	@Override
	public String toString() {
		return String
				.format("WeatherData [location=%s, temperature=%s, feelsLike=%s, weather=%s, weatherImage=%s, wind=%s, humidity=%s, latitude=%s, longitude=%s, providedBy=%s]",
						location, temperature, feelsLike, weather, weatherImage, wind, humidity, latitude, longitude, providedBy);
	}
	
}
