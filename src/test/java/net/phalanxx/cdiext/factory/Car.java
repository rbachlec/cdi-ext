package net.phalanxx.cdiext.factory;


public abstract class Car {

    private String factoryName;

    public Car() {
        super();
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public abstract String getBrand();

}
