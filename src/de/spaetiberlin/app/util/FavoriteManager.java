package de.spaetiberlin.app.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import de.spaetiberlin.app.models.Shop;

public class FavoriteManager {

  private static FavoriteManager instance;
  private SharedPreferences settings;
  private List<String> favorites;
  private Map<String, Shop> storeMap;

  public static FavoriteManager getInstance() {
    if (instance == null) {
      instance = new FavoriteManager();
    }
    return instance;
  }

  /**
   * 
   * Initializes the favorite manager with an activity context and a map of
   * shops. This needs to be called once before the manager can be used.
   * 
   * @param context
   *          the context of the calling activity
   * @param storeMap
   * @return the instance object of the FavoriteManager
   */
  public FavoriteManager init(final Context context) {
    this.settings = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
    final String serialized = settings.getString("favorites", null);
    if (serialized != null) {
      favorites = new LinkedList<String>(Arrays.asList(TextUtils.split(serialized, ",")));
    } else {
      favorites = new LinkedList<String>();
    }
    return this;
  }

  public void setStoreMap(Map<String, Shop> storeMap) {
    this.storeMap = storeMap;
  }

  private FavoriteManager() {
  }

  /**
   * Gets all favorites as Shop objects, not just their ids. Note that this
   * iterates over the internal list of ids every time and thus might be slow
   * when used often.
   * 
   * @return all favorited shops.
   */
  public Shop[] getFavorites() {
    final Shop[] shops = new Shop[favorites.size()];
    for (int i = 0; i < favorites.size(); i++) {
      shops[i] = storeMap.get(favorites.get(i));
    }
    return shops;
  }

  public void remove(final String shopId) {
    favorites.remove(favorites.indexOf(shopId));
    saveFavorites();
  }

  public void add(final String shopId) {
    favorites.add(shopId);
    saveFavorites();
  }

  private void saveFavorites() {
    final SharedPreferences.Editor editor = settings.edit();
    editor.putString("favorites", TextUtils.join(",", favorites));
    editor.commit();
  }

  public boolean isFavorite(final String id) {
    return favorites.contains(id);
  }

}
