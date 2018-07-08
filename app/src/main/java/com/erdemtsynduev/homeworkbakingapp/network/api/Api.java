package com.erdemtsynduev.homeworkbakingapp.network.api;

import com.erdemtsynduev.homeworkbakingapp.network.response.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface Api {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Observable<List<Recipe>> getRecipes();
}
