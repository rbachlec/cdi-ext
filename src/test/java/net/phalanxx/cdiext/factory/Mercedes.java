package net.phalanxx.cdiext.factory;

public class Mercedes extends Car {

    public static final String BRAND = "Mercedes";

    public Mercedes() {
        super();
    }

    @Override
    public String getBrand() {
        return BRAND;
    }

}