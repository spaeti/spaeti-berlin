package de.spaetiberlin.app;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public abstract class SpaetiAbstractActivity extends SherlockFragmentActivity {

	protected int width;
	protected SlidingMenu sidemenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display display = getWindowManager().getDefaultDisplay();
		// if android version is >=13, we can use display.getSize, otherwise, deprecated fallback
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth();
		}

		sidemenu = new SlidingMenu(this);
		sidemenu.setMode(SlidingMenu.LEFT);
		sidemenu.setFadeDegree(0.35f);
		sidemenu.setBehindWidth((int) (width / 1.5));
		sidemenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		sidemenu.setMenu(R.layout.sidemenu);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sidemenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void goToMap(View view) {
		sidemenu.showContent();
		if (!this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
			Intent myIntent = new Intent(this, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
					.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(myIntent);
		}
	}

	public void goToAddSpaeti(View view) {
		// if (!this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
		// Intent myIntent = new Intent(this, MainActivity.class);
		// startActivity(myIntent);
		// }
	}

	public void goToFavorites(View view) {
		sidemenu.showContent();
		if (!this.getClass().getSimpleName().equals(FavoriteActivity.class.getSimpleName())) {
			Intent myIntent = new Intent(this, FavoriteActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
					.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(myIntent);
		}
	}

	public void goToSettings(View view) {
		sidemenu.showContent();
		if (!this.getClass().getSimpleName().equals(SettingsActivity.class.getSimpleName())) {
			Intent myIntent = new Intent(this, SettingsActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
					.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(myIntent);
		}
	}

	public void goToAbout(View view) {
		sidemenu.showContent();
		if (!this.getClass().getSimpleName().equals(AboutActivity.class.getSimpleName())) {
			Intent myIntent = new Intent(this, AboutActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
					.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
					.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(myIntent);
		}
	}

}
