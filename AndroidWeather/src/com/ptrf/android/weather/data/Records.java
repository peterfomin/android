package com.ptrf.android.weather.data;

/**
 * Temperature records data transfer object.
 */
public class Records extends WeatherData {
	private Record high;
	private Record low;
	
	public Record getHigh() {
		return high;
	}
	
	public void setHigh(Record high) {
		this.high = high;
	}

	public Record getLow() {
		return low;
	}

	public void setLow(Record low) {
		this.low = low;
	}

	@Override
	public String toString() {
		return String.format("Records [high=%s, low=%s]", high, low);
	}
	
}
