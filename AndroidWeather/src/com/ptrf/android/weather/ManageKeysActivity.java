package com.ptrf.android.weather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ptrf.android.weather.util.PreferencesUtility;

public class ManageKeysActivity extends ListActivity {
	
    private static final String PREFERENCE_KEY = "keys";

    private List<String> listItems;
    private ArrayAdapter<String> adapter;

    @Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.keys);
		//initialize list from preferences
		listItems = new ArrayList<String>();
		listItems.addAll(Arrays.asList(PreferencesUtility.getFromPreferences(this, PREFERENCE_KEY)));
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
		setListAdapter(adapter);
	}

    /**
     * Handle add item action.
     * @param v
     */
    public void addItems(View view) {
    	EditText editText = (EditText) findViewById(R.id.editTextKey);
        String value = editText.getText().toString();
		if (value == null || value.trim().equals("")) {
			Toast.makeText(ManageKeysActivity.this, "Please specify the key", Toast.LENGTH_LONG).show();
		} else {
			listItems.add(value);
			PreferencesUtility.addToPreferences(this, PREFERENCE_KEY, value);
			adapter.notifyDataSetChanged();
		}
    }

    /**
     * Handle delete action.
     */
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Delete?");
		adb.setMessage("Are you sure you want to delete " + listItems.get(position) +"?");
		adb.setNegativeButton("Cancel", null);
		adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int choice) {
				listItems.remove(position);
				PreferencesUtility.removeFromPreferences(ManageKeysActivity.this, PREFERENCE_KEY, position);
				adapter.notifyDataSetChanged();
			}
		});
		adb.show();
	}

	
	
}
