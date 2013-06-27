package de.spaetiberlin.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class JsonUtil {

  public static void getJSON(final String url, final Handler handler) {

    final Thread thread = new Thread() {

      @Override
      public void run() {

        final StringBuilder builder = new StringBuilder();
        final HttpClient client = new DefaultHttpClient();
        final HttpGet httpGet = new HttpGet(url);
        try {
          final HttpResponse response = client.execute(httpGet);
          final StatusLine statusLine = response.getStatusLine();
          final int statusCode = statusLine.getStatusCode();
          if (statusCode == 200) {
            final HttpEntity entity = response.getEntity();
            final InputStream content = entity.getContent();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
              builder.append(line);
            }
          } else {
            Log.e(JsonUtil.class.toString(), "Failed to download file");
          }
        } catch (final ClientProtocolException e) {
          e.printStackTrace();
        } catch (final IOException e) {
          e.printStackTrace();
        }
        final Message m = new Message();
        final Bundle b = new Bundle();
        b.putString("json", builder.toString());
        m.setData(b);
        handler.sendMessage(m);

      }
    };

    thread.start();

  }

}
