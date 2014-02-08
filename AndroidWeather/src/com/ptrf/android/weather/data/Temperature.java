package com.ptrf.android.weather.data;

/**
 * Temperature data transfer object.
 *
 */
public class Temperature {
	/**
	 * Constant representing the degree character.
	 */
	private static final char DEGREE = '\u00B0';
	
	private String valueF;
	private String valueC;
	
	/**
	 * Creates an instance of the Temperature.
	 */
	public Temperature() {
	}
	
	/**
	 * Creates an instance of the Temperature.
	 * @param valueF
	 * @param valueC
	 */
	public Temperature(String valueF, String valueC) {
		this.valueF = valueF;
		this.valueC = valueC;
	}

	public String getValueF() {
		return valueF;
	}

	public String getValueFWithUnit() {
		return getValueF() + DEGREE +"F";
	}
	
	public void setValueF(String valueF) {
		this.valueF = valueF;
	}

	public String getValueC() {
		return valueC;
	}
	
	public String getValueCWithUnit() {
		return getValueC() + DEGREE +"C";
	}

	public void setValueC(String valueC) {
		this.valueC = valueC;
	}

	@Override
	public String toString() {
		return String.format("Temperature [valueF=%s, valueC=%s]", valueF, valueC);
	}

}
