package de.spaetiberlin.app;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

import de.spaetiberlin.app.models.Shop;

public abstract class SpaetiAbstractActivity extends SherlockFragmentActivity {

  protected static int width = 0;
  protected SlidingMenu sidemenu;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // performance: only get size on first start
    if (width == 0) {
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
    }

    sidemenu = new SlidingMenu(this);
    sidemenu.setMode(SlidingMenu.LEFT);
    sidemenu.setFadeEnabled(true);
    sidemenu.setFadeDegree(0.50f);
    sidemenu.setBehindWidth((int) (width / 1.5));
    sidemenu.setShadowWidth(20);
    sidemenu.setBehindScrollScale(0);
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
        goToActivity(FavoriteActivity.class);
        return true;
      case R.id.action_map:
        goToMap(null, false);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void goToMap(final Shop spaeti, final boolean open) {
    sidemenu.showContent();
    if (!this.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
      final Intent myIntent = new Intent(this, MainActivity.class);
      if (spaeti != null) {
        myIntent.setAction("goToSpaeti");
        myIntent.putExtra("lat", spaeti.lat);
        myIntent.putExtra("lng", spaeti.lng);
        myIntent.putExtra("id", spaeti.id);
      }
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
          .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  // for starting in the menu
  public void goToMap(final View view) {
    goToMap(null, false);
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

  public <T> void goToActivity(final Class<T> activity) {
    sidemenu.showContent();
    if (!this.getClass().getSimpleName().equals(activity.getSimpleName())) {
      final Intent myIntent = new Intent(this, activity);
      myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
          .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(myIntent);
    }
  }

  public void goToFavorites(final View view) {
    goToActivity(FavoriteActivity.class);
  }

  public void goToSettings(final View view) {
    goToActivity(SettingsActivity.class);
  }

  public void goToAbout(final View view) {
    goToActivity(AboutActivity.class);
  }

  @Override
  public boolean onKeyDown(final int keycode, final KeyEvent e) {
    switch (keycode) {
      case KeyEvent.KEYCODE_MENU:
        sidemenu.showMenu();
        return true;
    }
    return super.onKeyDown(keycode, e);
  }

}
