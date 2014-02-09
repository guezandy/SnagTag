package com.snag.snagtag;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PurchasedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchased);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchased, menu);
		return true;
	}

}
