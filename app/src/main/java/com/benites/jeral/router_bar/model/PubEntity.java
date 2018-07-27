package com.benites.jeral.router_bar.model;


public class PubEntity {

    private String _id;

    private String name;

    private AddressEntity address;

    private String image;

    private HourEntity hour;

    private Boolean hora24;

    private Boolean delivery;

    private SocialEntity social;

    private String state;

    private String userRegister;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HourEntity getHour() {
        return hour;
    }

    public void setHour(HourEntity hour) {
        this.hour = hour;
    }

    public Boolean getHora24() {
        return hora24;
    }

    public void setHora24(Boolean hora24) {
        this.hora24 = hora24;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public SocialEntity getSocial() {
        return social;
    }

    public void setSocial(SocialEntity social) {
        this.social = social;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserRegister() {
        return userRegister;
    }

    public void setUserRegister(String userRegister) {
        this.userRegister = userRegister;
    }

    @Override
    public String toString() {
        return "PubEntity{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", image='" + image + '\'' +
                ", hour=" + hour +
                ", hora24=" + hora24 +
                ", delivery=" + delivery +
                ", social=" + social +
                ", state='" + state + '\'' +
                ", userRegister='" + userRegister + '\'' +
                '}';
    }
}
