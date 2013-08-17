package net.phalanxx.cdiext.scope;

import javax.enterprise.context.spi.CreationalContext;

/**
 * Typed interface for a contextual instance container.
 *
 * @author rbachlec
 * @param <T> Type of instance
 */
public interface ContextualInstance<T> {

    /**
     * Returns the instance.
     *
     * @return instance
     */
    T getInstance();

    /**
     * Returns the creational context.
     *
     * @return creational context
     */
    CreationalContext<T> getCreationalContext();

}
