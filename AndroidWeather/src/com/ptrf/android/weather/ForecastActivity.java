package com.ptrf.android.weather;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.ptrf.android.weather.data.WeatherForecast;
import com.ptrf.android.weather.service.ResultReceiver;
import com.ptrf.android.weather.service.WeatherServiceTask;
import com.ptrf.android.weather.service.WeatherServiceTaskFactory;

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
	private ArrayAdapter<WeatherForecast> adapter;

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
	@SuppressLint("DefaultLocale")
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
	    	List<WeatherForecast> weatherForecastList = forecast.getWeatherForecast();
	    	
	    	//add header row, identified by null value
	    	weatherForecastList.add(0, null);
	    	
	    	//convert list into array type for the adapter interface
			WeatherForecast[] weatherForecast = weatherForecastList.toArray(new WeatherForecast[]{});
			
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
    public class ForecastDataAdapter extends ArrayAdapter<WeatherForecast> {

        public ForecastDataAdapter(Context context, int textViewResourceId, WeatherForecast[] objects) {
            super(context, textViewResourceId, objects);
        }

        /**
         * Since there are two types of the list elements then the corresponding type needs to be associated with the item. 
         */
        @Override
		public int getItemViewType(int position) {
        	WeatherForecast forecast = getItem(position);
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
                WeatherForecast forecast = getItem(position);
                if (view == null) {
                	//obtain inflater
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //inflate the current row using specific layout
                    int layout = (forecast == null ? R.layout.forecast_header : R.layout.forecast_row);
                    view = inflater.inflate(layout, null);
                }

                //locate all of the conditionally shown UI components inside of the layout
                TextView forecastTempLow = (TextView) view.findViewById(R.id.forecastTempLow);
                TextView forecastTempHigh = (TextView) view.findViewById(R.id.forecastTempHigh);
                TextView forecastWindDir = (TextView) view.findViewById(R.id.forecastWindDir);
                TextView forecastWindSpeed = (TextView) view.findViewById(R.id.forecastWindSpeed);
                TextView forecastPrecipitation = (TextView) view.findViewById(R.id.forecastPrecipitation);
                
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                	//remove widgets to free more space
                	forecastWindDir.setVisibility(View.GONE);
                	forecastWindSpeed.setVisibility(View.GONE);
                } else {
                	//restore removed widgets in the landscape mode
                	forecastWindDir.setVisibility(View.VISIBLE);
                	forecastWindSpeed.setVisibility(View.VISIBLE);						
                }

                if (forecast != null) {
                	//locate the rest of the UI components inside of the layout
                	TextView forecastDay = (TextView) view.findViewById(R.id.forecastDay);
                	ImageView forecastConditionImage = (ImageView) view.findViewById(R.id.forecastConditionImage);
                	TextView forecastCondition = (TextView) view.findViewById(R.id.forecastCondition);
                	
                	//transfer the data into the UI elements
                	forecastDay.setText(forecast.getDay());
                	forecastConditionImage.setImageDrawable(forecast.getWeatherImage());
                	forecastCondition.setText(forecast.getWeather());
                	forecastTempLow.setText(forecast.getTemperatureLow().getValueFWithUnit());
                	forecastTempHigh.setText(forecast.getTemperatureHigh().getValueFWithUnit());
                	forecastWindDir.setText(forecast.getWind().getDirection());
                	forecastWindSpeed.setText(forecast.getWind().getSpeedMphWithUnit());
                	forecastPrecipitation.setText(forecast.getPrecipitation());
                }

                return view;
        }

    }
}
