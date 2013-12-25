package com.ptrf.android.weather;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ptrf.android.weather.location.LocationService;
import com.ptrf.android.weather.service.CurrentConditionsTask;
import com.ptrf.android.weather.service.WeatherServiceTask;

/**
 * Main activity.
 */
public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getName();
	
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

		buttonGetData = (Button) findViewById(R.id.buttonGetData);
		editTextSearchString = (EditText) findViewById(R.id.editTextSearchString);
		location = (TextView) findViewById(R.id.textViewLocation);
		temperature = (TextView) findViewById(R.id.textViewTemperature);
		weather = (TextView) findViewById(R.id.textViewWeather);
		wind = (TextView) findViewById(R.id.textViewWind);

		//Setup the Button's OnClickListener
		buttonGetData.setOnClickListener(new DataButtonListener());

		locationService = new LocationService(getApplicationContext());
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

		@Override
		public void onClick(View v) {
			//Get the weather data
			WeatherServiceTask task = new CurrentConditionsTask();
			String url = "http://api.wunderground.com/api/%s/conditions/q/%s.json";
			String key = "878810199c30c035";

			buttonGetData.setEnabled(false);
			//Executes the task with the specified parameters
			task.execute(url, key, editTextSearchString.getText().toString());
			
			WeatherData result = null;
			try {
				//Waits if necessary for the computation to complete, and then retrieves its result.
				result = task.get();
			} catch (Exception e) {
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
			
			Location location = locationService.getCurrentLocation();
			Log.d(TAG, "location="+ location);
		}

	}

}
