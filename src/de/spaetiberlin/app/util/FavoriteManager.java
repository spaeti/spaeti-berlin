package de.spaetiberlin.app.util;

import java.util.List;

public class FavoriteManager {

  private static FavoriteManager instance;
  private List<String> favorites;

  public static FavoriteManager getInstance() {
    if (instance == null) {
      instance = new FavoriteManager();
    }
    return instance;
  }

  private FavoriteManager() {
  }

}
