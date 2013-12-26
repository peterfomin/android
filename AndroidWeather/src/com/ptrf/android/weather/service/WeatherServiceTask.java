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

import com.ptrf.android.weather.WeatherData;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Abstract Weather service task.
 * 
 */
public abstract class WeatherServiceTask extends AsyncTask<String, Void, WeatherData> {
	private static final String TAG = WeatherServiceTask.class.getName();

	private Throwable exception = null;
	
	@Override
	protected WeatherData doInBackground(String... args) {
		
		String requestUrl = createRequestUrl(args);

		WeatherData result = null;
		setException(null);
		try {

			// Create the HTTP request
			HttpParams httpParameters = new BasicHttpParams();

			// Setup timeouts
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			HttpConnectionParams.setSoTimeout(httpParameters, 15000);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpGet httpget = new HttpGet(requestUrl);

			Log.d(TAG, "Executing rest service call using url="+ requestUrl);
			HttpResponse response = httpclient.execute(httpget);
			Log.d(TAG, "response status="+ response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();

			String content = EntityUtils.toString(entity);
			Log.v(TAG, "response content="+ content);
			
			// Create a JSON object from the request response
			JSONObject json = new JSONObject(content);
			
			//error handling
			checkError(json);
			
			result = createWeatherData(json);

		} catch (Exception e) {
			setException(e);
		}

		return result;
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