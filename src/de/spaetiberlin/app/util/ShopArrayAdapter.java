package de.spaetiberlin.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.spaetiberlin.app.R;
import de.spaetiberlin.app.models.Shop;

public class ShopArrayAdapter extends ArrayAdapter<Shop> {

  private final Context context;
  private final Shop[] shops;

  public ShopArrayAdapter(final Context context, final int resource, final Shop[] objects) {
    super(context, R.layout.favorite_row, objects);

    this.context = context;
    this.shops = objects;
  }

  @Override
  public View getView(final int position, final View convertView, final ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View rowView = inflater.inflate(R.layout.favorite_row, parent, false);
    final TextView nameView = (TextView) rowView.findViewById(R.id.firstLine);
    final TextView addressView = (TextView) rowView.findViewById(R.id.secondLine);
    nameView.setText(shops[position].name);
    addressView.setText(shops[position].street);
    return rowView;
  }

}
