package de.spaetiberlin.app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Shop {

  public static String[] favorites;

  public String name;
  public String id;
  public double lat;
  public double lng;
  public String street;
  public JSONArray opened;
  public JSONArray closed;
  public boolean pizza;
  public boolean condoms;
  public boolean newspapers;
  public boolean chips;

  public boolean favorite = false;

  public Shop(JSONObject jsonObject) {

    try {
      name = jsonObject.getString("name");
      id = jsonObject.getString("_id");
    } catch (JSONException e1) {
      e1.printStackTrace();
    }

    try {
      JSONObject assortment = jsonObject.getJSONObject("assortment");
      pizza = assortment.getBoolean("pizza");
      condoms = assortment.getBoolean("condoms");
      newspapers = assortment.getBoolean("newspapers");
      chips = assortment.getBoolean("chips");
    } catch (JSONException e1) {
      e1.printStackTrace();
    }

    try {
      JSONObject spaetiLocation = jsonObject.getJSONObject("location");
      lat = spaetiLocation.getDouble("lat");
      lng = spaetiLocation.getDouble("lng");
      street = spaetiLocation.getString("street");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    try {
      JSONObject businessHours = jsonObject.getJSONObject("businessHours");
      opened = businessHours.getJSONArray("opened");
      closed = businessHours.getJSONArray("closed");
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

}
