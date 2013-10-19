package de.spaetiberlin.app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;

import de.spaetiberlin.app.models.Shop;
import de.spaetiberlin.app.util.FavoriteManager;
import de.spaetiberlin.app.util.ShopArrayAdapter;

public class FavoriteActivity extends SpaetiAbstractActivity {

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_favorites);

    final ListView listView = (ListView) findViewById(R.id.listview);

    final Shop[] shops = FavoriteManager.getInstance().getFavorites();

    final ShopArrayAdapter adapter = new ShopArrayAdapter(this,
        android.R.layout.simple_list_item_1, shops);

    listView.setAdapter(adapter);

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(final AdapterView<?> parent, final View view, final int position,
          final long id) {
        goToMap(shops[position], true);
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getSupportMenuInflater().inflate(R.menu.favorites, menu);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    return true;
  }
}
