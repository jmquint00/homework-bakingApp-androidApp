package com.erdemtsynduev.homeworkbakingapp.network.api;

import retrofit2.http.GET;

public interface Api {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}
