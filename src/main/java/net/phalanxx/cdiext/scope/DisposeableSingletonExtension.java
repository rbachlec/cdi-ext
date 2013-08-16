package net.phalanxx.cdiext.scope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI extension registering the {@link DisposeableSingleton} scope.
 *
 * @author rbachlec
 */
public class DisposeableSingletonExtension implements Extension {

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        bbd.addScope(DisposeableSingleton.class, false, false);
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        abd.addContext(new DisposeableSingletonContextImpl());
    }

}
