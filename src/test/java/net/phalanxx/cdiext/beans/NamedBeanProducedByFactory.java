package net.phalanxx.cdiext.beans;

import javax.inject.Named;

import net.phalanxx.cdiext.factory.CreatedByFactory;

@Named("SpecialFactoryProducedBean")
@CreatedByFactory(factory=TestFactory.class)
public class NamedBeanProducedByFactory extends AbstractTestBean {

    public static final String BEAN_ID = "NamedBeanProducedByFactory";

    public NamedBeanProducedByFactory() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}