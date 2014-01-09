package com.ptrf.android.weather.service;

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
import android.os.AsyncTask;
import android.util.Log;

import com.ptrf.android.weather.WeatherData;

/**
 * Abstract Weather service task that extends AsyncTask for asynchronous processing of the weather service request.
 */
public abstract class WeatherServiceTask extends AsyncTask<String, Void, WeatherData> {
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
	protected WeatherData doInBackground(String... args) {
		
		//call subclass'implemenation to create the request URL
		String requestUrl = createRequestUrl(args);

		WeatherData result = null;
		setException(null);
		try {

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

	/**
	 * Checks for error json element and throws exception if found.
	 * @param json
	 * @throws Exception
	 */
	private void checkError(JSONObject json) throws Exception {
		
		JSONObject response = json.getJSONObject("response");
		if (! response.isNull("error")) {
			JSONObject error = response.getJSONObject("error");
			throw new Exception(error.getString("description"));
		}
		if (! response.isNull("results")) {
			//JSONObject error = response.getJSONObject("results");
			//TODO: handle multiple locations returned
			throw new Exception("Location specified is not unique. Please refine your location.");
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
	 * @param args
	 * @return service request url
	 */
	protected abstract String createRequestUrl(String[] args);

	/**
	 * Returns an instance of WeatherData retrieved from the JSON object.
	 * 
	 * @param json
	 * @return instance of WeatherData
	 * @throws JSONException
	 */
	protected abstract WeatherData createWeatherData(JSONObject json)
			throws JSONException;

}