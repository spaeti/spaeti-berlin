package de.spaetiberlin.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.fortysevendeg.android.swipelistview.SwipeListView;

public class FavoriteActivity extends SpaetiAbstractActivity {

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_favorites);

    final SwipeListView swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);

    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, new String[] { "as", "asdfasd", "1231" });

    swipeListView.setAdapter(adapter);
  }
}
