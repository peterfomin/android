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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ptrf.android.weather.util.FavoritesUtility;

/**
 * Activity that shows favorite locations.
 */
public class FavoritesActivity extends ListActivity {
	private static final String TAG = FavoritesActivity.class.toString();
	
	/**
	 * List data adapter.
	 * An Adapter object acts as a bridge between an {@link AdapterView} and the
	 * underlying data for that view. The Adapter provides access to the data items.
	 * The Adapter is also responsible for making a {@link android.view.View} for
	 * each item in the data set.
	 */
	private ArrayAdapter<String> adapter;

	/**
	 * Called when the activity is starting.
	 */
    @Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.favorites);
		
		ListView listView = (ListView) findViewById(android.R.id.list);
		//enable long click event for list items
		listView.setLongClickable(true);
		//attach the LongClickListener to the list
		listView.setOnItemLongClickListener(new FavoritesLongClickListener());

		//initialize list from saved json file
		setListAdapter(createArrayAdapter());
	}

    /**
     * Creates new adapter using favorites data.
     * @return new instance of ArrayAdapter
     */
    private ArrayAdapter<String> createArrayAdapter() {
    	List<String> items = new ArrayList<String>();
    	items.addAll(FavoritesUtility.getFavoriteLocations(this));
    	adapter = new ArrayAdapter<String>(this, R.layout.favorites_item, items);
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
		//when MainActivity is launched, all tasks on top of it are cleared so that MainActivity is top.
		//A new back stack is created with MainActivity at the top, and using singleTop ensures that MainActivity is created only once
		//since MainActivity is now on top due to FLAG_ACTIVITY_CLEAR_TOP
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra(MainActivity.FAVORITE_LOCATION_PARAMETER, location);
	    startActivity(intent);
	}

	/**
	 * Removes all favorites.
	 * @param button
	 */
	public void removeAll(View button) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.removeAllFavorites);
        adb.setMessage(getString(R.string.msg_removeAllFavorites));
        adb.setNegativeButton(getString(R.string.cancel), null);
        adb.setPositiveButton(getString(R.string.ok), new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int choice) {
                	//remove all items from adapter
                	adapter.clear();
                	//remove all items from from storage
                	FavoritesUtility.removeAllFavoriteLocations(FavoritesActivity.this);
                	//notify adapter of the changes made to force the data refresh
                    adapter.notifyDataSetChanged();
                }
        });
        adb.show();
	}

	/**
	 * FavoritesLongClickListener that processes the long click event. 
	 *
	 */
	private class FavoritesLongClickListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			AlertDialog.Builder adb = new AlertDialog.Builder(FavoritesActivity.this);
	        adb.setTitle(R.string.removeFavorite);
	        adb.setMessage(getString(R.string.msg_removeFavorite));
	        adb.setNegativeButton(getString(R.string.cancel), null);
	        adb.setPositiveButton(getString(R.string.ok), new AlertDialog.OnClickListener() {
	                public void onClick(DialogInterface dialog, int choice) {
	                	//obtain selected favorite location from adapter
	                	String item = adapter.getItem(position);
	                	//remove selected favorite location from adapter
	                	adapter.remove(item);
	                	//remove selected favorite location from storage
						FavoritesUtility.remove(FavoritesActivity.this, item);
						//notify adapter of the changes made to force the data refresh
	                    adapter.notifyDataSetChanged();
	                }
	        });
	        adb.show();
			return true;
		}
	}
}
