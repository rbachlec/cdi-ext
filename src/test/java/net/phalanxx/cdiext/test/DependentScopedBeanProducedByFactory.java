package net.phalanxx.cdiext.test;

import net.phalanxx.cdiext.factory.CreatedByFactory;

@CreatedByFactory(factory=TestFactory.class)
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