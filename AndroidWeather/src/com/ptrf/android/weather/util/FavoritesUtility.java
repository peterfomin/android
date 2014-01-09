package com.ptrf.android.weather.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Favorites utility that encapsulates favorites read/write functionality.
 * Marked as abstract to emphasize the static usage only i.e. can't be instantiated.
 */
public abstract class FavoritesUtility {
	private static final String TAG = FavoritesUtility.class.toString();

	/**
	 * JSON KEY for favorites array.
	 */
	private static final String FAVORITES = "favorites";
	
	/**
	 * Name of the favorite file.
	 */
    private static final String FAVORITES_FILE_NAME = "favorites.json";
	
    /**
     * Reads favorite locations.
     * @param context context
     * @return list of favorite locations
     */
	public static List<String> getFavoriteLocations(Context context) {
		File file = new File(context.getFilesDir(), FAVORITES_FILE_NAME);
		List<String> locations = new ArrayList<String>();
		try {
			String data = StorageUtility.read(file);
			JSONObject json = new JSONObject(data);
			JSONArray favorites = json.getJSONArray(FAVORITES);
			for (int i = 0; i < favorites.length(); i++) {
				String location = favorites.getString(i);
				locations.add(location);
			}
		} catch (FileNotFoundException e) {
			//ignore this one - no favorites stored
		} catch (Exception e) {
			Log.e(TAG, "Failed to obtain favorites for file="+ file, e);
		}
		return locations;
	}

	/**
	 * Adds new favorite to the list.
	 * @param favorite new favorite location
	 */
	public static void add(Context context, String favorite) {

		File file = new File(context.getFilesDir(), FAVORITES_FILE_NAME);
		String data = null;
		//read existing favorites
		try {
			data = StorageUtility.read(file);
		} catch (FileNotFoundException e) {
			//ignore this one - no favorites stored
		} catch (Exception e) {
			Log.e(TAG, "Failed to obtain favorites for file="+ file, e);
		}
		
		try {
			JSONObject json = null;
			if (data == null) {
				//no favorites yet, create new json array
				json = new JSONObject();
				JSONArray favorites = new JSONArray();
				json.put(FAVORITES, favorites);
			} else {
				json = new JSONObject(data);
			}
			//get the list of favorite locations
			JSONArray favorites = json.getJSONArray(FAVORITES);
			//add new favorite to the end of the list
			favorites.put(favorite);
			//save changes
			StorageUtility.save(file, json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Failed to add new favorite location="+ favorite, e);
		}
	}

	/**
	 * Removes favorites file.
	 * @param context
	 */
	public static void removeAllFavoriteLocations(Context context) {
		File file = new File(context.getFilesDir(), FAVORITES_FILE_NAME);
		file.delete();
	}
	
	/**
	 * Removes favorite location from the list of favorite locations.
	 * @param context
	 * @param locationToRemove
	 */
	public static void remove(Context context, String locationToRemove) {
		File file = new File(context.getFilesDir(), FAVORITES_FILE_NAME);
		try {
			String data = StorageUtility.read(file);
			JSONObject json = new JSONObject(data);
			//current list of favorite locations
			JSONArray favorites = json.getJSONArray(FAVORITES);
			//new list of favorite locations, w/out locationToRemove
			JSONArray newFavorites = new JSONArray();
			for (int i = 0; i < favorites.length(); i++) {
				String location = favorites.getString(i);
				if (! location.equals(locationToRemove)) {
					newFavorites.put(location);
				}
			}
			//set new favorite locations in JSON
			json.put(FAVORITES, newFavorites);
			//save changes
			StorageUtility.save(file, json.toString());
		} catch (FileNotFoundException e) {
			//ignore this one - no favorites stored
		} catch (Exception e) {
			Log.e(TAG, "Failed to remove favorite for file="+ file, e);
		}
		
	}
}
