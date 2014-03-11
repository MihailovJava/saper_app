package com.comfymobile.saadat.json;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Author Grinch
 * Date: 11.03.14
 * Time: 19:53
 */
public class EventRequest {
    @SerializedName("title")
    public String title;
    @SerializedName("text")
    public String text;
    @SerializedName("time")
    public String time;
    @SerializedName("city")
    public String city;
    @SerializedName("address")
    public String address;
    @SerializedName("additional")
    public String additional;
    @SerializedName("date")
    public Date date = new Date();
}
