package net.phalanxx.cdiext.scope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI extension registering the {@link DisposableSingleton} scope.
 *
 * @author rbachlec
 */
public class DisposableSingletonExtension implements Extension {

    /**
     * Adds scope before bean discovery phase.
     *
     * @param bbd {@link BeforeBeanDiscovery} event
     */
    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
        bbd.addScope(DisposableSingleton.class, false, false);
    }

    /**
     * Adds context after bean discovery phase.
     *
     * @param abd {@link AfterBeanDiscovery} event
     */
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
        abd.addContext(new DisposableSingletonContextImpl());
    }

}
