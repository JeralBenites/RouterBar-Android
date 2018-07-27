package com.benites.jeral.router_bar.model;


public class AddressEntity {
    private String street;
    private CoordenatesEntity coord;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public CoordenatesEntity getCoord() {
        return coord;
    }

    public void setCoord(CoordenatesEntity coord) {
        this.coord = coord;
    }
}
