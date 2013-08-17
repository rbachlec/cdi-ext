package net.phalanxx.cdiext.beans;

import javax.inject.Named;

@Named("SpecialBean")
public class NamedBean extends AbstractTestBean {

    public static final String BEAN_ID = "NamedBean";

    public NamedBean() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}