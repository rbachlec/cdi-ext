package net.phalanxx.cdiext.util.test;

import javax.inject.Named;

@Named("SpecialBean")
public class NamedBean implements TestBean {


    public NamedBean() {
        super();
    }

}
