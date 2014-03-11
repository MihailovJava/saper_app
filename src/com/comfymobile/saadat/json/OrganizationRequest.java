package com.comfymobile.saadat.json;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Author Grinch
 * Date: 05.03.14
 * Time: 23:23
 */
public class OrganizationRequest {
    @SerializedName("name")
    public String name;
    @SerializedName("category")
    public String category;
    @SerializedName("address")
    public String address;
    @SerializedName("tn")
    public String tn;
    @SerializedName("site")
    public String site;
    @SerializedName("additional")
    public String additional;
    @SerializedName("date")
    public Date date = new Date();
}
