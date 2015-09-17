package com.tasomaniac.muzei.earthview;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

public interface EarthViewService {

    String BASE_URL = "http://earthview.withgoogle.com";

    @GET
    Call<EarthView> nextEartView(@Url String url);

}
