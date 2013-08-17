package net.phalanxx.cdiext.beans;

import javax.enterprise.context.ApplicationScoped;

@TestStereotypeApplicationScoped
@ApplicationScoped
public class ApplicationScopedStereotypedBean extends AbstractTestBean {

    public static final String BEAN_ID = "ApplicationScopedStereotypedBean";

    public ApplicationScopedStereotypedBean() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}
