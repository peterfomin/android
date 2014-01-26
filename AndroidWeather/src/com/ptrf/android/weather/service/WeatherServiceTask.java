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

import com.ptrf.android.weather.R;
import com.ptrf.android.weather.data.WeatherData;

/**
 * Abstract Weather service task that extends AsyncTask for asynchronous processing of the weather service request.
 */
public abstract class WeatherServiceTask extends AsyncTask<Object, Void, WeatherData> {
	private static final String TAG = WeatherServiceTask.class.getName();

	/**
	 * Exception that occurred during task execution.
	 */
	private Throwable exception = null;

	/**
	 * Instance of ProgressDialog that is used for showing the "Please wait" message.
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * Parent context, i.e. activity. 
	 */
	private Context context;
	
	/**
	 * Protected constructor for subclasses.
	 * @param context parent context, i.e. activity, etc, used for progress dialog creation and result callback
	 */
	protected WeatherServiceTask(Context context) {
		if (!(context instanceof ResultReceiver)) {
			throw new IllegalArgumentException("Context must implement "+ ResultReceiver.class.getName());
		}
		this.context = context;
	}
	
	/**
	 * Runs before {@link #doInBackground}.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(getContext(), getString(R.string.progressDialogTitle), getString(R.string.msg_pleaseWait), true, false);
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
		
		WeatherData result = null;
		setException(null);
		try {
			//call subclass' method to create the service request url
			String requestUrl = createRequestUrl(deviceLocation, enteredLocation);

			// Create the HTTP request
			HttpParams httpParameters = new BasicHttpParams();

			// Setup timeouts
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);

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
			setException(new Exception(getString(R.string.msg_serviceRequestTimeout), e));
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
		
		//context is guaranteed to be an instance of ResultReceiver by the constructor
		//it's safe to cast context to ResultReceiver
		ResultReceiver resultReceiver = (ResultReceiver) getContext();
		//pass the result into the ResultReceiver instance 
		resultReceiver.receiveResult(result, getException());

		//dismiss progress dialog
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * Returns the service request url for the rest service call.
	 * 
	 * @param deviceLocation location of the device - should be used by default if specified
	 * @param enteredLocation location entered by user - should be used if device location is not specified
	 * @return service request url
	 * @throws Exception if an error occurs
	 */
	protected abstract String createRequestUrl(Location deviceLocation, String enteredLocation) throws Exception;

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
	
	public Throwable getException() {
		return exception;
	}
	
	private void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Returns parent context i.e. activity.
	 */
	protected Context getContext() {
		return context;
	}

	/**
	 * Returns default shared preferences.
	 * @return default shared preferences.
	 */
	protected SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(getContext());
	}


	/**
	 * Helper method to get the resource string value using context and resource id.
	 * @param resId resource id
	 * @return resource string value
	 */
	protected String getString(int resId) {
		return getContext().getString(resId);
	}

}