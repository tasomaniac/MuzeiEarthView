package com.tasomaniac.muzei.earthview.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface EarthViewApi {

    @GET
    Call<EarthView> nextEarthView(@Url String url);

}
