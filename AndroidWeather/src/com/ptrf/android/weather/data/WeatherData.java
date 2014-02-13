package com.ptrf.android.weather.data;

/**
 * Base abstract data transfer object that holds the weather data.
 * Marked as abstract to prevent instantiation of this class. 
 */
public abstract class WeatherData {

	private String location;
	private String latitude;
	private String longitude;
	private String providedBy;
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
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
				.format("WeatherData [location=%s, latitude=%s, longitude=%s, providedBy=%s]", location, latitude, longitude, providedBy);
	}
	
}
