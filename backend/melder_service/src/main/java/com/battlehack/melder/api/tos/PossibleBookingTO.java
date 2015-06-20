package com.battlehack.melder.api.tos;

/**
 * Created by aakhmerov on 20.06.15.
 */
public class PossibleBookingTO {
    private String date;
    private String place;
    private String url;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
