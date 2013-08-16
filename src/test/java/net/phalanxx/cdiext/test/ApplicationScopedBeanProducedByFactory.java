package net.phalanxx.cdiext.test;

import javax.enterprise.context.ApplicationScoped;

import net.phalanxx.cdiext.factory.CreatedByFactory;

@ApplicationScoped
@CreatedByFactory(factory=TestFactory.class)
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