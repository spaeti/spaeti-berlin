package de.spaetiberlin.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;

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

		// gets the width of the screen
		int width;
		Display display = getWindowManager().getDefaultDisplay();
		// if android version is >=13, we can use display.getSize, otherwise, deprecated fallback
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth();
		}

		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setFadeDegree(0.35f);
		menu.setBehindWidth(width / 2);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.sidemenu);

		// Get the location manager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the location provider -> use default
		String provider = locationManager.getBestProvider(new Criteria(), false);
		Location location = locationManager.getLastKnownLocation(provider);

		SupportMapFragment mySupportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		final GoogleMap mMap = mySupportMapFragment.getMap();

		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
				location.getLongitude()), 14));

		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				try {
					JSONArray jsonArray = new JSONArray(msg.getData().getString("spaetis"));
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						JSONObject spaetiLocation = jsonObject.getJSONObject("location");
						mMap.addMarker(new MarkerOptions().position(
								new LatLng(spaetiLocation.getDouble("lat"), spaetiLocation
										.getDouble("lng"))).title(jsonObject.getString("name")));
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
				String data = JsonUtil.getJSON("http://spaeti.pavo.uberspace.de/dev/spaeti/");
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
