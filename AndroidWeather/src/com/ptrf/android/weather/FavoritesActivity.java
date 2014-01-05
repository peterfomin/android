package com.ptrf.android.weather;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ptrf.android.weather.util.FavoritesUtility;

/**
 * Activity that shows favorite locations.
 */
public class FavoritesActivity extends ListActivity {
	private static final String TAG = FavoritesActivity.class.toString();
	
	private ArrayAdapter<String> adapter;

    @Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.favorites);
		//initialize list from saved json file
		setListAdapter(createArrayAdapter());
	}

    /**
     * Creates new adapter using favorites data.
     * @return
     */
    private ArrayAdapter<String> createArrayAdapter() {
    	List<String> items = new ArrayList<String>();
    	items.addAll(FavoritesUtility.getFavoriteLocations(this));
    	adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
    	Log.d(TAG, "Created new adapter="+ adapter );
		return adapter;
    }
    
    /**
     * Handle favorite selection.
     */
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		String location = adapter.getItem(position);
		Intent intent = new Intent(this, MainActivity.class);
	    intent.putExtra(MainActivity.FAVORITE_LOCATION_PARAMETER, location);
	    startActivity(intent);
	}

	/**
	 * Removes all favorites.
	 * @param button
	 */
	public void removeAll(View button) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Remove?");
        adb.setMessage("Are you sure you want to remove all favorites?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int choice) {
                	//remove all items
                	adapter.clear();
                	FavoritesUtility.removeAllFavoriteLocations(FavoritesActivity.this);
                    adapter.notifyDataSetChanged();
                }
        });
        adb.show();
	}
}
