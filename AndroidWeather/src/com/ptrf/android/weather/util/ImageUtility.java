package com.ptrf.android.weather.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Image Utility.
 * Marked as abstract to emphasize the static usage only i.e. can't be instantiated.
 */
public abstract class ImageUtility {
	private static final String TAG = ImageUtility.class.toString();
	
	/**
	 * Creates a Drawable instance that represents the image specified by given URL.
	 * @param urlAddress url of the image
	 * @return Drawable instance that represents the image
	 */
	public static Drawable createImageFromURL(String urlAddress) {
		Drawable drawable = null;
		InputStream inputStream = null;
		try {
			//create URL instance
			URL url = new URL(urlAddress);
			//get URL's content as InputStream
			inputStream = (InputStream) url.getContent();
			//create instance of Drawable from InputStream
			drawable = Drawable.createFromStream(inputStream, "src");
		} catch (Exception e) {
			//log any exception
			Log.e(TAG, "Failed to create image from url="+ urlAddress, e);
		} finally {
			//try to close input stream
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "Failed to close input stream for url="+ urlAddress, e);
				}
			}
		}
		return drawable;
	}
}
