package net.phalanxx.cdiext.scope;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context implementation for the {@link DisposeableSingleton} scope.
 *
 * @author rbachlec
 */
public class DisposeableSingletonContextImpl implements Context {

    private Logger log = LoggerFactory.getLogger(DisposeableSingletonContextImpl.class);

    private ConcurrentHashMap<Bean, ContextualInstance<?>> beanStore = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getScope() {
        return DisposeableSingleton.class;
    }

    /** {@inheritDoc} */
    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;

        T instance;
        ContextualInstance<?> contextualInstance = beanStore.get(bean);
        if (contextualInstance == null) {
            log.debug("Creating instance of bean " + bean.toString() + ".");
            instance = contextual.create(creationalContext);
            contextualInstance = new DisposeableSingletonInstance(instance, creationalContext);
            beanStore.put(bean, contextualInstance);
        } else {
            log.debug("Found existing instance of bean " + bean.toString() + ".");
            instance = (T) contextualInstance.getInstance();
        }

        return instance;
    }

    /** {@inheritDoc} */
    @Override
    public <T> T get(Contextual<T> contextual) {
         ContextualInstance<?> contextualInstance = beanStore.get(contextual);
         return contextualInstance != null ? (T) contextualInstance.getInstance() : null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Removes a given instance from the {@link DisposeableSingleton} context.
     *
     * @param <T> type of instance to be disposed
     * @param instance instance to be disposed
     */
    protected <T> void dispose(T instance) {
        if (!contains(instance)) {
            throw new IllegalArgumentException("The given instance could not be found in " +
                                               "the DisposeableSingletonContext.");
        }

        for (Map.Entry<Bean, ContextualInstance<?>> entry : beanStore.entrySet()) {
            Bean bean = entry.getKey();
            ContextualInstance<? extends Object> contextualInstance = entry.getValue();

            if (contextualInstance.getInstance() == instance) {
                log.debug("Disposing instance of bean " + bean.toString() + ".");
                contextualInstance.getCreationalContext().release();
                beanStore.remove(bean);
                break;
            }
        }
    }

    /**
     * Checks if the given object is in the {@link DisposeableSingleton} context.
     *
     * @param <T> type of the object to search for
     * @param singleton object to be searched for
     * @return true/false
     */
    protected <T> Boolean contains(T instance) {
        for (Map.Entry<Bean, ContextualInstance<?>> entry : beanStore.entrySet()) {
            ContextualInstance<? extends Object> contextualInstance = entry.getValue();
            if (contextualInstance.getInstance() == instance) {
                return true;
            }
        }

        return false;
    }

    /**
     * A container class for disposeable singleton instances.
     *
     * @param <T> type of instance in container
     */
    private class DisposeableSingletonInstance<T> implements ContextualInstance<T> {
        private T instance;
        private CreationalContext<T> creationalContext;

        public DisposeableSingletonInstance(T instance, CreationalContext<T> creationalContext) {
            this.instance = instance;
            this.creationalContext = creationalContext;
        }

        @Override
        public T getInstance() {
            return instance;
        }

        @Override
        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
    }

}
