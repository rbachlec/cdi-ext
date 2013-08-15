package net.phalanxx.cdiext.factory;

@CreatedByFactory(factory=CarFactory.class)
public class Volkswagen extends Car {

    public static final String BRAND = "Volkswagen";

    public Volkswagen() {
        super();
    }

    @Override
    public String getBrand() {
        return BRAND;
    }

}