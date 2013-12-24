package com.ptrf.android.weather;

/**
 * Data transfer object that holds the weather data.
 *
 */
public class WeatherData {

	private String location;
	private String temperature;
	private String weather;
	private String wind;
	
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

	@Override
	public String toString() {
		return String
				.format("%s [location=%s, temperature=%s, weather=%s, wind=%s]",
						super.toString(), location, temperature, weather, wind);
	}

}
