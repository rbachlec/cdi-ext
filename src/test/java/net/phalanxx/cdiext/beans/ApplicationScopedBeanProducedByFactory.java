package net.phalanxx.cdiext.beans;

import javax.enterprise.context.ApplicationScoped;

import net.phalanxx.cdiext.factory.ProducedByFactory;

@ApplicationScoped
@ProducedByFactory(factory=TestFactory.class)
public class ApplicationScopedBeanProducedByFactory extends AbstractTestBean {

    public static final String BEAN_ID = "ApplicationScopedBeanProducedByFactory";

    public ApplicationScopedBeanProducedByFactory() {
        super();
    }

    @Override
    public String getBeanId() {
        return BEAN_ID;
    }

}