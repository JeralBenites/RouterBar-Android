package pe.com.dev420.router_bar.model;


public class AddressEntity {
    private String street;

    private CoordenatesEntity loc;

    private Double radius;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public CoordenatesEntity getLoc() {
        return loc;
    }

    public void setLoc(CoordenatesEntity loc) {
        this.loc = loc;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
