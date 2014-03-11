package com.comfymobile.saadat.json;

import com.google.gson.annotations.SerializedName;

/**
 * Author Grinch
 * Date: 05.03.14
 * Time: 23:23
 */
public class OrganizationRequest {
    @SerializedName("name")
    String name;
    @SerializedName("category")
    String category;
    @SerializedName("address")
    String address;
    @SerializedName("tn")
    String tn;
    @SerializedName("site")
    String site;
    @SerializedName("additional")
    String additional;
}
