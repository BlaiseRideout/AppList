package com.blaiseritchie.applist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.ListView;

public class Settings extends AppCompatActivity {
	private ListView list;
	static private SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);

		//getActionBar().setTitle("Settings");
		this.list = (ListView)findViewById(R.id.settings_listview);

		loadPrefs();
		propagateList();
	}

	private void loadPrefs() {
		Settings.sharedPref = this.getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
	}

	private void propagateList() {
		ArrayAdapter
	}
}
