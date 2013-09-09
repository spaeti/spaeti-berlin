package de.spaetiberlin.app.widgets;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerButton extends Button implements TimePickerDialog.OnTimeSetListener {

  final protected TimePickerDialog dialog;

  protected int hourOfDay = 9;
  protected int minute = 30;

  public TimePickerButton(final Context context, final int startHour, final int startMinute) {
    super(context);
    this.hourOfDay = startHour;
    this.minute = startMinute;
    dialog = new TimePickerDialog(context, this, hourOfDay, minute,
        DateFormat.is24HourFormat(context));
    this.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(final View v) {
        dialog.show();
      }
    });
    this.setText(hourOfDay + ":" + minute);
  }

  @Override
  public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
    this.hourOfDay = hourOfDay;
    this.minute = minute;
    this.setText(hourOfDay + ":" + minute);
  }

  /**
   * @return the hourOfDay
   */
  public int getHourOfDay() {
    return hourOfDay;
  }

  /**
   * @return the minute
   */
  public int getMinute() {
    return minute;
  }

}
