package com.ptrf.android.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.ptrf.android.weather.location.LocationService;
import com.ptrf.android.weather.service.CurrentConditionsTask;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Main activity.
 */
public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getName();
	
	private CheckBox checkboxUseCurrentLocation;
	private Button buttonGetData = null;
	private EditText editTextSearchString = null;
	private TextView location = null;
	private TextView temperature = null;
	private TextView weather = null;
	private TextView wind = null;

	private LocationService locationService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkboxUseCurrentLocation = (CheckBox) findViewById(R.id.checkboxUseCurrentLocation);
		buttonGetData = (Button) findViewById(R.id.buttonGetData);
		editTextSearchString = (EditText) findViewById(R.id.editTextSearchString);
		location = (TextView) findViewById(R.id.textViewLocation);
		temperature = (TextView) findViewById(R.id.textViewTemperature);
		weather = (TextView) findViewById(R.id.textViewWeather);
		wind = (TextView) findViewById(R.id.textViewWind);

		//initialize location service
		locationService = new LocationService(getApplicationContext());

		//Setup the Button's OnClickListener
		buttonGetData.setOnClickListener(new DataButtonListener());

		//Setup the Checkbox's OnClickListener
		UseCurrentLocationChangeListener useCurrentLocationChangeListener = new UseCurrentLocationChangeListener();
		checkboxUseCurrentLocation.setOnCheckedChangeListener(useCurrentLocationChangeListener);
		//trigger onchange event
		useCurrentLocationChangeListener.onCheckedChanged(checkboxUseCurrentLocation, checkboxUseCurrentLocation.isChecked());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			}

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
