package de.spaetiberlin.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.spaetiberlin.app.util.GeoUtil;
import de.spaetiberlin.app.util.JsonUtil;
import de.spaetiberlin.app.widgets.RangeBox;
import de.spaetiberlin.app.widgets.TimePickerButton;

public class AddSpaetiActivity extends SherlockFragmentActivity {

  protected GoogleMap map;
  protected EditText addressText;
  protected EditText nameText;
  protected LinearLayout openingContainer;
  protected Marker marker;
  protected Map<RangeBox, List<TimePickerButton>> openingMap;

  protected ImageButton pizzaButton;
  protected ImageButton condomButton;
  protected ImageButton newspaperButton;
  protected ImageButton chipsButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_spaeti);

    openingContainer = (LinearLayout) findViewById(R.id.openingContainer);

    addressText = (EditText) findViewById(R.id.addressInput);
    nameText = (EditText) findViewById(R.id.nameInput);

    openingMap = new HashMap<RangeBox, List<TimePickerButton>>();

    pizzaButton = (ImageButton) findViewById(R.id.pizza_button);
    condomButton = (ImageButton) findViewById(R.id.condom_button);
    newspaperButton = (ImageButton) findViewById(R.id.newspaper_button);
    chipsButton = (ImageButton) findViewById(R.id.chips_button);

    if (android.os.Build.VERSION.SDK_INT >= 11) {
      pizzaButton.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(final View v) {
          if (pizzaButton.getAlpha() > 0.3) {
            pizzaButton.setAlpha((float) 0.2);
          } else {
            pizzaButton.setAlpha((float) 1);
          }
        }
      });
      condomButton.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(final View v) {
          if (condomButton.getAlpha() > 0.3) {
            condomButton.setAlpha((float) 0.2);
          } else {
            condomButton.setAlpha((float) 1);
          }
        }
      });
      newspaperButton.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(final View v) {
          if (newspaperButton.getAlpha() > 0.3) {
            newspaperButton.setAlpha((float) 0.2);
          } else {
            newspaperButton.setAlpha((float) 1);
          }
        }
      });
      chipsButton.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(final View v) {
          if (chipsButton.getAlpha() > 0.3) {
            chipsButton.setAlpha((float) 0.2);
          } else {
            chipsButton.setAlpha((float) 1);
          }
        }
      });
    }

    final ImageButton searchGeoButton = (ImageButton) findViewById(R.id.searchGeoButton);

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
            searchGeoButton.setBackgroundResource(R.drawable.green_button);
            searchGeoButton.setImageResource(R.drawable.navigation_accept);
          }
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    });

    addressText.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(final CharSequence s, final int start, final int before,
          final int count) {
        // TODO Auto-generated method stub

      }

      @Override
      public void beforeTextChanged(final CharSequence s, final int start, final int count,
          final int after) {
        // TODO Auto-generated method stub

      }

      @Override
      public void afterTextChanged(final Editable s) {
        searchGeoButton.setBackgroundResource(R.drawable.grey_button);
        searchGeoButton.setImageResource(R.drawable.location_place);
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
        addressText.setTextColor(Color.GRAY);
        addressText.setText("Loslassen um Adresse festzulegen");
      }

      @Override
      public void onMarkerDragEnd(final Marker arg0) {
        addressText.setTextColor(Color.BLACK);
        final LatLng position = arg0.getPosition();
        setAddressText(position);
        searchGeoButton.setBackgroundResource(R.drawable.green_button);
        searchGeoButton.setImageResource(R.drawable.navigation_accept);
      }

      @Override
      public void onMarkerDrag(final Marker arg0) {
      }
    });

    addOpenings(null);
  }

  public void addOpenings(View view) {
    final LinearLayout container = new LinearLayout(this);
    final List<TimePickerButton> list = new ArrayList<TimePickerButton>();

    final RangeBox rangeBox = new RangeBox(this);

    final TimePickerButton timePickerButton = new TimePickerButton(this, 9, 30);
    final TimePickerButton timePickerButton2 = new TimePickerButton(this, 22, 30);

    final TextView textView = new TextView(this);
    textView.setText("bis");

    container.addView(rangeBox);
    container.addView(timePickerButton);
    container.addView(textView);
    container.addView(timePickerButton2);

    openingContainer.addView(container);
    list.add(timePickerButton);
    list.add(timePickerButton2);
    openingMap.put(rangeBox, list);
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

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.submit_spaeti:
        submitSpaeti();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void submitSpaeti() {
    try {
      JSONObject spaeti = new JSONObject();
      spaeti.put("name", nameText.getText().toString());
      JSONObject location = new JSONObject();
      location.put("lng", marker.getPosition().longitude);
      location.put("lat", marker.getPosition().latitude);
      location.put("street", addressText.getText().toString());
      spaeti.put("location", location);

      JSONObject assortment = new JSONObject();
      if (android.os.Build.VERSION.SDK_INT >= 11) {
        assortment.put("pizza", pizzaButton.getAlpha() > 0.3 ? true : false);
        assortment.put("condoms", condomButton.getAlpha() > 0.3 ? true : false);
        assortment.put("newspapers", newspaperButton.getAlpha() > 0.3 ? true : false);
        assortment.put("chips", chipsButton.getAlpha() > 0.3 ? true : false);
      }
      spaeti.put("assortment", assortment);

      Integer[] open = new Integer[] { null, null, null, null, null, null, null };
      Integer[] closed = new Integer[] { null, null, null, null, null, null, null };

      for (RangeBox key : openingMap.keySet()) {
        final TimePickerButton startTimePicker = openingMap.get(key).get(0);
        final TimePickerButton endTimePicker = openingMap.get(key).get(1);
        final Integer startTime = startTimePicker.getHourOfDay() * 100
            + startTimePicker.getMinute();
        final Integer endTime = endTimePicker.getHourOfDay() * 100 + endTimePicker.getMinute();
        final String[] selected = key.getSelected();
        for (int i = 0; i < selected.length; i++) {
          if (selected[i] != null) {
            open[i] = startTime;
            closed[i] = endTime;
          }
        }
      }

      JSONArray openJson = new JSONArray();
      JSONArray closedJson = new JSONArray();

      for (int i = 0; i < open.length; i++) {
        if (open[i] == null) {
          open[i] = 0;
        }
        openJson.put(i, open[i]);
        if (closed[i] == null) {
          closed[i] = 0;
        }
        closedJson.put(i, closed[i]);
      }

      JSONObject businessHours = new JSONObject();
      businessHours.put("opened", openJson);
      businessHours.put("closed", closedJson);

      spaeti.put("businessHours", businessHours);

      JsonUtil.putJSON("http://spaeti.pavo.uberspace.de/dev/spaeti/", spaeti.toString(),
          new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
              if (msg.getData().getInt("status") == 200) {
                Toast toast = Toast.makeText(AddSpaetiActivity.this, "Späti erfolgreich erstellt",
                    Toast.LENGTH_LONG);
                toast.show();
              } else {
                Toast toast = Toast.makeText(AddSpaetiActivity.this, "Fehler beim erstellen.",
                    Toast.LENGTH_LONG);
                toast.show();
              }
              return false;
            }
          }));

      Intent intent = new Intent(this, MainActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

      startActivity(intent);

    } catch (JSONException e) {
      e.printStackTrace();
    }

  }
}
