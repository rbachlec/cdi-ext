package net.phalanxx.cdiext.test;

public class DependentScopedBean extends AbstractTestBean {

    public static final String BEAN_ID = "DependentScopedBean";

    public DependentScopedBean() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}