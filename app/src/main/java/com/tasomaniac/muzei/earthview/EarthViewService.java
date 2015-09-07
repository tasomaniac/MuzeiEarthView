package com.tasomaniac.muzei.earthview;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

/**
 * Created by tasomaniac on 7/9/15.
 */
public interface EarthViewService {

    String BASE_URL = "http://earthview.withgoogle.com";

    String FIRST_EARTH_VIEW
            = "http://earthview.withgoogle.com/_api/istanbul-turkey-1888.json";

    @GET
    Call<EartView> nextEartView(@Url String url);

    class EartView {

        private String slug;
        private String mapsLink;
        private String api;
        private String downloadUrl;
        private String region;
        private String mapsTitle;
        private String thumbUrl;
        private String id;
        private String country;
        private String url;
        private String title;
        private double lat;
        private double lng;
        private String attribution;
        private String photoUrl;
        private String prevUrl;
        private String prevApi;
        private String nextApi;
        private String nextUrl;


        public EartView() {
        }

        public void setNextUrl(String nextUrl) {
            this.nextUrl = nextUrl;
        }

        public String getNextUrl() {
            return nextUrl;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getSlug() {
            return slug;
        }

        public void setMapsLink(String mapsLink) {
            this.mapsLink = mapsLink;
        }

        public String getMapsLink() {
            return mapsLink;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public String getApi() {
            return api;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setNextApi(String nextApi) {
            this.nextApi = nextApi;
        }

        public String getNextApi() {
            return nextApi;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getRegion() {
            return region;
        }

        public void setMapsTitle(String mapsTitle) {
            this.mapsTitle = mapsTitle;
        }

        public String getMapsTitle() {
            return mapsTitle;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public String getId() {
            return id;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountry() {
            return country;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLat() {
            return lat;
        }

        public void setAttribution(String attribution) {
            this.attribution = attribution;
        }

        public String getAttribution() {
            return attribution;
        }

        public void setPrevUrl(String prevUrl) {
            this.prevUrl = prevUrl;
        }

        public String getPrevUrl() {
            return prevUrl;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLng() {
            return lng;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPrevApi(String prevApi) {
            this.prevApi = prevApi;
        }

        public String getPrevApi() {
            return prevApi;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EartView && ((EartView) obj).getId().equals(id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "nextUrl = " + nextUrl + ", slug = " + slug + ", mapsLink = " + mapsLink + ", api = " + api + ", downloadUrl = " + downloadUrl + ", nextApi = " + nextApi + ", region = " + region + ", mapsTitle = " + mapsTitle + ", thumbUrl = " + thumbUrl + ", id = " + id + ", country = " + country + ", url = " + url + ", title = " + title + ", lat = " + lat + ", attribution = " + attribution + ", prevUrl = " + prevUrl + ", lng = " + lng + ", photoUrl = " + photoUrl + ", prevApi = " + prevApi;
        }
    }
}
