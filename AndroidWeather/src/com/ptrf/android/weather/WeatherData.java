package com.ptrf.android.weather;

/**
 * Data transfer object that holds the weather data.
 */
public class WeatherData {

	private String location;
	private String temperature;
	private String weather;
	private String wind;
	private String latitude;
	private String longitude;
	private String providedBy;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
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
				.format("WeatherData [location=%s, temperature=%s, weather=%s, wind=%s, latitude=%s, longitude=%s, providedBy=%s]",
						location, temperature, weather, wind, latitude, longitude, providedBy);
	}

}
