package net.phalanxx.cdiext.scope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI extension registering the {@link DestroyableSingleton} scope.
 *
 * @author rbachlec
 */
public class DestroyableSingletonExtension implements Extension {

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {
        bbd.addScope(DestroyableSingleton.class, false, false);
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        abd.addContext(new DestroyableSingletonContextImpl());
    }

}
