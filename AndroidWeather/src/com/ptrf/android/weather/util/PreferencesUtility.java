package com.ptrf.android.weather.util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Preferences Utility.
 * Marked as abstract to emphasize the static usage only.
 */
public abstract class PreferencesUtility {

	/**
	 * Adds new value to the comma separated list of values associated with the key.
	 * @param activity activity
	 * @param key preferences key
	 * @param value new value to add at the end of the list
	 */
    public static void addToPreferences(Activity activity, String key, String value){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String values = sharedPreferences.getString(key, "");
        if (values != "") {
        	values = values + ",";
		}
        values = values + value;
        updatePreferenceValue(sharedPreferences, key, values);                        
    }

	private static void updatePreferenceValue(SharedPreferences sharedPreferences, String key, String value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
	}
    
    /**
     * Retrieves the list of comma separated values associated with the key as an array. 
     * @param activity activity
     * @param key preferences key
     * @return list of values
     */
    public static String[] getFromPreferences(Activity activity, String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        String[] values = value.split(",");
        return values;        
    }

    /**
     * Removes value from the comma separated list of values associated with the key.
     * @param activity
     * @param key
     * @param index
     */
    public static void removeFromPreferences(Activity activity, String key, int index) {
    	String[] values = getFromPreferences(activity, key);
    	if (index >= 0 && index < values.length) {
    		StringBuilder builder = new StringBuilder();
    		for (int i = 0; i < values.length; i++) {
				if (i == index) {
					//remove element at index
					continue;
				}
				if (builder.length() != 0) {
					builder.append(",");
				}
				builder.append(values[i]);
			}
    		//update the preference value
    		SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
    		updatePreferenceValue(sharedPreferences, key, builder.toString());
		}
    }
}
