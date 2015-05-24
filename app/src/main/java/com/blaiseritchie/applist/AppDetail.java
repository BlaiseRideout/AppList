package com.blaiseritchie.applist;

import android.graphics.drawable.Drawable;

public class AppDetail implements Comparable<AppDetail> {
	public Drawable icon;
	public CharSequence name;
	public CharSequence id;
	public String header;
	public boolean isHidden = false;

	public AppDetail(Drawable icon, CharSequence name, CharSequence id, String header) {
		this.name = name;
		this.icon = icon;
		this.id = id;
		this.header = header;
	}

	public String toString() {
		return this.name.toString() + this.id.toString();
	}

	public int compareTo(AppDetail b) {
		return this.name.toString().toLowerCase().compareTo(b.name.toString().toLowerCase());
	}
}
