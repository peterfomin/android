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
public abstract class WeatherServiceTask extends
		AsyncTask<String, Void, WeatherData> {

	@Override
	protected WeatherData doInBackground(String... args) {

		String requestUrl = createRequestUrl(args);

		WeatherData result = null;
		try {

			// Create the HTTP request
			HttpParams httpParameters = new BasicHttpParams();

			// Setup timeouts
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
			HttpConnectionParams.setSoTimeout(httpParameters, 15000);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpGet httpget = new HttpGet(requestUrl);

			Log.d(this.toString(), "Executing rest service call using url="
					+ requestUrl);
			HttpResponse response = httpclient.execute(httpget);
			Log.d(this.toString(), "response status="
					+ response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();

			String content = EntityUtils.toString(entity);

			// Create a JSON object from the request response
			JSONObject json = new JSONObject(content);

			result = createWeatherData(json);

		} catch (Exception e) {
			Log.e(this.toString(), "Error:", e);
		}

		return result;
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