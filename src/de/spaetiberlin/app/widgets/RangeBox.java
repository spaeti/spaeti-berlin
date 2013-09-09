package de.spaetiberlin.app.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class RangeBox extends Button {

  private final String[] items = new String[] { "Montag", "Dienstag", "Mittwoch", "Donnerstag",
      "Freitag", "Samstag", "Sonntag" };

  private final String[] abbr = new String[] { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };

  private final String[] selected = new String[] { "Mo", "Di", "Mi", "Do", "Fr", null, null };

  public RangeBox(final Context context) {
    super(context);
    init(context);
  }

  public RangeBox(final Context context, final AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public RangeBox(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(final Context context) {
    this.setText("Mo-Fr");

    final AlertDialog dialog = new AlertDialog.Builder(context)
        .setTitle("Test")
        .setMultiChoiceItems(items, new boolean[] { true, true, true, true, true, false, false },
            new OnMultiChoiceClickListener() {

              @Override
              public void onClick(final DialogInterface dialog, final int which,
                  final boolean isChecked) {
                if (isChecked) {
                  selected[which] = abbr[which];
                } else {
                  selected[which] = null;
                }
              }
            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialogs, final int id) {
            RangeBox.this.setText(RangeBox.chain(new ArrayList<String>(Arrays.asList(selected)))
                .replaceAll(", $", ""));
          }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(final DialogInterface dialog, final int id) {
          }
        }).create();

    this.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(final View v) {
        dialog.show();
      }
    });
  }

  private static String chain(final List<String> list) {
    Collections.reverse(list);

    String chained = "";

    while (list.size() > 0) {
      String first = null, last = null, check = null;
      while (first == null && list.size() > 0) {
        check = list.remove(list.size() - 1);
        if (check != null) {
          first = check;
        }
      }
      do {
        if ((list.size() > 0)) {
          check = list.remove(list.size() - 1);
        } else {
          check = null;
        }
        if (check != null) {
          last = check;
        }
      } while (check != null);
      if (first != null) {
        if (last != null) {
          chained += first + "-" + last + ", ";
        } else {
          chained += first + ", ";
        }
      }
    }
    return chained;
  }

  /**
   * @return the selected
   */
  public String[] getSelected() {
    return selected;
  }
}
