package net.phalanxx.cdiext.scope;

import javax.enterprise.context.spi.CreationalContext;

/**
 *
 * @author rbachlec
 * @param <T> Type of instance
 */
public interface ContextualInstance<T> {

    T getInstance();

    CreationalContext<T> getCreationalContext();

}
