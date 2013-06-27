package de.spaetiberlin.app;

import java.io.IOException;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.spaetiberlin.app.util.GeoUtil;

public class AddSpaetiActivity extends SherlockFragmentActivity {

  protected GoogleMap map;
  protected EditText addressText;
  protected Marker marker;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_spaeti);

    addressText = (EditText) findViewById(R.id.addressInput);

    final Button searchGeoButton = (Button) findViewById(R.id.searchGeoButton);

    searchGeoButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(final View v) {
        try {
          final Address locationFromName = GeoUtil.getLocationFromName(addressText.getText()
              .toString(), AddSpaetiActivity.this);
          if (locationFromName != null) {
            final double latitude = locationFromName.getLatitude();
            final double longitude = locationFromName.getLongitude();
            marker.setPosition(new LatLng(latitude, longitude));
            moveMap(latitude, longitude);
            setAddressText(new LatLng(latitude, longitude));
          }
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    });

    final FragmentManager myFragmentManager = getSupportFragmentManager();
    final SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
        .findFragmentById(R.id.addSpaetiMap);
    map = mySupportMapFragment.getMap();

    map.getUiSettings().setMyLocationButtonEnabled(true);
    map.setMyLocationEnabled(true);

    try {
      final Location location = GeoUtil.getLastKnownLocation(this);
      moveMap(location.getLatitude(), location.getLongitude());
      marker = map.addMarker(new MarkerOptions().draggable(true).position(
          new LatLng(location.getLatitude(), location.getLongitude())));
    } catch (final Exception e) {
      moveMap(GeoUtil.BERLIN, 10);
      marker = map.addMarker(new MarkerOptions().draggable(true).position(GeoUtil.BERLIN));
    }

    map.setOnMarkerDragListener(new OnMarkerDragListener() {

      @Override
      public void onMarkerDragStart(final Marker arg0) {
        addressText.setTextColor(5000);
        addressText.setText("Loslassen um Adresse festzulegen");
      }

      @Override
      public void onMarkerDragEnd(final Marker arg0) {
        final LatLng position = arg0.getPosition();
        setAddressText(position);
      }

      @Override
      public void onMarkerDrag(final Marker arg0) {
      }
    });

  }

  public void moveMap(final LatLng latLng, final int zoom) {
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
  }

  public void moveMap(final double lat, final double lng) {
    moveMap(new LatLng(lat, lng), 15);
  }

  public void moveMap(final double lat, final double lng, final int zoom) {
    moveMap(new LatLng(lat, lng), zoom);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getSupportMenuInflater().inflate(R.menu.add_spaeti, menu);
    return true;
  }

  public void setAddressText(final LatLng position) {
    try {
      addressText.setText(GeoUtil.getNameFromLocation(position.latitude, position.longitude,
          AddSpaetiActivity.this));
    } catch (final IOException e) {
      addressText.setText("Fehler, bitte manuell eintragen.");
      e.printStackTrace();
    }
  }

}
