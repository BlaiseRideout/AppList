package com.blaiseritchie.applist;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class AppList extends Activity {

	private PackageManager manager;
	private List<AppDetail> apps;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);
		getApps();
		fillListView();
		addClickListener();
	}

	private void getApps() {
		manager = getPackageManager();
		apps = new ArrayList<AppDetail>();

		Intent i = new Intent(Intent.ACTION_MAIN, null);
		i.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
		for(ResolveInfo ri : availableActivities)
			apps.add(new AppDetail(ri.activityInfo.loadIcon(manager), ri.loadLabel(manager), ri.activityInfo.packageName));

		Collections.sort(apps, new Comparator<AppDetail>() {
			public int compare(AppDetail a, AppDetail b) {
				return a.name.toString().toLowerCase().compareTo(b.name.toString().toLowerCase());
			}
		});
	}

	private void fillListView() {
		list = (ListView)findViewById(R.id.apps_listview);

		ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this, R.layout.list_item, apps) {
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = getLayoutInflater().inflate(R.layout.list_item, null);

				ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
				appIcon.setImageDrawable(apps.get(position).icon);

				TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
				appLabel.setText(apps.get(position).name);

				return convertView;
			}
		};

		list.setAdapter(adapter);
	}

	private void addClickListener() {
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				Intent i = manager.getLaunchIntentForPackage(apps.get(pos).id.toString());
				AppList.this.startActivity(i);
			}
		});
	}
}
