package net.phalanxx.cdiext.test;

import javax.inject.Inject;

@TestStereotype
public class StereotypedBeanWithInjectionPoint extends AbstractTestBean {

    public static final String BEAN_ID = "StereotypedBeanWithInjectionPoint";

    @Inject DependentScopedBean dependentScopedBean;

    public StereotypedBeanWithInjectionPoint() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}
