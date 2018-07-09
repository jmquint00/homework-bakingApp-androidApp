package com.erdemtsynduev.homeworkbakingapp.screen.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.erdemtsynduev.homeworkbakingapp.R;
import com.erdemtsynduev.homeworkbakingapp.network.response.Recipe;
import com.erdemtsynduev.homeworkbakingapp.screen.recipeinfo.RecipeInfoActivity;

public class MainActivity extends AppCompatActivity implements RecipesFragment.OnRecipeClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeInfoActivity.class);
        intent.putExtra(RecipeInfoActivity.RECIPE_KEY, recipe);
        startActivity(intent);
    }
}
