package net.phalanxx.cdiext.test;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationScopedBean extends AbstractTestBean {

    public static final String BEAN_ID = "ApplicationScopedBean";

    public ApplicationScopedBean() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}
