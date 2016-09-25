package com.tasomaniac.muzei.earthview.data.api;

public class EarthView {

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

    public String getMapsLink() {
        return mapsLink;
    }

    public String getApi() {
        return api;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getNextApi() {
        return nextApi;
    }

    public String getRegion() {
        return region;
    }

    public String getMapsTitle() {
        return mapsTitle;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public double getLat() {
        return lat;
    }

    public String getAttribution() {
        return attribution;
    }

    public String getPrevUrl() {
        return prevUrl;
    }

    public double getLng() {
        return lng;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPrevApi() {
        return prevApi;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EarthView && ((EarthView) obj).getId().equals(id);
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
