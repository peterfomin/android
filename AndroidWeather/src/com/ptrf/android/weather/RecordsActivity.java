package com.ptrf.android.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ptrf.android.weather.data.Record;
import com.ptrf.android.weather.data.Records;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.service.ResultReceiver;
import com.ptrf.android.weather.service.ServiceProvider;
import com.ptrf.android.weather.service.ServiceProviderUtility;
import com.ptrf.android.weather.service.WeatherServiceTask;
import com.ptrf.android.weather.service.WeatherServiceTaskFactory;
import com.ptrf.android.weather.util.UnitsOfMeasure;

/**
 * Records activity that shows weather records data.
 */
public class RecordsActivity extends Activity implements ResultReceiver {
	private static final String TAG = RecordsActivity.class.toString();

	/**
	 * Location parameter passed from the main activity.
	 */
	protected static final String LOCATION_PARAMETER = "location";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set activity layout
		setContentView(R.layout.records);
		
		//prefill location if it's passed
		String favoriteLocation = getIntent().getStringExtra(LOCATION_PARAMETER);
		if (favoriteLocation != null) {
			TextView location = (TextView) findViewById(R.id.location);
			location.setText(favoriteLocation);
		}
		
		//force data refresh
		refreshWeatherData();
	}

    /**
     * Refresh the weather data using the selected weather service provider.
     */
	private void refreshWeatherData() {
		
		WeatherServiceTask task = null;
		try {
			//get the service provider associated with the current provider selection
			ServiceProvider serviceProvider = ServiceProviderUtility.getServiceProvider(this);
			//create new records task
			task = WeatherServiceTaskFactory.createWeatherServiceTask(this, serviceProvider.getRecordsTaskClass());
		} catch (Exception e) {
			//if an exception thrown creating new task then call receiveResult to report it
			receiveResult(null, e);
			return;
		}
		
		//get the location field
		TextView location = (TextView) findViewById(R.id.location);
		
		//execute the task with the specified parameters
		task.execute(null, location.getText());
	}
	
	/**
	 * Receives result from the WeatherServiceTask.
	 * @param data weather data result
	 * @param exception exception if an error occurred
	 */
	@Override
	public void receiveResult(WeatherData data, Throwable exception) {
		if (exception != null) {
			Log.e(TAG, "Failed to execute weather service task:", exception);
			Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}

		Log.v(TAG, "result=" + data);

		if (data != null) {
			//expect current conditions data
			Records result = (Records) data;
			
			//set temp low fields
			TextView lowNormalF = (TextView) findViewById(R.id.lowNormalF);
			TextView lowNormalC = (TextView) findViewById(R.id.lowNormalC);
			Record low = result.getLow();
			lowNormalF.setText(low.getNormal().getValueFWithUnit());
			lowNormalC.setText(low.getNormal().getValueCWithUnit());
			
			TextView lowRecordF = (TextView) findViewById(R.id.lowRecordF);
			TextView lowRecordC = (TextView) findViewById(R.id.lowRecordC);
			lowRecordF.setText(low.getRecord().getValueFWithUnit());
			lowRecordC.setText(low.getRecord().getValueCWithUnit());

			TextView lowRecordYear = (TextView) findViewById(R.id.lowRecordYear);
			lowRecordYear.setText(low.getYear());

			//set temp high fields
			TextView highNormalF = (TextView) findViewById(R.id.highNormalF);
			TextView highNormalC = (TextView) findViewById(R.id.highNormalC);
			highNormalF.setText(result.getHigh().getNormal().getValueFWithUnit());
			highNormalC.setText(result.getHigh().getNormal().getValueCWithUnit());
			
			TextView highRecordF = (TextView) findViewById(R.id.highRecordF);
			TextView highRecordC = (TextView) findViewById(R.id.highRecordC);
			highRecordF.setText(result.getHigh().getRecord().getValueFWithUnit());
			highRecordC.setText(result.getHigh().getRecord().getValueCWithUnit());

			TextView highRecordYear = (TextView) findViewById(R.id.highRecordYear);
			highRecordYear.setText(result.getHigh().getYear());
			
			//set provided by data
			TextView textViewDataProvider = (TextView)findViewById(R.id.textViewDataProvider);
			textViewDataProvider.setText(result.getProvidedBy());

			//get default shared preferences 
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			
			//set the English and Metric flags
			boolean showEnglishUnits = UnitsOfMeasure.showEnglishUnits(sharedPreferences);
			boolean showMetricUnits = UnitsOfMeasure.showMetricUnits(sharedPreferences);
			
			//set conditional visibility based on the preferences
			
			lowNormalF.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);
			lowNormalC.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);
			lowRecordF.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);
			lowRecordC.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);
			
			highNormalF.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);
			highNormalC.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);
			highRecordF.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);
			highRecordC.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);
		}
	}
	
}
