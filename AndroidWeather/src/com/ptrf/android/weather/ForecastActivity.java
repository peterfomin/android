package com.ptrf.android.weather;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ptrf.android.weather.data.Forecast;
import com.ptrf.android.weather.data.WeatherData;
import com.ptrf.android.weather.data.DailyForecast;
import com.ptrf.android.weather.service.ResultReceiver;
import com.ptrf.android.weather.service.WeatherServiceTask;
import com.ptrf.android.weather.service.WeatherServiceTaskFactory;
import com.ptrf.android.weather.util.UnitsOfMeasure;

/**
 * Forecast activity that shows weather forecast data.
 */
public class ForecastActivity extends Activity implements ResultReceiver {
	private static final String TAG = ForecastActivity.class.toString();

	/**
	 * Location parameter passed from the main activity.
	 */
	protected static final String LOCATION_PARAMETER = "location";
	
	/**
	 * List data adapter.
	 */
	private ArrayAdapter<DailyForecast> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set activity layout
		setContentView(R.layout.forecast);
		
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
     * Refresh the weather data calling the wunderground.com weather service provider.
     */
	private void refreshWeatherData() {
		
		//create new task based on the provider service settings
		WeatherServiceTask task = null;
		try {
			//indicate the forecast task using second parameter
			task = WeatherServiceTaskFactory.createWeatherServiceTask(this, true);
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
	 * Receives result from the WeatherServiceTask and sets the data adapter for the ListView.
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
			// expect forecast data
	    	Forecast forecast = (Forecast) data;
	    	List<DailyForecast> weatherForecastList = forecast.getDailyForecasts();
	    	
	    	//add header row, identified by null value
	    	weatherForecastList.add(0, null);
	    	
	    	//convert list into array type for the adapter interface
			DailyForecast[] weatherForecast = weatherForecastList.toArray(new DailyForecast[weatherForecastList.size()]);
			
			//create new list data adapter
			adapter = new ForecastDataAdapter(this, R.layout.forecast_row, weatherForecast);
	    	Log.d(TAG, "Created new adapter="+ adapter );
			
			//assign data adapter to the forecast list
			ListView gridView = (ListView) findViewById(R.id.forecastGrid);
			gridView.setAdapter(adapter);
			
			TextView textViewDataProvider = (TextView)findViewById(R.id.textViewDataProvider);
			textViewDataProvider.setText(data.getProvidedBy());
		}
	}
	
	/**
	 * Array adapter for the forecast data to display in the ListView.
	 */
    public class ForecastDataAdapter extends ArrayAdapter<DailyForecast> {

        public ForecastDataAdapter(Context context, int textViewResourceId, DailyForecast[] objects) {
            super(context, textViewResourceId, objects);
        }

        /**
         * Since there are two types of the list elements then the corresponding type needs to be associated with the item. 
         */
        @Override
		public int getItemViewType(int position) {
        	DailyForecast forecast = getItem(position);
			return (forecast == null ? 0 : 1);
		}

        /**
         * Since there are two types of the list elements it needs to be indicated by this method return value. 
         */
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		/**
         * Creates the View instance for every forecast UI element defined in the layout. 
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                //get item associated with the list position
                DailyForecast forecast = getItem(position);
                if (view == null) {
                	//obtain inflater
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //inflate the current row using specific layout
                    int layout = (forecast == null ? R.layout.forecast_header : R.layout.forecast_row);
                    view = inflater.inflate(layout, null);
                }

                //locate all of the conditionally shown UI components inside of the layout
                TextView forecastTempLowF = (TextView) view.findViewById(R.id.forecastTempLowF);
                TextView forecastTempLowC = (TextView) view.findViewById(R.id.forecastTempLowC);
                TextView forecastTempHighF = (TextView) view.findViewById(R.id.forecastTempHighF);
                TextView forecastTempHighC = (TextView) view.findViewById(R.id.forecastTempHighC);
                TextView forecastWindDir = (TextView) view.findViewById(R.id.forecastWindDir);
                TextView forecastWindSpeedMph = (TextView) view.findViewById(R.id.forecastWindSpeedMph);
                TextView forecastWindSpeedKph = (TextView) view.findViewById(R.id.forecastWindSpeedKph);
                
                //remove widgets to free more space in the PORTRAIT mode
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                	forecastWindDir.setVisibility(View.GONE);
                	forecastWindSpeedMph.setVisibility(View.GONE);
                	forecastWindSpeedKph.setVisibility(View.GONE);
                } else {
                	//restore removed widgets in the LANDSCAPE mode
                	forecastWindDir.setVisibility(View.VISIBLE);
                	forecastWindSpeedMph.setVisibility(View.VISIBLE);						
                	forecastWindSpeedKph.setVisibility(View.VISIBLE);						
                }
                
				//get default shared preferences 
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ForecastActivity.this);
				//set the English and Metric flags
				boolean showEnglishUnits = UnitsOfMeasure.showEnglishUnits(sharedPreferences);
				boolean showMetricUnits = UnitsOfMeasure.showMetricUnits(sharedPreferences);
				
				//set proper visibility based on the units of measure configured for both header and data rows
				//the English components in the header should not be removed
				forecastTempLowC.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);
				forecastTempLowF.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);
				forecastTempHighC.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);
				forecastTempHighF.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);
            	//if wind direction was not removed then set proper visibility for wind speed
            	//otherwise wind speed components have been removed to free space in this orientation mode
            	if (forecastWindDir.getVisibility() == View.VISIBLE) {
            		forecastWindSpeedKph.setVisibility(showMetricUnits ? View.VISIBLE : View.GONE);				
            		forecastWindSpeedMph.setVisibility(showEnglishUnits ? View.VISIBLE : View.GONE);						
            	}
            	
				//forecast instance is not null unless it's a header row
                if (forecast != null) {
                	
                	//locate the rest of the UI components inside of the layout
                	TextView forecastDay = (TextView) view.findViewById(R.id.forecastDay);
                	ImageView forecastConditionImage = (ImageView) view.findViewById(R.id.forecastConditionImage);
                	TextView forecastCondition = (TextView) view.findViewById(R.id.forecastCondition);
                	TextView forecastPrecipitation = (TextView) view.findViewById(R.id.forecastPrecipitation);
                	
                	//transfer the data into the UI elements
                	forecastDay.setText(forecast.getDay());
                	forecastConditionImage.setImageDrawable(forecast.getWeatherImage());
                	forecastCondition.setText(forecast.getWeather());
                	forecastTempLowF.setText(forecast.getTemperatureLow().getValueFWithUnit());
                	forecastTempLowC.setText(forecast.getTemperatureLow().getValueCWithUnit());
                	forecastTempHighF.setText(forecast.getTemperatureHigh().getValueFWithUnit());
                	forecastTempHighC.setText(forecast.getTemperatureHigh().getValueCWithUnit());
                	forecastWindDir.setText(forecast.getWind().getDirection());
                	forecastWindSpeedMph.setText(forecast.getWind().getSpeedMphWithUnit());
                	forecastWindSpeedKph.setText(forecast.getWind().getSpeedKphWithUnit());
                	forecastPrecipitation.setText(forecast.getPrecipitation());
                } else {
                	//forecast == null means it's a header row
                	//if both English and metric units are shown then remove the text from the Metric headers
                	//if not then restore the metric header text using English header so it would display properly if only metric units are shown
                	forecastTempLowC.setText(showMetricUnits && showEnglishUnits ? "" : forecastTempLowF.getText().toString());
                	forecastTempHighC.setText(showMetricUnits && showEnglishUnits ? "" : forecastTempHighF.getText().toString());
                	forecastWindSpeedKph.setText(showMetricUnits && showEnglishUnits ? "" : forecastWindSpeedMph.getText().toString());
                }

                return view;
        }

    }
}
