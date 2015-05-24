package com.blaiseritchie.applist;

import android.graphics.drawable.Drawable;

public class AppDetail {
	public Drawable icon;
	public CharSequence name;
	public CharSequence id;

	public AppDetail(Drawable icon, CharSequence name, CharSequence id) {
		this.name = name;
		this.icon = icon;
		this.id = id;
	}
}
