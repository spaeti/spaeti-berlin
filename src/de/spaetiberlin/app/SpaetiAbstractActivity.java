package de.spaetiberlin.app;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.View;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public abstract class SpaetiAbstractActivity extends SherlockFragmentActivity {

  protected int width;
  protected SlidingMenu sidemenu;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Display display = getWindowManager().getDefaultDisplay();
    // if android version is >=13, we can use display.getSize, otherwise,
    // deprecated fallback
    if (android.os.Build.VERSION.SDK_INT >= 13) {
      final Point size = new Point();
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
  public boolean onCreateOptionsMenu(final Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getSupportMenuInflater().inflate(R.menu.main, menu);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      sidemenu.toggle();
      return true;
    case R.id.action_add:
      goToAddSpaeti();
      return true;
    case R.id.action_favorites:
      goToFavorites();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  public void goToMap(final View view) {
    sidemenu.showContent();
    if (!this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
      final Intent myIntent = new Intent(this, MainActivity.class);
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
          .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  public void goToAddSpaeti() {
    if (!this.getClass().getSimpleName().equals(AddSpaetiActivity.class.getSimpleName())) {
      final Intent myIntent = new Intent(this, AddSpaetiActivity.class);
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  // for starting in the menu
  public void goToAddSpaeti(final View view) {
    goToAddSpaeti();
  }

  public void goToFavorites() {
    sidemenu.showContent();
    if (!this.getClass().getSimpleName().equals(FavoriteActivity.class.getSimpleName())) {
      final Intent myIntent = new Intent(this, FavoriteActivity.class);
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
          .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  // for starting in the menu
  public void goToFavorites(final View view) {
    goToFavorites();
  }

  public void goToSettings(final View view) {
    sidemenu.showContent();
    if (!this.getClass().getSimpleName().equals(SettingsActivity.class.getSimpleName())) {
      final Intent myIntent = new Intent(this, SettingsActivity.class);
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
          .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  public void goToAbout(final View view) {
    sidemenu.showContent();
    if (!this.getClass().getSimpleName().equals(AboutActivity.class.getSimpleName())) {
      final Intent myIntent = new Intent(this, AboutActivity.class);
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
          .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  public void showTimePicker() {
    new TimePickerDialog(this, new OnTimeSetListener() {

      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub

      }
    }, 9, 30, DateFormat.is24HourFormat(this));
  }

}
