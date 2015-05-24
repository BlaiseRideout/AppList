package com.blaiseritchie.applist;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

public class AppList extends Activity {

	private SharedPreferences sharedPref;
	private PackageManager manager;
	private List<AppDetail> apps;
	private Set<String> favorites;
	private Set<String> hidden;
	private boolean showHidden;
	private boolean gridList;
	private ListView list;
	private GridView grid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);

		loadPrefs();
		initializeViews();
		getApps();
		fillListView();
		addClickListener();
	}

	private void initializeViews() {
		this.list = (ListView)findViewById(R.id.apps_listview);
		this.grid = (GridView)findViewById(R.id.apps_gridview);

		if(gridList) {
			this.grid.setVisibility(View.VISIBLE);
			this.list.setVisibility(View.GONE);
			registerForContextMenu(this.grid);
		}
		else {
			this.list.setVisibility(View.VISIBLE);
			this.grid.setVisibility(View.GONE);
			registerForContextMenu(list);
		}
	}

	private void loadPrefs() {
		this.sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		this.favorites = this.sharedPref.getStringSet(getString(R.string.favorites), null);
		if(this.favorites == null) {
			this.favorites = new HashSet<String>();
			SharedPreferences.Editor editor = this.sharedPref.edit();
			editor.putStringSet(getString(R.string.favorites), this.favorites);
			editor.commit();
		}
		this.hidden = this.sharedPref.getStringSet(getString(R.string.hidden), null);
		if(this.hidden == null) {
			this.hidden = new HashSet<String>();
			SharedPreferences.Editor editor = this.sharedPref.edit();
			editor.putStringSet(getString(R.string.hidden), this.hidden);
			editor.commit();
		}
		this.showHidden = this.sharedPref.getBoolean(getString(R.string.showHidden), false);
		this.gridList = this.sharedPref.getBoolean(getString(R.string.gridList), false);
	}

	private void getApps() {
		manager = getPackageManager();
		apps = new ArrayList<AppDetail>();

		Intent i = new Intent(Intent.ACTION_MAIN, null);
		i.addCategory(Intent.CATEGORY_LAUNCHER);

		List<AppDetail> favoriteApps = new ArrayList<AppDetail>();

		List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
		for(ResolveInfo ri : availableActivities) {
			AppDetail app = new AppDetail(ri.activityInfo.loadIcon(manager), ri.loadLabel(manager), ri.activityInfo.packageName, "");
			app.isHidden = this.hidden.contains(app.toString());
			if(!app.isHidden || this.showHidden) {
				if(favorites.contains(app.toString()))
					favoriteApps.add(app);
				else
					apps.add(app);
			}
		}

		Collections.sort(apps);

		if(favoriteApps.size() > 0) {
			Collections.sort(favoriteApps);
			favoriteApps.get(0).header = "Favorites";
			apps.get(0).header = "Apps";
			favoriteApps.addAll(apps);
			apps = favoriteApps;
		}
	}

	private void fillListView() {
		if(!this.gridList) {
			ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.list_item, apps) {
				public View getView(int position, View convertView, ViewGroup parent) {
					if(convertView == null)
						convertView = getLayoutInflater().inflate(R.layout.list_item, null);

					AppDetail app = apps.get(position);

					TextView header = (TextView)convertView.findViewById(R.id.header);
					if(!app.header.equals("")) {
						header.setText(app.header);
						header.setVisibility(View.VISIBLE);
					}
					else
						header.setVisibility(View.GONE);

					ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
					appIcon.setImageDrawable(app.icon);

					TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
					appLabel.setText(app.name);

					if(app.isHidden)
						appLabel.setTextColor(getResources().getColor(R.color.hidden));

					return convertView;
				}
			};
			list.setAdapter(adapter);
		}
		else {
			ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.grid_item, apps) {
				public View getView(int position, View convertView, ViewGroup parent) {
					if(convertView == null)
						convertView = getLayoutInflater().inflate(R.layout.grid_item, null);

					AppDetail app = apps.get(position);

					ImageView appIcon = (ImageView)convertView.findViewById(R.id.grid_app_icon);
					appIcon.setImageDrawable(app.icon);

					TextView appLabel = (TextView)convertView.findViewById(R.id.grid_app_title);
					appLabel.setText(app.name);

					if(app.isHidden)
						appLabel.setTextColor(getResources().getColor(R.color.hidden));

					return convertView;
				}
			};
			grid.setAdapter(adapter);
		}
	}

	private void addClickListener() {
		if(!this.gridList)
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
					Intent i = manager.getLaunchIntentForPackage(apps.get(pos).id.toString());
					AppList.this.startActivity(i);
				}
			});
		else
			grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
					Intent i = manager.getLaunchIntentForPackage(apps.get(pos).id.toString());
					AppList.this.startActivity(i);
				}
			});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
		AppDetail detail = (AppDetail)list.getItemAtPosition(acmi.position);

		menu.add(0, acmi.position, 0, "App details");

		if(acmi.position < favorites.size())
			menu.add(0, acmi.position, 0, "Remove from favorites");
		else
			menu.add(0, acmi.position, 0, "Add to favorites");

		if(hidden.contains(detail.toString()))
			menu.add(0, acmi.position, 0, "Unhide");
		else
			menu.add(0, acmi.position, 0, "Hide");

		if(this.showHidden)
			menu.add(0, acmi.position, 0, "Hide hidden");
		else
			menu.add(0, acmi.position, 0, "Show hidden");

		if(this.gridList)
			menu.add(0, acmi.position, 0, "List view");
		else
			menu.add(0, acmi.position, 0, "Grid view");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getTitle().equals("App details")) {
			startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + apps.get(item.getItemId()).id)));
		}
		else if(item.getTitle().equals("Add to favorites")) {
			favorites.add(apps.get(item.getItemId()).toString());
			SharedPreferences.Editor editor = this.sharedPref.edit();
			editor.putStringSet(getString(R.string.favorites), favorites);
			editor.commit();
			getApps();
			fillListView();
		}
		else if(item.getTitle().equals("Remove from favorites")) {
			favorites.remove(apps.get(item.getItemId()).toString());
			SharedPreferences.Editor editor = this.sharedPref.edit();
			editor.putStringSet(getString(R.string.favorites), favorites);
			editor.commit();
			getApps();
			fillListView();
		}
		else if(item.getTitle().equals("Hide")) {
			hidden.add(apps.get(item.getItemId()).toString());
			SharedPreferences.Editor editor = this.sharedPref.edit();
			editor.putStringSet(getString(R.string.hidden), hidden);
			editor.commit();
			getApps();
			fillListView();
		}
		else if(item.getTitle().equals("Unhide")) {
			hidden.remove(apps.get(item.getItemId()).toString());
			SharedPreferences.Editor editor = this.sharedPref.edit();
			editor.putStringSet(getString(R.string.hidden), hidden);
			editor.commit();
			getApps();
			fillListView();
		}
		else if(item.getTitle().equals("Show hidden")) {
			this.showHidden = true;
			getApps();
			fillListView();
		}
		else if(item.getTitle().equals("Hide hidden")) {
			this.showHidden = false;
			getApps();
			fillListView();
		}
		else if(item.getTitle().equals("Grid view")) {
			this.gridList = true;
			initializeViews();
			fillListView();
		}
		else if(item.getTitle().equals("List view")) {
			this.gridList = false;
			initializeViews();
			fillListView();
		}
		return false;
	}
}
