package com.example.dialekto;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String PREF_NAME = "FavoritesPref";
    private static final String KEY_FAVORITES = "Favorites";

    private static FavoritesManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private List<CardItem> favoriteItems;

    private FavoritesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadFavorites();
    }

    public static synchronized FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context);
        }
        return instance;
    }

    // Load favorites from SharedPreferences
    private void loadFavorites() {
        String json = sharedPreferences.getString(KEY_FAVORITES, null);
        Type type = new TypeToken<List<CardItem>>() {}.getType();
        favoriteItems = json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    // Save favorites to SharedPreferences
    private void saveFavorites() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FAVORITES, gson.toJson(favoriteItems));
        editor.apply();
    }

    public List<CardItem> getFavoriteItems() {
        return favoriteItems;
    }

    public void addFavorite(CardItem item) {
        if (!favoriteItems.contains(item)) {
            favoriteItems.add(item);
            saveFavorites(); // Save to SharedPreferences
        }
    }

    public void removeFavorite(CardItem item) {
        if (favoriteItems.contains(item)) {
            favoriteItems.remove(item);
            saveFavorites(); // Save to SharedPreferences
        }
    }

    public boolean isFavorite(CardItem item) {
        return favoriteItems.contains(item);
    }
}