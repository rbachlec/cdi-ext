package net.phalanxx.cdiext.factory;

public class CarFactory implements Factory {

    public static final String FACTORY_NAME = "CarFactory";

    @Override
    public <T> T createInstance(Class<T> clazz) {
        try {
            T car = clazz.newInstance();
            ((Car) car).setFactoryName(FACTORY_NAME);

            return car;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Fatal error on producing car.", ex);
        }
    }
}
