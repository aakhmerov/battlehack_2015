package com.battlehack.melder.api.tos;

/**
 * Created by aakhmerov on 20.06.15.
 */
public class PossibleBookingTO {
    private String date;
    private String placeName;
    private String placeAddress;
    private String dateUrl;
    private String bookingUrl;
    private String bookingTime;




    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getDateUrl() {
        return dateUrl;
    }

    public void setDateUrl(String dateUrl) {
        this.dateUrl = dateUrl;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public void setBasics(PossibleBookingTO bookingInitial) {
        this.setDateUrl(bookingInitial.getDateUrl());
        this.setDate(bookingInitial.getDate());
    }
}
