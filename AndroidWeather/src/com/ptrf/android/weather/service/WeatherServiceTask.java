package com.ptrf.android.weather.service;

import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ptrf.android.weather.WeatherData;

/**
 * Abstract Weather service task that extends AsyncTask for asynchronous processing of the weather service request.
 */
public abstract class WeatherServiceTask extends AsyncTask<Object, Void, WeatherData> {
	private static final String TAG = WeatherServiceTask.class.getName();

	private Throwable exception = null;
	private ProgressDialog progressDialog;
	private Context context;
	
	/**
	 * Protected constructor for subclasses.
	 * @param context parent context, i.e. activity, etc, used for progress dialog creation and result callback
	 */
	protected WeatherServiceTask(Context context) {
		this.context = context;
	}
	
	/**
	 * Runs before {@link #doInBackground}.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(context, "Retrieving data", "Please wait...", true, false);
	}

	/**
	 * Makes a weather service call and returns the populated instance of WeatherData.
	 */
	@Override
	protected WeatherData doInBackground(Object... args) {
		
		//expect first argument to be device location
		Location deviceLocation = (Location)args[0];

		//expect second argument to be entered location
		String enteredLocation = (String)args[1];
		
		//obtain shared preferences used for the request creation
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		WeatherData result = null;
		setException(null);
		try {
			//call subclass' method to create the service request url
			String requestUrl = createRequestUrl(preferences, deviceLocation, enteredLocation);

			// Create the HTTP request
			HttpParams httpParameters = new BasicHttpParams();

			// Setup timeouts
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			HttpConnectionParams.setSoTimeout(httpParameters, 15000);

			//create new instance of the HttpClient
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpGet httpget = new HttpGet(requestUrl);

			Log.d(TAG, "Executing rest service call using url="+ requestUrl);
			HttpResponse response = httpclient.execute(httpget);
			Log.d(TAG, "response status="+ response.getStatusLine().toString());

			//get service response entity
			HttpEntity entity = response.getEntity();

			//get service response as string
			String content = EntityUtils.toString(entity);
			Log.v(TAG, "response content="+ content);
			
			// Create a JSON object from the request response
			JSONObject json = new JSONObject(content);
			
			//error handling
			checkError(json);
			
			//call subclass' implementation to convert JSON into instance of WeatherData
			result = createWeatherData(json);

		} catch (SocketTimeoutException e) {
			//SocketTimeoutException does not provide an error message - create our own exception
			setException(new Exception("Service request timed out", e));
		} catch (Exception e) {
			setException(e);
		}

		return result;
	}

	/**
	 * Runs after {@link #doInBackground}.
	 */
	@Override
	protected void onPostExecute(WeatherData result) {
		super.onPostExecute(result);
		
		if (context instanceof ResultReceiver) {
			//pass the result into the ResultReceiver instance 
			ResultReceiver resultReceiver = (ResultReceiver) context;
			resultReceiver.receiveResult(result, getException());
		}
		
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public Throwable getException() {
		return exception;
	}
	
	private void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Returns the service request url for the rest service call.
	 * 
	 * @param preferences application shared preferences
	 * @param deviceLocation location of the device - should be used by default if specified
	 * @param enteredLocation location entered by user - should be used if device location is not specified
	 * @return service request url
	 * @throws Exception if an error occurs
	 */
	protected abstract String createRequestUrl(SharedPreferences preferences, Location deviceLocation, String enteredLocation) throws Exception;

	/**
	 * Returns an instance of WeatherData retrieved from the JSON object.
	 * 
	 * @param json
	 * @return instance of WeatherData
	 * @throws JSONException
	 */
	protected abstract WeatherData createWeatherData(JSONObject json) throws JSONException;


	/**
	 * Checks for error json element and throws exception if found.
	 * @param json
	 * @throws Exception
	 */
	protected abstract void checkError(JSONObject json) throws Exception;
}