package de.spaetiberlin.app;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import de.spaetiberlin.app.models.Shop;
import de.spaetiberlin.app.util.CallBackHandler;
import de.spaetiberlin.app.util.FavoriteManager;
import de.spaetiberlin.app.util.GeoUtil;
import de.spaetiberlin.app.util.ServerUtil;

public class MainActivity extends SpaetiAbstractActivity {

  protected GoogleMap map;
  protected DrawerLayout drawerLayout;
  private ScrollView shopInfo;
  private Shop selectedShop;
  private final int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
  final Map<String, Shop> markersAndStores = new HashMap<String, Shop>();
  final Map<String, Shop> storeMap = new HashMap<String, Shop>();
  private SharedPreferences settings;

  TextView shopNameText;
  TextView shopAdressText;

  TextView mondayOpenText;
  TextView tuesdayOpenText;
  TextView wednesdayOpenText;
  TextView thursdayOpenText;
  TextView fridayOpenText;
  TextView saturdayOpenText;
  TextView sundayOpenText;

  ImageView spaetiImage;
  ImageView chipsImage;
  ImageView pizzaImage;
  ImageView condomImage;
  ImageView newspaperImage;

  ProgressBar imageLoadingIndicator;
  DisplayImageOptions displayImageOptions;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    settings = getPreferences(MODE_PRIVATE);

    // init favorite manager
    FavoriteManager.getInstance().init(this);

    final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        getApplicationContext()).build();
    ImageLoader.getInstance().init(config);
    displayImageOptions = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
        .cacheInMemory(true).displayer(new FadeInBitmapDisplayer(300)).build();

    // initialize shopinfo drawer to the right
    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawerLayout.setScrimColor(Color.parseColor("#77eeeeee"));
    shopInfo = (ScrollView) findViewById(R.id.right_drawer);
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, shopInfo);

    // get all the views in the shop info
    shopNameText = (TextView) findViewById(R.id.shopNameText);
    shopAdressText = (TextView) findViewById(R.id.shopAdressText);

    mondayOpenText = (TextView) findViewById(R.id.mondayOpen);
    tuesdayOpenText = (TextView) findViewById(R.id.tuesdayOpen);
    wednesdayOpenText = (TextView) findViewById(R.id.wednesdayOpen);
    thursdayOpenText = (TextView) findViewById(R.id.thursdayOpen);
    fridayOpenText = (TextView) findViewById(R.id.fridayOpen);
    saturdayOpenText = (TextView) findViewById(R.id.saturdayOpen);
    sundayOpenText = (TextView) findViewById(R.id.sundayOpen);

    spaetiImage = (ImageView) findViewById(R.id.spaetiImage);
    chipsImage = (ImageView) findViewById(R.id.chipsImage);
    pizzaImage = (ImageView) findViewById(R.id.pizzaImage);
    condomImage = (ImageView) findViewById(R.id.condomImage);
    newspaperImage = (ImageView) findViewById(R.id.newspaperImage);

    imageLoadingIndicator = (ProgressBar) findViewById(R.id.imageLoadingIndicator);

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

    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

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

    final int updateRate = settings.getInt("updateRateInHours", 24 * 5);

    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
    final Date today = new Date(System.currentTimeMillis() - 3600000 * updateRate);

    try {
      final Date lastUpdate = df.parse(settings.getString("lastUpdate", "27/05/1991 00:00:00"));
      // update the database if enough time has passed
      if (lastUpdate.before(today)) {
        Toast.makeText(MainActivity.this, "Aktualisiere Spätidaten", Toast.LENGTH_SHORT).show();

        ServerUtil.updateSpaetis(this, new CallBackHandler() {

          @Override
          public void callBack() {
            loadSpaetis();

            // write new date to settings
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString("lastUpdate", df.format(new Date()));
            editor.commit();

            Toast.makeText(MainActivity.this, "Aktualisiert.", Toast.LENGTH_LONG).show();
          }
        });

      }
    } catch (final ParseException e1) {
      e1.printStackTrace();
      // write new date to settings in an attempt to fix the broken string
      final SharedPreferences.Editor editor = settings.edit();
      editor.putString("lastUpdate", df.format(today));
      editor.commit();
    }
    loadSpaetis();
    FavoriteManager.getInstance().setStoreMap(storeMap);

    map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

      @Override
      public void onInfoWindowClick(final Marker clickedMarker) {

        selectedShop = markersAndStores.get(clickedMarker.getId());

        showSelectedShopDetails();

      }

    });

    handleIntent(getIntent());
  }

  private void showSelectedShopDetails() {
    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, shopInfo);

    shopNameText.setText(selectedShop.name);
    shopAdressText.setText(selectedShop.street);

    pizzaImage.setVisibility(selectedShop.pizza ? View.VISIBLE : View.INVISIBLE);
    condomImage.setVisibility(selectedShop.condoms ? View.VISIBLE : View.INVISIBLE);
    newspaperImage.setVisibility(selectedShop.newspapers ? View.VISIBLE : View.INVISIBLE);
    chipsImage.setVisibility(selectedShop.chips ? View.VISIBLE : View.INVISIBLE);

    try {
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

    } catch (final JSONException e) {
      e.printStackTrace();
    }

    ImageLoader.getInstance().displayImage(
        "http://maps.googleapis.com/maps/api/streetview?size=600x300&location=" + selectedShop.lat
            + "," + selectedShop.lng + "&fov=120&heading=0&pitch=10&sensor=true", spaetiImage,
        displayImageOptions, new SimpleImageLoadingListener() {

          @Override
          public void onLoadingStarted(final String imageUri, final View view) {
            imageLoadingIndicator.setVisibility(View.VISIBLE);
          }

          @Override
          public void onLoadingComplete(final String imageUri, final View view,
              final Bitmap loadedImage) {
            imageLoadingIndicator.setVisibility(View.GONE);
          }
        });
    drawerLayout.openDrawer(shopInfo);
    drawerLayout.setDrawerListener(new SimpleDrawerListener() {

      @Override
      public void onDrawerOpened(final View drawerView) {
        supportInvalidateOptionsMenu();
      }

      @Override
      public void onDrawerClosed(final View drawerView) {
        supportInvalidateOptionsMenu();
      }

    });
  }

  private void loadSpaetis() {
    try {
      final String data = ServerUtil.getStringFromFile("spaetis.json", this);
      try {
        final JSONArray jsonArray = new JSONArray(data);
        for (int i = 0; i < jsonArray.length(); i++) {
          final Shop shop = new Shop(jsonArray.getJSONObject(i));

          shop.favorite = FavoriteManager.getInstance().isFavorite(shop.id);

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
                          "geöffnet: " + convertToTime(shop.opened.getInt(newDayOfWeek)) + " - "
                              + convertToTime(shop.closed.getInt(newDayOfWeek)))).getId(), shop);
          storeMap.put(shop.id, shop);
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

  private static String convertToTime(final int value) {
    return String.format(Locale.getDefault(), "%02d:%02d", value / 100, value - value / 100 * 100);
  }

  public void onStarClick(final MenuItem item) {
    if (selectedShop.favorite) {
      FavoriteManager.getInstance().remove(selectedShop.id);
      selectedShop.favorite = false;
      item.setIcon(R.drawable.rating_not_important);
    } else {
      FavoriteManager.getInstance().add(selectedShop.id);
      selectedShop.favorite = true;
      item.setIcon(R.drawable.rating_important_dark);
    }
  }

  public void shareSelected() {
    final Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, selectedShop.name);
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, selectedShop.name + "\n"
        + selectedShop.street);
    startActivity(Intent.createChooser(sharingIntent, "Späti Senden"));
  }

  public void navigateToSelected() {
    final Uri uri = Uri.parse("geo:" + selectedShop.lat + "," + selectedShop.lng);
    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
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
      final ProgressDialog dialog = new ProgressDialog(this);
      dialog.setMessage("Please wait...");
      dialog.setCancelable(false);
      dialog.show();

      final String query = intent.getStringExtra(SearchManager.QUERY);
      final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(final Message msg) {
          final Bundle bundle = msg.getData();
          dialog.hide();
          if (bundle.getBoolean("found")) {
            moveMap(bundle.getDouble("lat"), bundle.getDouble("lng"));
          } else {
            Toast
                .makeText(MainActivity.this, "Ort wurde leider nicht gefunden.", Toast.LENGTH_LONG)
                .show();
          }
          return false;
        }
      });
      final Thread thread = new Thread() {
        @Override
        public void run() {
          try {
            final Address address = GeoUtil.getLocationFromName(query, MainActivity.this);
            final Message m = new Message();
            final Bundle b = new Bundle();
            if (address != null) {
              b.putBoolean("found", true);
              b.putDouble("lat", address.getLatitude());
              b.putDouble("lng", address.getLongitude());
            } else {
              b.putBoolean("found", false);
            }
            m.setData(b);
            handler.sendMessage(m);
          } catch (final IOException e) {
            e.printStackTrace();
            // if something goes wrong, don't block the user with the loading
            // dialog
            dialog.hide();
          }
        };
      };

      thread.run();
    }
    if ("goToSpaeti".equals(intent.getAction())) {
      try {
        final Bundle bundle = intent.getExtras();
        moveMap(bundle.getDouble("lat"), bundle.getDouble("lng"));
        final String id = bundle.getString("id");
        if (id != null) {
          selectedShop = storeMap.get(id);
          showSelectedShopDetails();
        }
      } catch (final Exception e) {
        moveMap(GeoUtil.BERLIN, 11);
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (drawerLayout.isDrawerVisible(shopInfo)) {
          drawerLayout.closeDrawer(shopInfo);
          return true;
        }
        sidemenu.toggle();
        return true;
      case R.id.action_share:
        shareSelected();
        return true;
      case R.id.action_directions:
        navigateToSelected();
        return true;
      case R.id.action_addToFavorites:
        onStarClick(item);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    if (drawerLayout.isDrawerVisible(shopInfo)) {
      getSupportMenuInflater().inflate(R.menu.detail, menu);
      final MenuItem starButton = menu.findItem(R.id.action_addToFavorites);
      if (selectedShop != null) {
        if (selectedShop.favorite) {
          starButton.setIcon(R.drawable.rating_important_dark);
        } else {
          starButton.setIcon(R.drawable.rating_not_important);
        }
      } else {
        drawerLayout.closeDrawer(shopInfo);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, shopInfo);
      }

    } else {
      getSupportMenuInflater().inflate(R.menu.map, menu);
      final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
      final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
      // Assumes current activity is the searchable activity
      searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    return true;
  }

}
