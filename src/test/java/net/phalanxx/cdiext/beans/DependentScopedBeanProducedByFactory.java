package net.phalanxx.cdiext.beans;

import net.phalanxx.cdiext.factory.ProducedByFactory;

@ProducedByFactory(factory=TestFactory.class)
public class DependentScopedBeanProducedByFactory extends AbstractTestBean {

    public static final String BEAN_ID = "DependentScopedBeanProducedByFactory";

    public DependentScopedBeanProducedByFactory() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}