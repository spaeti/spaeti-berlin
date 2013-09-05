package de.spaetiberlin.app.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

  private ImageView bmImage;

  public DownloadImageTask(ImageView bmImage) {
    this.bmImage = bmImage;
  }

  protected void onPreExecute() {

  }

  protected Bitmap doInBackground(String... urls) {
    String urldisplay = urls[0];
    Bitmap mIcon11 = null;
    try {
      InputStream in = new java.net.URL(urldisplay).openStream();
      mIcon11 = BitmapFactory.decodeStream(in);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mIcon11;
  }

  protected void onPostExecute(Bitmap result) {
    // set image of your imageview
    bmImage.setImageBitmap(result);
  }
}
