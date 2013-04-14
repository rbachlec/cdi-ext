package net.phalanxx.cdiext.scope;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 *
 * @author rbachlec
 */
public interface ContextualInstance<T> {

    T getInstance();

    CreationalContext<T> getCreationalContext();

    Contextual<T> getContextual();

}
