package de.spaetiberlin.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
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

import de.spaetiberlin.app.util.GeoUtil;
import de.spaetiberlin.app.util.JsonUtil;

public class MainActivity extends SpaetiAbstractActivity {

  protected GoogleMap map;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final SlidingMenu shopInfo = new SlidingMenu(this);
    shopInfo.setMode(SlidingMenu.RIGHT);
    shopInfo.setSlidingEnabled(false);
    shopInfo.setFadeEnabled(false);
    shopInfo.setBehindWidth((int) (width / 1.3));
    shopInfo.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    shopInfo.setMenu(R.layout.shop_detail_side);
    shopInfo.setOnClosedListener(new OnClosedListener() {

      @Override
      public void onClosed() {
        shopInfo.setSlidingEnabled(false);
        sidemenu.setSlidingEnabled(true);
      }
    });

    final Map<String, String> markersAndStores = new HashMap<String, String>();

    final FragmentManager myFragmentManager = getSupportFragmentManager();
    final SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
        .findFragmentById(R.id.map);
    map = mySupportMapFragment.getMap();

    map.getUiSettings().setMyLocationButtonEnabled(true);
    map.setMyLocationEnabled(true);

    try {
      final Location location = GeoUtil.getLastKnownLocation(this);
      moveMap(location.getLatitude(), location.getLongitude());
    } catch (final Exception e) {
      moveMap(GeoUtil.BERLIN, 11);
    }

    map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

      @Override
      public void onInfoWindowClick(final Marker arg0) {
        shopInfo.showMenu();
        shopInfo.setSlidingEnabled(true);
        sidemenu.setSlidingEnabled(false);
        JsonUtil.getJSON(
            "http://spaeti.pavo.uberspace.de/dev/spaeti/" + markersAndStores.get(arg0.getId()),
            new Handler(new Handler.Callback() {

              @Override
              public boolean handleMessage(final Message msg) {
                try {
                  final JSONObject jsonObject = new JSONObject(msg.getData().getString("json"));
                  final JSONObject businessHours = jsonObject.getJSONObject("businessHours");
                  final JSONArray opened = businessHours.getJSONArray("opened");
                  final JSONArray closed = businessHours.getJSONArray("closed");
                  ((TextView) findViewById(R.id.shopNameText)).setText(jsonObject.getString("name"));
                  ((TextView) findViewById(R.id.shopAdressText)).setText(jsonObject.getJSONObject(
                      "location").getString("street"));
                  ((TextView) findViewById(R.id.mondayOpen)).setText(convertToTime(opened.getInt(0))
                      + " - " + convertToTime(closed.getInt(0)));
                  ((TextView) findViewById(R.id.tuesdayOpen)).setText(convertToTime(opened
                      .getInt(1)) + " - " + convertToTime(closed.getInt(1)));
                  ((TextView) findViewById(R.id.wednesdayOpen)).setText(convertToTime(opened
                      .getInt(2)) + " - " + convertToTime(closed.getInt(2)));
                  ((TextView) findViewById(R.id.thursdayOpen)).setText(convertToTime(opened
                      .getInt(3)) + " - " + convertToTime(closed.getInt(3)));
                  ((TextView) findViewById(R.id.fridayOpen)).setText(convertToTime(opened.getInt(4))
                      + " - " + convertToTime(closed.getInt(4)));
                  ((TextView) findViewById(R.id.saturdayOpen)).setText(convertToTime(opened
                      .getInt(5)) + " - " + convertToTime(closed.getInt(5)));
                  ((TextView) findViewById(R.id.sundayOpen)).setText(convertToTime(opened.getInt(6))
                      + " - " + convertToTime(closed.getInt(6)));
                } catch (final JSONException e) {
                  e.printStackTrace();
                }

                return true;
              }
            }));
      }
    });

    JsonUtil.getJSON("http://spaeti.pavo.uberspace.de/dev/spaeti/", new Handler(
        new Handler.Callback() {

          @Override
          public boolean handleMessage(final Message msg) {
            try {
              final JSONArray jsonArray = new JSONArray(msg.getData().getString("json"));
              for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                final JSONObject spaetiLocation = jsonObject.getJSONObject("location");
                markersAndStores.put(
                    map.addMarker(
                        new MarkerOptions().position(
                            new LatLng(spaetiLocation.getDouble("lat"), spaetiLocation
                                .getDouble("lng"))).title(jsonObject.getString("name"))).getId(),
                    jsonObject.getString("_id"));
              }
            } catch (final JSONException e) {
              e.printStackTrace();
            }

            return true;
          }
        }));

    handleIntent(getIntent());
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

  public void onStarClick(final View view) {
    final ImageButton button = (ImageButton) view;
    button.setImageResource(R.drawable.rating_important_dark);
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
  public boolean onCreateOptionsMenu(final Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getSupportMenuInflater().inflate(R.menu.main, menu);
    final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    // Assumes current activity is the searchable activity
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    return true;
  }

}
