package net.phalanxx.cdiext.factory.test;

import net.phalanxx.cdiext.factory.Factory;

public class TestFactory implements Factory {

    @Override
    public <T> T createInstance(Class<T> clazz) {
        try {
            T car = clazz.newInstance();
            ((AbstractTestBean) car).setProducedByFactory(Boolean.TRUE);

            return car;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Fatal error on producing car.", ex);
        }
    }
}
