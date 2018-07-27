package com.benites.jeral.router_bar.storage.network.entity.Route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Creado por Jeral Benites el dia 20/03/2018 papu.
 */

public class OverviewPolyline {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    void setPoints(String points) {
        this.points = points;
    }

}
