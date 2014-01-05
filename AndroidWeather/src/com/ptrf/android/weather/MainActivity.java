package com.ptrf.android.weather;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ptrf.android.weather.location.LocationService;
import com.ptrf.android.weather.service.CurrentConditionsTask;
import com.ptrf.android.weather.service.WeatherServiceTask;
import com.ptrf.android.weather.util.FavoritesUtility;

/**
 * Main activity.
 */
public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	private static final String TAG = MainActivity.class.getName();

	private static final String SHOW_LOCATION_COORDINATES = "showLocationCoordinates";
	protected static final String FAVORITE_LOCATION_PARAMETER = MainActivity.class.getName()+ "favoriteLocation";
	
	private CheckBox checkboxUseCurrentLocation;
	private Button buttonGetData = null;
	private ToggleButton buttonAddToFavorites = null;
	private EditText editTextSearchString = null;
	private TextView location = null;
	private TextView temperature = null;
	private TextView weather = null;
	private TextView wind = null;
	private TextView textViewLatitude = null;
	private TextView textViewLongitude = null;

	private LocationService locationService = null;

	private List<String> favoriteLocations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//initialize favorite locations
		favoriteLocations = FavoritesUtility.getFavoriteLocations(this);
		
		checkboxUseCurrentLocation = (CheckBox) findViewById(R.id.checkboxUseCurrentLocation);
		buttonGetData = (Button) findViewById(R.id.buttonGetData);
		buttonAddToFavorites = (ToggleButton) findViewById(R.id.buttonAddToFavorites);
		editTextSearchString = (EditText) findViewById(R.id.editTextSearchString);
		location = (TextView) findViewById(R.id.textViewLocation);
		temperature = (TextView) findViewById(R.id.textViewTemperature);
		weather = (TextView) findViewById(R.id.textViewWeather);
		wind = (TextView) findViewById(R.id.textViewWind);
		textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
		textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);

		//prefill favorite location if it's passed
		Intent intent = getIntent();
		String favoriteLocation = intent.getStringExtra(FAVORITE_LOCATION_PARAMETER);
		if (favoriteLocation != null) {
			editTextSearchString.setText(favoriteLocation);
			checkboxUseCurrentLocation.setChecked(false);
		}
		
		//initialize location service
		locationService = new LocationService(getApplicationContext());

		//Setup the Button's OnClickListener
		DataButtonListener dataButtonListener = new DataButtonListener();
		buttonGetData.setOnClickListener(dataButtonListener);

		//set initial state of the favorite button
		String currentLocationValue = location.getText().toString();
		if (currentLocationValue == null || currentLocationValue.trim().equals("")) {
			//hide favorite button
			buttonAddToFavorites.setVisibility(View.INVISIBLE);
		} else {
			//unhide favorite button
			buttonAddToFavorites.setVisibility(View.VISIBLE);
		}
		
		//Setup the Favorites Button's OnClickListener
		buttonAddToFavorites.setOnClickListener(new FavoritesButtonListener());
		
		//register listener for settings changes
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(this);
		//trigger onchange event to display the screen controls properly according to the current settings
		onSharedPreferenceChanged(preferences, SHOW_LOCATION_COORDINATES);
		
		//Setup the Checkbox's OnClickListener
		UseCurrentLocationChangeListener useCurrentLocationChangeListener = new UseCurrentLocationChangeListener();
		checkboxUseCurrentLocation.setOnCheckedChangeListener(useCurrentLocationChangeListener);
		//trigger onchange event to display the screen controls properly according to the current settings
		useCurrentLocationChangeListener.onCheckedChanged(checkboxUseCurrentLocation, checkboxUseCurrentLocation.isChecked());
		
		if (favoriteLocation != null) {
			//if we had a favorite location then force refresh
			dataButtonListener.onClick(buttonGetData);
		}
	}

	/**
	 * Returns true if location passed is in the list of favorite locations.
	 * @param location
	 * @return
	 */
	private boolean isInFavorites(String location) {
		for (String favorite : favoriteLocations) {
			if (favorite.equals(location)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Process menu options selection.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.favorites:
			startActivity(new Intent(this, FavoritesActivity.class));
			break;
		}

		return true;
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
    	Log.v(TAG, "changed setting key="+ key +" values="+ sharedPreferences.getAll());
    	
        if (SHOW_LOCATION_COORDINATES.equals(key)) {
            boolean showLocationCoordinates = sharedPreferences.getBoolean(key, false);
            Log.d(TAG, "changed setting key="+ key +" value="+ showLocationCoordinates);
            if (showLocationCoordinates) {
				textViewLatitude.setVisibility(View.VISIBLE);
				textViewLongitude.setVisibility(View.VISIBLE);
				findViewById(R.id.textViewLatitudeLabel).setVisibility(View.VISIBLE);
				findViewById(R.id.textViewLongitudeLabel).setVisibility(View.VISIBLE);
			} else {
				textViewLatitude.setVisibility(View.INVISIBLE);
				textViewLongitude.setVisibility(View.INVISIBLE);
				findViewById(R.id.textViewLatitudeLabel).setVisibility(View.INVISIBLE);
				findViewById(R.id.textViewLongitudeLabel).setVisibility(View.INVISIBLE);
//				textViewLatitude.setText("");
//				textViewLongitude.setText("");
			}
        }
    }
	
	/**
	 * Listener for get data button.
	 *
	 */
	private class DataButtonListener implements OnClickListener {

		@SuppressLint("DefaultLocale")
		@Override
		public void onClick(View v) {
			//Get the weather data
			WeatherServiceTask task = new CurrentConditionsTask();
			String url = "http://api.wunderground.com/api/%s/conditions/q/%s.json";
			String key = "878810199c30c035";

			String query = null;
			if (checkboxUseCurrentLocation.isChecked()) {
				Location location = locationService.getCurrentLocation();
				Log.d(TAG, "location="+ location);
				if (location == null) {
					Toast.makeText(MainActivity.this, "Current location data is not available. Please enable location setting.", Toast.LENGTH_LONG).show();
					return;
				}
				query = String.format("%.6f,%.6f", location.getLatitude(), location.getLongitude());
			} else {
				query = editTextSearchString.getText().toString();
				if (query == null || query.trim().equals("")) {
					Toast.makeText(MainActivity.this, "Please specify the location or use Current location setting.", Toast.LENGTH_LONG).show();
					return;
				}
				//UW service requires spaces to be replaced with '_'
				query = query.replaceAll(" +", "_");
			}
			
			buttonGetData.setEnabled(false);

			//Executes the task with the specified parameters
			task.execute(url, key, query);
			
			WeatherData result = null;
			try {
				//Waits if necessary for the computation to complete, and then retrieves its result.
				result = task.get();
				if (task.getException() != null) {
					throw new RuntimeException(task.getException().getMessage(), task.getException());
				}
				//show favorite button if receive a successful result
				buttonAddToFavorites.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Log.e(TAG, "Failed to execute weather service task:", e);
				Toast.makeText(MainActivity.this, "Error occured: "+ e.getMessage(), Toast.LENGTH_LONG).show();
			} finally {
				buttonGetData.setEnabled(true);
			}
			
			if (result != null) {
				location.setText(result.getLocation());
				temperature.setText(result.getTemperature());
				weather.setText(result.getWeather());
				wind.setText(result.getWind());
				textViewLatitude.setText(result.getLatitude());
				textViewLongitude.setText(result.getLongitude());
				
				//set favorite button state
				buttonAddToFavorites.setChecked(isInFavorites(result.getLocation()));
			}

		}

	}
	
	/**
	 * Listener for favorites button.
	 *
	 */
	private class FavoritesButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String favorite = location.getText().toString();
			CompoundButton button = (CompoundButton) v;
			if (button.isChecked()) {
				//Add to favorites
				FavoritesUtility.add(MainActivity.this, favorite);
			} else {
				//remove from favorites
				FavoritesUtility.remove(MainActivity.this, favorite);
			}
			favoriteLocations = FavoritesUtility.getFavoriteLocations(MainActivity.this);
		}
	}
	
	/**
	 * UseCurrentLocation checkbox state change listener.
	 *
	 */
	public class UseCurrentLocationChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			if (isChecked) {
				locationService.startLocationUpdates();
				editTextSearchString.setVisibility(View.INVISIBLE);
				editTextSearchString.setText("");
			} else {
				locationService.stopLocationUpdates();
				editTextSearchString.setVisibility(View.VISIBLE);
			}
		}

	}

}
