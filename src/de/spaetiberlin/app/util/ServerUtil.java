package de.spaetiberlin.app.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerUtil {

  public static void updateSpaetis(final Context context, final CallBackHandler callBackHandler) {
    ServerUtil.getJSON("http://spaeti.pavo.uberspace.de/dev/spaeti/", new Handler(
        new Handler.Callback() {

          @Override
          public boolean handleMessage(final Message msg) {
            try {
              final FileOutputStream fos = context.openFileOutput("spaetis.json",
                  Context.MODE_PRIVATE);
              fos.write(msg.getData().getString("json").getBytes());
              fos.close();
              callBackHandler.callBack();

            } catch (final IOException e) {
              e.printStackTrace();
            }

            return true;
          }
        }));
  }

  public static String getStringFromFile(final String filePath, final Context context)
      throws IOException {
    final FileInputStream in = context.openFileInput(filePath);
    final InputStreamReader inputStreamReader = new InputStreamReader(in);
    final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    final StringBuilder sb = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      sb.append(line);
    }

    return sb.toString();
  }

  public static void postJSON(final String url, final String body, final Handler handler) {
    final Thread thread = new Thread() {

      @Override
      public void run() {

        final HttpClient client = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(url);

        try {
          httpPost.setEntity(new StringEntity(body, "UTF8"));
        } catch (final UnsupportedEncodingException e) {
          e.printStackTrace();
        }

        httpPost.setHeader("Content-type", "application/json");

        try {
          final HttpResponse response = client.execute(httpPost);
          if (response != null) {
            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            final Message m = new Message();
            final Bundle b = new Bundle();
            b.putInt("status", statusCode);
            m.setData(b);
            handler.sendMessage(m);
          }
        } catch (final ClientProtocolException e) {
          e.printStackTrace();
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    };

    thread.start();
  }

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
            Log.e(ServerUtil.class.toString(), "Failed to download file");
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
