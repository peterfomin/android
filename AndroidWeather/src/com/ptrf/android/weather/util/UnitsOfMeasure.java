package com.ptrf.android.weather.util;

import android.content.SharedPreferences;

/**
 * Enumeration that defines units of measure constants.
 *
 */
public enum UnitsOfMeasure {
	Both, English, Metric;
	
    /**
     * Returns true if English units should be shown.
     * @param configured units
     * @return true if English units should be shown.
     */
    public static boolean showEnglishUnits(UnitsOfMeasure configured) {
    	//return true is English units should be shown
		return (configured == UnitsOfMeasure.English || configured == UnitsOfMeasure.Both);
	}

    /**
     * Returns true if English units should be shown.
     * @param sharedPreferences
     * @return true if English units should be shown.
     */
    public static boolean showEnglishUnits(SharedPreferences sharedPreferences) {
    	//get configured units
    	UnitsOfMeasure configured = getConfiguredUnits(sharedPreferences);
    	//return true is English units should be shown
		return (configured == UnitsOfMeasure.English || configured == UnitsOfMeasure.Both);
	}
    
    /**
     * Returns true if Metric units should be shown.
     * @param configured units
     * @return true if Metric units should be shown.
     */
    public static boolean showMetricUnits(UnitsOfMeasure configured) {
    	//return true is Metric units should be shown
		return (configured == UnitsOfMeasure.Metric || configured == UnitsOfMeasure.Both);
	}

    /**
     * Returns true if Metric units should be shown.
     * @param sharedPreferences
     * @return true if Metric units should be shown.
     */
    public static boolean showMetricUnits(SharedPreferences sharedPreferences) {
    	//get configured units
    	UnitsOfMeasure configured = getConfiguredUnits(sharedPreferences);
    	//return true is Metric units should be shown
		return (configured == UnitsOfMeasure.Metric || configured == UnitsOfMeasure.Both);
	}
	
	/**
	 * Shared Preferences key for showLocationCoordinates setting.
	 */
	public static final String UNITS_OF_MEASURE_KEY = "unitsOfMeasure";
	
    /**
     * Returns currently configured units of measure.
     * @param sharedPreferences shared preferences
     * @return currently configured units of measure.
     */
	private static UnitsOfMeasure getConfiguredUnits(SharedPreferences sharedPreferences) {
		//get currently configured value for units of measure
    	String unitsConfigured = sharedPreferences.getString(UNITS_OF_MEASURE_KEY, null);

    	UnitsOfMeasure units = null;
		try {
			//get enum value corresponding to the configuration value
			units = UnitsOfMeasure.valueOf(unitsConfigured);
		} catch (Exception e) {
			//if the default value is not set or 
			//set incorrectly due to older version not longer supported then set default manually
			units = UnitsOfMeasure.Both;
		}
		return units;
	}
}
