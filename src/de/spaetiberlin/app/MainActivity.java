package de.spaetiberlin.app;

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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.slidingmenu.lib.SlidingMenu;

import de.spaetiberlin.app.util.JsonUtil;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setFadeDegree(0.35f);
		menu.setBehindWidth(500);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.sidemenu);

		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		final GoogleMap mMap = mySupportMapFragment.getMap();

		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);
		// Get the location manager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use
		// default
		String provider = locationManager
				.getBestProvider(new Criteria(), false);

		locationManager.requestLocationUpdates(provider, 100, 1,
				new LocationListener() {
					public void onLocationChanged(Location location) {
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(location.getLatitude(), location
										.getLongitude()), 14));
					}

					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub

					}
				});

		try {
			Location location = locationManager.getLastKnownLocation(provider);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 14));
		} catch (Exception e) {
			e.printStackTrace();
		}

		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				try {
					JSONArray jsonArray = new JSONArray(msg.getData()
							.getString("spaetis"));
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						JSONObject spaetiLocation = jsonObject
								.getJSONObject("location");
						double lat = spaetiLocation.getDouble("lat");
						double lng = spaetiLocation.getDouble("lng");
						mMap.addMarker(new MarkerOptions().position(
								new LatLng(lat, lng)).title(
								jsonObject.getString("name")));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return true;
			}
		});

		Thread thread = new Thread() {

			@Override
			public void run() {

				String data = JsonUtil
						.getJSON("http://spaeti.pavo.uberspace.de/dev/spaeti/");
				Message m = new Message();
				Bundle b = new Bundle();
				b.putString("spaetis", data);
				m.setData(b);
				handler.sendMessage(m);

			}
		};

		thread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
