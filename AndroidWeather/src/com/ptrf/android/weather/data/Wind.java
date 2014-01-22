package com.ptrf.android.weather.data;

/**
 * Wind data transfer object.
 *
 */
public class Wind {

	private String direction;
	private String speedMph;
	private String speedKph;
	
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getSpeedMph() {
		return speedMph;
	}

	public String getSpeedMphWithUnit() {
		return getSpeedMph() + "MPH" ;
	}
	
	public void setSpeedMph(String speedMph) {
		this.speedMph = speedMph;
	}
	
	public String getSpeedKph() {
		return speedKph;
	}

	public String getSpeedKphWithUnit() {
		return getSpeedKph() + "KMH";
	}
	
	public void setSpeedKph(String speedKph) {
		this.speedKph = speedKph;
	}
	
	@Override
	public String toString() {
		return String.format("Wind [direction=%s, speedMph=%s, speedKph=%s]",
				direction, speedMph, speedKph);
	}
	
}
