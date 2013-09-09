package de.spaetiberlin.app.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class GeoUtil {

  public static final LatLng BERLIN = new LatLng(52.519171, 13.406091);

  public static Location getLastKnownLocation(final Context context) throws Exception {
    // Get the location manager
    final LocationManager locationManager = (LocationManager) context
        .getSystemService(Context.LOCATION_SERVICE);

    // Define the criteria how to select the location provider -> use
    // default
    return locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(),
        false));
  }

  public static Address getLocationFromName(final String name, final Context context)
      throws IOException {
    final Geocoder gc = new Geocoder(context, Locale.getDefault());
    Address address = null;
    if (Geocoder.isPresent()) {
      final List<Address> list = gc.getFromLocationName(name + ",Berlin", 1);

      if (list.size() > 0) {
        address = list.get(0);
      }
    }
    return address;
  }

  public static String getNameFromLocation(final double lat, final double lng, final Context context)
      throws IOException {
    final Geocoder gc = new Geocoder(context, Locale.getDefault());
    if (Geocoder.isPresent()) {
      final List<Address> list = gc.getFromLocation(lat, lng, 1);

      if (list.size() > 0) {
        final Address address = list.get(0);
        return address.getAddressLine(0);
      }
    }
    return "Fehler, bitte manuell eintragen";
  }

}
