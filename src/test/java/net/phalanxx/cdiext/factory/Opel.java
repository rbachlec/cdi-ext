package net.phalanxx.cdiext.factory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@CreatedByFactory(factory=CarFactory.class)
public class Opel extends Car {

    public static final String BRAND = "Opel";

    public Opel() {
        super();
    }

    @Override
    public String getBrand() {
        return BRAND;
    }

}