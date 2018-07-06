package com.erdemtsynduev.homeworkbakingapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.erdemtsynduev.homeworkbakingapp.network.models.Recipe;

public class ApplicationPreferences {

    public static final String APP_PREFERENCES_NAME = "app_preferences";

    public static void saveRecipe(Context context, Recipe recipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        prefs.putString(context.getString(R.string.widget_recipe_key), Recipe.toBase64String(recipe));

        prefs.apply();
    }

    public static Recipe loadRecipe(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String recipeBase64 = prefs.getString(context.getString(R.string.widget_recipe_key), "");

        return "".equals(recipeBase64) ? null : Recipe.fromBase64(prefs.getString(context.getString(R.string.widget_recipe_key), ""));
    }
}
