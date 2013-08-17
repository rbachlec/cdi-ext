package net.phalanxx.cdiext.beans;

import javax.inject.Inject;

@TestStereotypeApplicationScoped
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

    public DependentScopedBean getDependentScopedBean() {
        return dependentScopedBean;
    }

}
