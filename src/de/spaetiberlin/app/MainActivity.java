package de.spaetiberlin.app;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;

import de.spaetiberlin.app.util.JsonUtil;

public class MainActivity extends SpaetiAbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		final GoogleMap mMap = mySupportMapFragment.getMap();

		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use
		// default
		String provider = locationManager.getBestProvider(new Criteria(), false);

		locationManager.requestLocationUpdates(provider, 100, 1, new LocationListener() {

			public void onLocationChanged(Location location) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						location.getLatitude(), location.getLongitude()), 14));
			}

			@Override
			public void onProviderDisabled(String arg0) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
		});

		try {
			Location location = locationManager.getLastKnownLocation(provider);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
					location.getLongitude()), 14));
		} catch (Exception e) {
			e.printStackTrace();
		}

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				shopInfo.showMenu();
				shopInfo.setSlidingEnabled(true);
				sidemenu.setSlidingEnabled(false);
				JsonUtil.getJSON(
						"http://spaeti.pavo.uberspace.de/dev/spaeti/"
								+ markersAndStores.get(arg0.getId()), new Handler(
								new Handler.Callback() {

									@Override
									public boolean handleMessage(Message msg) {
										try {
											JSONObject jsonObject = new JSONObject(msg.getData()
													.getString("json"));
											JSONObject businessHours = jsonObject
													.getJSONObject("businessHours");
											JSONArray opened = businessHours.getJSONArray("opened");
											JSONArray closed = businessHours.getJSONArray("closed");
											((TextView) findViewById(R.id.shopNameText))
													.setText(jsonObject.getString("name"));
											((TextView) findViewById(R.id.shopAdressText))
													.setText(jsonObject.getJSONObject("location")
															.getString("street"));
											((TextView) findViewById(R.id.mondayOpen))
													.setText(convertToTime(opened.getInt(0))
															+ " - "
															+ convertToTime(closed.getInt(0)));
											((TextView) findViewById(R.id.tuesdayOpen))
													.setText(convertToTime(opened.getInt(1))
															+ " - "
															+ convertToTime(closed.getInt(1)));
											((TextView) findViewById(R.id.wednesdayOpen))
													.setText(convertToTime(opened.getInt(2))
															+ " - "
															+ convertToTime(closed.getInt(2)));
											((TextView) findViewById(R.id.thursdayOpen))
													.setText(convertToTime(opened.getInt(3))
															+ " - "
															+ convertToTime(closed.getInt(3)));
											((TextView) findViewById(R.id.fridayOpen))
													.setText(convertToTime(opened.getInt(4))
															+ " - "
															+ convertToTime(closed.getInt(4)));
											((TextView) findViewById(R.id.saturdayOpen))
													.setText(convertToTime(opened.getInt(5))
															+ " - "
															+ convertToTime(closed.getInt(5)));
											((TextView) findViewById(R.id.sundayOpen))
													.setText(convertToTime(opened.getInt(6))
															+ " - "
															+ convertToTime(closed.getInt(6)));
										} catch (JSONException e) {
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
					public boolean handleMessage(Message msg) {
						try {
							JSONArray jsonArray = new JSONArray(msg.getData().getString("json"));
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								JSONObject spaetiLocation = jsonObject.getJSONObject("location");
								double lat = spaetiLocation.getDouble("lat");
								double lng = spaetiLocation.getDouble("lng");
								markersAndStores.put(
										mMap.addMarker(
												new MarkerOptions().position(new LatLng(lat, lng))
														.title(jsonObject.getString("name")))
												.getId(), jsonObject.getString("_id"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						return true;
					}
				}));

	}

	private String convertToTime(int value) {
		return String.format(Locale.getDefault(), "%02d:%02d", value / 100,
				(value - value / 100 * 100));
	}

	public void onStarClick(View view) {
		ImageButton button = ((ImageButton) view);
		button.setImageResource(R.drawable.rating_important_dark);
	}

}
