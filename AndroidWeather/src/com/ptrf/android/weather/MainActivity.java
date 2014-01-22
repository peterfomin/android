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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ptrf.android.weather.data.Temperature;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.location.LocationService;
import com.ptrf.android.weather.service.ResultReceiver;
import com.ptrf.android.weather.service.WeatherServiceTask;
import com.ptrf.android.weather.service.WeatherServiceTaskFactory;
import com.ptrf.android.weather.util.FavoritesUtility;

/**
 * Main activity. Responsible for displaying main set of controls, 
 * calling the weather service and displaying the weather data.
 * It implements ResultReceiver interface to receive the data from the WeatherServiceTask.
 */
public class MainActivity extends Activity implements OnSharedPreferenceChangeListener, ResultReceiver {
	private static final String TAG = MainActivity.class.getName();

	/**
	 * Shared Preferences key for showLocationCoordinates setting.
	 */
	private static final String SHOW_LOCATION_COORDINATES = "showLocationCoordinates";
	
	/**
	 * Parameter key to pass the favorite location as part of the Intent created in FavoritesActivity.
	 */
	protected static final String FAVORITE_LOCATION_PARAMETER = MainActivity.class.getName()+ "favoriteLocation";
	
	//UI components
	private CheckBox checkboxUseCurrentLocation;
	private ToggleButton buttonAddToFavorites = null;
	private EditText editTextSearchString = null;
	private TextView location = null;
	private TextView weather = null;
	private ImageView weatherImage = null;
	private TextView textViewLatitude = null;
	private TextView textViewLongitude = null;

	/**
	 * Reference to the location service.
	 */
	private LocationService locationService = null;

	/**
	 * Cached list of the favorite locations used to display the proper favorite button state.
	 */
	private List<String> favoriteLocations;

	/**
	 * Called when activity is started with intent using Intent.FLAG_ACTIVITY_CLEAR_TOP.
	 * See {@link MainActivity#onListItemClick} for more details.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent intent="+ intent);

		//prefill favorite location if it's passed
		String favoriteLocation = intent.getStringExtra(FAVORITE_LOCATION_PARAMETER);
		if (favoriteLocation != null) {
			editTextSearchString.setText(favoriteLocation);
			checkboxUseCurrentLocation.setChecked(false);
		}
		
		//re-initialize favorite locations cache
		favoriteLocations = FavoritesUtility.getFavoriteLocations(this);
		
		//force data refresh
		refreshWeatherData();
	}

	/**
	 * Called when new instance of the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate savedInstanceState="+ savedInstanceState);
		
		setContentView(R.layout.main);

		//initialize location service
		locationService = new LocationService(getApplicationContext());
		
		//initialize UI components
		checkboxUseCurrentLocation = (CheckBox) findViewById(R.id.checkboxUseCurrentLocation);
		buttonAddToFavorites = (ToggleButton) findViewById(R.id.buttonAddToFavorites);
		editTextSearchString = (EditText) findViewById(R.id.editTextSearchString);
		location = (TextView) findViewById(R.id.textViewLocation);
		weather = (TextView) findViewById(R.id.textViewWeather);
		weatherImage = (ImageView) findViewById(R.id.imageViewWeather);
		textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
		textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
		
		//add OnEditorActionListener to handle return key on the text field
		editTextSearchString.setOnEditorActionListener(new SearchEditorActionListener());
		
		//Setup the Favorites Button's OnClickListener
		buttonAddToFavorites.setOnClickListener(new FavoritesButtonListener());
		
		//register listener for settings changes
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(this);
		//trigger onchange event to display the screen controls properly according to the current settings
		onSharedPreferenceChanged(preferences, SHOW_LOCATION_COORDINATES);
		
		//Setup the Checkbox's OnClickListener
		checkboxUseCurrentLocation.setOnCheckedChangeListener(new UseCurrentLocationChangeListener());
		if (checkboxUseCurrentLocation.isChecked()) {
			//start location updates to get the last known location
			locationService.startLocationUpdates();
		}

		//initialize favorite locations cache
		favoriteLocations = FavoritesUtility.getFavoriteLocations(this);

		//force data refresh
		refreshWeatherData();
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
	
	/**
	 * Initialize the contents of the Activity's standard options menu.
	 * The content of the menu is defined in /res/menu/main.xml.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Process menu options selection.
	 * This method is called whenever an item in your options menu is selected.
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

		//Return false to allow normal menu processing to proceed, true to consume it here.
		return true;
	}

	/**
	 * Called when a shared preference is changed, added, or removed.
	 */
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
				textViewLatitude.setVisibility(View.GONE);
				textViewLongitude.setVisibility(View.GONE);
				findViewById(R.id.textViewLatitudeLabel).setVisibility(View.GONE);
				findViewById(R.id.textViewLongitudeLabel).setVisibility(View.GONE);
			}
        }
    }
    
    /**
     * Refresh the weather data calling the wunderground.com weather service provider.
     */
	@SuppressLint("DefaultLocale")
	private void refreshWeatherData() {
		
		//create new task based on the provider service settings
		WeatherServiceTask task = null;
		try {
			task = WeatherServiceTaskFactory.createWeatherServiceTask(this);
		} catch (Exception e) {
			//if an exception thrown creating new task then call receiveResult to report it
			receiveResult(null, e);
			return;
		}
		
		Location deviceLocation = null;
		String enteredLocation = null;
		if (checkboxUseCurrentLocation.isChecked()) {
			//use current device location
			//get current device location from the LocationService
			deviceLocation = locationService.getCurrentLocation();
			Log.d(TAG, "location="+ deviceLocation);
			if (deviceLocation == null) {
				Toast.makeText(this, getString(R.string.msg_currentLocationNotAvailable), Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			//use location specified
			enteredLocation = editTextSearchString.getText().toString();
			if (enteredLocation == null || enteredLocation.trim().equals("")) {
				Toast.makeText(this, getString(R.string.msg_enteredLocationNotAvailable), Toast.LENGTH_LONG).show();
				return;
			}
		}
		
		//execute the task with the specified parameters
		task.execute(deviceLocation, enteredLocation);
		
		return;
	}
    
	/**
	 * Receives result from the WeatherServiceTask.
	 * @param result weather data result
	 * @param exception exception if an error occurred
	 */
	@Override
	public void receiveResult(WeatherData result, Throwable exception) {
		if (exception != null) {
			Log.e(TAG, "Failed to execute weather service task:", exception);
			Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}

		Log.v(TAG, "result="+ result);
		
		if (result != null) {
			//show favorite button if received a successful result
			buttonAddToFavorites.setVisibility(View.VISIBLE);

			//set values of the UI components based on the data received
			location.setText(result.getLocation());
			
			TextView temperatureF = (TextView) findViewById(R.id.textViewTemperatureF);
			TextView temperatureC = (TextView) findViewById(R.id.textViewTemperatureC);
			temperatureF.setText(result.getTemperature().getValueFWithUnit());
			temperatureC.setText(result.getTemperature().getValueCWithUnit());
			
			TextView feelsLikeLabel = (TextView) findViewById(R.id.textViewFeelsLikeLabel);
			TextView feelsLikeF = (TextView) findViewById(R.id.textViewFeelsLikeF);
			TextView feelsLikeC = (TextView) findViewById(R.id.textViewFeelsLikeC);
			Temperature feelsLike = result.getFeelsLike();
			if (feelsLike != null) {
				feelsLikeF.setText(feelsLike.getValueFWithUnit());
				feelsLikeC.setText(feelsLike.getValueCWithUnit());
				feelsLikeLabel.setVisibility(View.VISIBLE);
				feelsLikeF.setVisibility(View.VISIBLE);
				feelsLikeC.setVisibility(View.VISIBLE);
			} else {
				feelsLikeLabel.setVisibility(View.GONE);
				feelsLikeF.setVisibility(View.GONE);
				feelsLikeC.setVisibility(View.GONE);
			}

			weather.setText(result.getWeather());
			weatherImage.setImageDrawable(result.getWeatherImage());
			
			TextView windDirection = (TextView) findViewById(R.id.textViewWindDirection);
			TextView windSpeedMph = (TextView) findViewById(R.id.textViewWindSpeedMph);
			TextView windSpeedKph = (TextView) findViewById(R.id.textViewWindSpeedKph);
			windDirection.setText(result.getWind().getDirection());
			windSpeedMph.setText(result.getWind().getSpeedMphWithUnit());
			windSpeedKph.setText(result.getWind().getSpeedKphWithUnit());
			
			TextView humidity = (TextView) findViewById(R.id.textViewHumidity);
			humidity.setText(result.getHumidity());
			
			textViewLatitude.setText(result.getLatitude());
			textViewLongitude.setText(result.getLongitude());
			
			//set favorite button state
			buttonAddToFavorites.setChecked(isInFavorites(result.getLocation()));
			
			TextView textViewDataProvider = (TextView)findViewById(R.id.textViewDataProvider);
			textViewDataProvider.setText(result.getProvidedBy());
		}
	}
	
    /**
     * Listener for search text.
     */
	public class SearchEditorActionListener implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			refreshWeatherData();
			return true;
		}

	}
	
	/**
	 * Listener for favorite button.
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
			//refresh cached favorite locations
			favoriteLocations = FavoritesUtility.getFavoriteLocations(MainActivity.this);
		}
	}
	
	/**
	 * UseCurrentLocation checkbox state change listener.
	 */
	public class UseCurrentLocationChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			if (isChecked) {
				locationService.startLocationUpdates();
				editTextSearchString.setVisibility(View.INVISIBLE);
				editTextSearchString.setText("");
				refreshWeatherData();
			} else {
				locationService.stopLocationUpdates();
				editTextSearchString.setVisibility(View.VISIBLE);
			}
		}

	}

}
