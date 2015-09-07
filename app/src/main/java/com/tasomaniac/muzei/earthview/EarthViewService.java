package com.tasomaniac.muzei.earthview;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

public interface EarthViewService {

    String BASE_URL = "http://earthview.withgoogle.com";

    String FIRST_EARTH_VIEW
            = "http://earthview.withgoogle.com/_api/istanbul-turkey-1888.json";

    @GET
    Call<EartView> nextEartView(@Url String url);

}
