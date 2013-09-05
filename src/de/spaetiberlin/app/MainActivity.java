package de.spaetiberlin.app;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;

import de.spaetiberlin.app.models.Shop;
import de.spaetiberlin.app.util.CallBackHandler;
import de.spaetiberlin.app.util.DownloadImageTask;
import de.spaetiberlin.app.util.GeoUtil;
import de.spaetiberlin.app.util.ServerUtil;

public class MainActivity extends SpaetiAbstractActivity {

  protected GoogleMap map;
  protected SlidingMenu shopInfo;
  private Shop selectedShop;
  private List<String> favorites;
  private final int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
  final Map<String, Shop> markersAndStores = new HashMap<String, Shop>();
  private SharedPreferences settings;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // initiate the shopinfo slide menu to the right
    shopInfo = new SlidingMenu(this);
    shopInfo.setMode(SlidingMenu.RIGHT);
    shopInfo.setSlidingEnabled(false);
    shopInfo.setFadeEnabled(false);
    shopInfo.setBehindScrollScale(0);
    shopInfo.setBehindWidth((int) (width / 1.2));
    shopInfo.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    shopInfo.setMenu(R.layout.shop_detail_side);
    shopInfo.setOnClosedListener(new OnClosedListener() {

      @Override
      public void onClosed() {
        shopInfo.setSlidingEnabled(false);
        sidemenu.setSlidingEnabled(true);
      }
    });

    // get all the views in the shop info
    final TextView shopNameText = (TextView) findViewById(R.id.shopNameText);
    final TextView shopAdressText = (TextView) findViewById(R.id.shopAdressText);

    final TextView mondayOpenText = (TextView) findViewById(R.id.mondayOpen);
    final TextView tuesdayOpenText = (TextView) findViewById(R.id.tuesdayOpen);
    final TextView wednesdayOpenText = (TextView) findViewById(R.id.wednesdayOpen);
    final TextView thursdayOpenText = (TextView) findViewById(R.id.thursdayOpen);
    final TextView fridayOpenText = (TextView) findViewById(R.id.fridayOpen);
    final TextView saturdayOpenText = (TextView) findViewById(R.id.saturdayOpen);
    final TextView sundayOpenText = (TextView) findViewById(R.id.sundayOpen);

    final ImageView spaetiImage = (ImageView) findViewById(R.id.spaetiImage);
    final ImageView chipsImage = (ImageView) findViewById(R.id.chipsImage);
    final ImageView pizzaImage = (ImageView) findViewById(R.id.pizzaImage);
    final ImageView condomImage = (ImageView) findViewById(R.id.condomImage);
    final ImageView newspaperImage = (ImageView) findViewById(R.id.newspaperImage);

    final ImageButton starButton = (ImageButton) findViewById(R.id.starButton);

    // make today green

    final int darkGreen = Color.parseColor("#55aa55");

    switch (dayOfWeek) {
      case 1:
        ((TextView) findViewById(R.id.sunday)).setTextColor(darkGreen);
        sundayOpenText.setTextColor(darkGreen);
        break;
      case 2:
        ((TextView) findViewById(R.id.monday)).setTextColor(darkGreen);
        mondayOpenText.setTextColor(darkGreen);
        break;
      case 3:
        ((TextView) findViewById(R.id.tuesday)).setTextColor(darkGreen);
        tuesdayOpenText.setTextColor(darkGreen);
        break;
      case 4:
        ((TextView) findViewById(R.id.wednesday)).setTextColor(darkGreen);
        wednesdayOpenText.setTextColor(darkGreen);
        break;
      case 5:
        ((TextView) findViewById(R.id.thursday)).setTextColor(darkGreen);
        thursdayOpenText.setTextColor(darkGreen);
        break;
      case 6:
        ((TextView) findViewById(R.id.friday)).setTextColor(darkGreen);
        fridayOpenText.setTextColor(darkGreen);
        break;
      case 7:
        ((TextView) findViewById(R.id.saturday)).setTextColor(darkGreen);
        saturdayOpenText.setTextColor(darkGreen);
        break;
      default:
        // this can not logically happen
    }

    final SupportMapFragment mySupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    map = mySupportMapFragment.getMap();

    map.getUiSettings().setMyLocationButtonEnabled(true);
    map.setMyLocationEnabled(true);

    // try to get the last know location and move the map there
    // sometimes on crashes slower devices, so we default to center of berlin
    try {
      final Location location = GeoUtil.getLastKnownLocation(this);
      moveMap(location.getLatitude(), location.getLongitude());
    } catch (final Exception e) {
      moveMap(GeoUtil.BERLIN, 11);
    }

    settings = getPreferences(MODE_PRIVATE);

    String serialized = settings.getString("favorites", null);
    if (serialized != null) {
      favorites = new LinkedList<String>(Arrays.asList(TextUtils.split(serialized, ",")));
    } else {
      favorites = new LinkedList<String>();
    }

    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    final Date today = new Date(System.currentTimeMillis() - 1800 * 1000);
    try {
      final Date lastUpdate = df.parse(settings.getString("lastUpdate", "27/05/1991"));
      // update the database if half an hour has passed
      Log.i(getLocalClassName(), "Updating...");
      if (lastUpdate.before(today)) {

        ServerUtil.updateSpaetis(this, new CallBackHandler() {

          @Override
          public void callBack() {
            loadSpaetis();

            // write new date to settings
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString("lastUpdate", df.format(today));
            editor.commit();
          }
        });

      }
      loadSpaetis();
    } catch (final ParseException e1) {
      e1.printStackTrace();
    }

    map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

      @Override
      public void onInfoWindowClick(final Marker arg0) {
        shopInfo.showMenu();
        shopInfo.setSlidingEnabled(true);
        sidemenu.setSlidingEnabled(false);
        try {
          selectedShop = markersAndStores.get(arg0.getId());

          new DownloadImageTask(spaetiImage)
              .execute("http://maps.googleapis.com/maps/api/streetview?size=600x300&location="
                  + selectedShop.lat + "," + selectedShop.lng
                  + "&fov=120&heading=0&pitch=10&sensor=true");

          shopNameText.setText(selectedShop.name);
          shopAdressText.setText(selectedShop.street);

          mondayOpenText.setText(convertToTime(selectedShop.opened.getInt(0)) + " - "
              + convertToTime(selectedShop.closed.getInt(0)));
          tuesdayOpenText.setText(convertToTime(selectedShop.opened.getInt(1)) + " - "
              + convertToTime(selectedShop.closed.getInt(1)));
          wednesdayOpenText.setText(convertToTime(selectedShop.opened.getInt(2)) + " - "
              + convertToTime(selectedShop.closed.getInt(2)));
          thursdayOpenText.setText(convertToTime(selectedShop.opened.getInt(3)) + " - "
              + convertToTime(selectedShop.closed.getInt(3)));
          fridayOpenText.setText(convertToTime(selectedShop.opened.getInt(4)) + " - "
              + convertToTime(selectedShop.closed.getInt(4)));
          saturdayOpenText.setText(convertToTime(selectedShop.opened.getInt(5)) + " - "
              + convertToTime(selectedShop.closed.getInt(5)));
          sundayOpenText.setText(convertToTime(selectedShop.opened.getInt(6)) + " - "
              + convertToTime(selectedShop.closed.getInt(6)));

          pizzaImage.setVisibility(selectedShop.pizza ? View.VISIBLE : View.INVISIBLE);
          condomImage.setVisibility(selectedShop.condoms ? View.VISIBLE : View.INVISIBLE);
          newspaperImage.setVisibility(selectedShop.newspapers ? View.VISIBLE : View.INVISIBLE);
          chipsImage.setVisibility(selectedShop.chips ? View.VISIBLE : View.INVISIBLE);

          if (selectedShop.favorite) {
            starButton.setImageResource(R.drawable.rating_important_dark);
          } else {
            starButton.setImageResource(R.drawable.rating_not_important_dark);
          }
        } catch (final JSONException e) {
          e.printStackTrace();
        }
      }
    });

    handleIntent(getIntent());
  }

  private void loadSpaetis() {
    try {
      final String data = ServerUtil.getStringFromFile("spaetis.json", this);
      try {
        final JSONArray jsonArray = new JSONArray(data);
        for (int i = 0; i < jsonArray.length(); i++) {
          final Shop shop = new Shop(jsonArray.getJSONObject(i));

          shop.favorite = favorites.contains(shop.id);

          int newDayOfWeek = dayOfWeek - 1;
          if (dayOfWeek == 1) {
            newDayOfWeek = 6;
          }

          markersAndStores.put(
              map.addMarker(
                  new MarkerOptions()
                      .position(new LatLng(shop.lat, shop.lng))
                      .title(shop.name)
                      .snippet(
                          "gešffnet: " + convertToTime(shop.opened.getInt(newDayOfWeek)) + " - "
                              + convertToTime(shop.closed.getInt(newDayOfWeek)))).getId(), shop);
        }

      } catch (final JSONException e) {
        e.printStackTrace();
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void moveMap(final LatLng latLng, final int zoom) {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
  }

  public void moveMap(final double lat, final double lng) {
    moveMap(new LatLng(lat, lng), 14);
  }

  public void moveMap(final double lat, final double lng, final int zoom) {
    moveMap(new LatLng(lat, lng), zoom);
  }

  private String convertToTime(final int value) {
    return String.format(Locale.getDefault(), "%02d:%02d", value / 100, value - value / 100 * 100);
  }

  public void saveFavorites() {
    final SharedPreferences.Editor editor = settings.edit();
    editor.putString("favorites", TextUtils.join(",", favorites));
    editor.commit();
  }

  public void onStarClick(final View view) {
    final ImageButton button = (ImageButton) view;
    if (selectedShop.favorite) {
      favorites.remove(favorites.indexOf(selectedShop.id));
      saveFavorites();
      selectedShop.favorite = false;
      button.setImageResource(R.drawable.rating_not_important_dark);
    } else {
      favorites.add(selectedShop.id);
      saveFavorites();
      selectedShop.favorite = true;
      button.setImageResource(R.drawable.rating_important_dark);
    }
  }

  public void shareSelected(final View view) {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    String shareBody = selectedShop.name + "\n" + selectedShop.street;
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, selectedShop.name);
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    startActivity(Intent.createChooser(sharingIntent, "SpŠti Senden"));
  }

  public void navigateToSelected(final View view) {
    Uri uri = Uri.parse("geo:" + selectedShop.lat + "," + selectedShop.lng);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  @Override
  public void onNewIntent(final Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    handleIntent(intent);
  }

  protected void handleIntent(final Intent intent) {
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      final String query = intent.getStringExtra(SearchManager.QUERY);
      try {
        final Address address = GeoUtil.getLocationFromName(query, this);
        if (address != null) {
          moveMap(address.getLatitude(), address.getLongitude());
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public boolean onKeyDown(final int keyCode, final KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      if (shopInfo.isMenuShowing()) {
        shopInfo.showContent();
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);

  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (shopInfo.isMenuShowing()) {
          shopInfo.showContent();
          return true;
        }
        sidemenu.toggle();
        return true;
      case R.id.action_search:
        if (shopInfo.isMenuShowing()) {
          shopInfo.showContent();
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getSupportMenuInflater().inflate(R.menu.map, menu);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    // Assumes current activity is the searchable activity
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    return true;
  }

}
